package com.tmdt.projectpremium.service;

import com.tmdt.projectpremium.dto.OrderItemResponseDTO;
import com.tmdt.projectpremium.dto.OrderResponseDTO;
import com.tmdt.projectpremium.dto.request.AddOrderReq;
import com.tmdt.projectpremium.dto.request.OrderItemReq;
import com.tmdt.projectpremium.dto.request.OrderReq;
import com.tmdt.projectpremium.entity.Order;
import com.tmdt.projectpremium.entity.OrderItem;
import com.tmdt.projectpremium.entity.Product;
import com.tmdt.projectpremium.entity.User;
import com.tmdt.projectpremium.repository.OrderItemRep;
import com.tmdt.projectpremium.repository.OrderRep;
import com.tmdt.projectpremium.repository.ProductRep;
import com.tmdt.projectpremium.repository.UserRepository;
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

    // Save The Order
    public Order saveOrder(AddOrderReq orderReq) {
        OrderReq req = orderReq.getOrderInfo();
        List<OrderItemReq> itemReqList = orderReq.getItems();

        // check user id
        User user = userRep.findById(req.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user_id:" + req.getUserId()));

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
        order.setTotalPrice(req.getTotalPrice());

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

            // Cập nhật đã thanh toán và chuyển sang PROCESSING — chờ admin xác nhận
            order.setPaymentStatus("PAID");
            order.setOrderStatus("PROCESSING");
            productKeyService.assignKeysToOrder(order);
            rep.save(order);
        }
    }
}
