package dao;

import config.DatabaseConfig;
import model.GiaSu;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GiaSuDAO {

    /** Lấy toàn bộ danh sách gia sư */
    public List<GiaSu> danhSachTatCa() {
        List<GiaSu> list = new ArrayList<>();
        String sql = "SELECT * FROM GiaSu ORDER BY HoTen";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** Tìm kiếm theo tên hoặc SĐT, lọc theo trạng thái */
    public List<GiaSu> timKiem(String tuKhoa, String trangThai) {
        List<GiaSu> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder(
            "SELECT * FROM GiaSu WHERE (HoTen LIKE ? OR SDT LIKE ?)");
        if (trangThai != null && !trangThai.isEmpty() && !trangThai.equals("Tất cả"))
            sb.append(" AND TrangThaiDuyet = ?");
        sb.append(" ORDER BY HoTen");

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sb.toString())) {

            String kw = "%" + (tuKhoa == null ? "" : tuKhoa) + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            if (trangThai != null && !trangThai.isEmpty() && !trangThai.equals("Tất cả"))
                ps.setString(3, trangThai);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** Lấy gia sư theo mã */
    public GiaSu layTheoMa(String maGS) {
        String sql = "SELECT * FROM GiaSu WHERE MaGS = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maGS);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /** Thêm gia sư mới */
    public boolean them(GiaSu gs) {
        String sql = "INSERT INTO GiaSu(MaGS,HoTen,NgaySinh,GioiTinh,SDT,Email," +
                     "TruongDH,ChuyenNganh,HinhAnh,TrangThaiDuyet,TenDangNhap) " +
                     "VALUES(?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, gs.getMaGS());
            ps.setString(2, gs.getHoTen());
            ps.setDate(3, gs.getNgaySinh());
            ps.setString(4, gs.getGioiTinh());
            ps.setString(5, gs.getSdt());
            ps.setString(6, gs.getEmail());
            ps.setString(7, gs.getTruongDH());
            ps.setString(8, gs.getChuyenNganh());
            ps.setString(9, gs.getHinhAnh());
            ps.setString(10, gs.getTrangThaiDuyet() != null ? gs.getTrangThaiDuyet() : "ChoDuyet");
            ps.setString(11, gs.getTenDangNhap());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Cập nhật hồ sơ gia sư */
    public boolean capNhat(GiaSu gs) {
        String sql = "UPDATE GiaSu SET HoTen=?,NgaySinh=?,GioiTinh=?,SDT=?,Email=?," +
                     "TruongDH=?,ChuyenNganh=?,HinhAnh=?,TrangThaiDuyet=? WHERE MaGS=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, gs.getHoTen());
            ps.setDate(2, gs.getNgaySinh());
            ps.setString(3, gs.getGioiTinh());
            ps.setString(4, gs.getSdt());
            ps.setString(5, gs.getEmail());
            ps.setString(6, gs.getTruongDH());
            ps.setString(7, gs.getChuyenNganh());
            ps.setString(8, gs.getHinhAnh());
            ps.setString(9, gs.getTrangThaiDuyet());
            ps.setString(10, gs.getMaGS());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Duyệt / Từ chối / Khóa hồ sơ */
    public boolean capNhatTrangThai(String maGS, String trangThai) {
        String sql = "UPDATE GiaSu SET TrangThaiDuyet = ? WHERE MaGS = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, trangThai);
            ps.setString(2, maGS);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Kiểm tra gia sư đang có lớp chưa xong (ràng buộc trước khi khóa) */
    public boolean dangCoLopHoatDong(String maGS) {
        String sql = "SELECT 1 FROM PhieuGiaoLop p " +
                     "JOIN LopHoc l ON p.MaLop = l.MaLop " +
                     "WHERE p.MaGS = ? AND l.TrangThaiLop = N'DaGiao'";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maGS);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }

        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Lấy gia sư theo tên đăng nhập */
    public GiaSu layTheoTenDangNhap(String tenDangNhap) {
        String sql = "SELECT * FROM GiaSu WHERE TenDangNhap = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /** Sinh mã GS tiếp theo: GS001, GS002... */
    public String sinhMaGS() {
        String sql = "SELECT TOP 1 MaGS FROM GiaSu ORDER BY MaGS DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                String last = rs.getString("MaGS"); // vd: GS003
                int num = Integer.parseInt(last.replaceAll("\\D", "")) + 1;
                return String.format("GS%03d", num);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "GS001";
    }

    // ----------------------------------------------------------------
    private GiaSu mapRow(ResultSet rs) throws SQLException {
        GiaSu gs = new GiaSu();
        gs.setMaGS(rs.getString("MaGS"));
        gs.setHoTen(rs.getString("HoTen"));
        gs.setNgaySinh(rs.getDate("NgaySinh"));
        gs.setGioiTinh(rs.getString("GioiTinh"));
        gs.setSdt(rs.getString("SDT"));
        gs.setEmail(rs.getString("Email"));
        gs.setTruongDH(rs.getString("TruongDH"));
        gs.setChuyenNganh(rs.getString("ChuyenNganh"));
        gs.setHinhAnh(rs.getString("HinhAnh"));
        gs.setTrangThaiDuyet(rs.getString("TrangThaiDuyet"));
        gs.setTenDangNhap(rs.getString("TenDangNhap"));
        return gs;
    }

    /** Kiểm tra SĐT đã tồn tại chưa */
    public boolean sdtDaTonTai(String sdt, String maGS_loaiTru) {
        String sql = "SELECT 1 FROM GiaSu WHERE SDT = ? AND MaGS <> ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sdt);
            ps.setString(2, maGS_loaiTru == null ? "" : maGS_loaiTru);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

}
/* sdtDaTonTai appended below - needs to be inside class */
