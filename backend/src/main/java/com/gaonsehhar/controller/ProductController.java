package com.gaonsehhar.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import com.gaonsehhar.product.ProductDtos;
import com.gaonsehhar.product.ProductDtos.ProductResponse;
import com.gaonsehhar.service.ProductServiceContract;

import static com.gaonsehhar.product.ProductDtos.*;

@RestController @RequestMapping({"/api/products", "/api/v1/products"})
public class ProductController {
 private final ProductServiceContract service;
 public ProductController(ProductServiceContract service){this.service=service;}
 @GetMapping public Page<ProductResponse> search(@RequestParam(required=false) String category,@RequestParam(required=false) String q,@PageableDefault(size=20) Pageable pageable){return service.search(category,q,pageable);}
 @GetMapping("/{id}") public ProductResponse get(@PathVariable Long id){return service.findById(id);}

}
