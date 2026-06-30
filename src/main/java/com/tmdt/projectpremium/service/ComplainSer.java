package com.tmdt.projectpremium.service;

import com.tmdt.projectpremium.dto.request.CheckComplainIdReq;
import com.tmdt.projectpremium.dto.request.ComplainReq;
import com.tmdt.projectpremium.dto.request.RejectedComplainReq;
import com.tmdt.projectpremium.dto.request.SendMailForUserReq;
import com.tmdt.projectpremium.dto.response.ShowComplainRes;
import com.tmdt.projectpremium.entity.*;
import com.tmdt.projectpremium.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComplainSer {
    private final ComplainRep rep;
    private final OrderRep orderRep;
    private final OrderItemRep orderItemRep;
    private final UserRepository userRepository;
    private final ProductRep productRep;
    private final MailSender mailSender;
    private final static String SUB_MAIL = "Khiếu nại về sản phẩm";

    // Send Mail
    public void sendMailForUser(SendMailForUserReq req) {
        // Send Mail
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(req.getEmail());
        if (req.isRejected()) message.setSubject("Lí do từ chối");
        else message.setSubject("Nhận Key bản quyền");
        // Text
        message.setText(req.getDes());

        mailSender.send(message);
    }

    // Show Complain Admin
    public List<ShowComplainRes> showComplainAdmin() {
        List<Complain> c = rep.findAll();
        List<ShowComplainRes> res = c.stream().map(complain -> {
            ShowComplainRes dto = new ShowComplainRes();
            dto.setStatus(complain.getStatus());
            dto.setDescription(complain.getDescription());
            dto.setReason(complain.getReason());
            dto.setDate(complain.getDate());
            dto.setOrderId(complain.getOrder().getId());
            dto.setUserName(complain.getUser().getFullName());
            dto.setEmail(complain.getUser().getEmail());
            dto.setRejected(complain.getReasonRejected());
            dto.setId(complain.getId());

            if (complain.getSellerId() != 0) {
                User userSeller = userRepository.findById(complain.getSellerId()).orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản Seller với ID: " + complain.getSellerId()));
                dto.setUserSeller(userSeller.getFullName());
                dto.setEmailSeller(userSeller.getEmail());
            } else {
                dto.setUserSeller(null);
                dto.setEmailSeller(null);
            }

            return dto;
        }).collect(Collectors.toList());

        return res;
    }

    // Get Status
    public String getStatusComplain(long complainId) {
        Complain c = rep.findById(complainId).orElseThrow(() -> new RuntimeException("Không tìm thấy đơn khiếu nại với ID: " + complainId));
        return c.getStatus();
    }

    // Change Status
    public void changeStatusComplain(RejectedComplainReq req) {
        Complain complain = rep.findById(req.getComplainId()).orElseThrow(() -> new RuntimeException("Không tìm thấy đơn khiếu nại với ID: " + req.getComplainId()));
        if (req.getStatus().equals("REJECTED")) complain.setReasonRejected(req.getRejected());
        complain.setStatus(req.getStatus());
        rep.save(complain);
    }

    // Show Complain
    public List<ShowComplainRes> showComplain(CheckComplainIdReq req) {
        List<Complain> c = rep.findBySellerId(req.getSellerId());
        List<ShowComplainRes> res = c.stream().map(complain -> {
            ShowComplainRes dto = new ShowComplainRes();
            dto.setStatus(complain.getStatus());
            dto.setDescription(complain.getDescription());
            dto.setReason(complain.getReason());
            dto.setDate(complain.getDate());
            dto.setOrderId(complain.getOrder().getId());
            dto.setUserName(complain.getUser().getFullName());
            dto.setEmail(complain.getUser().getEmail());
            dto.setRejected(complain.getReasonRejected());
            dto.setId(complain.getId());

            if (req.getSellerId() != 0) {
                User userSeller = userRepository.findById(req.getSellerId()).orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản Seller với ID: " + req.getSellerId()));
                dto.setUserSeller(userSeller.getFullName());
                dto.setEmailSeller(userSeller.getEmail());
            } else {
                dto.setUserSeller(null);
                dto.setEmailSeller(null);
            }

            return dto;
        }).collect(Collectors.toList());

        return res;
    }

    // Send Mail To Complain
    public void sendMailComplain(ComplainReq req) {
        // Check
        Order order = orderRep.findById(req.getOrderId()).orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + req.getOrderId()));
        User user = userRepository.findById(req.getUserId()).orElse(null);
        Product product = productRep.findById(req.getProductId()).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + req.getProductId()));
        long sellerId;
        if (product.getSeller() != null) {
            sellerId = product.getSeller().getId();
        } else sellerId = 0;

        // Save Data
        Complain c = new Complain();
        c.setDescription(req.getDescription());
        c.setReason(req.getReason());
        c.setOrder(order);
        c.setProduct(product);
        c.setUser(user);
        c.setSellerId(sellerId);
        c.setStatus("PENDING");
        c.setDate(LocalDateTime.now());
        Complain saveC = rep.save(c);

        // Save Order Item Data (isComplain)
        OrderItem orderItem = order.getOrderItems().stream().filter(oi -> oi.getProduct().getId() == req.getProductId()).findFirst()
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại trong đơn hàng này"));
        orderItem.setComplain(saveC);
        orderItemRep.save(orderItem);

        User userSeller;
        if (sellerId != 0) userSeller = userRepository.findById(sellerId).orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản Seller với ID: " + sellerId));
        else return;

        // Send Mail
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(userSeller.getEmail());
        message.setSubject(SUB_MAIL);
        // Text
        String text ="Mã Đơn: #PK-" + req.getOrderId() + "\n" + "Mã Sản phẩm: " + req.getProductId() + "\n" + req.getReason() + "\n" + req.getDescription();
        message.setText(text);

        mailSender.send(message);
    }
}