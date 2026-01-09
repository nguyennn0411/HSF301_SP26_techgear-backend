# TechGear Store - Backend (API)

## 1. Giới thiệu (Overview)
Đây là phần Server-side của hệ thống thương mại điện tử **TechGear Store** (Chuyên kinh doanh linh kiện máy tính).
Dự án cung cấp các RESTful API để phục vụ cho Client (ReactJS), bao gồm các chức năng:
- **Authentication:** Đăng ký, Đăng nhập, Phân quyền (Admin/User).
- **Product Management:** CRUD sản phẩm, danh mục.
- **Order Processing:** Quản lý giỏ hàng và đơn hàng.

## 2. Công nghệ sử dụng (Tech Stack)
- **Language:** Java 17+
- **Framework:** Spring Boot 3.x
- **Database:** Microsoft SQL Server
- **ORM:** Spring Data JPA (Hibernate)
- **Security:** Spring Security & JWT (Json Web Token)

## 3. Yêu cầu hệ thống (Prerequisites)
Trước khi chạy, đảm bảo máy tính đã cài đặt:
- JDK 17 hoặc 21.
- Maven (đã có sẵn `mvnw` trong project).
- SQL Server (đang chạy và enable TCP/IP cổng 1433).

## 4. Hướng dẫn cài đặt & Chạy (Installation & Run)

### Bước 1: Cấu hình Database
1. Mở SQL Server Management Studio (SSMS).
2. Tạo một Database rỗng tên là: `TechGearDB`.
3. Mở file `src/main/resources/application.properties` trong project.
4. Cập nhật `username` và `password` của SQL Server máy bạn:
   ```properties
   spring.datasource.username=sa
   spring.datasource.password=YOUR_PASSWORD_HERE

### Bước 2: Chạy ứng dụng
Mở Terminal tại thư mục gốc của dự án và chạy lệnh:
1. Với Windows:
./mvnw spring-boot:run
2. Với Mac/Linux:
chmod +x mvnw
./mvnw spring-boot:run
### Bước 3: Kiểm tra
Sau khi chạy thành công, Server sẽ hoạt động tại: http://localhost:8080. Bạn có thể test API đơn giản (nếu có) hoặc chờ kết nối từ Frontend.