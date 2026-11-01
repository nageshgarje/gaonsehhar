package com.gaonsehhar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"com.gaonsehhar.repository", "com.gaonsehhar.tracking"})
public class GaonSeGharApplication {
    public static void main(String[] args) {
        SpringApplication.run(GaonSeGharApplication.class, args);
    }
}
