package dao;

import config.DatabaseConfig;
import model.LopHoc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LopHocDAO {

    private static final String SELECT_JOIN =
        "SELECT l.*, p.HoTen AS TenPhuHuynh, p.SDT AS SdtPhuHuynh " +
        "FROM LopHoc l JOIN PhuHuynh p ON l.MaPH = p.MaPH ";

    /** Danh sách tất cả lớp */
    public List<LopHoc> danhSachTatCa() {
        List<LopHoc> list = new ArrayList<>();
        String sql = SELECT_JOIN + "ORDER BY l.NgayTao DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** Tìm kiếm theo mã/SĐT phụ huynh, lọc theo trạng thái */
    public List<LopHoc> timKiem(String tuKhoa, String trangThai) {
        List<LopHoc> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder(SELECT_JOIN +
            "WHERE (l.MaLop LIKE ? OR p.SDT LIKE ? OR p.HoTen LIKE ?)");
        if (trangThai != null && !trangThai.isEmpty() && !trangThai.equals("Tất cả"))
            sb.append(" AND l.TrangThaiLop = ?");
        sb.append(" ORDER BY l.NgayTao DESC");

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sb.toString())) {

            String kw = "%" + (tuKhoa == null ? "" : tuKhoa) + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);
            if (trangThai != null && !trangThai.isEmpty() && !trangThai.equals("Tất cả"))
                ps.setString(4, trangThai);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** Lớp đang chờ giao (bảng tin gia sư) */
    public List<LopHoc> danhSachChoGiao() {
        List<LopHoc> list = new ArrayList<>();
        String sql = SELECT_JOIN + "WHERE l.TrangThaiLop = N'ChoGiao' ORDER BY l.NgayTao DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public LopHoc layTheoMa(String maLop) {
        String sql = SELECT_JOIN + "WHERE l.MaLop = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maLop);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean them(LopHoc lop) {
        String sql = "INSERT INTO LopHoc(MaLop,MaPH,MonHoc,CapHoc,SoBuoiTuan," +
                     "ThoiGianHoc,MucLuong,DiaChiHoc,YeuCauKhac,TrangThaiLop,NgayTao) " +
                     "VALUES(?,?,?,?,?,?,?,?,?,?,GETDATE())";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, lop.getMaLop());
            ps.setString(2, lop.getMaPH());
            ps.setString(3, lop.getMonHoc());
            ps.setString(4, lop.getCapHoc());
            ps.setInt(5, lop.getSoBuoiTuan());
            ps.setString(6, lop.getThoiGianHoc());
            ps.setDouble(7, lop.getMucLuong());
            ps.setString(8, lop.getDiaChiHoc());
            ps.setString(9, lop.getYeuCauKhac());
            ps.setString(10, lop.getTrangThaiLop() != null ? lop.getTrangThaiLop() : "ChoGiao");
            return ps.executeUpdate() > 0;

        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean capNhat(LopHoc lop) {
        String sql = "UPDATE LopHoc SET MonHoc=?,CapHoc=?,SoBuoiTuan=?,ThoiGianHoc=?," +
                     "MucLuong=?,DiaChiHoc=?,YeuCauKhac=? WHERE MaLop=? AND TrangThaiLop=N'ChoGiao'";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, lop.getMonHoc());
            ps.setString(2, lop.getCapHoc());
            ps.setInt(3, lop.getSoBuoiTuan());
            ps.setString(4, lop.getThoiGianHoc());
            ps.setDouble(5, lop.getMucLuong());
            ps.setString(6, lop.getDiaChiHoc());
            ps.setString(7, lop.getYeuCauKhac());
            ps.setString(8, lop.getMaLop());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean capNhatTrangThai(String maLop, String trangThai) {
        String sql = "UPDATE LopHoc SET TrangThaiLop = ? WHERE MaLop = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, trangThai);
            ps.setString(2, maLop);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Sinh mã lớp tiếp theo */
    public String sinhMaLop() {
        String sql = "SELECT TOP 1 MaLop FROM LopHoc ORDER BY MaLop DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                int num = Integer.parseInt(rs.getString("MaLop").replaceAll("\\D", "")) + 1;
                return String.format("L%03d", num);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "L001";
    }

    private LopHoc mapRow(ResultSet rs) throws SQLException {
        LopHoc lop = new LopHoc();
        lop.setMaLop(rs.getString("MaLop"));
        lop.setMaPH(rs.getString("MaPH"));
        lop.setMonHoc(rs.getString("MonHoc"));
        lop.setCapHoc(rs.getString("CapHoc"));
        lop.setSoBuoiTuan(rs.getInt("SoBuoiTuan"));
        lop.setThoiGianHoc(rs.getString("ThoiGianHoc"));
        lop.setMucLuong(rs.getDouble("MucLuong"));
        lop.setDiaChiHoc(rs.getString("DiaChiHoc"));
        lop.setYeuCauKhac(rs.getString("YeuCauKhac"));
        lop.setTrangThaiLop(rs.getString("TrangThaiLop"));
        lop.setNgayTao(rs.getTimestamp("NgayTao"));
        try { lop.setTenPhuHuynh(rs.getString("TenPhuHuynh")); } catch (SQLException ignored) {}
        try { lop.setSdtPhuHuynh(rs.getString("SdtPhuHuynh")); } catch (SQLException ignored) {}
        return lop;
    }
}
