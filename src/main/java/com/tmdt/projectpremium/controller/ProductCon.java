package com.tmdt.projectpremium.controller;

import com.tmdt.projectpremium.dto.response.ProductInfoRes;
import com.tmdt.projectpremium.service.ProductSer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@RequiredArgsConstructor
public class ProductCon {
    private final ProductSer ser;

    @GetMapping("/all")
    public List<ProductInfoRes> getAllProduct() {
        return ser.getAllProduct();
    }

    @GetMapping("/{productId}")
    public ProductInfoRes getProductById(@PathVariable long productId) {
        return ser.getProductById(productId);
    }
}
