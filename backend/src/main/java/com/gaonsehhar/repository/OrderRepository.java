package com.gaonsehhar.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.gaonsehhar.order.Order;

public interface OrderRepository extends JpaRepository<Order,Long> { 
    
    List<Order> findByBuyerIdOrderByCreatedAtDesc(Long buyerId); 

}
