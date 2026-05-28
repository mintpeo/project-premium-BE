package com.tmdt.projectpremium.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDTO {
    private long id;
    private String name;
    private String icon;
}