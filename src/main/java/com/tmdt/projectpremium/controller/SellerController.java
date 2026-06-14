package com.tmdt.projectpremium.controller;

import com.tmdt.projectpremium.entity.Order;
import com.tmdt.projectpremium.entity.Product;
import com.tmdt.projectpremium.entity.SellerEarning;
import com.tmdt.projectpremium.repository.SellerEarningRepository;
import com.tmdt.projectpremium.service.AdminService;
import com.tmdt.projectpremium.service.SellerBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/seller")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class SellerController {

    private final AdminService adminService;
    private final SellerBalanceService sellerBalanceService;
    private final SellerEarningRepository sellerEarningRep;

    @GetMapping("/products/{sellerId}")
    public ResponseEntity<List<Product>> getSellerProducts(@PathVariable Long sellerId) {
        return ResponseEntity.ok(adminService.getProductsBySeller(sellerId));
    }

    @PostMapping("/products")
    public ResponseEntity<?> createProduct(@RequestBody Map<String, Object> body) {
        try {
            String name = (String) body.get("name");
            String img = (String) body.get("img");
            Integer price = body.get("price") != null ? Integer.valueOf(body.get("price").toString()) : null;
            Integer priceOri = body.get("priceOri") != null ? Integer.valueOf(body.get("priceOri").toString()) : null;
            Long sellerId = body.get("sellerId") != null ? Long.valueOf(body.get("sellerId").toString()) : null;
            @SuppressWarnings("unchecked")
            List<Long> categoryIds = body.get("categoryIds") != null
                    ? ((List<Integer>) body.get("categoryIds")).stream().map(Long::valueOf).toList()
                    : null;
            if (name == null || name.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Name is required"));
            }
            if (sellerId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "sellerId is required"));
            }
            Product created = adminService.createProduct(name, img, price, priceOri, sellerId, categoryIds);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            String name = (String) body.get("name");
            String img = (String) body.get("img");
            Integer price = body.get("price") != null ? Integer.valueOf(body.get("price").toString()) : null;
            Integer priceOri = body.get("priceOri") != null ? Integer.valueOf(body.get("priceOri").toString()) : null;
            @SuppressWarnings("unchecked")
            List<Long> categoryIds = body.get("categoryIds") != null
                    ? ((List<Integer>) body.get("categoryIds")).stream().map(Long::valueOf).toList()
                    : null;
            Product updated = adminService.updateProduct(id, name, img, price, priceOri, categoryIds);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            adminService.deleteProduct(id);
            return ResponseEntity.ok(Map.of("message", "Deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/balance/{sellerId}")
    public ResponseEntity<Map<String, Object>> getBalance(@PathVariable Long sellerId) {
        return ResponseEntity.ok(sellerBalanceService.getSellerBalance(sellerId));
    }

    @GetMapping("/transactions/{sellerId}")
    public ResponseEntity<List<SellerEarning>> getTransactions(@PathVariable Long sellerId) {
        return ResponseEntity.ok(sellerEarningRep.findBySellerIdOrderByCreatedAtDesc(sellerId));
    }

    @GetMapping("/orders/{sellerId}")
    public ResponseEntity<List<Order>> getSellerOrders(@PathVariable Long sellerId) {
        return ResponseEntity.ok(adminService.getOrdersBySeller(sellerId));
    }
}