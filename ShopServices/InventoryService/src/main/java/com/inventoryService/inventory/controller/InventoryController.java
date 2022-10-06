package com.inventoryService.inventory.controller;

import com.inventoryService.inventory.dto.InventoryResponse;
import com.inventoryService.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping()
    public List<InventoryResponse> isInStock(@RequestParam List<String> skuCodeList){
        return inventoryService.isInStock(skuCodeList);
    }
}
