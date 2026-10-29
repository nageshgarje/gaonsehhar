package com.gaonsehhar.product;

import com.gaonsehhar.user.User;
import com.gaonsehhar.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import static com.gaonsehhar.product.ProductDtos.*;

@RestController
@RequestMapping({"/api/admin/products", "/api/v1/admin/products"})
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {
  private final ProductService service;
  private final UserRepository users;
  public AdminProductController(ProductService service, UserRepository users) { this.service=service; this.users=users; }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ProductResponse create(@Valid @RequestBody CreateProductRequest request) {
    User owner=users.findByEmailIgnoreCase("village-sellers@gaonsehhar.local")
      .or(() -> users.findFirstByRole(com.gaonsehhar.user.Role.SELLER))
      .orElseThrow(() -> new IllegalStateException("A seller account is required to own catalog products"));
    return service.create(request, owner);
  }

  @PutMapping("/{id}")
  public ProductResponse update(@PathVariable Long id, @Valid @RequestBody UpdateProductRequest request) {
    return service.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) { service.deleteByAdmin(id); }
}
