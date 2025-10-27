package com.system.order_dataaccess.entity;

import jakarta.persistence.*;
// ... (imports)
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders", schema = "\"order\"")
public class OrderEntity {

    @Id
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;
    @Column(name = "restaurant_id", nullable = false)
    private UUID restaurantId;
    @Column(name = "tracking_id", nullable = false, unique = true)
    private UUID trackingId;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price; // Giá cuối

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @Column(name = "failure_messages", columnDefinition = "VARCHAR")
    private String failureMessages;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;


    @Column(name = "original_price", precision = 10, scale = 2)
    private BigDecimal originalPrice;

    @Column(name = "discount", precision = 10, scale = 2)
    private BigDecimal discount;

    @Column(name = "voucher_code")
    private String voucherCode;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "comment")
    private String comment;


    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> items = new ArrayList<>();


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

    public OrderEntity() {}
    public void addItem(OrderItemEntity item) { items.add(item); item.setOrder(this); }
    public void removeItem(OrderItemEntity item) { items.remove(item); item.setOrder(null); }
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
    public String getOrderStatus() { return orderStatus != null ? orderStatus.name() : null; }
    public void setOrderStatus(OrderStatus orderStatus) { this.orderStatus = orderStatus; }
    public String getFailureMessages() { return failureMessages; }
    public void setFailureMessages(String failureMessages) { this.failureMessages = failureMessages; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
    public List<OrderItemEntity> getItems() { return items; }
    public void setItems(List<OrderItemEntity> items) { this.items = items; if (items != null) { items.forEach(item -> item.setOrder(this)); } }
}