package com.tmdt.projectpremium.controller;

import com.tmdt.projectpremium.dto.request.CommentRequest;
import com.tmdt.projectpremium.dto.response.CommentResponse;
import com.tmdt.projectpremium.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@RequiredArgsConstructor
public class CommentController {
    private final CommentService ser;

    @GetMapping("/{productId}")
    public ResponseEntity<List<CommentResponse>> getByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(ser.getByProductId(productId));
    }

    @PostMapping
    public ResponseEntity<CommentResponse> create(@Valid @RequestBody CommentRequest req) {
        return ResponseEntity.ok(ser.create(req));
    }
}
