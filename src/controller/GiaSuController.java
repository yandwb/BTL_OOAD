package controller;

import dao.GiaSuDAO;
import dao.TaiKhoanDAO;
import model.GiaSu;
import model.TaiKhoan;

import java.util.List;

/**
 * Controller xử lý UC02 – Quản lý gia sư
 */
public class GiaSuController {

    private GiaSuDAO    giaSuDAO    = new GiaSuDAO();
    private TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    /** UC02.2 – Tìm kiếm gia sư */
    public List<GiaSu> timKiem(String tuKhoa, String trangThai) {
        return giaSuDAO.timKiem(tuKhoa, trangThai);
    }

    public GiaSu layTheoMa(String maGS) {
        return giaSuDAO.layTheoMa(maGS);
    }

    /**
     * UC02.1 – Thêm mới gia sư (tạo cả tài khoản đồng thời)
     * @return null nếu thành công, chuỗi lỗi nếu thất bại
     */
    public boolean themMoi(GiaSu gs, String tenDangNhap, String matKhauKhoiTao) {
        // Kiểm tra trùng SĐT / Email
        if (giaSuDAO.sdtDaTonTai(gs.getSdt(), null)) return false;
        if (taiKhoanDAO.tonTai(tenDangNhap)) return false;

        // Tạo tài khoản
        TaiKhoan tk = new TaiKhoan(tenDangNhap, matKhauKhoiTao, "GiaSu", "HoatDong");
        boolean tkOk = taiKhoanDAO.themTaiKhoan(tk);
        if (!tkOk) return false;

        gs.setTenDangNhap(tenDangNhap);
        return giaSuDAO.them(gs);
    }

    /** UC02.3 – Cập nhật hồ sơ gia sư */
    public boolean capNhat(GiaSu gs) {
        return giaSuDAO.capNhat(gs);
    }

    /** UC02.4 / UC02.5 – Duyệt / Từ chối / Khóa */
    public boolean capNhatTrangThai(String maGS, String trangThai) {
        boolean ok = giaSuDAO.capNhatTrangThai(maGS, trangThai);
        if (ok && "DaKhoa".equals(trangThai)) {
            GiaSu gs = giaSuDAO.layTheoMa(maGS);
            if (gs != null) taiKhoanDAO.capNhatTrangThai(gs.getTenDangNhap(), "BiKhoa");
        }
        return ok;
    }

    /**
     * Kiểm tra ràng buộc trước khi khóa
     * @return null nếu được phép, chuỗi cảnh báo nếu không được phép
     */
    public String kiemTraTruocKhoa(String maGS) {
        if (giaSuDAO.dangCoLopHoatDong(maGS)) {
            return "Gia sư đang có lớp học đang hoạt động!\nKhông thể khóa tài khoản.";
        }
        return null;
    }

    public String sinhMaGS() {
        return giaSuDAO.sinhMaGS();
    }

    // Kiểm tra SĐT trùng (dùng khi validate form)
    private boolean sdtDaTonTai(String sdt, String maGS_loaiTru) {
        return giaSuDAO.sdtDaTonTai(sdt, maGS_loaiTru);
    }
}
