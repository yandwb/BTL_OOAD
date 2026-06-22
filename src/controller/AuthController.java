package controller;

import dao.GiaSuDAO;
import dao.NhanVienDAO;
import dao.TaiKhoanDAO;
import model.GiaSu;
import model.NhanVien;
import model.TaiKhoan;
import util.SessionManager;

/**
 * Controller xử lý UC01 – Quản lý xác thực
 */
public class AuthController {

    private TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private GiaSuDAO    giaSuDAO    = new GiaSuDAO();
    private SessionManager session  = SessionManager.getInstance();

    /**
     * UC01.1 – Đăng nhập
     * @return null nếu thành công, chuỗi lỗi nếu thất bại
     */
    public String dangNhap(String tenDangNhap, String matKhau) {
        TaiKhoan tk = taiKhoanDAO.dangNhap(tenDangNhap, matKhau);

        if (tk == null) {
            return "Tên đăng nhập hoặc mật khẩu không đúng!";
        }
        if ("BiKhoa".equals(tk.getTrangThai())) {
            return "Tài khoản đã bị khóa. Vui lòng liên hệ trung tâm!";
        }

        // Lấy mã định danh người dùng (MaNV hoặc MaGS)
        String maNguoiDung = null;
        if ("GiaSu".equals(tk.getVaiTro())) {
            GiaSu gs = giaSuDAO.layTheoTenDangNhap(tenDangNhap);
            if (gs != null) {
                maNguoiDung = gs.getMaGS();
                // Kiểm tra hồ sơ đã được duyệt chưa
                if ("ChoDuyet".equals(gs.getTrangThaiDuyet())) {
                    return "Hồ sơ của bạn đang chờ duyệt. Vui lòng chờ thông báo từ trung tâm!";
                }
                if ("TuChoi".equals(gs.getTrangThaiDuyet())) {
                    return "Hồ sơ của bạn đã bị từ chối. Vui lòng liên hệ trung tâm!";
                }
            }
        } else {
            NhanVien nv = nhanVienDAO.layTheoTenDangNhap(tenDangNhap);
            if (nv != null) maNguoiDung = nv.getMaNV();
        }

        session.dangNhap(tk, maNguoiDung);
        return null; // thành công
    }

    /**
     * UC01.2 – Đăng xuất
     */
    public void dangXuat() {
        session.dangXuat();
    }

    /**
     * UC01.3 – Quên mật khẩu (reset bằng SĐT – đơn giản hóa cho desktop app)
     * @return null nếu thành công, chuỗi lỗi nếu thất bại
     */
    public String quenMatKhau(String tenDangNhap, String sdt, String matKhauMoi) {
        if (matKhauMoi == null || matKhauMoi.trim().length() < 6) {
            return "Mật khẩu mới phải có ít nhất 6 ký tự!";
        }
        boolean xacNhan = taiKhoanDAO.xacThucSdtVaTenDN(tenDangNhap, sdt);
        if (!xacNhan) {
            return "Tên đăng nhập hoặc SĐT không khớp với hệ thống!";
        }
        boolean ok = taiKhoanDAO.doiMatKhau(tenDangNhap, matKhauMoi.trim());
        return ok ? null : "Có lỗi xảy ra khi cập nhật mật khẩu!";
    }

    /**
     * UC01.4 – Đổi mật khẩu (khi đã đăng nhập)
     * @return null nếu thành công, chuỗi lỗi nếu thất bại
     */
    public String doiMatKhau(String matKhauCu, String matKhauMoi, String xacNhanMoi) {
        if (matKhauMoi == null || matKhauMoi.trim().length() < 6) {
            return "Mật khẩu mới phải có ít nhất 6 ký tự!";
        }
        if (!matKhauMoi.equals(xacNhanMoi)) {
            return "Mật khẩu mới và xác nhận không khớp!";
        }
        String tenDN = session.getCurrentUser().getTenDangNhap();
        TaiKhoan tk  = taiKhoanDAO.dangNhap(tenDN, matKhauCu);
        if (tk == null) {
            return "Mật khẩu hiện tại không đúng!";
        }
        boolean ok = taiKhoanDAO.doiMatKhau(tenDN, matKhauMoi.trim());
        return ok ? null : "Có lỗi xảy ra khi cập nhật mật khẩu!";
    }
}
