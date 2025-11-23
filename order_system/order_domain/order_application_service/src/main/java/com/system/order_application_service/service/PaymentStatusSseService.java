package com.system.order_application_service.service;

import com.system.order_application_service.dto.PaymentStatusUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class PaymentStatusSseService {

    private final ConcurrentHashMap<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final ScheduledExecutorService cleanupExecutor = Executors.newSingleThreadScheduledExecutor();

    public PaymentStatusSseService() {
        // Schedule cleanup of completed emitters every 5 minutes
        cleanupExecutor.scheduleAtFixedRate(this::cleanupCompletedEmitters, 5, 5, TimeUnit.MINUTES);
    }

    public SseEmitter createEmitter(String orderId) {
        log.info("Creating SSE emitter for order: {}", orderId);

        // Create emitter with 30-minute timeout
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);

        emitter.onCompletion(() -> {
            log.info("SSE emitter completed for order: {}", orderId);
            emitters.remove(orderId);
        });

        emitter.onTimeout(() -> {
            log.info("SSE emitter timed out for order: {}", orderId);
            emitters.remove(orderId);
        });

        emitter.onError(throwable -> {
            log.error("SSE emitter error for order: {}", orderId, throwable);
            emitters.remove(orderId);
        });

        // Send initial status
        try {
            emitter.send(SseEmitter.event()
                .name("status")
                .data(PaymentStatusUpdate.builder()
                    .orderId(java.util.UUID.fromString(orderId))
                    .status("PENDING")
                    .message("Waiting for payment processing")
                    .timestamp(LocalDateTime.now())
                    .build()));
        } catch (IOException e) {
            log.error("Error sending initial status for order: {}", orderId, e);
            emitter.complete();
            return null;
        }

        emitters.put(orderId, emitter);
        return emitter;
    }

    public void sendStatusUpdate(String orderId, PaymentStatusUpdate statusUpdate) {
        SseEmitter emitter = emitters.get(orderId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                    .name("status")
                    .data(statusUpdate));

                log.info("Sent status update for order {}: {}", orderId, statusUpdate.getStatus());

                // // If payment is completed or failed, complete the emitter after 5 seconds
                // if ("COMPLETED".equals(statusUpdate.getStatus()) || "FAILED".equals(statusUpdate.getStatus())) {
                //     cleanupExecutor.schedule(() -> {
                //         emitter.complete();
                //         emitters.remove(orderId);
                //         log.info("Completed SSE emitter for order {} after 5 second delay", orderId);
                //     }, 5, TimeUnit.SECONDS);
                // }
            } catch (IOException e) {
                log.error("Error sending status update for order: {}", orderId, e);
                emitter.completeWithError(e);
                emitters.remove(orderId);
            }
        } else {
            log.warn("No active SSE emitter found for order: {}", orderId);
        }
    }

    private void cleanupCompletedEmitters() {
        log.debug("Cleaning up completed emitters. Current count: {}", emitters.size());
        // Emitters are automatically removed on completion/timeout, but we can add additional cleanup logic here if needed
    }
}