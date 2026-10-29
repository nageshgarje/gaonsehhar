package com.gaonsehhar.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.util.Optional;
import com.gaonsehhar.product.Product;
import com.gaonsehhar.service.OrderService;
import com.gaonsehhar.user.Role;
import com.gaonsehhar.user.User;
import com.gaonsehhar.repository.OrderRepository;
import com.gaonsehhar.repository.ProductRepository;
import com.gaonsehhar.repository.UserRepository;

import org.junit.jupiter.api.Test;
import static com.gaonsehhar.order.OrderDtos.*;

class OrderServiceTest {
 @Test void rejectsInsufficientStock() {
   User seller=new User("Seller","seller@test.com","hash",null,null,Role.SELLER);
   Product product=new Product(seller,"Honey","Pure honey","Food",new BigDecimal("300"),1,"kg",null,"Pune");
   ProductRepository products=mock(ProductRepository.class); when(products.findByIdForUpdate(1L)).thenReturn(Optional.of(product));
   User buyer=new User("Buyer","buyer@test.com","hash",null,null,Role.CUSTOMER);
   OrderService service=new OrderService(mock(OrderRepository.class),products);
   assertThrows(com.gaonsehhar.common.ApiException.class,()->service.create(new CreateOrderRequest(java.util.List.of(new OrderLine(1L,2)),"Buyer","999","Address",PaymentMethod.COD,null),buyer));
 }
}
