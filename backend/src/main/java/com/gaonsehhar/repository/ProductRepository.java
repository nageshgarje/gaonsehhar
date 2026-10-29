package com.gaonsehhar.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;
import org.springframework.data.repository.query.Param;

import com.gaonsehhar.product.Product;

public interface ProductRepository extends JpaRepository<Product,Long> {
 @Query("select p from Product p join fetch p.seller where p.active=true")
 Page<Product> searchAll(Pageable pageable);

 @Query("select p from Product p join fetch p.seller where p.active=true and lower(p.category)=lower(:category)")
 Page<Product> searchByCategory(@Param("category") String category, Pageable pageable);

 @Query("select p from Product p join fetch p.seller where p.active=true and lower(p.name) like lower(concat('%',concat(:query,'%')))")
 Page<Product> searchByQuery(@Param("query") String query, Pageable pageable);
 
 @Query("select p from Product p join fetch p.seller where p.active=true and lower(p.category)=lower(:category) and lower(p.name) like lower(concat('%',concat(:query,'%')))")
 Page<Product> search(@Param("category") String category,@Param("query") String query, Pageable pageable);
 
 Optional<Product> findByIdAndSellerId(Long id,Long sellerId);
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select p from Product p where p.id=:id") Optional<Product> findByIdForUpdate(@Param("id") Long id);
}
