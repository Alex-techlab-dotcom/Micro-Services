package com.inventoryService.inventory.service;


import com.inventoryService.inventory.dto.InventoryResponse;
import com.inventoryService.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCodeList){
        /*
           Retrieve items in skuCodeList from database
           and map them to a List of InventoryResponse entities
         */
       return inventoryRepository.findBySkuCodeIn(skuCodeList)
               .stream()
               .map(inventory -> InventoryResponse.builder()
                       .skuCode(inventory.getSkuCode())
                       .isInStock(inventory.getQuantity()>0)
                       .build())
               .toList();
    }
}
