package com.tmdt.projectpremium.controller;

import com.tmdt.projectpremium.entity.CartItem;
import com.tmdt.projectpremium.service.CartItemSer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cartItem")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class CartItemCon {
    private final CartItemSer ser;

    @GetMapping("/{cartId}")
    public List<CartItem> getAllCartItem(@PathVariable Long cartId) {
        return ser.getAllCartItem(cartId);
    }
}
