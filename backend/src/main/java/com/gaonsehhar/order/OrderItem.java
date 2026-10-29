package com.gaonsehhar.order;

import com.gaonsehhar.product.Product;
import com.gaonsehhar.user.User;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity @Table(name="order_items")
public class OrderItem {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="order_id") private Order order;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="product_id") private Product product;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="seller_id") private User seller;
 @Column(nullable=false) private Integer quantity;
 @Column(name="unit_price",nullable=false) private BigDecimal unitPrice;
 @Column(name="product_name",nullable=false) private String productName;
 protected OrderItem(){}
 public OrderItem(Product product,Integer quantity){this.product=product;this.seller=product.getSeller();this.quantity=quantity;this.unitPrice=product.getPrice();this.productName=product.getName();}
 void setOrder(Order order){this.order=order;} public Long getId(){return id;} public Product getProduct(){return product;} public Integer getQuantity(){return quantity;} public BigDecimal getUnitPrice(){return unitPrice;} public String getProductName(){return productName;}
}
