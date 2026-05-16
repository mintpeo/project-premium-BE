package com.tmdt.projectpremium.service;

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

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderSer {
    private final OrderRep rep;
    private final OrderItemRep orderItemRep;
    private final UserRepository userRep;
    private final ProductRep productRep;
    private final CartSer cartSer;

    // Save The Order
    public boolean saveOrder(AddOrderReq orderReq) {
        OrderReq req = orderReq.getOrderInfo();
        List<OrderItemReq> itemReqList = orderReq.getItems();

        // check user id
        User user = userRep.findById(req.getUserId()).orElseThrow(() -> new RuntimeException("Không tìm thấy user_id:" + req.getUserId()));

        // set up Order
        Order order = new Order();
        order.setUser(user);
        order.setFullName(req.getFullName());
        order.setPhoneNumber(req.getPhoneNumber());
        order.setPaymentMethod(req.getPaymentMethod());
        order.setPaymentStatus(req.getPaymentStatus());
        order.setOrderStatus(req.getOrderStatus());
        order.setOrderDate(LocalDateTime.now());
        order.setNote(req.getNote());
        order.setTotalPrice(req.getTotalPrice());

        rep.save(order); // save order first then do...

        // save Order Item
        for (OrderItemReq itemReq : itemReqList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            Product product = productRep.findById(itemReq.getProductId()).orElseThrow(() -> new RuntimeException("Không tìm thấy product_id:" + itemReq.getProductId()));
            orderItem.setProduct(product);
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setTypeUser(itemReq.getTypeUser());
            orderItem.setDuration(itemReq.getDuration());

            orderItemRep.save(orderItem); // save tung phan
        }

        // delete items after add order success
        List<Long> cartItemIdList = cartSer.getCartItemId(req.getUserId());
        for (Long number : cartItemIdList) cartSer.removeProductInCart(number);
        return true;
    }
}
