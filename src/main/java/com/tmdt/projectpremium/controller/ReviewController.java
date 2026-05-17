package com.tmdt.projectpremium.controller;

import com.tmdt.projectpremium.dto.request.ReviewRequest;
import com.tmdt.projectpremium.dto.response.ReviewResponse;
import com.tmdt.projectpremium.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/review")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/{productId}")
    public List<ReviewResponse> getByProductId(@PathVariable Long productId) {
        return reviewService.getByProductId(productId);
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> create(@Valid @RequestBody ReviewRequest req) {
        return ResponseEntity.ok(reviewService.create(req));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleError(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
