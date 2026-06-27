package com.tmdt.projectpremium.controller;

import com.tmdt.projectpremium.entity.*;
import com.tmdt.projectpremium.repository.*;
import com.tmdt.projectpremium.service.AdminService;
import com.tmdt.projectpremium.service.SellerBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/seller")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class SellerController {

    private final AdminService adminService;
    private final SellerBalanceService sellerBalanceService;
    private final SellerEarningRepository sellerEarningRep;
    private final CouponRepository couponRep;
    private final CommentRepository commentRep;
    private final ReviewRepository reviewRep;
    private final ProductRep productRep;
    private final OrderRep orderRep;
    private final UserRepository userRep;
    private final WithdrawRequestRepository withdrawRep;

    // ===== PRODUCTS =====

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

    // ===== ORDERS =====

    @GetMapping("/orders/{sellerId}")
    public ResponseEntity<List<Order>> getSellerOrders(@PathVariable Long sellerId) {
        return ResponseEntity.ok(adminService.getOrdersBySeller(sellerId));
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

    // ===== BALANCE & TRANSACTIONS =====

    @GetMapping("/balance/{sellerId}")
    public ResponseEntity<Map<String, Object>> getBalance(@PathVariable Long sellerId) {
        return ResponseEntity.ok(sellerBalanceService.getSellerBalance(sellerId));
    }

    @GetMapping("/transactions/{sellerId}")
    public ResponseEntity<List<SellerEarning>> getTransactions(@PathVariable Long sellerId) {
        return ResponseEntity.ok(sellerEarningRep.findBySellerIdOrderByCreatedAtDesc(sellerId));
    }

    // ===== COUPONS =====

    @GetMapping("/coupons/{sellerId}")
    public ResponseEntity<List<Coupon>> getSellerCoupons(@PathVariable Long sellerId) {
        return ResponseEntity.ok(couponRep.findBySellerId(sellerId));
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
            Long sellerId = body.get("sellerId") != null ? Long.valueOf(body.get("sellerId").toString()) : null;

            if (couponRep.existsByCode(code)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Mã giảm giá đã tồn tại"));
            }

            User seller = sellerId != null ? userRep.findById(sellerId).orElse(null) : null;

            Coupon coupon = Coupon.builder()
                    .code(code.toUpperCase())
                    .discountType(discountType)
                    .discountValue(discountValue)
                    .minOrderValue(minOrderValue)
                    .maxUses(maxUses)
                    .expiryDate(expiryDate)
                    .active(true)
                    .seller(seller)
                    .build();
            return ResponseEntity.ok(couponRep.save(coupon));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/coupons/{id}")
    public ResponseEntity<?> updateCoupon(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            Coupon coupon = couponRep.findById(id)
                    .orElseThrow(() -> new RuntimeException("Coupon not found"));
            if (body.containsKey("code")) coupon.setCode(((String) body.get("code")).toUpperCase());
            if (body.containsKey("discountType")) coupon.setDiscountType((String) body.get("discountType"));
            if (body.containsKey("discountValue")) coupon.setDiscountValue(Integer.parseInt(body.get("discountValue").toString()));
            if (body.containsKey("minOrderValue")) coupon.setMinOrderValue(Integer.valueOf(body.get("minOrderValue").toString()));
            if (body.containsKey("maxUses")) coupon.setMaxUses(Integer.valueOf(body.get("maxUses").toString()));
            if (body.containsKey("expiryDate")) coupon.setExpiryDate(LocalDateTime.parse(body.get("expiryDate").toString()));
            if (body.containsKey("active")) coupon.setActive(Boolean.parseBoolean(body.get("active").toString()));
            return ResponseEntity.ok(couponRep.save(coupon));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/coupons/{id}")
    public ResponseEntity<?> deleteCoupon(@PathVariable Long id) {
        try {
            couponRep.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Đã xoá mã giảm giá"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ===== REVIEWS =====

    @GetMapping("/reviews/{sellerId}")
    public ResponseEntity<?> getProductReviews(@PathVariable Long sellerId) {
        try {
            List<Long> productIds = productRep.findBySellerId(sellerId).stream()
                    .map(Product::getId).toList();
            List<Review> reviews = new ArrayList<>();
            for (Long pid : productIds) {
                reviews.addAll(reviewRep.findByProductIdOrderByCreatedAtDesc(pid));
            }
            reviews.sort((a, b) -> {
                if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
                if (a.getCreatedAt() == null) return 1;
                if (b.getCreatedAt() == null) return -1;
                return b.getCreatedAt().compareTo(a.getCreatedAt());
            });
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Unknown error"));
        }
    }

    // ===== COMMENTS (Reply) =====

    @GetMapping("/comments/{sellerId}")
    public ResponseEntity<?> getProductComments(@PathVariable Long sellerId) {
        try {
            List<Long> productIds = productRep.findBySellerId(sellerId).stream()
                    .map(Product::getId).toList();
            List<Comment> comments = new ArrayList<>();
            for (Long pid : productIds) {
                comments.addAll(commentRep.findByProductIdOrderByCreatedAtDesc(pid));
            }
            comments.sort((a, b) -> {
                if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
                if (a.getCreatedAt() == null) return 1;
                if (b.getCreatedAt() == null) return -1;
                return b.getCreatedAt().compareTo(a.getCreatedAt());
            });
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Unknown error"));
        }
    }

    @PostMapping("/comments/reply")
    public ResponseEntity<?> replyComment(@RequestBody Map<String, Object> body) {
        try {
            Long productId = Long.valueOf(body.get("productId").toString());
            Long parentId = Long.valueOf(body.get("parentId").toString());
            Long userId = Long.valueOf(body.get("userId").toString());
            String content = (String) body.get("content");

            User user = userRep.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Comment reply = Comment.builder()
                    .productId(productId)
                    .user(user)
                    .content(content)
                    .parentId(parentId)
                    .approved(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            return ResponseEntity.ok(commentRep.save(reply));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @PutMapping("/comments/read/{productId}")
    public ResponseEntity<?> markCommentsAsRead(@PathVariable Long productId) {
        try {
            List<Comment> comments = commentRep.findByProductIdOrderByCreatedAtDesc(productId);
            List<Comment> toUpdate = new ArrayList<>();
            for (Comment c : comments) {
                if (!c.isRead()) {
                    c.setRead(true);
                    toUpdate.add(c);
                }
            }
            if (!toUpdate.isEmpty()) {
                commentRep.saveAll(toUpdate);
            }
            return ResponseEntity.ok(Map.of("message", "Marked as read"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/reviews/read/{productId}")
    public ResponseEntity<?> markReviewsAsRead(@PathVariable Long productId) {
        try {
            List<Review> reviews = reviewRep.findByProductIdOrderByCreatedAtDesc(productId);
            List<Review> toUpdate = new ArrayList<>();
            for (Review r : reviews) {
                if (!r.isRead()) {
                    r.setRead(true);
                    toUpdate.add(r);
                }
            }
            if (!toUpdate.isEmpty()) {
                reviewRep.saveAll(toUpdate);
            }
            return ResponseEntity.ok(Map.of("message", "Marked as read"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ===== WITHDRAW =====

    @GetMapping("/withdraw/{sellerId}")
    public ResponseEntity<?> getWithdrawRequests(@PathVariable Long sellerId) {
        return ResponseEntity.ok(withdrawRep.findBySellerIdOrderByCreatedAtDesc(sellerId));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> createWithdrawRequest(@RequestBody Map<String, Object> body) {
        try {
            Long sellerId = Long.valueOf(body.get("sellerId").toString());
            long amount = Long.parseLong(body.get("amount").toString());
            String note = (String) body.get("note");

            User seller = userRep.findById(sellerId)
                    .orElseThrow(() -> new RuntimeException("Seller not found"));
            SellerBalance balance = sellerBalanceService.getSellerBalanceEntity(sellerId);
            if (balance.getAvailableAmount() < amount) {
                return ResponseEntity.badRequest().body(Map.of("error", "Số dư khả dụng không đủ"));
            }

            balance.setAvailableAmount(balance.getAvailableAmount() - amount);
            balance.setUpdatedAt(LocalDateTime.now());
            sellerBalanceService.saveBalance(balance);

            WithdrawRequest req = new WithdrawRequest();
            req.setSeller(seller);
            req.setAmount(amount);
            req.setNote(note);
            req.setStatus("PENDING");
            req.setCreatedAt(LocalDateTime.now());
            withdrawRep.save(req);

            return ResponseEntity.ok(Map.of("message", "Yêu cầu rút tiền đã được tạo", "id", req.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ===== REVENUE STATS =====

    @GetMapping("/revenue/{sellerId}")
    public ResponseEntity<?> getSellerRevenue(@PathVariable Long sellerId,
                                               @RequestParam(defaultValue = "30d") String period) {
        try {
            LocalDateTime since;
            switch (period) {
                case "7d": since = LocalDateTime.now().minusDays(7); break;
                case "30d": since = LocalDateTime.now().minusDays(30); break;
                case "90d": since = LocalDateTime.now().minusDays(90); break;
                case "1y": since = LocalDateTime.now().minusYears(1); break;
                default: since = LocalDateTime.of(2000, 1, 1, 0, 0);
            }

            List<SellerEarning> earnings = sellerEarningRep.findBySellerIdAndCreatedAtAfter(sellerId, since);
            Map<String, Long> dailyRevenue = new LinkedHashMap<>();
            java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM");
            java.time.LocalDate start = since.toLocalDate();
            java.time.LocalDate today = java.time.LocalDate.now();
            for (java.time.LocalDate d = start; !d.isAfter(today); d = d.plusDays(1)) {
                dailyRevenue.put(d.format(fmt), 0L);
            }
            for (SellerEarning e : earnings) {
                String key = e.getCreatedAt().format(fmt);
                dailyRevenue.merge(key, e.getAmount(), Long::sum);
            }

            List<Map<String, Object>> result = new ArrayList<>();
            for (Map.Entry<String, Long> entry : dailyRevenue.entrySet()) {
                Map<String, Object> point = new HashMap<>();
                point.put("date", entry.getKey());
                point.put("revenue", entry.getValue());
                result.add(point);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
