package com.gaonsehhar.tracking;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderLocationRepository extends JpaRepository<OrderLocation, Long> {
  List<OrderLocation> findByOrderIdOrderByRecordedAtAsc(Long orderId);
}
