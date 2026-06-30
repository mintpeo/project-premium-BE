package com.tmdt.projectpremium.service;

import com.tmdt.projectpremium.entity.Order;
import com.tmdt.projectpremium.entity.OrderItem;
import com.tmdt.projectpremium.entity.ProductKey;
import com.tmdt.projectpremium.entity.Product;
import com.tmdt.projectpremium.repository.OrderItemRep;
import com.tmdt.projectpremium.repository.ProductKeyRepository;
import com.tmdt.projectpremium.repository.ProductRep;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductKeyService {

    private final ProductKeyRepository productKeyRep;
    private final ProductRep productRep;
    private final OrderItemRep orderItemRep;

    @Transactional(readOnly = true)
    public List<ProductKey> getKeysBySeller(Long sellerId) {
        return productKeyRep.findBySellerId(sellerId);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getKeyStatsBySeller(Long sellerId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalKeys", productKeyRep.countBySellerId(sellerId));
        stats.put("soldKeys", productKeyRep.countBySoldTrueAndSellerId(sellerId));
        stats.put("availableKeys", productKeyRep.countBySoldFalseAndSellerId(sellerId));
        return stats;
    }

    @Transactional
    public ProductKey addProductKey(Long productId, String keyCode, Long sellerId) {
        Product product = productRep.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        if (product.getSeller() == null || product.getSeller().getId() != sellerId) {
            throw new RuntimeException("Sản phẩm không thuộc quyền quản lý của bạn");
        }
        ProductKey pk = ProductKey.builder()
                .product(product)
                .keyCode(keyCode)
                .sold(false)
                .build();
        return productKeyRep.save(pk);
    }

    @Transactional
    public void addProductKeysBulk(Long productId, List<String> keyCodes, Long sellerId) {
        Product product = productRep.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        if (product.getSeller() == null || product.getSeller().getId() != sellerId) {
            throw new RuntimeException("Sản phẩm không thuộc quyền quản lý của bạn");
        }
        for (String code : keyCodes) {
            ProductKey pk = ProductKey.builder()
                    .product(product)
                    .keyCode(code)
                    .sold(false)
                    .build();
            productKeyRep.save(pk);
        }
    }

    @Transactional
    public void deleteProductKey(Long id, Long sellerId) {
        ProductKey pk = productKeyRep.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy key"));
        if (pk.getProduct().getSeller() == null || pk.getProduct().getSeller().getId() != sellerId) {
            throw new RuntimeException("Key không thuộc quyền quản lý của bạn");
        }
        if (pk.isSold()) {
            throw new RuntimeException("Không thể xoá key đã bán");
        }
        productKeyRep.deleteById(id);
    }

    @Transactional
    public void assignKeysToOrder(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            if (item.getKeyCode() == null || item.getKeyCode().isEmpty()) {
                int quantity = item.getQuantity();
                Long productId = item.getProduct().getId();
                
                // Lấy các key chưa bán của sản phẩm này
                List<ProductKey> availableKeys = productKeyRep.findAvailableKeys(productId, PageRequest.of(0, quantity));
                
                List<String> assignedCodes = new ArrayList<>();
                for (int i = 0; i < quantity; i++) {
                    if (i < availableKeys.size()) {
                        ProductKey key = availableKeys.get(i);
                        key.setSold(true);
                        key.setOrderItem(item);
                        productKeyRep.save(key);
                        assignedCodes.add(key.getKeyCode());
                    } else {
                        // Hết key, tạo placeholder
                        String placeholder = "PENDING-REPLENISH-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
                        assignedCodes.add("[" + placeholder + " - Liên hệ Seller]");
                    }
                }
                
                item.setKeyCode(String.join(", ", assignedCodes));
                orderItemRep.save(item);
            }
        }
    }
}
