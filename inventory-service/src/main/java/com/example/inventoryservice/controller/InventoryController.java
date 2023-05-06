package com.example.inventoryservice.controller;

import com.example.inventoryservice.dto.InventoryResponseDto;
import com.example.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    // Expecting requests in format api/inventor?sku-code="abc","xyz"
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponseDto> inStock(@RequestParam List<String> skuCode) {
        return inventoryService.inStock(skuCode);
    }
}
