package com.tmdt.projectpremium.service;

import com.tmdt.projectpremium.entity.Product;
import com.tmdt.projectpremium.repository.ProductRep;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSer {
    private final ProductRep rep;

    public List<Product> getAllProduct() {
        return rep.findAll();
    }

    public Product getProductById(long productId) {
        return rep.findById(productId).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + productId));
    }
}
