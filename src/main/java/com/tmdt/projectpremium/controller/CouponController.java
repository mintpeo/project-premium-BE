package com.tmdt.projectpremium.controller;

import com.tmdt.projectpremium.entity.Coupon;
import com.tmdt.projectpremium.repository.CouponRepository;
import com.tmdt.projectpremium.repository.OrderRep;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coupons")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class CouponController {

    private final CouponRepository couponRep;
    private final OrderRep orderRep;

    @GetMapping("/available")
    public ResponseEntity<?> getAvailableCoupons(@RequestParam(defaultValue = "0") int totalPrice) {
        try {
            List<Coupon> all = couponRep.findAll();
            List<Map<String, Object>> result = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();
            for (Coupon c : all) {
                if (!c.isActive()) continue;
                if (c.getExpiryDate() != null && c.getExpiryDate().isBefore(now)) continue;
                if (c.getMaxUses() != null && c.getUsedCount() >= c.getMaxUses()) continue;

                int discount;
                if (c.getDiscountType() != null && c.getDiscountType().startsWith("PERCENT")) {
                    discount = totalPrice * c.getDiscountValue() / 100;
                } else {
                    discount = c.getDiscountValue();
                }
                if (discount > totalPrice) discount = totalPrice;

                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("id", c.getId());
                entry.put("code", c.getCode());
                entry.put("discountType", c.getDiscountType());
                entry.put("discountValue", c.getDiscountValue());
                entry.put("discount", discount);
                entry.put("minOrderValue", c.getMinOrderValue());
                entry.put("expiryDate", c.getExpiryDate());
                entry.put("remaining", c.getMaxUses() != null ? c.getMaxUses() - c.getUsedCount() : null);
                entry.put("canUse", c.getMinOrderValue() == null || totalPrice >= c.getMinOrderValue());
                result.add(entry);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/apply")
    public ResponseEntity<?> applyCoupon(@RequestBody Map<String, Object> body) {
        try {
            String code = ((String) body.get("code")).trim().toUpperCase();
            int totalPrice = Integer.parseInt(body.get("totalPrice").toString());
            Long userId = Long.valueOf(body.get("userId").toString());

            Coupon coupon = couponRep.findByCode(code)
                    .orElseThrow(() -> new RuntimeException("Mã giảm giá không tồn tại"));

            if (!coupon.isActive()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Mã giảm giá đã bị vô hiệu hoá"));
            }

            if (coupon.getExpiryDate() != null && coupon.getExpiryDate().isBefore(LocalDateTime.now())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Mã giảm giá đã hết hạn"));
            }

            if (coupon.getMaxUses() != null && coupon.getUsedCount() >= coupon.getMaxUses()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Mã giảm giá đã hết lượt sử dụng"));
            }

            if (coupon.getMinOrderValue() != null && totalPrice < coupon.getMinOrderValue()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Đơn hàng tối thiểu " + String.format("%,d", coupon.getMinOrderValue()) + "đ để sử dụng mã này"
                ));
            }

            int discount;
            if (coupon.getDiscountType() != null && coupon.getDiscountType().startsWith("PERCENT")) {
                discount = totalPrice * coupon.getDiscountValue() / 100;
            } else {
                discount = coupon.getDiscountValue();
            }

            if (discount > totalPrice) {
                discount = totalPrice;
            }

            return ResponseEntity.ok(Map.of(
                "code", coupon.getCode(),
                "discountType", coupon.getDiscountType(),
                "discountValue", coupon.getDiscountValue(),
                "discount", discount,
                "minOrderValue", coupon.getMinOrderValue()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
