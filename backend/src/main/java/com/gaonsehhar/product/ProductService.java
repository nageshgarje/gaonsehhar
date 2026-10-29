package com.gaonsehhar.product;

import com.gaonsehhar.common.ApiException;
import com.gaonsehhar.user.Role;
import com.gaonsehhar.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.gaonsehhar.product.ProductDtos.*;

@Service
public class ProductService {
 private final ProductRepository products;
 public ProductService(ProductRepository products){this.products=products;}
 @Transactional(readOnly=true)
 public Page<ProductResponse> search(String category,String query,Pageable pageable){
   boolean hasCategory=category != null && !category.isBlank(); boolean hasQuery=query != null && !query.isBlank();
   if(hasCategory && hasQuery) return products.search(category,query,pageable).map(this::toResponse);
   if(hasCategory) return products.searchByCategory(category,pageable).map(this::toResponse);
   if(hasQuery) return products.searchByQuery(query,pageable).map(this::toResponse);
   return products.searchAll(pageable).map(this::toResponse);
 }
 @Transactional(readOnly=true) public ProductResponse findById(Long id){return toResponse(products.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,"Product not found")));}
 @Transactional public ProductResponse create(CreateProductRequest r,User owner){if(owner.getRole()!=Role.SELLER && owner.getRole()!=Role.ADMIN)throw new ApiException(HttpStatus.FORBIDDEN,"Only sellers or admins can create products");Product p=new Product(owner,r.name(),r.description(),r.category(),r.price(),r.stock(),r.unit(),r.imageUrl(),r.village());return toResponse(products.save(p));}
 @Transactional public ProductResponse update(Long id,UpdateProductRequest r){Product p=products.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,"Product not found"));p.update(r.name(),r.description(),r.category(),r.price(),r.stock(),r.unit(),r.imageUrl(),r.village());return toResponse(products.save(p));}
 @Transactional public void deleteByAdmin(Long id){Product p=products.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,"Product not found"));products.delete(p);}
 @Transactional public void delete(Long id,User seller){Product p=products.findByIdAndSellerId(id,seller.getId()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,"Product not found"));products.delete(p);}
 private ProductResponse toResponse(Product p){return new ProductResponse(p.getId(),p.getSeller().getId(),p.getSeller().getName(),p.getName(),p.getDescription(),p.getCategory(),p.getPrice(),p.getStock(),p.getUnit(),p.getImageUrl(),p.getVillage());}
}
