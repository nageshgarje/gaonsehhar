package com.gaonsehhar.product;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ProductDtos {
 public record CreateProductRequest(@NotBlank @Size(max=160) String name,@NotBlank String description,@NotBlank String category,@DecimalMin("0.01") BigDecimal price,@PositiveOrZero Integer stock,@NotBlank String unit,String imageUrl,String village){}
 public record UpdateProductRequest(@NotBlank @Size(max=160) String name,@NotBlank String description,@NotBlank String category,@DecimalMin("0.01") BigDecimal price,@PositiveOrZero Integer stock,@NotBlank String unit,String imageUrl,String village) {}
 public record ProductResponse(Long id,Long sellerId,String sellerName,String name,String description,String category,BigDecimal price,Integer stock,String unit,String imageUrl,String village){}
}
