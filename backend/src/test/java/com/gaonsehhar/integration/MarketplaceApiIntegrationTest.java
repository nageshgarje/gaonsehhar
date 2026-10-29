package com.gaonsehhar.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaonsehhar.product.Product;
import com.gaonsehhar.product.ProductRepository;
import com.gaonsehhar.user.Role;
import com.gaonsehhar.user.User;
import com.gaonsehhar.user.UserRepository;
import com.gaonsehhar.order.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MarketplaceApiIntegrationTest {
  @Autowired MockMvc mvc;
  @Autowired ObjectMapper mapper;
  @Autowired UserRepository users;
  @Autowired ProductRepository products;
  @Autowired OrderRepository orders;
  @Autowired PasswordEncoder passwords;

  @BeforeEach void reset() {
    orders.deleteAll(); products.deleteAll(); users.deleteAll();
    users.save(new User("Admin", "admin@gaonsehhar.test", passwords.encode("admin123456"), null, "Rampur", Role.ADMIN));
    users.save(new User("Customer", "customer@gaonsehhar.test", passwords.encode("customer123456"), null, "Rampur", Role.CUSTOMER));
    User seller=users.save(new User("Seller", "seller@gaonsehhar.test", passwords.encode("seller123456"), null, "Rampur", Role.SELLER));
    products.save(new Product(seller, "Organic Rice", "Village red rice", "Grains", new BigDecimal("120.00"), 20, "kg", null, "Rampur"));
  }

  private String login(String email, String password) throws Exception {
    MvcResult result=mvc.perform(post("/api/v1/auth/login")
      .contentType(MediaType.APPLICATION_JSON)
      .content(mapper.writeValueAsString(Map.of("email",email,"password",password))))
      .andExpect(status().isOk()).andReturn();
    return mapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
  }

  @Test void customerCanRegisterAndBrowseButCannotManageProducts() throws Exception {
    mvc.perform(post("/api/v1/auth/register")
      .contentType(MediaType.APPLICATION_JSON)
      .content(mapper.writeValueAsString(Map.of("name","New Customer", "email","new.customer@test.local", "password","customer123456", "role","CUSTOMER"))))
      .andExpect(status().isCreated()).andExpect(jsonPath("$.user.role").value("CUSTOMER"));

    mvc.perform(get("/api/v1/products?category=Grains&q=rice"))
      .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].name").value("Organic Rice"));

    String customerToken=login("customer@gaonsehhar.test", "customer123456");
    mvc.perform(post("/api/v1/admin/products")
      .header("Authorization", "Bearer "+customerToken)
      .contentType(MediaType.APPLICATION_JSON)
      .content(productJson("Forbidden Rice", "desc")))
      .andExpect(status().isForbidden());

    Long id=products.findAll().get(0).getId();
    mvc.perform(put("/api/v1/admin/products/{id}", id)
      .header("Authorization", "Bearer "+customerToken)
      .contentType(MediaType.APPLICATION_JSON)
      .content(productJson("Changed by customer", "desc")))
      .andExpect(status().isForbidden());
  }

  @Test void adminCanCreateAndUpdateProductDetails() throws Exception {
    String adminToken=login("admin@gaonsehhar.test", "admin123456");
    MvcResult created=mvc.perform(post("/api/v1/admin/products")
      .header("Authorization", "Bearer "+adminToken)
      .contentType(MediaType.APPLICATION_JSON)
      .content(productJson("Wheat", "Fresh village wheat")))
      .andExpect(status().isCreated()).andExpect(jsonPath("$.name").value("Wheat")).andReturn();
    Long id=mapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

    mvc.perform(put("/api/v1/admin/products/{id}", id)
      .header("Authorization", "Bearer "+adminToken)
      .contentType(MediaType.APPLICATION_JSON)
      .content(productJson("Updated Wheat", "Updated description")))
      .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Updated Wheat"))
      .andExpect(jsonPath("$.description").value("Updated description"));
  }

  @Test void customerCanPlaceOrderAgainstPublicCatalog() throws Exception {
    String customerToken=login("customer@gaonsehhar.test", "customer123456");
    Long id=products.findAll().get(0).getId();
    String body="{\"items\":[{\"productId\":"+id+",\"quantity\":2}],\"deliveryName\":\"Customer\",\"deliveryPhone\":\"9999999999\",\"deliveryAddress\":\"Rampur delivery point\",\"paymentMethod\":\"UPI\",\"paymentReference\":\"demo@upi\"}";
    mvc.perform(post("/api/v1/orders")
      .header("Authorization", "Bearer "+customerToken)
      .contentType(MediaType.APPLICATION_JSON).content(body))
      .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("PLACED")).andExpect(jsonPath("$.paymentMethod").value("UPI"));
  }

  private String productJson(String name, String description) throws Exception {
    return mapper.writeValueAsString(Map.of("name",name,"description",description,"category","Grains","price",new BigDecimal("100.00"),"stock",10,"unit","kg","village","Rampur"));
  }
}
