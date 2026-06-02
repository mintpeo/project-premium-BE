package com.tmdt.projectpremium.service;

import com.tmdt.projectpremium.entity.*;
import com.tmdt.projectpremium.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final ReviewRepository reviewRep;
    private final CouponRepository couponRep;
    private final ProductKeyRepository productKeyRep;
    private final RefundRequestRepository refundRep;
    private final CommentRepository commentRep;
    private final PasswordEncoder passwordEncoder;

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

        List<Product> topProducts = productRep.findAll().stream()
                .sorted((a, b) -> Integer.compare(
                    b.getSold() != null ? b.getSold() : 0,
                    a.getSold() != null ? a.getSold() : 0
                ))
                .limit(10)
                .toList();
        stats.put("topProducts", topProducts);

        Map<String, Object> categoryRevenue = new HashMap<>();
        List<Order> successOrders = orderRep.findSuccessOrdersSince(LocalDateTime.of(2000, 1, 1, 0, 0));
        Map<String, Long> catRevMap = new HashMap<>();
        for (Order o : successOrders) {
            for (OrderItem item : o.getOrderItems()) {
                Product p = item.getProduct();
                if (p.getProductCates() != null) {
                    for (ProductCate pc : p.getProductCates()) {
                        String catName = pc.getCategory().getName();
                        catRevMap.merge(catName, (long) item.getPrice() * item.getQuantity(), Long::sum);
                    }
                }
            }
        }
        stats.put("categoryRevenue", catRevMap);

        long newCustomers = userRep.countByCreatedAtAfter(LocalDateTime.now().minusDays(30));
        stats.put("newCustomers30d", newCustomers);
        stats.put("totalCustomers", userRep.count());

        long pendingProducts = productRep.countByApprovedFalse();
        stats.put("pendingProducts", pendingProducts);

        long pendingReviews = reviewRep.countByApprovedFalse();
        stats.put("pendingReviews", pendingReviews);

        long pendingComments = commentRep.countByApprovedFalse();
        stats.put("pendingComments", pendingComments);

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

    @Transactional
    public User toggleUserBan(Long userId) {
        User user = userRep.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setBanned(!user.isBanned());
        return userRep.save(user);
    }

    @Transactional
    public void resetPassword(Long userId, String newPassword) {
        User user = userRep.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRep.save(user);
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRep.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        order.setOrderStatus(newStatus);
        return orderRep.save(order);
    }

    @Transactional
    public Product approveProduct(Long productId) {
        Product product = productRep.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        product.setApproved(true);
        return productRep.save(product);
    }

    @Transactional(readOnly = true)
    public List<Product> getPendingProducts() {
        return productRep.findByApprovedFalse();
    }

    @Transactional
    public Review approveReview(Long reviewId) {
        Review review = reviewRep.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + reviewId));
        review.setApproved(true);
        return reviewRep.save(review);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        reviewRep.deleteById(reviewId);
    }

    @Transactional(readOnly = true)
    public List<Review> getPendingReviews() {
        return reviewRep.findByApprovedFalseOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<Comment> getPendingComments() {
        return commentRep.findByApprovedFalseOrderByCreatedAtDesc();
    }

    @Transactional
    public Comment approveComment(Long commentId) {
        Comment comment = commentRep.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));
        comment.setApproved(true);
        return commentRep.save(comment);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        commentRep.deleteById(commentId);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getBestSellingProductTypes() {
        List<Order> successOrders = orderRep.findSuccessOrdersSince(LocalDateTime.of(2000, 1, 1, 0, 0));
        Map<String, Object> typeMap = new LinkedHashMap<>();
        for (Order o : successOrders) {
            for (OrderItem item : o.getOrderItems()) {
                Product p = item.getProduct();
                long revenue = (long) item.getPrice() * item.getQuantity();
                int sold = item.getQuantity();
                if (p.getProductCates() != null) {
                    for (ProductCate pc : p.getProductCates()) {
                        String catName = pc.getCategory().getName();
                        typeMap.merge(catName, Map.of(
                            "revenue", revenue,
                            "sold", sold
                        ), (old, add) -> Map.of(
                            "revenue", ((Number)((Map)old).get("revenue")).longValue() + ((Number)((Map)add).get("revenue")).longValue(),
                            "sold", ((Number)((Map)old).get("sold")).intValue() + ((Number)((Map)add).get("sold")).intValue()
                        ));
                    }
                }
            }
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Object> entry : typeMap.entrySet()) {
            Map<String, Object> point = new LinkedHashMap<>();
            point.put("name", entry.getKey());
            point.put("revenue", ((Map)entry.getValue()).get("revenue"));
            point.put("sold", ((Map)entry.getValue()).get("sold"));
            result.add(point);
        }
        result.sort((a, b) -> Integer.compare(
            ((Number)b.get("sold")).intValue(),
            ((Number)a.get("sold")).intValue()
        ));
        return result;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getRevenueByCategory() {
        List<Order> successOrders = orderRep.findSuccessOrdersSince(LocalDateTime.of(2000, 1, 1, 0, 0));
        Map<String, Long> catRevMap = new LinkedHashMap<>();
        for (Order o : successOrders) {
            for (OrderItem item : o.getOrderItems()) {
                Product p = item.getProduct();
                if (p.getProductCates() != null) {
                    for (ProductCate pc : p.getProductCates()) {
                        String catName = pc.getCategory().getName();
                        catRevMap.merge(catName, (long) item.getPrice() * item.getQuantity(), Long::sum);
                    }
                }
            }
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : catRevMap.entrySet()) {
            Map<String, Object> point = new HashMap<>();
            point.put("name", entry.getKey());
            point.put("revenue", entry.getValue());
            result.add(point);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getCustomerStats() {
        Map<String, Object> stats = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        stats.put("total", userRep.count());
        stats.put("new30d", userRep.countByCreatedAtAfter(now.minusDays(30)));
        stats.put("new7d", userRep.countByCreatedAtAfter(now.minusDays(7)));
        stats.put("newToday", userRep.countByCreatedAtAfter(now.withHour(0).withMinute(0).withSecond(0)));
        return stats;
    }

    @Transactional(readOnly = true)
    public List<Coupon> getAllCoupons() {
        return couponRep.findAll();
    }

    @Transactional
    public Coupon createCoupon(String code, String discountType, int discountValue, Integer minOrderValue, Integer maxUses, LocalDateTime expiryDate) {
        if (couponRep.existsByCode(code)) {
            throw new RuntimeException("Mã giảm giá đã tồn tại");
        }
        Coupon coupon = Coupon.builder()
                .code(code.toUpperCase())
                .discountType(discountType)
                .discountValue(discountValue)
                .minOrderValue(minOrderValue)
                .maxUses(maxUses)
                .expiryDate(expiryDate)
                .active(true)
                .build();
        return couponRep.save(coupon);
    }

    @Transactional
    public Coupon updateCoupon(Long id, String code, String discountType, Integer discountValue, Integer minOrderValue, Integer maxUses, LocalDateTime expiryDate, Boolean active) {
        Coupon coupon = couponRep.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found with id: " + id));
        if (code != null) coupon.setCode(code.toUpperCase());
        if (discountType != null) coupon.setDiscountType(discountType);
        if (discountValue != null) coupon.setDiscountValue(discountValue);
        if (minOrderValue != null) coupon.setMinOrderValue(minOrderValue);
        if (maxUses != null) coupon.setMaxUses(maxUses);
        if (expiryDate != null) coupon.setExpiryDate(expiryDate);
        if (active != null) coupon.setActive(active);
        return couponRep.save(coupon);
    }

    @Transactional
    public void deleteCoupon(Long id) {
        couponRep.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ProductKey> getAllProductKeys() {
        return productKeyRep.findAll();
    }

    @Transactional(readOnly = true)
    public List<ProductKey> getKeysByProduct(Long productId) {
        return productKeyRep.findByProductId(productId);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getKeyStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalKeys", productKeyRep.count());
        stats.put("soldKeys", productKeyRep.countBySoldTrue());
        stats.put("availableKeys", productKeyRep.countBySoldFalse());
        return stats;
    }

    @Transactional
    public ProductKey addProductKey(Long productId, String keyCode) {
        Product product = productRep.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        ProductKey pk = ProductKey.builder()
                .product(product)
                .keyCode(keyCode)
                .sold(false)
                .build();
        return productKeyRep.save(pk);
    }

    @Transactional
    public void addProductKeysBulk(Long productId, List<String> keyCodes) {
        Product product = productRep.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        for (String code : keyCodes) {
            ProductKey pk = ProductKey.builder()
                    .product(product)
                    .keyCode(code)
                    .sold(false)
                    .build();
            productKeyRep.save(pk);
        }
    }

    @Transactional
    public void deleteProductKey(Long id) {
        productKeyRep.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<RefundRequest> getAllRefundRequests() {
        return refundRep.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getRefundStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", refundRep.count());
        stats.put("pending", refundRep.countByStatus("PENDING"));
        stats.put("approved", refundRep.countByStatus("APPROVED"));
        stats.put("rejected", refundRep.countByStatus("REJECTED"));
        return stats;
    }

    @Transactional
    public RefundRequest processRefund(Long id, String status, String adminNote, Long adminId) {
        RefundRequest req = refundRep.findById(id)
                .orElseThrow(() -> new RuntimeException("Refund request not found with id: " + id));
        User admin = userRep.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        req.setStatus(status);
        if (adminNote != null) req.setAdminNote(adminNote);
        req.setProcessedBy(admin);
        req.setProcessedAt(LocalDateTime.now());
        return refundRep.save(req);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getReturningCustomerStats() {
        Map<String, Object> stats = new HashMap<>();
        List<Order> allOrders = orderRep.findAll();
        Map<Long, Long> userOrderCounts = allOrders.stream()
                .filter(o -> o.getUser() != null && "SUCCESS".equals(o.getOrderStatus()))
                .collect(Collectors.groupingBy(o -> o.getUser().getId(), Collectors.counting()));
        long returning = userOrderCounts.values().stream().filter(c -> c >= 2).count();
        long oneTime = userOrderCounts.values().stream().filter(c -> c == 1).count();
        stats.put("returningCustomers", returning);
        stats.put("oneTimeCustomers", oneTime);
        return stats;
    }

    public String exportOrdersToCsv() {
        List<Order> orders = orderRep.findAllByOrderByOrderDateDesc();
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Ngày,Khách hàng,Email,SĐT,Tổng tiền,Thanh toán,Trạng thái\n");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (Order o : orders) {
            sb.append(o.getId()).append(",");
            sb.append(o.getOrderDate() != null ? o.getOrderDate().format(fmt) : "").append(",");
            sb.append(escapeCsv(o.getFullName())).append(",");
            sb.append(escapeCsv(o.getUser() != null ? o.getUser().getEmail() : "")).append(",");
            sb.append(escapeCsv(o.getPhoneNumber())).append(",");
            sb.append(o.getTotalPrice()).append(",");
            sb.append(o.getPaymentStatus()).append(",");
            sb.append(o.getOrderStatus()).append("\n");
        }
        return sb.toString();
    }

    public String exportUsersToCsv() {
        List<User> users = userRep.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Họ tên,Email,SĐT,Vai trò,Ngày tạo,Khoá\n");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (User u : users) {
            sb.append(u.getId()).append(",");
            sb.append(escapeCsv(u.getFullName())).append(",");
            sb.append(escapeCsv(u.getEmail())).append(",");
            sb.append(escapeCsv(u.getPhoneNumber())).append(",");
            sb.append(u.getRole()).append(",");
            sb.append(u.getCreatedAt() != null ? u.getCreatedAt().format(fmt) : "").append(",");
            sb.append(u.isBanned() ? "Có" : "Không").append("\n");
        }
        return sb.toString();
    }

    public String exportProductsToCsv() {
        List<Product> products = productRep.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Tên sản phẩm,Giá bán,Giá gốc,Đã bán,Seller,Trạng thái\n");
        for (Product p : products) {
            sb.append(p.getId()).append(",");
            sb.append(escapeCsv(p.getName())).append(",");
            sb.append(p.getPrice()).append(",");
            sb.append(p.getPriceOri()).append(",");
            sb.append(p.getSold() != null ? p.getSold() : 0).append(",");
            sb.append(escapeCsv(p.getSeller() != null ? p.getSeller().getFullName() : "Sàn")).append(",");
            sb.append(p.isApproved() ? "Đã duyệt" : "Chờ duyệt").append("\n");
        }
        return sb.toString();
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRep.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}