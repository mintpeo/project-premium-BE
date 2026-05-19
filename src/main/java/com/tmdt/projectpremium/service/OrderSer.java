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

    // Save The Order
    public Order saveOrder(AddOrderReq orderReq) {
        OrderReq req = orderReq.getOrderInfo();
        List<OrderItemReq> itemReqList = orderReq.getItems();

        // check user id
        User user = userRep.findById(req.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user_id:" + req.getUserId()));

        // set up Order
        Order order = new Order();
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

            // 1. Cập nhật đã thanh toán và chuyển sang PROCESSING
            order.setPaymentStatus("PAID");
            order.setOrderStatus("PROCESSING");
            rep.save(order);

            // 2. Thử tự động xuất kho (Gán Key/Tài khoản)
            try {
                autoAssignKeysAndCompleteOrder(order);
            } catch (Exception e) {
                System.err.println("Cấp Key tự động thất bại cho đơn hàng " + orderId
                        + ". Đơn hàng sẽ giữ nguyên ở trạng thái PROCESSING.");
                e.printStackTrace();

            }
        }
    }

    private void autoAssignKeysAndCompleteOrder(Order order) {
        boolean allAssigned = true;

        for (OrderItem item : order.getOrderItems()) {
            /*
             * TODO Tương lai: Bạn sẽ gọi API vào kho thẻ (ví dụ KeyRep) để lấy Key chưa sử
             * dụng ra.
             * Ví dụ: String availableKey =
             * keyRep.getAvailableKey(item.getProduct().getId());
             */

            // Hiện tại: Mình giả lập hệ thống tự động sinh (hoặc lấy) một Key gán vào cho
            // khách
            String generatedKey = "ACC-" + item.getProduct().getId() + "-" + System.currentTimeMillis();
            item.setKeyCode(generatedKey);
            orderItemRep.save(item);

            // Nếu có sản phẩm nào hết Key trong kho, set allAssigned = false
        }

        // 3. Nếu cấp phát Key thành công toàn bộ, thông thường sẽ chuyển lên ĐÃ GIAO
        // (SUCCESS)
        if (allAssigned) {
            order.setOrderStatus("SUCCESS");
            rep.save(order);
        }
    }
}
