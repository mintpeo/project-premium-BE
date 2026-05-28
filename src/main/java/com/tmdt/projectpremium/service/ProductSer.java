package com.tmdt.projectpremium.service;

import com.tmdt.projectpremium.dto.response.ProductInfoRes;
import com.tmdt.projectpremium.entity.Product;
import com.tmdt.projectpremium.repository.ProductRep;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductSer {
    private final ProductRep rep;

    public List<ProductInfoRes> getAllProduct() {
        List<Product> products = rep.findAll();
        List<ProductInfoRes> customList = products.stream().map(product -> {
            List<String> categories = product.getProductCates().stream().map(cate -> cate.getCategory().getName() != null ? cate.getCategory().getName() : "")
                    .collect(Collectors.toList());
            List<String> duration = product.getProductDuras().stream().map(dura -> dura.getDuration().getName() != null ? dura.getDuration().getName(): "")
                    .collect(Collectors.toList());
            List<String> typesUser = product.getProductTypes().stream().map(types -> types.getTypeUser().getName() != null ? types.getTypeUser().getName() : "")
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
        }).collect(Collectors.toList());

        return customList;
    }

    public ProductInfoRes getProductById(long productId) {
        return rep.findById(productId).map(product -> {
            List<String> categories = product.getProductCates().stream().map(cate -> cate.getCategory().getName() != null ? cate.getCategory().getName() : "")
                    .collect(Collectors.toList());
            List<String> duration = product.getProductDuras().stream().map(dura -> dura.getDuration().getName() != null ? dura.getDuration().getName(): "")
                    .collect(Collectors.toList());
            List<String> typesUser = product.getProductTypes().stream().map(types -> types.getTypeUser().getName() != null ? types.getTypeUser().getName() : "")
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
        }).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + productId));
    }

    public List<ProductInfoRes> getProductsByCategoryId(long categoryId) {
        List<Product> products = rep.findByProductCates_Category_Id(categoryId);
        return products.stream().map(product -> {
            List<String> categories = product.getProductCates().stream().map(cate -> cate.getCategory().getName() != null ? cate.getCategory().getName() : "")
                    .collect(Collectors.toList());
            List<String> duration = product.getProductDuras().stream().map(dura -> dura.getDuration().getName() != null ? dura.getDuration().getName(): "")
                    .collect(Collectors.toList());
            List<String> typesUser = product.getProductTypes().stream().map(types -> types.getTypeUser().getName() != null ? types.getTypeUser().getName() : "")
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
        }).collect(Collectors.toList());
    }
}
