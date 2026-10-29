package com.gaonsehhar.service;

import com.gaonsehhar.product.ProductDtos.CreateProductRequest;
import com.gaonsehhar.product.ProductDtos.ProductResponse;
import com.gaonsehhar.product.ProductDtos.UpdateProductRequest;
import com.gaonsehhar.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductServiceContract {
  Page<ProductResponse> search(String category, String query, Pageable pageable);
  ProductResponse findById(Long id);
  ProductResponse create(CreateProductRequest request, User owner);
  ProductResponse createByAdmin(CreateProductRequest request);
  ProductResponse update(Long id, UpdateProductRequest request);
  void deleteByAdmin(Long id);
  void delete(Long id, User seller);
}
