# Hướng dẫn cấu hình PayOS Payment Gateway

## Bước 1: Đăng ký tài khoản PayOS

### Môi trường Test (Sandbox)
1. Truy cập: https://my.payos.vn hoặc https://payos.vn
2. Đăng ký tài khoản test/sandbox
3. Xác thực email

### Môi trường Production (Tiền thật)
1. Truy cập: https://my.payos.vn
2. Đăng ký tài khoản doanh nghiệp
3. Cung cấp:
   - Thông tin doanh nghiệp/cá nhân
   - Số tài khoản ngân hàng nhận tiền
   - Giấy tờ xác minh (CMND/CCCD, giấy phép kinh doanh)

## Bước 2: Lấy API Credentials

1. Đăng nhập vào https://my.payos.vn
2. Vào mục **Cài đặt** → **API Keys**
3. Lấy 3 thông tin sau:
   - **Client ID**: Mã định danh ứng dụng
   - **API Key**: Khóa API để gọi PayOS API
   - **Checksum Key**: Khóa để tạo chữ ký bảo mật

## Bước 3: Cấu hình Backend

Mở file `src/main/resources/application.properties` và cập nhật:

```properties
# PayOS Configuration
payos.client-id=YOUR_CLIENT_ID_HERE
payos.api-key=YOUR_API_KEY_HERE
payos.checksum-key=YOUR_CHECKSUM_KEY_HERE
payos.return-url=http://localhost:5173/payment/success
payos.cancel-url=http://localhost:5173/payment/cancel
```

**Thay thế:**
- `YOUR_CLIENT_ID_HERE` → Client ID từ PayOS
- `YOUR_API_KEY_HERE` → API Key từ PayOS
- `YOUR_CHECKSUM_KEY_HERE` → Checksum Key từ PayOS

## Bước 4: Restart Backend

```bash
# Dừng backend hiện tại (Ctrl+C)
# Chạy lại backend
./mvnw spring-boot:run
```

## Bước 5: Test thanh toán

1. Thêm sản phẩm vào giỏ hàng
2. Vào trang Checkout
3. Chọn "PayOS - Chuyển khoản ngân hàng"
4. Nhập thông tin và click "THANH TOÁN PAYOS"
5. Quét mã QR để thanh toán

### Môi trường Test
- Sử dụng app ngân hàng test hoặc công cụ test của PayOS
- Không mất tiền thật

### Môi trường Production
- Quét mã QR bằng app ngân hàng thật
- Tiền sẽ về tài khoản ngân hàng đã đăng ký với PayOS
- PayOS trừ phí giao dịch (1-3%)

## Bước 6: Setup Webhook (Optional - cho Production)

Webhook giúp PayOS thông báo kết quả thanh toán về server của bạn.

### Development (localhost)
1. Cài đặt ngrok: https://ngrok.com/download
2. Chạy ngrok:
   ```bash
   ngrok http 8080
   ```
3. Copy URL ngrok (VD: `https://abc123.ngrok.io`)
4. Vào PayOS dashboard → Webhook Settings
5. Nhập webhook URL: `https://abc123.ngrok.io/api/payment/webhook`

### Production
1. Deploy backend lên server có domain/IP public
2. Vào PayOS dashboard → Webhook Settings
3. Nhập webhook URL: `https://your-domain.com/api/payment/webhook`

## Lưu ý bảo mật

⚠️ **QUAN TRỌNG:**
- **KHÔNG** commit file `application.properties` có chứa credentials lên Git
- Sử dụng environment variables cho production:
  ```bash
  export PAYOS_CLIENT_ID=your_client_id
  export PAYOS_API_KEY=your_api_key
  export PAYOS_CHECKSUM_KEY=your_checksum_key
  ```
- Thêm vào `.gitignore`:
  ```
  application.properties
  application-prod.properties
  ```

## Troubleshooting

### Lỗi: "Có lỗi xảy ra khi tạo thanh toán"
- Kiểm tra credentials đã cấu hình đúng chưa
- Kiểm tra backend đang chạy trên port 8080
- Xem log backend để biết lỗi cụ thể

### Lỗi: "Cannot find symbol: class PayOS"
- Chạy `./mvnw clean install` để download dependencies
- Restart backend

### Webhook không hoạt động
- Kiểm tra URL webhook đã đăng ký đúng chưa
- Kiểm tra server có thể truy cập từ internet không
- Xem log PayOS dashboard để biết lỗi webhook

## Tài liệu tham khảo

- PayOS Documentation: https://payos.vn/docs
- PayOS API Reference: https://payos.vn/docs/api
- PayOS Java SDK: https://github.com/payOSHQ/payos-lib-java
- PayOS Demo Spring Boot: https://github.com/payOSHQ/payos-demo-java-spring
