package com.tmdt.projectpremium.service;

import com.tmdt.projectpremium.entity.*;
import com.tmdt.projectpremium.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SellerBalanceService {

    private final SellerBalanceRepository balanceRep;
    private final SellerEarningRepository earningRep;
    private final UserRepository userRep;

    @Transactional
    public void processOrderEarnings(Order order) {
        if (order.getOrderItems() == null) return;

        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            if (product.getSeller() == null) continue;

            User seller = product.getSeller();
            long sellerAmount = item.getPrice() != null ? item.getPrice().longValue() : 0L;

            SellerEarning earning = new SellerEarning();
            earning.setSeller(seller);
            earning.setOrder(order);
            earning.setProduct(product);
            earning.setAmount(sellerAmount);
            earning.setStatus("PENDING");
            earning.setCreatedAt(LocalDateTime.now());
            earningRep.save(earning);

            SellerBalance balance = balanceRep.findBySeller(seller).orElseGet(() -> {
                SellerBalance b = new SellerBalance();
                b.setSeller(seller);
                b.setCreatedAt(LocalDateTime.now());
                b.setUpdatedAt(LocalDateTime.now());
                return balanceRep.save(b);
            });

            balance.setPendingAmount(balance.getPendingAmount() + sellerAmount);
            balance.setTotalEarned(balance.getTotalEarned() + sellerAmount);
            balance.setUpdatedAt(LocalDateTime.now());
            balanceRep.save(balance);
        }
    }

    @Transactional(readOnly = true)
    public SellerBalance getSellerBalanceEntity(Long sellerId) {
        return balanceRep.findBySellerId(sellerId).orElse(null);
    }

    @Transactional
    public void saveBalance(SellerBalance balance) {
        balanceRep.save(balance);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getSellerBalance(Long sellerId) {
        SellerBalance balance = balanceRep.findBySellerId(sellerId).orElse(null);
        if (balance == null) {
            Map<String, Object> empty = new LinkedHashMap<>();
            empty.put("pendingAmount", 0L);
            empty.put("availableAmount", 0L);
            empty.put("totalEarned", 0L);
            return empty;
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("pendingAmount", balance.getPendingAmount());
        result.put("availableAmount", balance.getAvailableAmount());
        result.put("totalEarned", balance.getTotalEarned());
        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getEarningSummary() {
        List<SellerEarning> all = earningRep.findAll();
        long totalPending = 0, totalApproved = 0;
        for (SellerEarning e : all) {
            if ("PENDING".equals(e.getStatus())) totalPending += e.getAmount();
            else if ("APPROVED".equals(e.getStatus())) totalApproved += e.getAmount();
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalPending", totalPending);
        result.put("totalApproved", totalApproved);
        result.put("total", totalPending + totalApproved);
        return result;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getEarningBreakdown() {
        List<SellerEarning> all = earningRep.findAll();
        Map<Long, Map<String, Object>> sellerMap = new LinkedHashMap<>();

        for (SellerEarning e : all) {
            Long sellerId = e.getSeller().getId();
            sellerMap.putIfAbsent(sellerId, new LinkedHashMap<>());
            Map<String, Object> d = sellerMap.get(sellerId);

            if (!d.containsKey("sellerId")) {
                d.put("sellerId", sellerId);
                d.put("sellerName", e.getSeller().getFullName());
                d.put("sellerEmail", e.getSeller().getEmail());
                d.put("totalPending", 0L);
                d.put("totalApproved", 0L);
                d.put("total", 0L);
                d.put("earnings", new ArrayList<Map<String, Object>>());
            }

            long amount = e.getAmount();
            d.put("total", (Long) d.get("total") + amount);
            if ("PENDING".equals(e.getStatus())) {
                d.put("totalPending", (Long) d.get("totalPending") + amount);
            } else if ("APPROVED".equals(e.getStatus())) {
                d.put("totalApproved", (Long) d.get("totalApproved") + amount);
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> earnings = (List<Map<String, Object>>) d.get("earnings");
            Map<String, Object> ed = new LinkedHashMap<>();
            ed.put("id", e.getId());
            ed.put("amount", e.getAmount());
            ed.put("status", e.getStatus());
            ed.put("orderId", e.getOrder().getId());
            ed.put("productName", e.getProduct().getName());
            ed.put("createdAt", e.getCreatedAt().toString());
            earnings.add(ed);
        }

        return new ArrayList<>(sellerMap.values());
    }

    public List<Map<String, Object>> getPendingEarnings() {
        List<SellerEarning> earnings = earningRep.findByStatusOrderByCreatedAtAsc("PENDING");
        Map<Long, Map<String, Object>> sellerMap = new LinkedHashMap<>();

        for (SellerEarning e : earnings) {
            Long sellerId = e.getSeller().getId();
            sellerMap.putIfAbsent(sellerId, new LinkedHashMap<>());
            Map<String, Object> sellerData = sellerMap.get(sellerId);

            if (!sellerData.containsKey("sellerId")) {
                sellerData.put("sellerId", sellerId);
                sellerData.put("sellerName", e.getSeller().getFullName());
                sellerData.put("sellerEmail", e.getSeller().getEmail());
                sellerData.put("totalPending", 0L);
                sellerData.put("earnings", new ArrayList<Map<String, Object>>());
            }

            sellerData.put("totalPending", (Long) sellerData.get("totalPending") + e.getAmount());

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> earningsList = (List<Map<String, Object>>) sellerData.get("earnings");
            Map<String, Object> earningData = new LinkedHashMap<>();
            earningData.put("id", e.getId());
            earningData.put("amount", e.getAmount());
            earningData.put("orderId", e.getOrder().getId());
            earningData.put("productName", e.getProduct().getName());
            earningData.put("createdAt", e.getCreatedAt().toString());
            earningsList.add(earningData);
        }

        return new ArrayList<>(sellerMap.values());
    }

    @Transactional
    public void approveEarning(Long earningId, Long adminId) {
        SellerEarning earning = earningRep.findById(earningId)
                .orElseThrow(() -> new RuntimeException("Earning not found: " + earningId));
        if (!"PENDING".equals(earning.getStatus())) {
            throw new RuntimeException("Earning already processed");
        }

        User admin = userRep.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        earning.setStatus("APPROVED");
        earning.setReviewedAt(LocalDateTime.now());
        earning.setReviewedBy(admin);
        earningRep.save(earning);

        SellerBalance balance = balanceRep.findBySeller(earning.getSeller())
                .orElseThrow(() -> new RuntimeException("Seller balance not found"));
        balance.setPendingAmount(balance.getPendingAmount() - earning.getAmount());
        balance.setAvailableAmount(balance.getAvailableAmount() + earning.getAmount());
        balance.setUpdatedAt(LocalDateTime.now());
        balanceRep.save(balance);
    }

    @Transactional
    public void approveAllForSeller(Long sellerId, Long adminId) {
        List<SellerEarning> pendingEarnings = earningRep.findBySellerIdAndStatus(sellerId, "PENDING");
        if (pendingEarnings.isEmpty()) return;

        User admin = userRep.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        long totalAmount = 0;
        for (SellerEarning earning : pendingEarnings) {
            earning.setStatus("APPROVED");
            earning.setReviewedAt(LocalDateTime.now());
            earning.setReviewedBy(admin);
            totalAmount += earning.getAmount();
        }
        earningRep.saveAll(pendingEarnings);

        SellerBalance balance = balanceRep.findBySellerId(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller balance not found"));
        balance.setPendingAmount(balance.getPendingAmount() - totalAmount);
        balance.setAvailableAmount(balance.getAvailableAmount() + totalAmount);
        balance.setUpdatedAt(LocalDateTime.now());
        balanceRep.save(balance);
    }
}
