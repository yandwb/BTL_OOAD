package controller;

import dao.LopHocDAO;
import dao.PhuHuynhDAO;
import model.LopHoc;
import model.PhuHuynh;

import java.util.List;

/**
 * Controller xử lý UC04 – Quản lý lớp học
 */
public class LopHocController {

    private LopHocDAO    lopHocDAO    = new LopHocDAO();
    private PhuHuynhDAO  phuHuynhDAO  = new PhuHuynhDAO();

    /** UC04.4 – Tìm kiếm & xem danh sách lớp */
    public List<LopHoc> timKiem(String tuKhoa, String trangThai) {
        return lopHocDAO.timKiem(tuKhoa, trangThai);
    }

    public LopHoc layTheoMa(String maLop) {
        return lopHocDAO.layTheoMa(maLop);
    }

    /**
     * UC04.1 – Tạo lớp học mới
     * @return null nếu thành công, chuỗi lỗi nếu thất bại
     */
    public boolean them(LopHoc lop) {
        if (lop.getMonHoc() == null || lop.getMonHoc().trim().isEmpty()) return false;
        if (lop.getDiaChiHoc() == null || lop.getDiaChiHoc().trim().isEmpty()) return false;
        if (lop.getMucLuong() <= 0) return false;
        return lopHocDAO.them(lop);
    }

    /**
     * UC04.2 – Cập nhật thông tin lớp (chỉ khi trạng thái ChoGiao)
     */
    public boolean capNhat(LopHoc lop) {
        LopHoc existing = lopHocDAO.layTheoMa(lop.getMaLop());
        if (existing == null) return false;
        if (!"ChoGiao".equals(existing.getTrangThaiLop())) return false;
        return lopHocDAO.capNhat(lop);
    }

    /**
     * UC04.3 – Hủy lớp học (chỉ khi ChoGiao)
     */
    public boolean capNhatTrangThai(String maLop, String trangThai) {
        return lopHocDAO.capNhatTrangThai(maLop, trangThai);
    }

    /** Lấy danh sách phụ huynh (để đổ vào combobox) */
    public List<PhuHuynh> danhSachPhuHuynh() {
        return phuHuynhDAO.danhSachTatCa();
    }

    public String sinhMaLop() {
        return lopHocDAO.sinhMaLop();
    }
}
