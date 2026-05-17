package com.tmdt.projectpremium.service;

import com.tmdt.projectpremium.dto.request.AddToCartReq;
import com.tmdt.projectpremium.dto.response.ShowCartRes;
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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartSer {
    private final CartRep rep;
    private final CartItemRep cartItemRep;
    private final UserRepository userRep;
    private final ProductRep productRep;

    private Cart checkUserHaveCart(long userId) {
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


    // Remove Product In Cart
    public void removeProductInCart(long cartItemId) {
        if (!cartItemRep.existsById(cartItemId)) throw new RuntimeException("Không tìm thấy cartItem_id:" + cartItemId);
        cartItemRep.deleteById(cartItemId);
    }

    // Update Quantity
    public void updateQuantityCartItem(long cartItemId, int quantity) {
        if (quantity <= 0) return;

        CartItem cartItem = cartItemRep.findById(cartItemId).orElseThrow(() -> new RuntimeException("Không tìm thấy cartItem_id:" + cartItemId));
        cartItem.setQuantity(quantity);
        cartItemRep.save(cartItem);
    }

    // Just Get Cart Item Id
    public List<Long> getCartItemId(long userId) {
        List<Long> list = new ArrayList<>();
        Cart cart = checkUserHaveCart(userId);
        List<CartItem> cartItemList = cartItemRep.findByCartId(cart.getId());
        for (CartItem cartItem : cartItemList) {
            list.add(cartItem.getId());
        }

        return list;
    }

    // Show Your Cart
    public List<ShowCartRes> showYourCart(long userId) {
        Cart cart = checkUserHaveCart(userId);
        List<CartItem> cartItemList = cartItemRep.findByCartId(cart.getId());
        List<ShowCartRes> res = new ArrayList<>();
        for (CartItem cartItem : cartItemList) {
            ShowCartRes showCartRes = new ShowCartRes();
            showCartRes.setId(cartItem.getId());
            showCartRes.setProductId(cartItem.getProduct().getId());
            showCartRes.setProductName(cartItem.getProduct().getName());
            showCartRes.setProductImg(cartItem.getProduct().getImg());
            showCartRes.setProductPrice(cartItem.getProduct().getPrice());
            showCartRes.setQuantity(cartItem.getQuantity());
            showCartRes.setTypeUser(cartItem.getTypeUser());
            showCartRes.setDuration(cartItem.getDuration());

            res.add(showCartRes);
        }

        return res;
    }

    // Add To Cart
    // chua xu ly trung item
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
