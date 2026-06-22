package controller;

import dao.PhuHuynhDAO;
import model.PhuHuynh;

import java.util.List;

/**
 * Controller xử lý UC03 – Quản lý phụ huynh
 */
public class PhuHuynhController {

    private PhuHuynhDAO dao = new PhuHuynhDAO();

    /** UC03.4 – Tìm kiếm phụ huynh */
    public List<PhuHuynh> timKiem(String tuKhoa) {
        return dao.timKiem(tuKhoa);
    }

    public PhuHuynh layTheoMa(String maPH) {
        return dao.layTheoMa(maPH);
    }

    /**
     * UC03.1 – Thêm mới phụ huynh
     * @return null nếu thành công, chuỗi lỗi nếu thất bại
     */
    public boolean them(PhuHuynh ph) {
        // Validate
        if (ph.getHoTen() == null || ph.getHoTen().trim().isEmpty()) return false;
        if (!validSdt(ph.getSdt())) return false;
        if (dao.sdtDaTonTai(ph.getSdt(), null)) return false;
        return dao.them(ph);
    }

    /**
     * UC03.2 – Sửa thông tin phụ huynh
     */
    public boolean capNhat(PhuHuynh ph) {
        if (ph.getHoTen() == null || ph.getHoTen().trim().isEmpty()) return false;
        if (!validSdt(ph.getSdt())) return false;
        if (dao.sdtDaTonTai(ph.getSdt(), ph.getMaPH())) return false;
        return dao.capNhat(ph);
    }

    /**
     * UC03.3 – Khóa phụ huynh (soft-delete)
     */
    public boolean khoa(String maPH) {
        return dao.khoaTaiKhoan(maPH);
    }

    /**
     * Kiểm tra ràng buộc trước khi khóa
     * @return null nếu được phép, chuỗi cảnh báo nếu không được phép
     */
    public String kiemTraTruocKhoa(String maPH) {
        if (dao.dangCoLopHoatDong(maPH)) {
            return "Phụ huynh đang có lớp học đang hoạt động!\nKhông thể khóa tài khoản.";
        }
        return null;
    }

    public String sinhMaPH() {
        return dao.sinhMaPH();
    }

    // UC03.7 – Kiểm tra dữ liệu hợp lệ
    private boolean validSdt(String sdt) {
        return sdt != null && sdt.matches("\\d{10,11}");
    }

    private boolean validEmail(String email) {
        return email == null || email.isEmpty() || email.contains("@");
    }
}
