package com.tmdt.projectpremium.service;

import com.tmdt.projectpremium.dto.OrderItemResponseDTO;
import com.tmdt.projectpremium.dto.OrderResponseDTO;
import com.tmdt.projectpremium.dto.request.AddOrderReq;
import com.tmdt.projectpremium.dto.request.OrderItemReq;
import com.tmdt.projectpremium.dto.request.OrderReq;
import com.tmdt.projectpremium.entity.*;
import com.tmdt.projectpremium.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderSer {
    private final OrderRep rep;
    private final OrderItemRep orderItemRep;
    private final UserRepository userRep;
    private final ProductRep productRep;
    private final CartSer cartSer;
    private final SellerBalanceService sellerBalanceService;
    private final ProductKeyService productKeyService;
    private final CouponRepository couponRep;

    // Save The Order
    public Order saveOrder(AddOrderReq orderReq) {
        OrderReq req = orderReq.getOrderInfo();
        List<OrderItemReq> itemReqList = orderReq.getItems();

        // check user id
        User user = userRep.findById(req.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user_id:" + req.getUserId()));

        // Xử lý điểm thưởng
        int pointsUsed = req.getPointsUsed();
        int userPoints = user.getPoints() != null ? user.getPoints() : 0;
        if (pointsUsed > 0) {
            if (pointsUsed > userPoints) {
                throw new RuntimeException("Số điểm không đủ. Bạn có " + userPoints + " điểm.");
            }
            user.setPoints(userPoints - pointsUsed);
            userRep.save(user);
        }

        int totalPrice = req.getTotalPrice();

        // Xử lý mã giảm giá
        int discountAmount = 0;
        String couponCode = req.getCouponCode();
        if (couponCode != null && !couponCode.trim().isEmpty()) {
            Coupon coupon = couponRep.findByCode(couponCode.trim().toUpperCase())
                    .orElseThrow(() -> new RuntimeException("Mã giảm giá không tồn tại"));

            if (!coupon.isActive()) {
                throw new RuntimeException("Mã giảm giá đã bị vô hiệu hoá");
            }
            if (coupon.getExpiryDate() != null && coupon.getExpiryDate().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Mã giảm giá đã hết hạn");
            }
            if (coupon.getMaxUses() != null && coupon.getUsedCount() >= coupon.getMaxUses()) {
                throw new RuntimeException("Mã giảm giá đã hết lượt sử dụng");
            }

            // Tính rawTotal (tổng gốc trước khi trừ điểm và mã) để validate minOrderValue
            int pointDiscountVnd = req.getPointsUsed() > 0 ? req.getPointsUsed() * 100 : 0;
            int rawTotal = totalPrice + pointDiscountVnd + req.getCouponDiscount();
            if (coupon.getMinOrderValue() != null && rawTotal < coupon.getMinOrderValue()) {
                throw new RuntimeException("Đơn hàng tối thiểu " + String.format("%,d", coupon.getMinOrderValue()) + "đ để sử dụng mã này");
            }

            // Tính discountAmount chỉ để ghi nhận (không trừ vào totalPrice vì FE đã trừ rồi)
            if (coupon.getDiscountType() != null && coupon.getDiscountType().startsWith("PERCENT")) {
                discountAmount = rawTotal * coupon.getDiscountValue() / 100;
            } else {
                discountAmount = coupon.getDiscountValue();
            }
            if (discountAmount > rawTotal) {
                discountAmount = rawTotal;
            }

            coupon.setUsedCount(coupon.getUsedCount() + 1);
            couponRep.save(coupon);
        }

        // Tính điểm thưởng được hưởng: 1 điểm / 1000đ
        int pointsEarned = totalPrice / 1000;

        // set up Order
        Order order = new Order();
        // Tạo ID là số nguyên 15 chữ số (Timestamp + 2 số ngẫu nhiên) để lưu thẳng vào DB
        long generatedId = Long.parseLong(System.currentTimeMillis() + String.format("%02d", (int)(Math.random() * 100)));
        order.setId(generatedId);
        order.setUser(user);
        order.setFullName(req.getFullName());
        order.setPhoneNumber(req.getPhoneNumber());
        order.setPaymentMethod(req.getPaymentMethod());
        order.setPaymentStatus("PENDING");
        order.setOrderStatus("PENDING");
        order.setOrderDate(LocalDateTime.now());
        order.setNote(req.getNote());
        order.setTotalPrice(totalPrice);
        order.setPointsUsed(pointsUsed);
        order.setPointsEarned(pointsEarned);
        order.setCouponCode(couponCode);
        order.setDiscountAmount(discountAmount);

        rep.save(order); // save order first then do...

        // save Order Item
        for (OrderItemReq itemReq : itemReqList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            Product product = productRep.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy product_id:" + itemReq.getProductId()));
            orderItem.setProduct(product);
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setTypeUser(itemReq.getTypeUser());
            orderItem.setDuration(itemReq.getDuration());
            orderItem.setPrice(product.getPrice());

            orderItemRep.save(orderItem); // save tung phan
        }

        // delete items after add order success
        List<Long> cartItemIdList = cartSer.getCartItemId(req.getUserId());
        for (Long number : cartItemIdList)
            cartSer.removeProductInCart(number);
        return order;
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getUserOrderHistoryByStatus(Long userId, String status) {

        // 👈 2. Truyền cả 2 tham số userId và status vào hàm của Repository
        List<Order> orders = rep.findByUserIdAndOrderStatusOrderByIdDesc(userId, status);

        return orders.stream().map(order -> {
            OrderResponseDTO dto = new OrderResponseDTO();
            dto.setOrderId("" + order.getId());
            dto.setCreatedAt(order.getOrderDate());
            dto.setStatus(order.getOrderStatus());
            dto.setTotalPrice(order.getTotalPrice());

            // Map danh sách OrderItem sang OrderItemResponseDTO
            List<OrderItemResponseDTO> itemDTOs = order.getOrderItems().stream().map(item -> {
                OrderItemResponseDTO itemDTO = new OrderItemResponseDTO();

                // Nối chuỗi tên sản phẩm động: "Tên + (Thời hạn - Loại User)"
                String fullName = item.getProduct().getName() + " (" + item.getDuration() + " - " + item.getTypeUser()
                        + ")";
                itemDTO.setProductName(fullName);

                itemDTO.setProductId(item.getProduct().getId());
                itemDTO.setQuantity(item.getQuantity());
                itemDTO.setPrice(item.getPrice() > 0 ? item.getPrice() : item.getProduct().getPrice()); // Lấy giá lúc
                                                                                                        // mua từ
                                                                                                        // OrderItem
                                                                                                        // (hoặc
                                                                                                        // fallback về
                                                                                                        // Product nếu
                                                                                                        // đơn cũ chưa
                                                                                                        // có)
                itemDTO.setProductImg(item.getProduct().getImg());
                itemDTO.setKeyCode(item.getKeyCode());
                if (item.getComplain() == null) itemDTO.setComplainId(null);
                else itemDTO.setComplainId(item.getComplain().getId());
                return itemDTO;
            }).collect(Collectors.toList());

            dto.setItems(itemDTOs);
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public boolean cancelOrder(Long orderId) {
        Order order = rep.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        if ("PENDING".equals(order.getOrderStatus()) || "PROCESSING".equals(order.getOrderStatus())) {
            order.setOrderStatus("CANCELLED");
            rep.save(order);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean confirmOrder(Long orderId) {
        Order order = rep.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        if ("PROCESSING".equals(order.getOrderStatus())) {
            order.setOrderStatus("SUCCESS");
            rep.save(order);
            sellerBalanceService.processOrderEarnings(order);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public Order getOrderById(Long orderId) {
        return rep.findById(orderId).orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng ID: " + orderId));
    }

    @Transactional
    public void handlePaymentSuccess(Long orderId) {
        Order order = rep.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng ID: " + orderId));

        // Chỉ xử lý nếu đơn đang ở PENDING
        if ("PENDING".equals(order.getOrderStatus())) {

            // Cập nhật đã thanh toán và chuyển sang PROCESSING
            order.setPaymentStatus("PAID");
            order.setOrderStatus("PROCESSING");

            // Cộng điểm thưởng cho người dùng
            Integer earned = order.getPointsEarned();
            if (earned != null && earned > 0) {
                User user = order.getUser();
                user.setPoints((user.getPoints() != null ? user.getPoints() : 0) + earned);
                user.setTotalPointsEarned((user.getTotalPointsEarned() != null ? user.getTotalPointsEarned() : 0) + earned);
                userRep.save(user);
            }

            rep.save(order);

            // Thử gán key + tự động chuyển SUCCESS + tạo earning
            try {
                productKeyService.assignKeysToOrder(order);
                autoAssignKeysAndCompleteOrder(order);
            } catch (Exception e) {
                System.err.println("Cấp Key tự động thất bại cho đơn hàng " + orderId);
            }
        }
    }

    private void autoAssignKeysAndCompleteOrder(Order order) {
        boolean allAssigned = true;
        for (OrderItem item : order.getOrderItems()) {
            if (item.getKeyCode() == null || item.getKeyCode().isEmpty()) {
                allAssigned = false;
                break;
            }
        }
        if (allAssigned) {
            order.setOrderStatus("SUCCESS");
            rep.save(order);
            sellerBalanceService.processOrderEarnings(order);
        }
    }
}
