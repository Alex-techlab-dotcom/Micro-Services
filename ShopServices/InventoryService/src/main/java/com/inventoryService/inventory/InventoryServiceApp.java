package com.inventoryService.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class InventoryServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApp.class, args);
    }
}
