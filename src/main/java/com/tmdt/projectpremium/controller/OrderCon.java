package com.tmdt.projectpremium.controller;

import com.tmdt.projectpremium.dto.request.AddOrderReq;
import com.tmdt.projectpremium.service.OrderSer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class OrderCon {
    private final OrderSer ser;

    @PostMapping("/add")
    public boolean addOrder(@RequestBody AddOrderReq req) {
        return ser.saveOrder(req);
    }
}
