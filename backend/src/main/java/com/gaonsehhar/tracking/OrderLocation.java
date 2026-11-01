package com.gaonsehhar.tracking;

import com.gaonsehhar.order.Order;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="order_locations")
public class OrderLocation {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Long id;
  @ManyToOne(fetch=FetchType.LAZY, optional=false)
  @JoinColumn(name="order_id")
  private Order order;
  @Column(nullable=false) private String latitude;
  @Column(nullable=false) private String longitude;
  private String description;
  @Column(name="recorded_at", nullable=false) private Instant recordedAt=Instant.now();

  protected OrderLocation() {}
  public OrderLocation(Order order, String latitude, String longitude, String description) {
    this.order=order;
    this.latitude=latitude;
    this.longitude=longitude;
    this.description=description;
  }
  public Long getId(){return id;}
  public String getLatitude(){return latitude;}
  public String getLongitude(){return longitude;}
  public String getDescription(){return description;}
  public Instant getRecordedAt(){return recordedAt;}
}
