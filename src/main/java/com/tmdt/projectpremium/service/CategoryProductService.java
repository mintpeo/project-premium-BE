package com.tmdt.projectpremium.service;

import com.tmdt.projectpremium.dto.response.CategoryProductRes;
import com.tmdt.projectpremium.repository.category.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryProductService {
    private final NetflixProductRep netflixRep;
    private final AdobeProductRep adobeRep;
    private final GoogleProductRep googleRep;
    private final MicrosoftProductRep microsoftRep;
    private final SpotifyProductRep spotifyRep;
    private final CanvaProductRep canvaRep;
    private final AiProductRep aiRep;
    private final BaoMatProductRep baoMatRep;
    private final GamesProductRep gamesRep;

    private CategoryProductRes toRes(Object p) {
        if (p instanceof com.tmdt.projectpremium.entity.category.NetflixProduct e)
            return new CategoryProductRes(e.getId(), e.getImg(), e.getRating(), e.getSold(), e.getName(), e.getPriceOri(), e.getPrice());
        if (p instanceof com.tmdt.projectpremium.entity.category.AdobeProduct e)
            return new CategoryProductRes(e.getId(), e.getImg(), e.getRating(), e.getSold(), e.getName(), e.getPriceOri(), e.getPrice());
        if (p instanceof com.tmdt.projectpremium.entity.category.GoogleProduct e)
            return new CategoryProductRes(e.getId(), e.getImg(), e.getRating(), e.getSold(), e.getName(), e.getPriceOri(), e.getPrice());
        if (p instanceof com.tmdt.projectpremium.entity.category.MicrosoftProduct e)
            return new CategoryProductRes(e.getId(), e.getImg(), e.getRating(), e.getSold(), e.getName(), e.getPriceOri(), e.getPrice());
        if (p instanceof com.tmdt.projectpremium.entity.category.SpotifyProduct e)
            return new CategoryProductRes(e.getId(), e.getImg(), e.getRating(), e.getSold(), e.getName(), e.getPriceOri(), e.getPrice());
        if (p instanceof com.tmdt.projectpremium.entity.category.CanvaProduct e)
            return new CategoryProductRes(e.getId(), e.getImg(), e.getRating(), e.getSold(), e.getName(), e.getPriceOri(), e.getPrice());
        if (p instanceof com.tmdt.projectpremium.entity.category.AiProduct e)
            return new CategoryProductRes(e.getId(), e.getImg(), e.getRating(), e.getSold(), e.getName(), e.getPriceOri(), e.getPrice());
        if (p instanceof com.tmdt.projectpremium.entity.category.BaoMatProduct e)
            return new CategoryProductRes(e.getId(), e.getImg(), e.getRating(), e.getSold(), e.getName(), e.getPriceOri(), e.getPrice());
        if (p instanceof com.tmdt.projectpremium.entity.category.GamesProduct e)
            return new CategoryProductRes(e.getId(), e.getImg(), e.getRating(), e.getSold(), e.getName(), e.getPriceOri(), e.getPrice());
        return null;
    }

    public List<CategoryProductRes> getByCategory(String category) {
        return switch (category.toLowerCase()) {
            case "netflix" -> netflixRep.findAll().stream().map(this::toRes).collect(Collectors.toList());
            case "adobe" -> adobeRep.findAll().stream().map(this::toRes).collect(Collectors.toList());
            case "google" -> googleRep.findAll().stream().map(this::toRes).collect(Collectors.toList());
            case "microsoft" -> microsoftRep.findAll().stream().map(this::toRes).collect(Collectors.toList());
            case "spotify" -> spotifyRep.findAll().stream().map(this::toRes).collect(Collectors.toList());
            case "canva" -> canvaRep.findAll().stream().map(this::toRes).collect(Collectors.toList());
            case "ai" -> aiRep.findAll().stream().map(this::toRes).collect(Collectors.toList());
            case "bao-mat" -> baoMatRep.findAll().stream().map(this::toRes).collect(Collectors.toList());
            case "games" -> gamesRep.findAll().stream().map(this::toRes).collect(Collectors.toList());
            default -> List.of();
        };
    }

    public CategoryProductRes getById(String category, Long id) {
        Object entity = switch (category.toLowerCase()) {
            case "netflix" -> netflixRep.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
            case "adobe" -> adobeRep.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
            case "google" -> googleRep.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
            case "microsoft" -> microsoftRep.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
            case "spotify" -> spotifyRep.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
            case "canva" -> canvaRep.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
            case "ai" -> aiRep.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
            case "bao-mat" -> baoMatRep.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
            case "games" -> gamesRep.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
            default -> throw new RuntimeException("Category không hợp lệ");
        };
        return toRes(entity);
    }
}
