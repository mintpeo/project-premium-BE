package com.tmdt.projectpremium.service;

import com.tmdt.projectpremium.dto.AddToCartReq;
import com.tmdt.projectpremium.entity.Cart;
import com.tmdt.projectpremium.entity.CartItem;
import com.tmdt.projectpremium.entity.Product;
import com.tmdt.projectpremium.entity.User;
import com.tmdt.projectpremium.repository.CartItemRep;
import com.tmdt.projectpremium.repository.CartRep;
import com.tmdt.projectpremium.repository.ProductRep;
import com.tmdt.projectpremium.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartSer {
    private final CartRep rep;
    private final CartItemRep cartItemRep;
    private final UserRepository userRep;
    private final ProductRep productRep;

    private Cart checkUserHaveCart(Long userId) {
        Cart checkCart = rep.findByUserId(userId);
        if (checkCart == null) {
            // check user id
            User user = userRep.findById(userId).orElseThrow(() -> new RuntimeException("Không tìm thấy user_id:" + userId));
            Cart newCart = new Cart(); // create
            newCart.setUser(user);
            return rep.save(newCart);
        }
        return checkCart; // have cart
    }

    public void addToCart(AddToCartReq req) { // cartItem
        Cart cart = checkUserHaveCart(req.getUserId());
        Product product = productRep.findById(req.getProductId()).orElseThrow(() -> new RuntimeException("Không tìm thấy product_id:" + req.getProductId()));
        CartItem newCartItem = new CartItem();
        newCartItem.setCart(cart);
        newCartItem.setProduct(product);
        newCartItem.setQuantity(req.getQuantity());
        newCartItem.setDuration(req.getDuration());
        newCartItem.setTypeUser(req.getTypeUser());
        cartItemRep.save(newCartItem);
    }
}
