package com.gaonsehhar.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import static com.gaonsehhar.product.ProductDtos.*;

@RestController @RequestMapping({"/api/products", "/api/v1/products"})
public class ProductController {
 private final ProductService service;
 public ProductController(ProductService service){this.service=service;}
 @GetMapping public Page<ProductResponse> search(@RequestParam(required=false) String category,@RequestParam(required=false) String q,@PageableDefault(size=20) Pageable pageable){return service.search(category,q,pageable);}
 @GetMapping("/{id}") public ProductResponse get(@PathVariable Long id){return service.findById(id);}

}
