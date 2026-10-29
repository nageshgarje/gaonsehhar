package com.gaonsehhar.controller;

import com.gaonsehhar.service.ProductServiceContract;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import static com.gaonsehhar.product.ProductDtos.*;

@RestController
@RequestMapping({"/api/admin/products", "/api/v1/admin/products"})
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {
  private final ProductServiceContract service;
  public AdminProductController(ProductServiceContract service) {
    this.service = service;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ProductResponse create(@Valid @RequestBody CreateProductRequest request) {
    return service.createByAdmin(request);
  }

  @PutMapping("/{id}")
  public ProductResponse update(@PathVariable Long id, @Valid @RequestBody UpdateProductRequest request) {
    return service.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) { service.deleteByAdmin(id); }
}
