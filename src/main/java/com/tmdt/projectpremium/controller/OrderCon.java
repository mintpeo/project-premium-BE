package com.tmdt.projectpremium.controller;

import com.tmdt.projectpremium.dto.OrderResponseDTO;
import com.tmdt.projectpremium.dto.request.AddOrderReq;
import com.tmdt.projectpremium.service.OrderSer;
import com.tmdt.projectpremium.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class OrderCon {
    private final OrderSer ser;

    @org.springframework.beans.factory.annotation.Value("${payos.client-id}")
    private String clientId;

    @org.springframework.beans.factory.annotation.Value("${payos.api-key}")
    private String apiKey;

    @org.springframework.beans.factory.annotation.Value("${payos.checksum-key}")
    private String checksumKey;

    private long generatePayOSCode() {
        return System.currentTimeMillis() % 10000000000L;
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addOrder(@RequestBody AddOrderReq req) {
        try {
            Order order = ser.saveOrder(req);

            // Rút gọn mô tả để không vượt quá 25 ký tự của PayOS
            String description = "PK" + order.getId();
            // PayOS requires orderCode to be integer <= 9007199254740991
            long orderCode = order.getId();
            int amount = order.getTotalPrice();
            String returnUrl = "http://localhost:5173/payment/success?orderId=" + order.getId();
            String cancelUrl = "http://localhost:5173/payment/cancel?orderId=" + order.getId();

            // Generate Signature
            String dataStr = "amount=" + amount + "&cancelUrl=" + cancelUrl + "&description=" + description + "&orderCode=" + orderCode + "&returnUrl=" + returnUrl;
            javax.crypto.Mac sha256_HMAC = javax.crypto.Mac.getInstance("HmacSHA256");
            javax.crypto.spec.SecretKeySpec secret_key = new javax.crypto.spec.SecretKeySpec(checksumKey.getBytes(java.nio.charset.StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] hash = sha256_HMAC.doFinal(dataStr.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            String signature = hexString.toString();

            // Prepare Request Body
            Map<String, Object> body = new HashMap<>();
            body.put("orderCode", orderCode);
            body.put("amount", amount);
            body.put("description", description);
            body.put("returnUrl", returnUrl);
            body.put("cancelUrl", cancelUrl);
            body.put("signature", signature);

            List<Map<String, Object>> items = new java.util.ArrayList<>();
            Map<String, Object> item = new HashMap<>();
            item.put("name", "Thanh toan PK-" + orderCode);
            item.put("quantity", 1);
            item.put("price", amount);
            items.add(item);
            body.put("items", items);

            // Call API
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            headers.set("x-client-id", clientId);
            headers.set("x-api-key", apiKey);

            org.springframework.http.HttpEntity<Map<String, Object>> entity = new org.springframework.http.HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity("https://api-merchant.payos.vn/v2/payment-requests", entity, Map.class);
            
            Map<String, Object> responseBody = response.getBody();
            Map<String, String> res = new HashMap<>();
            if (responseBody != null && responseBody.get("data") != null) {
                Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                res.put("checkoutUrl", (String) data.get("checkoutUrl"));
            } else {
                throw new RuntimeException("PayOS API failed: " + responseBody);
            }
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    @PostMapping("/repay/{orderId}")
    public ResponseEntity<Map<String, String>> repayOrder(@PathVariable Long orderId) {
        org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        headers.set("x-client-id", clientId);
        headers.set("x-api-key", apiKey);

        try {
            Order order = ser.getOrderById(orderId);

            if (!"PENDING".equals(order.getOrderStatus())) {
                throw new RuntimeException("Đơn hàng này không ở trạng thái chờ thanh toán!");
            }

            String description = "PK" + order.getId();
            long orderCode = generatePayOSCode();
            int amount = order.getTotalPrice();
            String returnUrl = "http://localhost:5173/payment/success?orderId=" + order.getId();
            String cancelUrl = "http://localhost:5173/payment/cancel?orderId=" + order.getId();

            String dataStr = "amount=" + amount + "&cancelUrl=" + cancelUrl + "&description=" + description + "&orderCode=" + orderCode + "&returnUrl=" + returnUrl;
            javax.crypto.Mac sha256_HMAC = javax.crypto.Mac.getInstance("HmacSHA256");
            javax.crypto.spec.SecretKeySpec secret_key = new javax.crypto.spec.SecretKeySpec(checksumKey.getBytes(java.nio.charset.StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] hash = sha256_HMAC.doFinal(dataStr.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            String signature = hexString.toString();

            Map<String, Object> body = new HashMap<>();
            body.put("orderCode", orderCode);
            body.put("amount", amount);
            body.put("description", description);
            body.put("returnUrl", returnUrl);
            body.put("cancelUrl", cancelUrl);
            body.put("signature", signature);

            List<Map<String, Object>> items = new java.util.ArrayList<>();
            Map<String, Object> item = new HashMap<>();
            item.put("name", "Thanh toan PK-" + order.getId());
            item.put("quantity", 1);
            item.put("price", amount);
            items.add(item);
            body.put("items", items);

            org.springframework.http.HttpEntity<Map<String, Object>> entity = new org.springframework.http.HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity("https://api-merchant.payos.vn/v2/payment-requests", entity, Map.class);
            Map<String, Object> responseBody = response.getBody();

            Map<String, String> res = new HashMap<>();
            if (responseBody != null && responseBody.get("data") != null) {
                Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                res.put("checkoutUrl", (String) data.get("checkoutUrl"));
                return ResponseEntity.ok(res);
            }

            throw new RuntimeException("PayOS API failed: " + responseBody);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<OrderResponseDTO>> getOrderHistory(
            @RequestParam Long userId,
            @RequestParam String status
    ) {
        List<OrderResponseDTO> history = ser.getUserOrderHistoryByStatus(userId, status);
        return ResponseEntity.ok(history);
    }

    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId) {
        try {
            boolean success = ser.cancelOrder(orderId);
            if (success) {
                return ResponseEntity.ok(Map.of("message", "Đã huỷ đơn hàng thành công"));
            }
            return ResponseEntity.badRequest().body(Map.of("error", "Không thể huỷ đơn hàng này"));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/confirm/{orderId}")
    public ResponseEntity<?> confirmOrder(@PathVariable Long orderId) {
        try {
            boolean success = ser.confirmOrder(orderId);
            if (success) {
                return ResponseEntity.ok(Map.of("message", "Xác nhận nhận key thành công"));
            }
            return ResponseEntity.badRequest().body(Map.of("error", "Không thể xác nhận đơn hàng này hoặc đơn hàng chưa ở trạng thái Đang xử lý"));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/payment-success/{orderId}")
    public ResponseEntity<?> updatePaymentSuccess(@PathVariable Long orderId) {
        try {
            ser.handlePaymentSuccess(orderId);
            return ResponseEntity.ok(Map.of("message", "Đã cập nhật trạng thái đơn hàng thành công"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}

