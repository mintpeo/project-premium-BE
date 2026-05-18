# Hướng Dẫn Import Dữ Liệu Mẫu

## 📋 Mô Tả

File `seed_reviews.sql` chứa dữ liệu mẫu cho hệ thống đánh giá sản phẩm, bao gồm:

- **10 user mẫu** (CUSTOMER) với email và password đã hash
- **60+ reviews** cho 10 sản phẩm đầu tiên
- Đánh giá đa dạng từ 4-5 sao với nội dung thực tế
- Tự động cập nhật rating trung bình cho sản phẩm

## 🔐 Thông Tin Đăng Nhập User Mẫu

Tất cả user mẫu đều có password: **123456**

| Email | Họ Tên | Số Điện Thoại |
|-------|--------|---------------|
| nguyen.van.a@gmail.com | Nguyễn Văn A | 0901234567 |
| tran.thi.b@gmail.com | Trần Thị B | 0912345678 |
| le.van.c@gmail.com | Lê Văn C | 0923456789 |
| pham.thi.d@gmail.com | Phạm Thị D | 0934567890 |
| hoang.van.e@gmail.com | Hoàng Văn E | 0945678901 |
| vo.thi.f@gmail.com | Võ Thị F | 0956789012 |
| dang.van.g@gmail.com | Đặng Văn G | 0967890123 |
| bui.thi.h@gmail.com | Bùi Thị H | 0978901234 |
| do.van.i@gmail.com | Đỗ Văn I | 0989012345 |
| ngo.thi.k@gmail.com | Ngô Thị K | 0990123456 |

## 📊 Thống Kê Reviews

| Product ID | Tên Sản Phẩm (Ví dụ) | Số Reviews | Rating Trung Bình |
|------------|----------------------|------------|-------------------|
| 1 | Netflix Premium | 8 | ~4.75 |
| 2 | Spotify Premium | 6 | ~4.83 |
| 3 | Microsoft Office 365 | 5 | ~4.80 |
| 4 | Adobe Creative Cloud | 5 | ~4.80 |
| 5 | Google One | 4 | ~4.75 |
| 6 | Canva Pro | 5 | ~4.80 |
| 7 | ChatGPT Plus | 4 | ~4.75 |
| 8 | NordVPN | 4 | ~4.75 |
| 9 | Steam Games | 5 | ~4.80 |
| 10 | YouTube Premium | 5 | ~4.80 |

## 🚀 Cách Import Dữ Liệu

### Cách 1: Sử dụng MySQL Command Line

```bash
# Đăng nhập vào MySQL
mysql -u root -p

# Chọn database
USE tmdt;

# Import file SQL
SOURCE e:/TDMT/project-premium-BE/src/main/resources/data/seed_reviews.sql;
```

### Cách 2: Sử dụng MySQL Workbench

1. Mở MySQL Workbench
2. Kết nối đến database `tmdt`
3. File → Open SQL Script → Chọn file `seed_reviews.sql`
4. Click nút Execute (⚡) để chạy script

### Cách 3: Sử dụng Command Line trực tiếp

```bash
mysql -u root -p tmdt < e:/TDMT/project-premium-BE/src/main/resources/data/seed_reviews.sql
```

### Cách 4: Sử dụng PowerShell (Windows)

```powershell
# Di chuyển đến thư mục chứa file
cd e:\TDMT\project-premium-BE\src\main\resources\data

# Import vào database
Get-Content seed_reviews.sql | mysql -u root -p tmdt
```

## ⚠️ Lưu Ý

1. **Kiểm tra trước khi import**: Đảm bảo database `tmdt` đã tồn tại
2. **User trùng lặp**: Script sử dụng `ON DUPLICATE KEY UPDATE` để tránh lỗi nếu email đã tồn tại
3. **Product ID**: Đảm bảo các product với ID từ 1-10 đã tồn tại trong bảng `products`
4. **Backup**: Nên backup database trước khi import dữ liệu mẫu

## 🧹 Xóa Dữ Liệu Mẫu (Nếu Cần)

Nếu muốn xóa tất cả dữ liệu mẫu đã import:

```sql
-- Xóa tất cả reviews
DELETE FROM reviews WHERE user_id IN (
    SELECT id FROM users WHERE email LIKE '%@gmail.com' 
    AND email IN (
        'nguyen.van.a@gmail.com',
        'tran.thi.b@gmail.com',
        'le.van.c@gmail.com',
        'pham.thi.d@gmail.com',
        'hoang.van.e@gmail.com',
        'vo.thi.f@gmail.com',
        'dang.van.g@gmail.com',
        'bui.thi.h@gmail.com',
        'do.van.i@gmail.com',
        'ngo.thi.k@gmail.com'
    )
);

-- Xóa user mẫu (nếu muốn)
DELETE FROM users WHERE email IN (
    'nguyen.van.a@gmail.com',
    'tran.thi.b@gmail.com',
    'le.van.c@gmail.com',
    'pham.thi.d@gmail.com',
    'hoang.van.e@gmail.com',
    'vo.thi.f@gmail.com',
    'dang.van.g@gmail.com',
    'bui.thi.h@gmail.com',
    'do.van.i@gmail.com',
    'ngo.thi.k@gmail.com'
);

-- Reset rating về NULL hoặc 0
UPDATE products SET rating = NULL WHERE id IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
```

## 📝 Tùy Chỉnh

Bạn có thể tùy chỉnh file SQL để:
- Thêm nhiều reviews hơn
- Thay đổi nội dung đánh giá
- Thêm reviews cho các sản phẩm khác
- Điều chỉnh rating (số sao)
- Thay đổi thời gian tạo review

## 🔍 Kiểm Tra Sau Khi Import

```sql
-- Kiểm tra số lượng reviews
SELECT COUNT(*) as total_reviews FROM reviews;

-- Kiểm tra reviews theo sản phẩm
SELECT product_id, COUNT(*) as review_count, AVG(stars) as avg_rating 
FROM reviews 
GROUP BY product_id 
ORDER BY product_id;

-- Xem chi tiết reviews của một sản phẩm
SELECT r.id, r.stars, r.content, u.full_name, r.created_at
FROM reviews r
JOIN users u ON r.user_id = u.id
WHERE r.product_id = 1
ORDER BY r.created_at DESC;
```

## 📞 Hỗ Trợ

Nếu gặp vấn đề khi import dữ liệu, kiểm tra:
1. Kết nối database có đúng không
2. User MySQL có quyền INSERT không
3. Các bảng `users`, `products`, `reviews` đã tồn tại chưa
4. Foreign key constraints có đang bật không
