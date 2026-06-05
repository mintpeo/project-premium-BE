-- =====================================================
-- SEED DATA FOR REVIEWS
-- Tạo dữ liệu mẫu cho đánh giá sản phẩm
-- =====================================================

-- Tạo một số user mẫu để làm reviewer (nếu chưa có)
-- Password: "123456" đã được hash bằng BCrypt
INSERT INTO users (email, password, full_name, phone_number, role, created_at) VALUES
('nguyen.van.a@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', 'Nguyễn Văn A', '0901234567', 'CUSTOMER', '2024-01-15 10:30:00'),
('tran.thi.b@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', 'Trần Thị B', '0912345678', 'CUSTOMER', '2024-01-20 14:20:00'),
('le.van.c@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', 'Lê Văn C', '0923456789', 'CUSTOMER', '2024-02-01 09:15:00'),
('pham.thi.d@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', 'Phạm Thị D', '0934567890', 'CUSTOMER', '2024-02-10 16:45:00'),
('hoang.van.e@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', 'Hoàng Văn E', '0945678901', 'CUSTOMER', '2024-02-15 11:00:00'),
('vo.thi.f@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', 'Võ Thị F', '0956789012', 'CUSTOMER', '2024-03-01 13:30:00'),
('dang.van.g@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', 'Đặng Văn G', '0967890123', 'CUSTOMER', '2024-03-05 08:20:00'),
('bui.thi.h@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', 'Bùi Thị H', '0978901234', 'CUSTOMER', '2024-03-10 15:10:00'),
('do.van.i@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', 'Đỗ Văn I', '0989012345', 'CUSTOMER', '2024-03-15 10:50:00'),
('ngo.thi.k@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', 'Ngô Thị K', '0990123456', 'CUSTOMER', '2024-03-20 12:40:00')
ON DUPLICATE KEY UPDATE email=email;

-- =====================================================
-- REVIEWS CHO SẢN PHẨM ID = 1 (Netflix Premium)
-- =====================================================
INSERT INTO reviews (product_id, user_id, stars, content, created_at) VALUES
(1, 1, 5, 'Tài khoản Netflix Premium rất tốt! Chất lượng 4K mượt mà, không bị giật lag. Shop hỗ trợ nhiệt tình, giao tài khoản nhanh chóng. Đã dùng được 2 tháng rồi vẫn ổn định. Rất đáng tiền!', '2024-03-25 14:30:00'),
(1, 2, 5, 'Mình đã mua nhiều tài khoản Netflix ở các shop khác nhưng shop này là ổn định nhất. Giá cả hợp lý, tài khoản xài ngon lành. Sẽ ủng hộ dài dài!', '2024-03-26 09:15:00'),
(1, 3, 4, 'Tài khoản chạy tốt, xem phim 4K rất nét. Trừ 1 sao vì lúc đầu bị lỗi đăng nhập nhưng shop support nhanh và đổi tài khoản mới ngay. Overall rất hài lòng!', '2024-03-27 16:20:00'),
(1, 4, 5, 'Cực kỳ hài lòng! Tài khoản Premium xài mượt, có thể xem trên nhiều thiết bị. Giá rẻ hơn mua chính hãng rất nhiều. Recommend cho mọi người!', '2024-03-28 11:45:00'),
(1, 5, 5, 'Shop uy tín, giao hàng nhanh. Tài khoản Netflix dùng rất ổn định, chưa gặp vấn đề gì. Sẽ quay lại mua tiếp khi hết hạn!', '2024-03-29 13:10:00'),
(1, 6, 4, 'Tài khoản tốt, giá hợp lý. Chỉ có điều đôi khi bị đổi mật khẩu nhưng shop hỗ trợ đổi lại nhanh. Nhìn chung ok!', '2024-03-30 10:30:00'),
(1, 7, 5, 'Xài Netflix Premium của shop này đã 3 tháng rồi, rất ổn định. Chất lượng phim 4K đẹp, không bị giật. Giá cả phải chăng. 5 sao!', '2024-04-01 15:50:00'),
(1, 8, 5, 'Tuyệt vời! Mình và gia đình xem Netflix mỗi tối, tài khoản chạy mượt mà không lỗi. Shop phục vụ tốt, nhiệt tình. Sẽ giới thiệu bạn bè!', '2024-04-02 19:20:00'),

-- =====================================================
-- REVIEWS CHO SẢN PHẨM ID = 2 (Spotify Premium)
-- =====================================================
(2, 2, 5, 'Spotify Premium nghe nhạc chất lượng cao, không quảng cáo. Tài khoản xài ổn định, giá rẻ hơn mua chính hãng nhiều. Rất đáng mua!', '2024-03-25 10:15:00'),
(2, 3, 5, 'Mình là fan của Spotify, mua tài khoản Premium ở đây rất hài lòng. Nghe nhạc không giới hạn, chất lượng 320kbps cực đỉnh!', '2024-03-26 14:30:00'),
(2, 5, 4, 'Tài khoản Spotify tốt, nghe nhạc mượt. Trừ 1 sao vì có lần bị logout nhưng shop hỗ trợ nhanh. Nhìn chung ok!', '2024-03-27 11:20:00'),
(2, 7, 5, 'Cực kỳ hài lòng với Spotify Premium! Nghe nhạc offline tiện lợi, không quảng cáo làm phiền. Giá cả hợp lý. Recommend!', '2024-03-28 16:45:00'),
(2, 9, 5, 'Shop uy tín, giao tài khoản nhanh. Spotify Premium xài ngon, nghe nhạc chất lượng cao. Sẽ mua tiếp!', '2024-03-29 09:30:00'),
(2, 10, 5, 'Tuyệt vời! Nghe nhạc Spotify Premium mỗi ngày, tài khoản rất ổn định. Giá rẻ mà chất lượng tốt. 5 sao!', '2024-03-30 13:15:00'),

-- =====================================================
-- REVIEWS CHO SẢN PHẨM ID = 3 (Microsoft Office 365)
-- =====================================================
(3, 1, 5, 'Office 365 xài rất tốt! Đầy đủ tính năng Word, Excel, PowerPoint. Kích hoạt dễ dàng, hỗ trợ nhiệt tình. Rất đáng tiền cho dân văn phòng!', '2024-03-24 09:20:00'),
(3, 4, 5, 'Mình làm việc với Office mỗi ngày, mua key ở đây rất hài lòng. Kích hoạt thành công, xài ổn định. Giá rẻ hơn mua chính hãng rất nhiều!', '2024-03-25 14:10:00'),
(3, 6, 4, 'Office 365 chạy tốt, đầy đủ tính năng. Trừ 1 sao vì lúc đầu hơi khó kích hoạt nhưng shop hướng dẫn chi tiết. Overall tốt!', '2024-03-26 10:45:00'),
(3, 8, 5, 'Tuyệt vời! Office 365 kích hoạt thành công, xài mượt mà. Có cả OneDrive 1TB rất tiện. Giá cả phải chăng. Recommend!', '2024-03-27 15:30:00'),
(3, 10, 5, 'Shop uy tín, giao key nhanh. Office 365 xài ngon, đầy đủ tính năng. Sẽ mua tiếp khi hết hạn!', '2024-03-28 11:20:00'),

-- =====================================================
-- REVIEWS CHO SẢN PHẨM ID = 4 (Adobe Creative Cloud)
-- =====================================================
(4, 2, 5, 'Adobe Creative Cloud đầy đủ tính năng Photoshop, Illustrator, Premiere Pro. Kích hoạt dễ dàng, xài ổn định. Rất đáng tiền cho dân design!', '2024-03-23 10:30:00'),
(4, 3, 5, 'Mình làm designer, mua Adobe CC ở đây rất hài lòng. Đầy đủ app, chạy mượt mà. Giá rẻ hơn mua chính hãng nhiều lắm!', '2024-03-24 14:20:00'),
(4, 5, 4, 'Adobe CC chạy tốt, đầy đủ tính năng. Trừ 1 sao vì lúc đầu hơi khó cài đặt nhưng shop hỗ trợ nhiệt tình. Nhìn chung ok!', '2024-03-25 09:15:00'),
(4, 7, 5, 'Cực kỳ hài lòng! Adobe Creative Cloud xài ngon, render nhanh. Giá cả hợp lý cho dân làm content. Recommend!', '2024-03-26 16:40:00'),
(4, 9, 5, 'Shop uy tín, hỗ trợ tốt. Adobe CC kích hoạt thành công, xài ổn định. Sẽ ủng hộ dài dài!', '2024-03-27 11:50:00'),

-- =====================================================
-- REVIEWS CHO SẢN PHẨM ID = 5 (Google One)
-- =====================================================
(5, 1, 5, 'Google One 2TB rất tiện lợi! Backup ảnh, video thoải mái. Đồng bộ nhanh, ổn định. Giá rẻ hơn mua chính hãng. Rất hài lòng!', '2024-03-22 13:25:00'),
(5, 4, 5, 'Mình cần lưu trữ nhiều dữ liệu, mua Google One ở đây rất ok. 2TB xài thoải mái, tốc độ upload nhanh. Recommend!', '2024-03-23 10:40:00'),
(5, 6, 4, 'Google One chạy tốt, dung lượng lớn. Trừ 1 sao vì lúc đầu hơi lâu kích hoạt nhưng sau đó xài ổn. Overall tốt!', '2024-03-24 15:15:00'),
(5, 8, 5, 'Tuyệt vời! Google One 2TB backup ảnh video thoải mái. Giá cả phải chăng. Shop hỗ trợ tốt. 5 sao!', '2024-03-25 09:30:00'),

-- =====================================================
-- REVIEWS CHO SẢN PHẨM ID = 6 (Canva Pro)
-- =====================================================
(6, 2, 5, 'Canva Pro rất tiện lợi cho việc thiết kế! Đầy đủ template, font chữ, hình ảnh premium. Giá rẻ mà chất lượng tốt. Rất đáng mua!', '2024-03-21 11:20:00'),
(6, 3, 5, 'Mình làm marketing, Canva Pro là công cụ không thể thiếu. Mua ở đây giá tốt, tài khoản xài ổn định. Recommend!', '2024-03-22 14:35:00'),
(6, 5, 5, 'Canva Pro xài ngon, thiết kế nhanh gọn. Đầy đủ tính năng premium. Giá cả hợp lý. 5 sao!', '2024-03-23 10:10:00'),
(6, 7, 4, 'Tài khoản Canva Pro tốt, nhiều template đẹp. Trừ 1 sao vì có lần bị logout nhưng shop hỗ trợ nhanh. Overall ok!', '2024-03-24 16:25:00'),
(6, 9, 5, 'Shop uy tín, giao tài khoản nhanh. Canva Pro xài mượt, thiết kế chuyên nghiệp. Sẽ mua tiếp!', '2024-03-25 13:40:00'),

-- =====================================================
-- REVIEWS CHO SẢN PHẨM ID = 7 (ChatGPT Plus)
-- =====================================================
(7, 1, 5, 'ChatGPT Plus rất hữu ích! Trả lời nhanh, chính xác, không bị giới hạn. Giá rẻ hơn mua chính hãng. Rất đáng tiền!', '2024-03-20 09:15:00'),
(7, 4, 5, 'Mình dùng ChatGPT Plus mỗi ngày cho công việc. Mua ở đây giá tốt, tài khoản xài ổn định. Recommend!', '2024-03-21 14:20:00'),
(7, 6, 5, 'ChatGPT Plus xài ngon, hỗ trợ công việc hiệu quả. Giá cả hợp lý. Shop uy tín. 5 sao!', '2024-03-22 10:30:00'),
(7, 8, 4, 'Tài khoản ChatGPT Plus tốt, trả lời nhanh. Trừ 1 sao vì có lần bị lỗi nhưng shop hỗ trợ kịp thời. Overall ok!', '2024-03-23 15:45:00'),

-- =====================================================
-- REVIEWS CHO SẢN PHẨM ID = 8 (NordVPN)
-- =====================================================
(8, 2, 5, 'NordVPN rất tốt! Kết nối nhanh, ổn định, bảo mật cao. Giá rẻ hơn mua chính hãng nhiều. Rất hài lòng!', '2024-03-19 11:25:00'),
(8, 3, 5, 'Mình cần VPN để làm việc, NordVPN ở đây rất ok. Tốc độ nhanh, nhiều server. Recommend!', '2024-03-20 14:40:00'),
(8, 5, 4, 'NordVPN chạy tốt, bảo mật tốt. Trừ 1 sao vì đôi khi hơi chậm nhưng overall ổn. Giá cả hợp lý!', '2024-03-21 10:15:00'),
(8, 7, 5, 'Tuyệt vời! NordVPN kết nối nhanh, ổn định. Shop hỗ trợ tốt. Giá rẻ. 5 sao!', '2024-03-22 16:30:00'),

-- =====================================================
-- REVIEWS CHO SẢN PHẨM ID = 9 (Steam Games)
-- =====================================================
(9, 1, 5, 'Mua game Steam ở đây giá rẻ hơn nhiều! Giao key nhanh, kích hoạt thành công. Shop uy tín. Rất đáng mua!', '2024-03-18 13:20:00'),
(9, 4, 5, 'Mình là game thủ, mua game ở shop này rất hài lòng. Giá tốt, giao key nhanh. Sẽ ủng hộ dài dài!', '2024-03-19 10:35:00'),
(9, 6, 5, 'Game Steam kích hoạt thành công, chơi mượt mà. Giá rẻ hơn mua trên Steam. Recommend!', '2024-03-20 15:10:00'),
(9, 8, 4, 'Mua game tốt, giá ok. Trừ 1 sao vì lúc đầu hơi lâu giao key nhưng sau đó xài ổn. Overall tốt!', '2024-03-21 11:45:00'),
(9, 10, 5, 'Shop uy tín, giao key nhanh. Game Steam xài ngon. Giá cả phải chăng. 5 sao!', '2024-03-22 14:25:00'),

-- =====================================================
-- REVIEWS CHO SẢN PHẨM ID = 10 (YouTube Premium)
-- =====================================================
(10, 2, 5, 'YouTube Premium xem video không quảng cáo rất sướng! Nghe nhạc offline tiện lợi. Giá rẻ hơn mua chính hãng. Rất hài lòng!', '2024-03-17 10:30:00'),
(10, 3, 5, 'Mình xem YouTube mỗi ngày, mua Premium ở đây rất ok. Không quảng cáo, xem thoải mái. Recommend!', '2024-03-18 14:15:00'),
(10, 5, 4, 'YouTube Premium tốt, xem video mượt. Trừ 1 sao vì có lần bị logout nhưng shop hỗ trợ nhanh. Overall ok!', '2024-03-19 09:40:00'),
(10, 7, 5, 'Tuyệt vời! YouTube Premium xem video không quảng cáo, nghe nhạc offline. Giá cả hợp lý. 5 sao!', '2024-03-20 16:20:00'),
(10, 9, 5, 'Shop uy tín, giao tài khoản nhanh. YouTube Premium xài ngon. Sẽ mua tiếp!', '2024-03-21 11:55:00');

-- =====================================================
-- CẬP NHẬT RATING TRUNG BÌNH CHO CÁC SẢN PHẨM
-- =====================================================
UPDATE products SET rating = (
    SELECT AVG(stars) FROM reviews WHERE reviews.product_id = products.id
) WHERE id IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
