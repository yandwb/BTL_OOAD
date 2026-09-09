# BTL_OOAD
# Hệ thống Quản lý Trung tâm Gia sư (Tutor Center Management System)

Đây là kho lưu trữ mã nguồn cho Bài tập lớn môn **Phân tích và Thiết kế Hướng đối tượng (OOAD)**. Hệ thống được thiết kế nhằm số hóa và tối ưu hóa quy trình nghiệp vụ tại một trung tâm gia sư, từ việc quản lý hồ sơ, tiếp nhận yêu cầu mở lớp, đến việc ghép lớp, lập phiếu tính phí và báo cáo doanh thu.

## Công nghệ sử dụng

* **Ngôn ngữ lập trình:** Java (Sử dụng Java Swing / JavaFX cho giao diện GUI).
* **Cơ sở dữ liệu:** Microsoft SQL Server.
* **Kiến trúc phần mềm:** Client-Server 3 lớp, áp dụng chặt chẽ mẫu thiết kế **MVC (Model-View-Controller)** kết hợp **DAO (Data Access Object)**.

## Các tính năng chính

Hệ thống phân quyền chi tiết cho các đối tượng người dùng (Chủ trung tâm, Nhân viên, Gia sư) với các luồng nghiệp vụ cốt lõi sau:

* **Quản lý Xác thực:** Đăng nhập, đăng xuất, cấp lại mật khẩu xác thực qua OTP.


* **Quản lý Gia sư:** Thêm mới, tra cứu minh chứng bằng cấp, phê duyệt hồ sơ nhận lớp, khóa/xóa tài khoản.


* **Quản lý Phụ huynh:** Ghi nhận và lưu trữ thông tin, tra cứu lịch sử yêu cầu tìm gia sư của khách hàng.


* **Quản lý Lớp học:** Đăng tải yêu cầu tìm gia sư mới, cập nhật trạng thái lớp (Chờ giao, Đã giao, Hủy).


* **Quản lý Giao lớp:** Xử lý gia sư đăng ký ứng tuyển, duyệt giao lớp, lập phiếu tính công nợ (phí nhận lớp), xác nhận thanh toán và xử lý hoàn phí khi lớp hỏng.


* **Báo cáo Thống kê:** Thống kê doanh thu, hiệu suất đi dạy của gia sư, trích xuất báo cáo ra Excel/PDF dành riêng cho Chủ trung tâm.



## Cấu trúc thư mục (Gợi ý)

```text
BTL_OOAD/
├── src/
│   ├── controller/   # Điều phối luồng xử lý logic (LopHocController, GiaoLopController...)
│   ├── dao/          # Chứa các lớp thao tác trực tiếp với SQL Server bằng JDBC
│   ├── model/        # Các lớp thực thể POJO (TaiKhoan, GiaSu, LopHoc, PhieuGiaoLop...)
│   └── view/         # Mã nguồn giao diện người dùng UI (Views/Forms)
├── database/         # Chứa file script SQL (.sql) để tạo cơ sở dữ liệu
├── docs/             # Tài liệu báo cáo, Biểu đồ UML (Báo cáo.docx)
└── README.md

```

## Hướng dẫn cài đặt và chạy thử

1. **Clone repository:**
```bash
git clone https://github.com/yandwb/BTL_OOAD.git

```


2. **Khởi tạo Cơ sở dữ liệu:**
* Mở SQL Server Management Studio (SSMS).
* Chạy file script SQL nằm trong thư mục `database/` để khởi tạo CSDL và các bảng (Đã chuẩn hóa 3NF).




3. **Cấu hình Kết nối CSDL:**
* Mở project bằng IDE yêu thích (IntelliJ IDEA / Eclipse / NetBeans).
* Đảm bảo đã import thư viện JDBC (`mssql-jdbc.jar`) vào thư viện của project.
* Cập nhật chuỗi kết nối (Database URL, Username, Password) trong file cấu hình hoặc class kết nối thuộc thư mục DAO.


4. **Khởi chạy ứng dụng:**
* Biên dịch và chạy file `Main.java` (hoặc class chứa hàm main khởi tạo khung View Đăng nhập).

