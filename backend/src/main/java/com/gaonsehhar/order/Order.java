package com.gaonsehhar.order;

import com.gaonsehhar.user.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name="orders")
public class Order {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="buyer_id") private User buyer;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private OrderStatus status=OrderStatus.PLACED;
 @Column(name="delivery_name",nullable=false) private String deliveryName;
 @Column(name="delivery_phone",nullable=false) private String deliveryPhone;
 @Column(name="delivery_address",nullable=false) private String deliveryAddress;
 @Enumerated(EnumType.STRING) @Column(name="payment_method",nullable=false) private PaymentMethod paymentMethod=PaymentMethod.COD;
 @Enumerated(EnumType.STRING) @Column(name="payment_status",nullable=false) private PaymentStatus paymentStatus=PaymentStatus.PENDING;
 @Column(nullable=false) private BigDecimal total;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt=Instant.now();
 @OneToMany(mappedBy="order",cascade=CascadeType.ALL,orphanRemoval=true) private List<OrderItem> items=new ArrayList<>();
 protected Order(){}
 public Order(User buyer,String deliveryName,String deliveryPhone,String deliveryAddress,PaymentMethod paymentMethod){this.buyer=buyer;this.deliveryName=deliveryName;this.deliveryPhone=deliveryPhone;this.deliveryAddress=deliveryAddress;this.paymentMethod=paymentMethod;this.total=BigDecimal.ZERO;}
 public void addItem(OrderItem item){items.add(item);item.setOrder(this);}
 public void setTotal(BigDecimal total){this.total=total;} public void setPaymentStatus(PaymentStatus paymentStatus){this.paymentStatus=paymentStatus;} public Long getId(){return id;} public User getBuyer(){return buyer;} public OrderStatus getStatus(){return status;} public PaymentMethod getPaymentMethod(){return paymentMethod;} public PaymentStatus getPaymentStatus(){return paymentStatus;} public BigDecimal getTotal(){return total;} public Instant getCreatedAt(){return createdAt;} public List<OrderItem> getItems(){return items;} public String getDeliveryName(){return deliveryName;} public String getDeliveryPhone(){return deliveryPhone;} public String getDeliveryAddress(){return deliveryAddress;}
}
