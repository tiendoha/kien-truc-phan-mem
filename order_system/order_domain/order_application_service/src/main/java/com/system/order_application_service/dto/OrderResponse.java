package com.system.order_application_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Response object containing complete order information")
public class OrderResponse {
    @Schema(description = "Unique identifier of the order", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "Unique identifier of the customer", example = "987e6543-e21b-34d5-a678-542614174999")
    private UUID customerId;

    @Schema(description = "Unique identifier of the restaurant", example = "456e7890-e12b-34d5-a678-642614174888")
    private UUID restaurantId;

    @Schema(description = "Unique tracking identifier for the order", example = "789e0123-f45b-67c8-a901-532614174777")
    private UUID trackingId;

    @Schema(description = "Final price after discounts", example = "25.99")
    private BigDecimal price;

    @Schema(description = "Current status of the order", example = "PAID", allowableValues = {"PENDING", "PAID", "APPROVED"})
    private String orderStatus;

    @Schema(description = "List of items in the order")
    private List<OrderItemResponse> items;

    @Schema(description = "Failure messages if order processing failed")
    private String failureMessages;

    @Schema(description = "Timestamp when order was created")
    private ZonedDateTime createdAt;

    @Schema(description = "Original price before discounts", example = "30.99")
    private BigDecimal originalPrice;

    @Schema(description = "Discount amount applied", example = "5.00")
    private BigDecimal discount;

    @Schema(description = "Voucher code used", example = "SAVE10")
    private String voucherCode;

    @Schema(description = "Customer rating (1-5)", example = "5")
    private Integer rating;

    @Schema(description = "Customer comment about the order")
    private String comment;


    public OrderResponse() {
    }


    public OrderResponse(UUID id, UUID customerId, UUID restaurantId, UUID trackingId, BigDecimal price,
                         String orderStatus, List<OrderItemResponse> items, String failureMessages,
                         ZonedDateTime createdAt, BigDecimal originalPrice, BigDecimal discount,
                         String voucherCode, Integer rating, String comment) {
        this.id = id;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.trackingId = trackingId;
        this.price = price;
        this.orderStatus = orderStatus;
        this.items = items;
        this.failureMessages = failureMessages;
        this.createdAt = createdAt;
        this.originalPrice = originalPrice;
        this.discount = discount;
        this.voucherCode = voucherCode;
        this.rating = rating;
        this.comment = comment;
    }


    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }
    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }
    public String getVoucherCode() { return voucherCode; }
    public void setVoucherCode(String voucherCode) { this.voucherCode = voucherCode; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public UUID getRestaurantId() { return restaurantId; }
    public void setRestaurantId(UUID restaurantId) { this.restaurantId = restaurantId; }
    public UUID getTrackingId() { return trackingId; }
    public void setTrackingId(UUID trackingId) { this.trackingId = trackingId; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    public List<OrderItemResponse> getItems() { return items; }
    public void setItems(List<OrderItemResponse> items) { this.items = items; }
    public String getFailureMessages() { return failureMessages; }
    public void setFailureMessages(String failureMessages) { this.failureMessages = failureMessages; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
}