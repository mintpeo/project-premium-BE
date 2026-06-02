package com.tmdt.projectpremium.service;

import com.tmdt.projectpremium.dto.response.ProductInfoRes;
import com.tmdt.projectpremium.entity.Product;
import com.tmdt.projectpremium.repository.ProductRep;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductSer {
    private final ProductRep rep;

    private ProductInfoRes toProductInfoRes(Product product) {
        List<String> categories = product.getProductCates().stream()
                .map(cate -> cate.getCategory().getName() != null ? cate.getCategory().getName() : "")
                .collect(Collectors.toList());
        List<String> duration = product.getProductDuras().stream()
                .map(dura -> dura.getDuration().getName() != null ? dura.getDuration().getName() : "")
                .collect(Collectors.toList());
        List<String> typesUser = product.getProductTypes().stream()
                .map(types -> types.getTypeUser().getName() != null ? types.getTypeUser().getName() : "")
                .collect(Collectors.toList());
        return new ProductInfoRes(
                product.getId(),
                product.getImg(),
                product.getRating(),
                product.getSold(),
                product.getName(),
                product.getPriceOri(),
                product.getPrice(),
                categories,
                duration,
                typesUser
        );
    }

    public List<ProductInfoRes> getAllProduct() {
        return rep.findAll().stream().map(this::toProductInfoRes).collect(Collectors.toList());
    }

    public ProductInfoRes getProductById(long productId) {
        return rep.findById(productId)
                .map(this::toProductInfoRes)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + productId));
    }

    public List<ProductInfoRes> getProductsByCategoryId(long categoryId) {
        return rep.findByProductCates_Category_Id(categoryId)
                .stream().map(this::toProductInfoRes).collect(Collectors.toList());
    }

    public List<ProductInfoRes> searchProducts(String keyword, Long categoryId,
                                                Integer minPrice, Integer maxPrice,
                                                String sortBy, String sortDir) {
        List<Product> products = rep.searchProducts(keyword, categoryId, minPrice, maxPrice);

        List<ProductInfoRes> result = products.stream().map(this::toProductInfoRes).collect(Collectors.toList());

        if (sortBy != null) {
            Comparator<ProductInfoRes> comparator = switch (sortBy) {
                case "price" -> Comparator.comparingInt(ProductInfoRes::getPrice);
                case "rating" -> Comparator.comparingDouble(ProductInfoRes::getRating);
                case "sold" -> Comparator.comparingInt(p -> p.getSold() != null ? p.getSold() : 0);
                default -> Comparator.comparingLong(ProductInfoRes::getId);
            };
            if ("desc".equalsIgnoreCase(sortDir)) {
                comparator = comparator.reversed();
            }
            result.sort(comparator);
        }
        return result;
    }
}
