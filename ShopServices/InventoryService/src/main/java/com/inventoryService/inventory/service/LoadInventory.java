package com.inventoryService.inventory.service;

import com.inventoryService.inventory.model.Inventory;
import com.inventoryService.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class LoadInventory {
    private final InventoryRepository inventoryRepository;

    @Bean("LoadInventory")
    public CommandLineRunner LoadInventoryItems() {
        return args -> {

            if (inventoryRepository.findBySkuCode("iphone_14").isEmpty()) {

                Inventory Iphone_14_inventory = Inventory.builder()
                        .skuCode("iphone_14")
                        .quantity(100)
                        .build();

                inventoryRepository.save(Iphone_14_inventory);
            }

            if (inventoryRepository.findBySkuCode("iphone_14_red").isEmpty()) {

                Inventory Iphone_14_Red_inventory = Inventory.builder()
                        .skuCode("iphone_14_red")
                        .quantity(0)
                        .build();

                inventoryRepository.save(Iphone_14_Red_inventory);
            }
        };
    }
}
