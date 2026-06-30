package com.tmdt.projectpremium.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tmdt.projectpremium.entity.Order;
import com.tmdt.projectpremium.entity.OrderItem;
import com.tmdt.projectpremium.repository.OrderItemRep;
import com.tmdt.projectpremium.repository.OrderRep;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.tmdt.projectpremium.service.ProductKeyService;
import vn.payos.PayOS;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PaymentCon {
    private final PayOS payOS;
    private final OrderRep orderRep;
    private final OrderItemRep orderItemRep;
    private final ProductKeyService productKeyService;

    @PostMapping("/webhook")
    public ResponseEntity<ObjectNode> payOSWebhookHandler(@RequestBody ObjectNode body) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();

        try {
            JsonNode dataNode = body.get("data");
            if (dataNode != null) {
                String code = body.has("code") ? body.get("code").asText() : "";
                if ("00".equals(code) || "0000".equals(code)) {
                    long orderId = dataNode.get("orderCode").asLong();
                    Optional<Order> orderOpt = orderRep.findById(orderId);
                    
                    if (orderOpt.isPresent()) {
                        Order order = orderOpt.get();
                        order.setPaymentStatus("PAID");
                        order.setOrderStatus("PROCESSING");

                        productKeyService.assignKeysToOrder(order);
                        orderRep.save(order);
                    }
                }
            }

            response.put("error", 0);
            response.put("message", "Ok");
            response.set("data", null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", e.getMessage());
            response.set("data", null);
            return ResponseEntity.badRequest().body(response);
        }
    }
}
