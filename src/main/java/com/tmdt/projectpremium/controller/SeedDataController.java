package com.tmdt.projectpremium.controller;

import com.tmdt.projectpremium.entity.Review;
import com.tmdt.projectpremium.entity.User;
import com.tmdt.projectpremium.repository.ReviewRepository;
import com.tmdt.projectpremium.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/seed")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class SeedDataController {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/reviews")
    public ResponseEntity<String> seedReviews() {
        try {
            // Tạo users mẫu
            List<User> users = createSampleUsers();
            
            // Tạo reviews mẫu
            createSampleReviews(users);
            
            return ResponseEntity.ok("✅ Đã import thành công " + reviewRepository.count() + " reviews!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ Lỗi: " + e.getMessage());
        }
    }

    private List<User> createSampleUsers() {
        List<User> users = new ArrayList<>();
        String[][] userData = {
            {"nguyen.van.a@gmail.com", "Nguyễn Văn A", "0901234567"},
            {"tran.thi.b@gmail.com", "Trần Thị B", "0912345678"},
            {"le.van.c@gmail.com", "Lê Văn C", "0923456789"},
            {"pham.thi.d@gmail.com", "Phạm Thị D", "0934567890"},
            {"hoang.van.e@gmail.com", "Hoàng Văn E", "0945678901"},
            {"vo.thi.f@gmail.com", "Võ Thị F", "0956789012"},
            {"dang.van.g@gmail.com", "Đặng Văn G", "0967890123"},
            {"bui.thi.h@gmail.com", "Bùi Thị H", "0978901234"},
            {"do.van.i@gmail.com", "Đỗ Văn I", "0989012345"},
            {"ngo.thi.k@gmail.com", "Ngô Thị K", "0990123456"}
        };

        for (String[] data : userData) {
            if (!userRepository.existsByEmail(data[0])) {
                User user = User.builder()
                    .email(data[0])
                    .password(passwordEncoder.encode("123456"))
                    .fullName(data[1])
                    .phoneNumber(data[2])
                    .role(User.Role.CUSTOMER)
                    .createdAt(LocalDateTime.now())
                    .build();
                users.add(userRepository.save(user));
            } else {
                users.add(userRepository.findByEmail(data[0]).orElseThrow());
            }
        }
        return users;
    }

    private void createSampleReviews(List<User> users) {
        // Reviews cho Product 1 (Netflix Premium)
        addReview(1L, users.get(0), 5, "Tài khoản Netflix Premium rất tốt! Chất lượng 4K mượt mà, không bị giật lag. Shop hỗ trợ nhiệt tình, giao tài khoản nhanh chóng. Đã dùng được 2 tháng rồi vẫn ổn định. Rất đáng tiền!");
        addReview(1L, users.get(1), 5, "Mình đã mua nhiều tài khoản Netflix ở các shop khác nhưng shop này là ổn định nhất. Giá cả hợp lý, tài khoản xài ngon lành. Sẽ ủng hộ dài dài!");
        addReview(1L, users.get(2), 4, "Tài khoản chạy tốt, xem phim 4K rất nét. Trừ 1 sao vì lúc đầu bị lỗi đăng nhập nhưng shop support nhanh và đổi tài khoản mới ngay. Overall rất hài lòng!");
        addReview(1L, users.get(3), 5, "Cực kỳ hài lòng! Tài khoản Premium xài mượt, có thể xem trên nhiều thiết bị. Giá rẻ hơn mua chính hãng rất nhiều. Recommend cho mọi người!");
        addReview(1L, users.get(4), 5, "Shop uy tín, giao hàng nhanh. Tài khoản Netflix dùng rất ổn định, chưa gặp vấn đề gì. Sẽ quay lại mua tiếp khi hết hạn!");
        addReview(1L, users.get(5), 4, "Tài khoản tốt, giá hợp lý. Chỉ có điều đôi khi bị đổi mật khẩu nhưng shop hỗ trợ đổi lại nhanh. Nhìn chung ok!");
        addReview(1L, users.get(6), 5, "Xài Netflix Premium của shop này đã 3 tháng rồi, rất ổn định. Chất lượng phim 4K đẹp, không bị giật. Giá cả phải chăng. 5 sao!");
        addReview(1L, users.get(7), 5, "Tuyệt vời! Mình và gia đình xem Netflix mỗi tối, tài khoản chạy mượt mà không lỗi. Shop phục vụ tốt, nhiệt tình. Sẽ giới thiệu bạn bè!");

        // Reviews cho Product 2 (Spotify Premium)
        addReview(2L, users.get(1), 5, "Spotify Premium nghe nhạc chất lượng cao, không quảng cáo. Tài khoản xài ổn định, giá rẻ hơn mua chính hãng nhiều. Rất đáng mua!");
        addReview(2L, users.get(2), 5, "Mình là fan của Spotify, mua tài khoản Premium ở đây rất hài lòng. Nghe nhạc không giới hạn, chất lượng 320kbps cực đỉnh!");
        addReview(2L, users.get(4), 4, "Tài khoản Spotify tốt, nghe nhạc mượt. Trừ 1 sao vì có lần bị logout nhưng shop hỗ trợ nhanh. Nhìn chung ok!");
        addReview(2L, users.get(6), 5, "Cực kỳ hài lòng với Spotify Premium! Nghe nhạc offline tiện lợi, không quảng cáo làm phiền. Giá cả hợp lý. Recommend!");
        addReview(2L, users.get(8), 5, "Shop uy tín, giao tài khoản nhanh. Spotify Premium xài ngon, nghe nhạc chất lượng cao. Sẽ mua tiếp!");
        addReview(2L, users.get(9), 5, "Tuyệt vời! Nghe nhạc Spotify Premium mỗi ngày, tài khoản rất ổn định. Giá rẻ mà chất lượng tốt. 5 sao!");

        // Reviews cho Product 3 (Microsoft Office 365)
        addReview(3L, users.get(0), 5, "Office 365 xài rất tốt! Đầy đủ tính năng Word, Excel, PowerPoint. Kích hoạt dễ dàng, hỗ trợ nhiệt tình. Rất đáng tiền cho dân văn phòng!");
        addReview(3L, users.get(3), 5, "Mình làm việc với Office mỗi ngày, mua key ở đây rất hài lòng. Kích hoạt thành công, xài ổn định. Giá rẻ hơn mua chính hãng rất nhiều!");
        addReview(3L, users.get(5), 4, "Office 365 chạy tốt, đầy đủ tính năng. Trừ 1 sao vì lúc đầu hơi khó kích hoạt nhưng shop hướng dẫn chi tiết. Overall tốt!");
        addReview(3L, users.get(7), 5, "Tuyệt vời! Office 365 kích hoạt thành công, xài mượt mà. Có cả OneDrive 1TB rất tiện. Giá cả phải chăng. Recommend!");
        addReview(3L, users.get(9), 5, "Shop uy tín, giao key nhanh. Office 365 xài ngon, đầy đủ tính năng. Sẽ mua tiếp khi hết hạn!");

        // Reviews cho các sản phẩm khác (4-10)
        addMoreReviews(users);
    }

    private void addMoreReviews(List<User> users) {
        // Product 4 - Adobe Creative Cloud
        addReview(4L, users.get(1), 5, "Adobe Creative Cloud đầy đủ tính năng Photoshop, Illustrator, Premiere Pro. Kích hoạt dễ dàng, xài ổn định. Rất đáng tiền cho dân design!");
        addReview(4L, users.get(2), 5, "Mình làm designer, mua Adobe CC ở đây rất hài lòng. Đầy đủ app, chạy mượt mà. Giá rẻ hơn mua chính hãng nhiều lắm!");
        addReview(4L, users.get(4), 4, "Adobe CC chạy tốt, đầy đủ tính năng. Trừ 1 sao vì lúc đầu hơi khó cài đặt nhưng shop hỗ trợ nhiệt tình. Nhìn chung ok!");
        addReview(4L, users.get(6), 5, "Cực kỳ hài lòng! Adobe Creative Cloud xài ngon, render nhanh. Giá cả hợp lý cho dân làm content. Recommend!");
        addReview(4L, users.get(8), 5, "Shop uy tín, hỗ trợ tốt. Adobe CC kích hoạt thành công, xài ổn định. Sẽ ủng hộ dài dài!");

        // Product 5 - Google One
        addReview(5L, users.get(0), 5, "Google One 2TB rất tiện lợi! Backup ảnh, video thoải mái. Đồng bộ nhanh, ổn định. Giá rẻ hơn mua chính hãng. Rất hài lòng!");
        addReview(5L, users.get(3), 5, "Mình cần lưu trữ nhiều dữ liệu, mua Google One ở đây rất ok. 2TB xài thoải mái, tốc độ upload nhanh. Recommend!");
        addReview(5L, users.get(5), 4, "Google One chạy tốt, dung lượng lớn. Trừ 1 sao vì lúc đầu hơi lâu kích hoạt nhưng sau đó xài ổn. Overall tốt!");
        addReview(5L, users.get(7), 5, "Tuyệt vời! Google One 2TB backup ảnh video thoải mái. Giá cả phải chăng. Shop hỗ trợ tốt. 5 sao!");

        // Product 6 - Canva Pro
        addReview(6L, users.get(1), 5, "Canva Pro rất tiện lợi cho việc thiết kế! Đầy đủ template, font chữ, hình ảnh premium. Giá rẻ mà chất lượng tốt. Rất đáng mua!");
        addReview(6L, users.get(2), 5, "Mình làm marketing, Canva Pro là công cụ không thể thiếu. Mua ở đây giá tốt, tài khoản xài ổn định. Recommend!");
        addReview(6L, users.get(4), 5, "Canva Pro xài ngon, thiết kế nhanh gọn. Đầy đủ tính năng premium. Giá cả hợp lý. 5 sao!");
        addReview(6L, users.get(6), 4, "Tài khoản Canva Pro tốt, nhiều template đẹp. Trừ 1 sao vì có lần bị logout nhưng shop hỗ trợ nhanh. Overall ok!");
        addReview(6L, users.get(8), 5, "Shop uy tín, giao tài khoản nhanh. Canva Pro xài mượt, thiết kế chuyên nghiệp. Sẽ mua tiếp!");

        // Product 7 - ChatGPT Plus
        addReview(7L, users.get(0), 5, "ChatGPT Plus rất hữu ích! Trả lời nhanh, chính xác, không bị giới hạn. Giá rẻ hơn mua chính hãng. Rất đáng tiền!");
        addReview(7L, users.get(3), 5, "Mình dùng ChatGPT Plus mỗi ngày cho công việc. Mua ở đây giá tốt, tài khoản xài ổn định. Recommend!");
        addReview(7L, users.get(5), 5, "ChatGPT Plus xài ngon, hỗ trợ công việc hiệu quả. Giá cả hợp lý. Shop uy tín. 5 sao!");
        addReview(7L, users.get(7), 4, "Tài khoản ChatGPT Plus tốt, trả lời nhanh. Trừ 1 sao vì có lần bị lỗi nhưng shop hỗ trợ kịp thời. Overall ok!");

        // Product 8 - NordVPN
        addReview(8L, users.get(1), 5, "NordVPN rất tốt! Kết nối nhanh, ổn định, bảo mật cao. Giá rẻ hơn mua chính hãng nhiều. Rất hài lòng!");
        addReview(8L, users.get(2), 5, "Mình cần VPN để làm việc, NordVPN ở đây rất ok. Tốc độ nhanh, nhiều server. Recommend!");
        addReview(8L, users.get(4), 4, "NordVPN chạy tốt, bảo mật tốt. Trừ 1 sao vì đôi khi hơi chậm nhưng overall ổn. Giá cả hợp lý!");
        addReview(8L, users.get(6), 5, "Tuyệt vời! NordVPN kết nối nhanh, ổn định. Shop hỗ trợ tốt. Giá rẻ. 5 sao!");

        // Product 9 - Steam Games
        addReview(9L, users.get(0), 5, "Mua game Steam ở đây giá rẻ hơn nhiều! Giao key nhanh, kích hoạt thành công. Shop uy tín. Rất đáng mua!");
        addReview(9L, users.get(3), 5, "Mình là game thủ, mua game ở shop này rất hài lòng. Giá tốt, giao key nhanh. Sẽ ủng hộ dài dài!");
        addReview(9L, users.get(5), 5, "Game Steam kích hoạt thành công, chơi mượt mà. Giá rẻ hơn mua trên Steam. Recommend!");
        addReview(9L, users.get(7), 4, "Mua game tốt, giá ok. Trừ 1 sao vì lúc đầu hơi lâu giao key nhưng sau đó xài ổn. Overall tốt!");
        addReview(9L, users.get(9), 5, "Shop uy tín, giao key nhanh. Game Steam xài ngon. Giá cả phải chăng. 5 sao!");

        // Product 10 - YouTube Premium
        addReview(10L, users.get(1), 5, "YouTube Premium xem video không quảng cáo rất sướng! Nghe nhạc offline tiện lợi. Giá rẻ hơn mua chính hãng. Rất hài lòng!");
        addReview(10L, users.get(2), 5, "Mình xem YouTube mỗi ngày, mua Premium ở đây rất ok. Không quảng cáo, xem thoải mái. Recommend!");
        addReview(10L, users.get(4), 4, "YouTube Premium tốt, xem video mượt. Trừ 1 sao vì có lần bị logout nhưng shop hỗ trợ nhanh. Overall ok!");
        addReview(10L, users.get(6), 5, "Tuyệt vời! YouTube Premium xem video không quảng cáo, nghe nhạc offline. Giá cả hợp lý. 5 sao!");
        addReview(10L, users.get(8), 5, "Shop uy tín, giao tài khoản nhanh. YouTube Premium xài ngon. Sẽ mua tiếp!");
    }

    private void addReview(Long productId, User user, int stars, String content) {
        Review review = Review.builder()
            .productId(productId)
            .user(user)
            .stars(stars)
            .content(content)
            .createdAt(LocalDateTime.now().minusDays((long) (Math.random() * 30)))
            .build();
        reviewRepository.save(review);
    }
}
