package com.tmdt.projectpremium.controller;

import com.tmdt.projectpremium.entity.Category;
import com.tmdt.projectpremium.entity.Order;
import com.tmdt.projectpremium.entity.Product;
import com.tmdt.projectpremium.entity.User;
import com.tmdt.projectpremium.service.AdminService;
import com.tmdt.projectpremium.service.SellerBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final SellerBalanceService sellerBalanceService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/revenue")
    public ResponseEntity<List<Map<String, Object>>> getRevenue(@RequestParam(defaultValue = "30d") String period) {
        return ResponseEntity.ok(adminService.getRevenueByPeriod(period));
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders(
            @RequestParam(required = false) String status
    ) {
        List<Order> orders = adminService.getAllOrders();
        if (status != null && !status.isEmpty()) {
            orders = orders.stream()
                    .filter(o -> status.equals(o.getOrderStatus()))
                    .toList();
        }
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<?> updateUserRole(
            @PathVariable Long userId,
            @RequestBody Map<String, String> body
    ) {
        try {
            String newRole = body.get("role");
            if (newRole == null || newRole.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Role is required"));
            }
            User updated = adminService.updateUserRole(userId, newRole);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(adminService.getAllCategories());
    }

    @PostMapping("/categories")
    public ResponseEntity<?> createCategory(@RequestBody Map<String, String> body) {
        try {
            String name = body.get("name");
            String icon = body.get("icon");
            if (name == null || name.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Name is required"));
            }
            Category created = adminService.createCategory(name, icon);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String name = body.get("name");
            String icon = body.get("icon");
            Category updated = adminService.updateCategory(id, name, icon);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/categories/{id}/toggle")
    public ResponseEntity<?> toggleCategoryStatus(@PathVariable Long id) {
        try {
            adminService.toggleCategoryStatus(id);
            return ResponseEntity.ok(Map.of("message", "Updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            adminService.deleteCategory(id);
            return ResponseEntity.ok(Map.of("message", "Deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
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

    @GetMapping("/sellers")
    public ResponseEntity<List<Map<String, Object>>> getSellers() {
        return ResponseEntity.ok(adminService.getAllSellers());
    }

    @PutMapping("/sellers/{id}/verify")
    public ResponseEntity<?> verifySeller(@PathVariable Long id) {
        try {
            adminService.verifySeller(id);
            return ResponseEntity.ok(Map.of("message", "Seller verified successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/sellers/{id}/ban")
    public ResponseEntity<?> banSeller(@PathVariable Long id) {
        try {
            adminService.banSeller(id);
            return ResponseEntity.ok(Map.of("message", "Seller banned successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/seller-pending-earnings")
    public ResponseEntity<?> getPendingEarnings() {
        try {
            return ResponseEntity.ok(sellerBalanceService.getPendingEarnings());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/seller-earnings/{id}/approve")
    public ResponseEntity<?> approveEarning(
            @PathVariable Long id,
            @RequestBody Map<String, Long> body
    ) {
        try {
            Long adminId = body.get("adminId");
            if (adminId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "adminId is required"));
            }
            sellerBalanceService.approveEarning(id, adminId);
            return ResponseEntity.ok(Map.of("message", "Approved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/seller-earnings/approve-all/{sellerId}")
    public ResponseEntity<?> approveAllForSeller(
            @PathVariable Long sellerId,
            @RequestBody Map<String, Long> body
    ) {
        try {
            Long adminId = body.get("adminId");
            if (adminId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "adminId is required"));
            }
            sellerBalanceService.approveAllForSeller(sellerId, adminId);
            return ResponseEntity.ok(Map.of("message", "All earnings approved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}