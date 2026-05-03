package com.tmdt.projectpremium.service;

import com.tmdt.projectpremium.entity.CartItem;
import com.tmdt.projectpremium.repository.CartItemRep;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartItemSer {
    private final CartItemRep rep;

    public List<CartItem> getAllCartItem(Long id) {
        return rep.findByCartId(id);
    }
}
