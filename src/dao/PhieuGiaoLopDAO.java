package dao;

import config.DatabaseConfig;
import model.GiaSu;
import model.PhieuGiaoLop;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhieuGiaoLopDAO {

    // ===================== ĐĂNG KÝ NHẬN LỚP =====================

    /** Gia sư đăng ký nhận lớp */
    public boolean dangKy(String maLop, String maGS) {
        String sql = "INSERT INTO DangKyNhanLop(MaLop,MaGS,NgayDangKy,TrangThaiXetDuyet) " +
                     "VALUES(?,?,GETDATE(),N'ChoDuyet')";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maLop);
            ps.setString(2, maGS);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Kiểm tra gia sư đã đăng ký lớp này chưa */
    public boolean daDangKy(String maLop, String maGS) {
        String sql = "SELECT 1 FROM DangKyNhanLop WHERE MaLop=? AND MaGS=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maLop); ps.setString(2, maGS);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Lấy danh sách gia sư đã đăng ký 1 lớp (để nhân viên chọn) */
    public List<GiaSu> danhSachGiaSuDangKy(String maLop) {
        List<GiaSu> list = new ArrayList<>();
        String sql = "SELECT g.*, d.NgayDangKy FROM GiaSu g " +
                     "JOIN DangKyNhanLop d ON g.MaGS = d.MaGS " +
                     "WHERE d.MaLop = ? AND d.TrangThaiXetDuyet = N'ChoDuyet'";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maLop);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    GiaSu gs = new GiaSu();
                    gs.setMaGS(rs.getString("MaGS"));
                    gs.setHoTen(rs.getString("HoTen"));
                    gs.setSdt(rs.getString("SDT"));
                    gs.setEmail(rs.getString("Email"));
                    gs.setTruongDH(rs.getString("TruongDH"));
                    gs.setChuyenNganh(rs.getString("ChuyenNganh"));
                    gs.setTrangThaiDuyet(rs.getString("TrangThaiDuyet"));
                    list.add(gs);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ===================== PHIẾU GIAO LỚP =====================

    private static final String SELECT_JOIN =
        "SELECT p.*, g.HoTen AS TenGiaSu, l.MonHoc " +
        "FROM PhieuGiaoLop p " +
        "JOIN GiaSu g ON p.MaGS = g.MaGS " +
        "JOIN LopHoc l ON p.MaLop = l.MaLop ";

    public List<PhieuGiaoLop> danhSachTatCa() {
        List<PhieuGiaoLop> list = new ArrayList<>();
        String sql = SELECT_JOIN + "ORDER BY p.NgayGiao DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<PhieuGiaoLop> timKiem(String tuKhoa, String trangThai) {
        List<PhieuGiaoLop> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder(SELECT_JOIN +
            "WHERE (p.MaPhieu LIKE ? OR p.MaGS LIKE ? OR g.HoTen LIKE ?)");
        if (trangThai != null && !trangThai.isEmpty() && !trangThai.equals("Tất cả"))
            sb.append(" AND p.TrangThaiThanhToan = ?");
        sb.append(" ORDER BY p.NgayGiao DESC");

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sb.toString())) {

            String kw = "%" + (tuKhoa == null ? "" : tuKhoa) + "%";
            ps.setString(1, kw); ps.setString(2, kw); ps.setString(3, kw);
            if (trangThai != null && !trangThai.isEmpty() && !trangThai.equals("Tất cả"))
                ps.setString(4, trangThai);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public PhieuGiaoLop layTheoMa(String maPhieu) {
        String sql = SELECT_JOIN + "WHERE p.MaPhieu = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maPhieu);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /** Tạo phiếu giao lớp mới */
    public boolean taoPhieu(PhieuGiaoLop phieu) {
        String sql = "INSERT INTO PhieuGiaoLop(MaPhieu,MaLop,MaGS,MaNV,NgayGiao," +
                     "TyLePhi,SoTienCanDong,TrangThaiThanhToan) VALUES(?,?,?,?,GETDATE(),?,?,N'ChuaThanhToan')";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, phieu.getMaPhieu());
            ps.setString(2, phieu.getMaLop());
            ps.setString(3, phieu.getMaGS());
            ps.setString(4, phieu.getMaNV());
            ps.setDouble(5, phieu.getTyLePhi());
            ps.setDouble(6, phieu.getSoTienCanDong());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Xác nhận thanh toán */
    public boolean xacNhanThanhToan(String maPhieu) {
        String sql = "UPDATE PhieuGiaoLop SET TrangThaiThanhToan = N'DaThanhToan' WHERE MaPhieu = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maPhieu);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Xử lý hoàn phí */
    public boolean hoanPhi(String maPhieu, double soTienHoan, String lyDo) {
        String sql = "UPDATE PhieuGiaoLop SET TrangThaiThanhToan=N'DaHoanPhi'," +
                     "SoTienHoanLai=?,LyDoHoan=? WHERE MaPhieu=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, soTienHoan);
            ps.setString(2, lyDo);
            ps.setString(3, maPhieu);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Thống kê doanh thu theo khoảng thời gian */
    public double tongDoanhThu(Date tuNgay, Date denNgay) {
        String sql = "SELECT ISNULL(SUM(SoTienCanDong),0) FROM PhieuGiaoLop " +
                     "WHERE TrangThaiThanhToan = N'DaThanhToan' AND NgayGiao BETWEEN ? AND ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, tuNgay);
            ps.setDate(2, denNgay);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /** Sinh mã phiếu tiếp theo */
    public String sinhMaPhieu() {
        String sql = "SELECT TOP 1 MaPhieu FROM PhieuGiaoLop ORDER BY MaPhieu DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                int num = Integer.parseInt(rs.getString("MaPhieu").replaceAll("\\D", "")) + 1;
                return String.format("PGL%03d", num);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "PGL001";
    }

    private PhieuGiaoLop mapRow(ResultSet rs) throws SQLException {
        PhieuGiaoLop p = new PhieuGiaoLop();
        p.setMaPhieu(rs.getString("MaPhieu"));
        p.setMaLop(rs.getString("MaLop"));
        p.setMaGS(rs.getString("MaGS"));
        p.setMaNV(rs.getString("MaNV"));
        p.setNgayGiao(rs.getDate("NgayGiao"));
        p.setTyLePhi(rs.getDouble("TyLePhi"));
        p.setSoTienCanDong(rs.getDouble("SoTienCanDong"));
        p.setSoTienHoanLai(rs.getDouble("SoTienHoanLai"));
        p.setLyDoHoan(rs.getString("LyDoHoan"));
        p.setTrangThaiThanhToan(rs.getString("TrangThaiThanhToan"));
        try { p.setTenGiaSu(rs.getString("TenGiaSu")); } catch (SQLException ignored) {}
        try { p.setMonHoc(rs.getString("MonHoc")); } catch (SQLException ignored) {}
        return p;
    }
}
