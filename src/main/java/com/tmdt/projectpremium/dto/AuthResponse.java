package com.tmdt.projectpremium.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private boolean success;
    private String message;
    private Object data;

    public AuthResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.data = null;
    }
}
