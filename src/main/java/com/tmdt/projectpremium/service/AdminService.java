package com.tmdt.projectpremium.service;

import com.tmdt.projectpremium.entity.Category;
import com.tmdt.projectpremium.entity.Order;
import com.tmdt.projectpremium.entity.Product;
import com.tmdt.projectpremium.entity.ProductCate;
import com.tmdt.projectpremium.entity.User;
import com.tmdt.projectpremium.entity.OrderItem;
import com.tmdt.projectpremium.repository.CategoryRepository;
import com.tmdt.projectpremium.repository.OrderItemRep;
import com.tmdt.projectpremium.repository.OrderRep;
import com.tmdt.projectpremium.repository.ProductCateRepository;
import com.tmdt.projectpremium.repository.ProductRep;
import com.tmdt.projectpremium.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRep;
    private final ProductRep productRep;
    private final OrderRep orderRep;
    private final CategoryRepository categoryRep;
    private final ProductCateRepository productCateRep;
    private final OrderItemRep orderItemRep;

    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRep.count());
        stats.put("totalProducts", productRep.count());
        stats.put("totalOrders", orderRep.count());

        long totalRevenue = orderRep.findAll().stream()
                .filter(o -> "SUCCESS".equals(o.getOrderStatus()))
                .mapToLong(Order::getTotalPrice)
                .sum();
        stats.put("totalRevenue", totalRevenue);

        Map<String, Long> orderStatusCounts = new HashMap<>();
        orderStatusCounts.put("PENDING", orderRep.countByOrderStatus("PENDING"));
        orderStatusCounts.put("PROCESSING", orderRep.countByOrderStatus("PROCESSING"));
        orderStatusCounts.put("SUCCESS", orderRep.countByOrderStatus("SUCCESS"));
        orderStatusCounts.put("CANCELLED", orderRep.countByOrderStatus("CANCELLED"));
        stats.put("orderStatusCounts", orderStatusCounts);

        List<Product> topProducts = productRep.findAll();
        stats.put("topProducts", topProducts);

        return stats;
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRep.findAll();
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRep.findAllByOrderByOrderDateDesc();
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersBySeller(Long sellerId) {
        List<OrderItem> items = orderItemRep.findByProduct_Seller_Id(sellerId);
        return items.stream()
                .map(OrderItem::getOrder)
                .distinct()
                .sorted((a, b) -> b.getOrderDate().compareTo(a.getOrderDate()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getRevenueByPeriod(String period) {
        LocalDateTime since;
        switch (period) {
            case "7d": since = LocalDateTime.now().minusDays(7); break;
            case "30d": since = LocalDateTime.now().minusDays(30); break;
            case "90d": since = LocalDateTime.now().minusDays(90); break;
            case "1y": since = LocalDateTime.now().minusYears(1); break;
            default: since = LocalDateTime.of(2000, 1, 1, 0, 0);
        }

        List<Order> orders = orderRep.findSuccessOrdersSince(since);

        Map<String, Long> dailyRevenue = new LinkedHashMap<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");

        LocalDate start = since.toLocalDate();
        LocalDate today = LocalDate.now();
        for (LocalDate d = start; !d.isAfter(today); d = d.plusDays(1)) {
            dailyRevenue.put(d.format(fmt), 0L);
        }

        for (Order o : orders) {
            String key = o.getOrderDate().format(fmt);
            dailyRevenue.merge(key, (long) o.getTotalPrice(), Long::sum);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : dailyRevenue.entrySet()) {
            Map<String, Object> point = new HashMap<>();
            point.put("date", entry.getKey());
            point.put("revenue", entry.getValue());
            result.add(point);
        }
        return result;
    }

    @Transactional
    public User updateUserRole(Long userId, String newRole) {
        User user = userRep.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setRole(User.Role.valueOf(newRole));
        return userRep.save(user);
    }

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRep.findAll();
    }

    @Transactional
    public Category createCategory(String name, String icon) {
        Category category = new Category();
        category.setName(name);
        category.setIcon(icon);
        category.setActive(true);
        return categoryRep.save(category);
    }

    @Transactional
    public Category updateCategory(Long id, String name, String icon) {
        Category category = categoryRep.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        if (name != null) category.setName(name);
        if (icon != null) category.setIcon(icon);
        return categoryRep.save(category);
    }

    @Transactional
    public void toggleCategoryStatus(Long id) {
        Category category = categoryRep.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        category.setActive(!category.isActive());
        categoryRep.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRep.existsById(id)) {
            throw new RuntimeException("Category not found with id: " + id);
        }
        categoryRep.deleteById(id);
    }

    @Transactional
    public Product createProduct(String name, String img, Integer price, Integer priceOri, Long sellerId, List<Long> categoryIds) {
        Product product = new Product();
        product.setName(name);
        product.setImg(img);
        product.setPrice(price);
        product.setPriceOri(priceOri);
        product.setRating(0.0);
        product.setSold(0);
        if (sellerId != null) {
            User seller = userRep.findById(sellerId)
                    .orElseThrow(() -> new RuntimeException("Seller not found with id: " + sellerId));
            product.setSeller(seller);
        }
        Product saved = productRep.save(product);
        assignCategories(saved, categoryIds);
        return saved;
    }

    @Transactional
    public Product updateProduct(Long id, String name, String img, Integer price, Integer priceOri, List<Long> categoryIds) {
        Product product = productRep.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        if (name != null) product.setName(name);
        if (img != null) product.setImg(img);
        if (price != null) product.setPrice(price);
        if (priceOri != null) product.setPrice(priceOri);
        Product saved = productRep.save(product);
        if (categoryIds != null) {
            assignCategories(saved, categoryIds);
        }
        return saved;
    }

    private void assignCategories(Product product, List<Long> categoryIds) {
        if (categoryIds == null) return;
        productCateRep.deleteByProductId(product.getId());
        for (Long catId : categoryIds) {
            Category category = categoryRep.findById(catId).orElse(null);
            if (category != null) {
                ProductCate pc = new ProductCate();
                pc.setProduct(product);
                pc.setCategory(category);
                productCateRep.save(pc);
            }
        }
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsBySeller(Long sellerId) {
        return productRep.findBySellerId(sellerId);
    }

    @Transactional(readOnly = true)
    public List<Product> getPlatformProducts() {
        return productRep.findAll().stream()
                .filter(p -> p.getSeller() == null)
                .toList();
    }

    @Transactional
    public Product updateProduct(Long id, String name, String img, Integer price, Integer priceOri) {
        Product product = productRep.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        if (name != null) product.setName(name);
        if (img != null) product.setImg(img);
        if (price != null) product.setPrice(price);
        if (priceOri != null) product.setPriceOri(priceOri);
        return productRep.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRep.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllSellers() {
        List<User> sellers = userRep.findByRole(User.Role.SELLER);
        List<Map<String, Object>> result = new ArrayList<>();
        for (User seller : sellers) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("id", seller.getId());
            entry.put("email", seller.getEmail());
            entry.put("fullName", seller.getFullName());
            entry.put("phoneNumber", seller.getPhoneNumber());
            entry.put("sellerVerified", seller.isSellerVerified());
            entry.put("createdAt", seller.getCreatedAt() != null ? seller.getCreatedAt().toString() : "");
            entry.put("productCount", productRep.countBySellerId(seller.getId()));
            result.add(entry);
        }
        return result;
    }

    @Transactional
    public void verifySeller(Long id) {
        User seller = userRep.findById(id)
                .orElseThrow(() -> new RuntimeException("Seller not found with id: " + id));
        if (seller.getRole() != User.Role.SELLER) {
            throw new RuntimeException("User is not a seller");
        }
        seller.setSellerVerified(true);
        userRep.save(seller);
    }

    @Transactional
    public void banSeller(Long id) {
        User seller = userRep.findById(id)
                .orElseThrow(() -> new RuntimeException("Seller not found with id: " + id));
        seller.setSellerVerified(false);
        userRep.save(seller);
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRep.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}