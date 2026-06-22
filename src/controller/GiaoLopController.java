package controller;

import dao.GiaSuDAO;
import dao.LopHocDAO;
import dao.PhieuGiaoLopDAO;
import model.GiaSu;
import model.LopHoc;
import model.PhieuGiaoLop;

import java.sql.Date;
import java.util.List;

/**
 * Controller xử lý UC05 – Quản lý giao lớp
 */
public class GiaoLopController {

    private PhieuGiaoLopDAO phieuDAO = new PhieuGiaoLopDAO();
    private LopHocDAO       lopDAO   = new LopHocDAO();
    private GiaSuDAO        giaSuDAO = new GiaSuDAO();

    // ──────────────────────────────────────────────
    // UC05.1 – Gia sư đăng ký nhận lớp
    // ──────────────────────────────────────────────
    public String dangKyNhanLop(String maLop, String maGS) {
        // Kiểm tra lớp còn chờ giao không
        LopHoc lop = lopDAO.layTheoMa(maLop);
        if (lop == null || !"ChoGiao".equals(lop.getTrangThaiLop())) {
            return "Lớp học này không còn trong trạng thái chờ giao!";
        }
        // Kiểm tra hồ sơ gia sư đã duyệt chưa
        GiaSu gs = giaSuDAO.layTheoMa(maGS);
        if (gs == null || !"DaDuyet".equals(gs.getTrangThaiDuyet())) {
            return "Bạn cần cập nhật hồ sơ và chờ duyệt trước khi nhận lớp!";
        }
        // Kiểm tra đã đăng ký chưa
        if (phieuDAO.daDangKy(maLop, maGS)) {
            return "Bạn đã đăng ký lớp này rồi!";
        }
        boolean ok = phieuDAO.dangKy(maLop, maGS);
        return ok ? "Đăng ký thành công! Vui lòng chờ nhân viên duyệt." : "Có lỗi xảy ra!";
    }

    /** Lấy danh sách gia sư đã đăng ký 1 lớp (UC05.2) */
    public List<GiaSu> danhSachGiaSuDangKy(String maLop) {
        return phieuDAO.danhSachGiaSuDangKy(maLop);
    }

    /** Danh sách lớp đang chờ giao (bảng tin) */
    public List<LopHoc> danhSachChoGiao() {
        return lopDAO.danhSachChoGiao();
    }

    // ──────────────────────────────────────────────
    // UC05.2 + UC05.3 – Duyệt giao lớp + Lập phiếu
    // ──────────────────────────────────────────────
    /**
     * @return chuỗi thông báo kết quả
     */
    public String giaoLop(String maLop, String maGS, String maNV, double tyLePhi) {
        if (tyLePhi <= 0 || tyLePhi > 100) {
            return "Tỉ lệ phí không hợp lệ (phải từ 1 đến 100)!";
        }
        LopHoc lop = lopDAO.layTheoMa(maLop);
        if (lop == null || !"ChoGiao".equals(lop.getTrangThaiLop())) {
            return "Lớp học đã bị hủy hoặc đã được giao!";
        }

        // Tính tiền phí
        double soTienCanDong = lop.getMucLuong() * tyLePhi / 100.0;

        // Cập nhật trạng thái lớp → DaGiao
        boolean lopOk = lopDAO.capNhatTrangThai(maLop, "DaGiao");
        if (!lopOk) return "Lỗi cập nhật trạng thái lớp!";

        // Tạo phiếu giao lớp (UC05.3)
        PhieuGiaoLop phieu = new PhieuGiaoLop();
        phieu.setMaPhieu(phieuDAO.sinhMaPhieu());
        phieu.setMaLop(maLop);
        phieu.setMaGS(maGS);
        phieu.setMaNV(maNV);
        phieu.setTyLePhi(tyLePhi);
        phieu.setSoTienCanDong(soTienCanDong);
        phieu.setTrangThaiThanhToan("ChuaThanhToan");

        boolean phieuOk = phieuDAO.taoPhieu(phieu);
        if (!phieuOk) return "Lỗi tạo phiếu giao lớp!";

        return String.format("Giao lớp thành công!\nPhiếu: %s | Số tiền phí: %,.0f đ",
                             phieu.getMaPhieu(), soTienCanDong);
    }

    // ──────────────────────────────────────────────
    // UC05.4 – Thanh toán phí nhận lớp
    // ──────────────────────────────────────────────
    public String xacNhanThanhToan(String maPhieu) {
        PhieuGiaoLop p = phieuDAO.layTheoMa(maPhieu);
        if (p == null) return "Không tìm thấy phiếu!";
        if ("DaThanhToan".equals(p.getTrangThaiThanhToan())) return "Phiếu này đã được thanh toán!";
        boolean ok = phieuDAO.xacNhanThanhToan(maPhieu);
        return ok ? "Xác nhận thanh toán thành công!" : "Có lỗi xảy ra!";
    }

    // ──────────────────────────────────────────────
    // UC05.5 – Xử lý hoàn phí
    // ──────────────────────────────────────────────
    public String hoanPhi(String maPhieu, String maLop, double soTienHoan, String lyDo) {
        PhieuGiaoLop p = phieuDAO.layTheoMa(maPhieu);
        if (p == null) return "Không tìm thấy phiếu!";
        if (!"DaThanhToan".equals(p.getTrangThaiThanhToan())) {
            return "Chỉ có thể hoàn phí cho phiếu đã thanh toán!";
        }
        if (soTienHoan > p.getSoTienCanDong()) {
            return "Số tiền hoàn (" + String.format("%,.0f đ", soTienHoan) +
                   ") vượt quá số tiền đã đóng (" +
                   String.format("%,.0f đ", p.getSoTienCanDong()) + ")!";
        }
        // Cập nhật trạng thái lớp → Hong
        lopDAO.capNhatTrangThai(maLop, "Hong");
        // Cập nhật phiếu
        boolean ok = phieuDAO.hoanPhi(maPhieu, soTienHoan, lyDo);
        return ok ? String.format("Hoàn phí thành công! Số tiền hoàn: %,.0f đ", soTienHoan)
                  : "Có lỗi xảy ra!";
    }

    /** Tìm kiếm phiếu giao lớp */
    public List<PhieuGiaoLop> timKiem(String tuKhoa, String trangThai) {
        return phieuDAO.timKiem(tuKhoa, trangThai);
    }

    /** Lịch sử nhận lớp của một gia sư */
    public List<PhieuGiaoLop> lichSuNhanLop(String maGS) {
        return phieuDAO.timKiem(maGS, null);
    }
}
