package com.tmdt.projectpremium.controller;

import com.tmdt.projectpremium.dto.AddToCartReq;
import com.tmdt.projectpremium.service.CartSer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class CartCon {
    private final CartSer ser;

    @PostMapping("/addToCart")
    public void addToCart(@RequestBody AddToCartReq req) {
        ser.addToCart(req);
    }
}
