package com.tmdt.projectpremium.controller;

import com.tmdt.projectpremium.dto.OrderResponseDTO;
import com.tmdt.projectpremium.dto.request.AddOrderReq;
import com.tmdt.projectpremium.service.OrderSer;
import com.tmdt.projectpremium.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class OrderCon {
    private final OrderSer ser;
    private final UserService userService;

    @PostMapping("/add")
    public boolean addOrder(@RequestBody AddOrderReq req) {
        return ser.saveOrder(req);
    }

    @GetMapping("/history")
    public ResponseEntity<List<OrderResponseDTO>> getOrderHistory(
            @RequestParam Long userId,
            @RequestParam String status
    ) {
        List<OrderResponseDTO> history = ser.getUserOrderHistoryByStatus(userId, status);
        return ResponseEntity.ok(history);
    }
}

