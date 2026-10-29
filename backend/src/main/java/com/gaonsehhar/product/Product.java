package com.gaonsehhar.product;

import com.gaonsehhar.user.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity @Table(name="products")
public class Product {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="seller_id") private User seller;
 @Column(nullable=false) private String name; @Column(nullable=false) private String description; @Column(nullable=false) private String category;
 @Column(nullable=false) private BigDecimal price; @Column(nullable=false) private Integer stock; @Column(nullable=false) private String unit;
 @Column(name="image_url") private String imageUrl; private String village; @Column(nullable=false) private boolean active=true;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt=Instant.now(); @Column(name="updated_at",nullable=false) private Instant updatedAt=Instant.now();
 protected Product(){}
 public Product(User seller,String name,String description,String category,BigDecimal price,Integer stock,String unit,String imageUrl,String village){this.seller=seller;this.name=name;this.description=description;this.category=category;this.price=price;this.stock=stock;this.unit=unit;this.imageUrl=imageUrl;this.village=village;}
 public Long getId(){return id;} public User getSeller(){return seller;} public String getName(){return name;} public String getDescription(){return description;} public String getCategory(){return category;} public BigDecimal getPrice(){return price;} public Integer getStock(){return stock;} public String getUnit(){return unit;} public String getImageUrl(){return imageUrl;} public String getVillage(){return village;} public boolean isActive(){return active;}
 public void update(String name,String description,String category,BigDecimal price,Integer stock,String unit,String imageUrl,String village){this.name=name;this.description=description;this.category=category;this.price=price;this.stock=stock;this.unit=unit;this.imageUrl=imageUrl;this.village=village;this.updatedAt=Instant.now();}
 public void decrementStock(int quantity){if(quantity>stock)throw new IllegalStateException("Insufficient stock");stock-=quantity;}
}
