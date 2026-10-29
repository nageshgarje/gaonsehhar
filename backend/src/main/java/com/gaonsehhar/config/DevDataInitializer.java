package com.gaonsehhar.config;

import com.gaonsehhar.product.Product;
import com.gaonsehhar.product.ProductRepository;
import com.gaonsehhar.user.Role;
import com.gaonsehhar.user.User;
import com.gaonsehhar.user.UserRepository;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DevDataInitializer implements CommandLineRunner {
  private final UserRepository users;
  private final ProductRepository products;
  private final PasswordEncoder passwordEncoder;

  public DevDataInitializer(UserRepository users, ProductRepository products, PasswordEncoder passwordEncoder) {
    this.users=users;
    this.products=products;
    this.passwordEncoder=passwordEncoder;
  }

  @Override
  public void run(String... args) {
    if(products.count()>0) return;
    User seller=users.findFirstByRole(Role.SELLER).orElseGet(() -> users.save(new User("Rampur Farmer","seller@gaonsehhar.local",passwordEncoder.encode("Farmer123"),"9999999999","Rampur",Role.SELLER)));
    add(seller,"Sona Wheat","Golden village wheat","Grains",48,120,"kg","Rampur");
    add(seller,"Organic Red Rice","Naturally grown heirloom rice","Grains",120,80,"kg","Nandgaon");
    add(seller,"Fresh Farm Tomato","Fresh tomatoes harvested at dawn","Vegetables",38,100,"kg","Sangvi");
    add(seller,"Arhar Toor Dal","Premium unpolished toor dal","Pulses",165,60,"kg","Rampur");
    add(seller,"Forest Wild Honey","Raw village forest honey","Natural Products",280,14,"jar","Rampur");
  }

  private void add(User seller,String name,String description,String category,double price,int stock,String unit,String village) {
    products.save(new Product(seller,name,description,category,BigDecimal.valueOf(price),stock,unit,null,village));
  }
}
