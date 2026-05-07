package com.tmdt.projectpremium.controller;

import com.tmdt.projectpremium.dto.request.AddToCartReq;
import com.tmdt.projectpremium.dto.response.ShowCartRes;
import com.tmdt.projectpremium.service.CartSer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class CartCon {
    private final CartSer ser;

    @DeleteMapping("/delete")
    public void deleteProduct(@RequestParam long cartItemId) {
        ser.removeProductInCart(cartItemId);
    }

    @PatchMapping("/updateQuantity")
    public void updateQuantity(@RequestParam long cartItemId, @RequestParam int quantity) {
        ser.updateQuantityCartItem(cartItemId, quantity);
    }

    @GetMapping("/{userId}")
    public List<ShowCartRes> getYourCart(@PathVariable long userId) {
        return ser.showYourCart(userId);
    }

    @PostMapping("/addToCart")
    public void addToCart(@RequestBody AddToCartReq req) {
        ser.addToCart(req);
    }
}
