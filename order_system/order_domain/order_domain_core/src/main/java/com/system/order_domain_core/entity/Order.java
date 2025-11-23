package com.system.order_domain_core.entity;

import com.system.order_domain_core.exception.OrderDomainException;
import com.system.order_domain_core.valueobject.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private final OrderId id;
    private final CustomerId customerId;
    private final RestaurantId restaurantId;
    private final TrackingId trackingId;

    private BigDecimal price;
    private OrderStatus orderStatus;
    private List<OrderItem> items;
    private String failureMessages;
    private final ZonedDateTime createdAt;

    private BigDecimal originalPrice;
    private BigDecimal discount;
    private String voucherCode;

    private Integer rating;
    private String comment;


    public Order(OrderId id, CustomerId customerId, RestaurantId restaurantId, TrackingId trackingId,
                 BigDecimal price, OrderStatus orderStatus, List<OrderItem> items, String failureMessages,
                 ZonedDateTime createdAt, BigDecimal originalPrice, BigDecimal discount, String voucherCode,
                 Integer rating, String comment) {
        this.id = id;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.trackingId = trackingId;
        this.price = price;
        this.orderStatus = orderStatus;
        this.items = new ArrayList<>(items); // Cho phép thay đổi
        this.failureMessages = failureMessages;
        this.createdAt = createdAt;
        this.originalPrice = originalPrice;
        this.discount = discount;
        this.voucherCode = voucherCode;
        this.rating = rating;
        this.comment = comment;
    }


    /**
     * (Feature 4) Tính toán tổng tiền gốc
     */
    private void calculateOriginalPrice() {
        this.originalPrice = this.items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity() != null ? item.getQuantity() : 0)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.price = this.originalPrice;
    }

    /**
     * (Feature 4) Áp dụng Voucher
     */
    public void applyVoucher(Voucher voucher) {
        if (!this.orderStatus.equals(OrderStatus.PENDING)) {
            throw new OrderDomainException("Chỉ áp dụng voucher cho đơn hàng PENDING.");
        }

        BigDecimal calculatedDiscount = voucher.calculateDiscount(this.originalPrice);

        this.discount = calculatedDiscount;
        this.price = this.originalPrice.subtract(this.discount); // Giá cuối = Gốc - Giảm
        this.voucherCode = voucher.getCode();
    }

    /**
     * (Feature Update) Cập nhật món hàng
     */
    public void updateItems(List<OrderItem> newItems) {
        if (this.orderStatus != OrderStatus.PENDING) {
            throw new OrderDomainException("Chỉ có thể cập nhật đơn hàng ở trạng thái PENDING.");
        }
        this.items = new ArrayList<>(newItems);
        calculateOriginalPrice();

        this.discount = BigDecimal.ZERO;
        this.voucherCode = null;
    }

    /**
     * (Feature 5) Thêm đánh giá
     */
    public void addRating(int rating, String comment) {
        if (this.orderStatus != OrderStatus.APPROVED && this.orderStatus != OrderStatus.PAID) {
            throw new OrderDomainException("Không thể đánh giá đơn hàng chưa hoàn thành.");
        }
        if (rating < 1 || rating > 5) {
            throw new OrderDomainException("Rating phải từ 1 đến 5 sao.");
        }
        this.rating = rating;
        this.comment = comment;
    }

    public void pay() {
        if (this.orderStatus != OrderStatus.PENDING) {
            throw new OrderDomainException("Only PENDING orders can be paid");
        }
        this.orderStatus = OrderStatus.PAID;
    }

    public void cancel(String failureMessage) {
        if (this.orderStatus != OrderStatus.PENDING) {
            throw new OrderDomainException("Only PENDING orders can be cancelled");
        }
        this.orderStatus = OrderStatus.CANCELLED;
        this.failureMessages = (this.failureMessages == null ? "" : this.failureMessages + ", ") + failureMessage;
    }
    public static Order createOrder(OrderId id, CustomerId customerId, RestaurantId restaurantId,
                                    TrackingId trackingId, List<OrderItem> items) {
        Order order = new Order(id, customerId, restaurantId, trackingId, BigDecimal.ZERO, OrderStatus.PENDING,
                items, null, ZonedDateTime.now(), BigDecimal.ZERO, BigDecimal.ZERO, null, null, null);
        order.calculateOriginalPrice(); // Tính giá gốc và giá cuối
        return order;
    }


    public OrderId getId() { return id; }
    public CustomerId getCustomerId() { return customerId; }
    public RestaurantId getRestaurantId() { return restaurantId; }
    public TrackingId getTrackingId() { return trackingId; }
    public BigDecimal getPrice() { return price; }
    public OrderStatus getOrderStatus() { return orderStatus; }
    public List<OrderItem> getItems() { return items; }
    public String getFailureMessages() { return failureMessages; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public BigDecimal getOriginalPrice() { return originalPrice; }
    public BigDecimal getDiscount() { return discount; }
    public String getVoucherCode() { return voucherCode; }
    public Integer getRating() { return rating; }
    public String getComment() { return comment; }
}