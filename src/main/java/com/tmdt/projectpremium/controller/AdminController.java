package com.tmdt.projectpremium.controller;

import com.tmdt.projectpremium.entity.*;
import com.tmdt.projectpremium.repository.UserRepository;
import com.tmdt.projectpremium.repository.WithdrawRequestRepository;
import com.tmdt.projectpremium.service.AdminService;
import com.tmdt.projectpremium.service.SellerBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final SellerBalanceService sellerBalanceService;
    private final WithdrawRequestRepository withdrawRep;
    private final UserRepository userRep;

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

    @GetMapping("/products")
    public ResponseEntity<?> getAllProducts() {
        try {
            return ResponseEntity.ok(adminService.getAllProductsForAdmin());
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

    @PutMapping("/users/{userId}/ban")
    public ResponseEntity<?> toggleUserBan(@PathVariable Long userId) {
        try {
            User updated = adminService.toggleUserBan(userId);
            return ResponseEntity.ok(Map.of(
                "message", updated.isBanned() ? "Đã khoá tài khoản" : "Đã mở khoá tài khoản",
                "banned", updated.isBanned()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/users/{userId}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable Long userId, @RequestBody Map<String, String> body) {
        try {
            String newPassword = body.get("password");
            if (newPassword == null || newPassword.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Mật khẩu mới không được để trống"));
            }
            adminService.resetPassword(userId, newPassword);
            return ResponseEntity.ok(Map.of("message", "Đã reset mật khẩu thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId, @RequestBody Map<String, String> body) {
        try {
            String newStatus = body.get("status");
            if (newStatus == null || newStatus.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Status is required"));
            }
            Order updated = adminService.updateOrderStatus(orderId, newStatus);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/products/pending")
    public ResponseEntity<?> getPendingProducts() {
        try {
            return ResponseEntity.ok(adminService.getPendingProducts());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/products/{id}/approve")
    public ResponseEntity<?> approveProduct(@PathVariable Long id) {
        try {
            adminService.approveProduct(id);
            return ResponseEntity.ok(Map.of("message", "Đã duyệt sản phẩm"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/reviews/all")
    public ResponseEntity<?> getAllReviews() {
        try {
            return ResponseEntity.ok(adminService.getAllReviews());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/reviews/read/{productId}")
    public ResponseEntity<?> markReviewsAsRead(@PathVariable Long productId) {
        try {
            adminService.markReviewsAsRead(productId);
            return ResponseEntity.ok(Map.of("message", "Đã đánh dấu đọc"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/reviews/{id}/approve")
    public ResponseEntity<?> approveReview(@PathVariable Long id) {
        try {
            adminService.approveReview(id);
            return ResponseEntity.ok(Map.of("message", "Đã duyệt đánh giá"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/reviews/{id}/hide")
    public ResponseEntity<?> hideReview(@PathVariable Long id) {
        try {
            adminService.hideReview(id);
            return ResponseEntity.ok(Map.of("message", "Đã ẩn đánh giá"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/reviews/reply")
    public ResponseEntity<?> replyReview(@RequestBody Map<String, Object> body) {
        try {
            Long reviewId = Long.valueOf(body.get("reviewId").toString());
            String content = (String) body.get("content");
            return ResponseEntity.ok(adminService.replyReview(reviewId, content));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/revenue/by-category")
    public ResponseEntity<?> getRevenueByCategory() {
        try {
            return ResponseEntity.ok(adminService.getRevenueByCategory());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/comments/all")
    public ResponseEntity<?> getAllComments() {
        try {
            return ResponseEntity.ok(adminService.getAllComments());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/comments/read/{productId}")
    public ResponseEntity<?> markCommentsAsRead(@PathVariable Long productId) {
        try {
            adminService.markCommentsAsRead(productId);
            return ResponseEntity.ok(Map.of("message", "Đã đánh dấu đọc"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/comments/reply")
    public ResponseEntity<?> replyComment(@RequestBody Map<String, Object> body) {
        try {
            Long productId = Long.valueOf(body.get("productId").toString());
            Long parentId = Long.valueOf(body.get("parentId").toString());
            Long userId = Long.valueOf(body.get("userId").toString());
            String content = (String) body.get("content");
            Comment reply = adminService.replyComment(productId, parentId, userId, content);
            return ResponseEntity.ok(reply);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/comments/{id}/approve")
    public ResponseEntity<?> approveComment(@PathVariable Long id) {
        try {
            adminService.approveComment(id);
            return ResponseEntity.ok(Map.of("message", "Đã duyệt bình luận"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/comments/{id}/hide")
    public ResponseEntity<?> hideComment(@PathVariable Long id) {
        try {
            adminService.hideComment(id);
            return ResponseEntity.ok(Map.of("message", "Đã ẩn bình luận"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/best-selling-types")
    public ResponseEntity<?> getBestSellingTypes() {
        try {
            return ResponseEntity.ok(adminService.getBestSellingProductTypes());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/customer-stats")
    public ResponseEntity<?> getCustomerStats() {
        try {
            return ResponseEntity.ok(adminService.getCustomerStats());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/returning-customer-stats")
    public ResponseEntity<?> getReturningCustomerStats() {
        try {
            return ResponseEntity.ok(adminService.getReturningCustomerStats());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/coupons")
    public ResponseEntity<?> getAllCoupons() {
        try {
            return ResponseEntity.ok(adminService.getAllCoupons());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/coupons")
    public ResponseEntity<?> createCoupon(@RequestBody Map<String, Object> body) {
        try {
            String code = (String) body.get("code");
            String discountType = (String) body.get("discountType");
            int discountValue = Integer.parseInt(body.get("discountValue").toString());
            Integer minOrderValue = body.get("minOrderValue") != null ? Integer.valueOf(body.get("minOrderValue").toString()) : null;
            Integer maxUses = body.get("maxUses") != null ? Integer.valueOf(body.get("maxUses").toString()) : null;
            LocalDateTime expiryDate = body.get("expiryDate") != null ? LocalDateTime.parse(body.get("expiryDate").toString()) : null;
            Coupon created = adminService.createCoupon(code, discountType, discountValue, minOrderValue, maxUses, expiryDate);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/coupons/{id}")
    public ResponseEntity<?> updateCoupon(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            String code = (String) body.get("code");
            String discountType = (String) body.get("discountType");
            Integer discountValue = body.get("discountValue") != null ? Integer.valueOf(body.get("discountValue").toString()) : null;
            Integer minOrderValue = body.get("minOrderValue") != null ? Integer.valueOf(body.get("minOrderValue").toString()) : null;
            Integer maxUses = body.get("maxUses") != null ? Integer.valueOf(body.get("maxUses").toString()) : null;
            LocalDateTime expiryDate = body.get("expiryDate") != null ? LocalDateTime.parse(body.get("expiryDate").toString()) : null;
            Boolean active = body.get("active") != null ? Boolean.valueOf(body.get("active").toString()) : null;
            Coupon updated = adminService.updateCoupon(id, code, discountType, discountValue, minOrderValue, maxUses, expiryDate, active);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/coupons/{id}")
    public ResponseEntity<?> deleteCoupon(@PathVariable Long id) {
        try {
            adminService.deleteCoupon(id);
            return ResponseEntity.ok(Map.of("message", "Đã xoá mã giảm giá"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/product-keys")
    public ResponseEntity<?> getAllProductKeys() {
        try {
            return ResponseEntity.ok(adminService.getAllProductKeys());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/product-keys/stats")
    public ResponseEntity<?> getKeyStats() {
        try {
            return ResponseEntity.ok(adminService.getKeyStats());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/product-keys/product/{productId}")
    public ResponseEntity<?> getKeysByProduct(@PathVariable Long productId) {
        try {
            return ResponseEntity.ok(adminService.getKeysByProduct(productId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/product-keys")
    public ResponseEntity<?> addProductKey(@RequestBody Map<String, Object> body) {
        try {
            Long productId = Long.valueOf(body.get("productId").toString());
            String keyCode = (String) body.get("keyCode");
            ProductKey created = adminService.addProductKey(productId, keyCode);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/product-keys/bulk")
    public ResponseEntity<?> addProductKeysBulk(@RequestBody Map<String, Object> body) {
        try {
            Long productId = Long.valueOf(body.get("productId").toString());
            @SuppressWarnings("unchecked")
            List<String> keyCodes = (List<String>) body.get("keyCodes");
            if (keyCodes == null || keyCodes.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Danh sách key không được để trống"));
            }
            adminService.addProductKeysBulk(productId, keyCodes);
            return ResponseEntity.ok(Map.of("message", "Đã thêm " + keyCodes.size() + " key thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/product-keys/{id}")
    public ResponseEntity<?> deleteProductKey(@PathVariable Long id) {
        try {
            adminService.deleteProductKey(id);
            return ResponseEntity.ok(Map.of("message", "Đã xoá key"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/refunds")
    public ResponseEntity<?> getAllRefundRequests() {
        try {
            return ResponseEntity.ok(adminService.getAllRefundRequests());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/refunds/stats")
    public ResponseEntity<?> getRefundStats() {
        try {
            return ResponseEntity.ok(adminService.getRefundStats());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/refunds/{id}/process")
    public ResponseEntity<?> processRefund(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            String status = (String) body.get("status");
            String adminNote = (String) body.get("adminNote");
            Long adminId = body.get("adminId") != null ? Long.valueOf(body.get("adminId").toString()) : null;
            if (adminId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "adminId is required"));
            }
            RefundRequest updated = adminService.processRefund(id, status, adminNote, adminId);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/export/orders")
    public ResponseEntity<?> exportOrders() {
        try {
            String csv = adminService.exportOrdersToCsv();
            return ResponseEntity.ok()
                    .header("Content-Type", "text/csv; charset=UTF-8")
                    .header("Content-Disposition", "attachment; filename=orders.csv")
                    .body(csv);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/export/users")
    public ResponseEntity<?> exportUsers() {
        try {
            String csv = adminService.exportUsersToCsv();
            return ResponseEntity.ok()
                    .header("Content-Type", "text/csv; charset=UTF-8")
                    .header("Content-Disposition", "attachment; filename=users.csv")
                    .body(csv);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/export/products")
    public ResponseEntity<?> exportProducts() {
        try {
            String csv = adminService.exportProductsToCsv();
            return ResponseEntity.ok()
                    .header("Content-Type", "text/csv; charset=UTF-8")
                    .header("Content-Disposition", "attachment; filename=products.csv")
                    .body(csv);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/seller-earnings/summary")
    public ResponseEntity<?> getEarningSummary() {
        try {
            return ResponseEntity.ok(sellerBalanceService.getEarningSummary());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/seller-earnings/breakdown")
    public ResponseEntity<?> getEarningBreakdown() {
        try {
            return ResponseEntity.ok(sellerBalanceService.getEarningBreakdown());
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

    // ===== WITHDRAW REQUESTS =====

    @GetMapping("/sellers-with-balance")
    public ResponseEntity<?> getSellersWithBalance() {
        try {
            List<User> sellers = adminService.getAllSellersRaw();
            List<Map<String, Object>> result = new ArrayList<>();
            for (User s : sellers) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("id", s.getId());
                entry.put("fullName", s.getFullName());
                entry.put("email", s.getEmail());
                entry.put("phoneNumber", s.getPhoneNumber());
                entry.put("sellerVerified", s.isSellerVerified());
                SellerBalance b = sellerBalanceService.getSellerBalanceEntity(s.getId());
                if (b != null) {
                    entry.put("pendingAmount", b.getPendingAmount());
                    entry.put("availableAmount", b.getAvailableAmount());
                    entry.put("totalEarned", b.getTotalEarned());
                } else {
                    entry.put("pendingAmount", 0L);
                    entry.put("availableAmount", 0L);
                    entry.put("totalEarned", 0L);
                }
                result.add(entry);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/pay-commission")
    public ResponseEntity<?> payCommission(@RequestBody Map<String, Object> body) {
        try {
            Long sellerId = Long.valueOf(body.get("sellerId").toString());
            long amount = Long.parseLong(body.get("amount").toString());
            Long adminId = Long.valueOf(body.get("adminId").toString());
            String note = (String) body.get("note");

            User seller = userRep.findById(sellerId)
                    .orElseThrow(() -> new RuntimeException("Seller not found"));
            User admin = userRep.findById(adminId)
                    .orElseThrow(() -> new RuntimeException("Admin not found"));
            SellerBalance balance = sellerBalanceService.getSellerBalanceEntity(sellerId);
            if (balance == null || balance.getAvailableAmount() < amount) {
                return ResponseEntity.badRequest().body(Map.of("error", "Số dư khả dụng không đủ"));
            }

            balance.setAvailableAmount(balance.getAvailableAmount() - amount);
            balance.setUpdatedAt(LocalDateTime.now());
            sellerBalanceService.saveBalance(balance);

            WithdrawRequest req = new WithdrawRequest();
            req.setSeller(seller);
            req.setAmount(amount);
            req.setNote(note);
            req.setStatus("APPROVED");
            req.setCreatedAt(LocalDateTime.now());
            req.setProcessedAt(LocalDateTime.now());
            req.setProcessedBy(admin);
            withdrawRep.save(req);

            return ResponseEntity.ok(Map.of("message", "Đã trả hoa hồng cho " + seller.getFullName()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/withdraw-requests")
    public ResponseEntity<?> getWithdrawRequests(@RequestParam(defaultValue = "PENDING") String status) {
        try {
            return ResponseEntity.ok(withdrawRep.findByStatusOrderByCreatedAtDesc(status));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/withdraw-requests/{id}/approve")
    public ResponseEntity<?> approveWithdraw(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        try {
            Long adminId = body.get("adminId");
            if (adminId == null) return ResponseEntity.badRequest().body(Map.of("error", "adminId is required"));

            User admin = userRep.findById(adminId)
                    .orElseThrow(() -> new RuntimeException("Admin not found"));
            WithdrawRequest req = withdrawRep.findById(id)
                    .orElseThrow(() -> new RuntimeException("Withdraw request not found"));
            if (!"PENDING".equals(req.getStatus())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Request already processed"));
            }

            req.setStatus("APPROVED");
            req.setProcessedAt(LocalDateTime.now());
            req.setProcessedBy(admin);
            withdrawRep.save(req);

            return ResponseEntity.ok(Map.of("message", "Đã duyệt yêu cầu rút tiền"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/withdraw-requests/{id}/reject")
    public ResponseEntity<?> rejectWithdraw(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            Long adminId = Long.valueOf(body.get("adminId").toString());
            String adminNote = (String) body.get("adminNote");

            User admin = userRep.findById(adminId)
                    .orElseThrow(() -> new RuntimeException("Admin not found"));
            WithdrawRequest req = withdrawRep.findById(id)
                    .orElseThrow(() -> new RuntimeException("Withdraw request not found"));
            if (!"PENDING".equals(req.getStatus())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Request already processed"));
            }

            req.setStatus("REJECTED");
            req.setAdminNote(adminNote);
            req.setProcessedAt(LocalDateTime.now());
            req.setProcessedBy(admin);
            withdrawRep.save(req);

            SellerBalance balance = sellerBalanceService.getSellerBalanceEntity(req.getSeller().getId());
            if (balance != null) {
                balance.setAvailableAmount(balance.getAvailableAmount() + req.getAmount());
                balance.setUpdatedAt(LocalDateTime.now());
                sellerBalanceService.saveBalance(balance);
            }

            return ResponseEntity.ok(Map.of("message", "Đã từ chối yêu cầu rút tiền"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}