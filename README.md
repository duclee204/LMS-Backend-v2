# Phần 1: Lập kế hoạch và thiết kế kiểm thử

## 1. Chức năng kiểm thử đã chọn
- Quản lý khóa học (Course Management)
- Học video (Video Learning)
- Tham gia khóa học (Course Enrollment)

### 1.1. Mục tiêu kiểm thử
- Đảm bảo các chức năng trên hoạt động đúng theo yêu cầu nghiệp vụ.
- Phát hiện lỗi chức năng, giao diện và luồng xử lý.
- Đảm bảo tính ổn định khi tích hợp với các module khác.

### 1.2. Loại kiểm thử áp dụng
- **Kiểm thử chức năng (Functional Testing):** Đảm bảo các chức năng chính thực hiện đúng yêu cầu.
- **Kiểm thử tích hợp (Integration Testing):** Đảm bảo các module phối hợp đúng khi tích hợp.
- **Kiểm thử hồi quy (Regression Testing):** Đảm bảo các chức năng cũ không bị ảnh hưởng khi cập nhật mới.
- **Kiểm thử đơn vị (Unit Testing):** Kiểm tra logic từng hàm/module nhỏ.

**Lý do chọn:** Các loại kiểm thử này giúp phát hiện lỗi ở nhiều cấp độ, đảm bảo chất lượng tổng thể của hệ thống.

### 1.3. Thiết kế ca kiểm thử (Test Case)
| ID   | Tên ca kiểm thử                | Bước thực hiện                                                                 | Dữ liệu vào                | Kết quả mong đợi                                  |
|------|-------------------------------|-------------------------------------------------------------------------------|----------------------------|---------------------------------------------------|
| TC01 | Tạo khóa học mới              | 1. Đăng nhập admin\n2. Vào trang quản lý khóa học\n3. Nhấn "Tạo mới"\n4. Nhập thông tin\n5. Lưu | Thông tin khóa học mới     | Khóa học được tạo thành công                      |
| TC02 | Sửa thông tin khóa học        | 1. Đăng nhập admin\n2. Chọn khóa học\n3. Nhấn "Sửa"\n4. Thay đổi thông tin\n5. Lưu | Thông tin sửa đổi          | Thông tin khóa học được cập nhật                  |
| TC03 | Xóa khóa học                  | 1. Đăng nhập admin\n2. Chọn khóa học\n3. Nhấn "Xóa"\n4. Xác nhận                | ID khóa học                | Khóa học bị xóa khỏi hệ thống                     |
| TC04 | Xem danh sách khóa học        | 1. Đăng nhập\n2. Vào trang danh sách khóa học                                 | -                          | Hiển thị đúng danh sách khóa học                  |
| TC05 | Đăng ký tham gia khóa học     | 1. Đăng nhập user\n2. Chọn khóa học\n3. Nhấn "Đăng ký"                          | ID user, ID khóa học       | User được thêm vào danh sách học viên             |
| TC06 | Học video trong khóa học      | 1. Đăng nhập user\n2. Vào khóa học đã đăng ký\n3. Chọn video\n4. Xem video     | ID user, ID video          | Video phát đúng, ghi nhận tiến độ học             |
| TC07 | Đánh dấu hoàn thành video     | 1. Xem hết video\n2. Nhấn "Hoàn thành"                                        | ID user, ID video          | Trạng thái video chuyển sang "Đã hoàn thành"      |
| TC08 | Kiểm tra tiến độ học tập      | 1. Đăng nhập user\n2. Vào trang tiến độ học tập                                | ID user                    | Hiển thị đúng tiến độ các khóa học đã tham gia     |
| TC09 | Đăng xuất                     | 1. Đăng nhập\n2. Nhấn "Đăng xuất"                                             | -                          | Người dùng đăng xuất thành công                    |
| TC10 | Đăng nhập sai thông tin       | 1. Nhập sai tài khoản/mật khẩu\n2. Nhấn "Đăng nhập"                            | Tài khoản/mật khẩu sai     | Hiển thị thông báo lỗi đăng nhập                   |

> (Có thể bổ sung thêm test case tùy thực tế)
