package dao;

import config.DatabaseConfig;
import model.PhuHuynh;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhuHuynhDAO {

    public List<PhuHuynh> danhSachTatCa() {
        List<PhuHuynh> list = new ArrayList<>();
        String sql = "SELECT * FROM PhuHuynh ORDER BY HoTen";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<PhuHuynh> timKiem(String tuKhoa) {
        List<PhuHuynh> list = new ArrayList<>();
        String sql = "SELECT * FROM PhuHuynh WHERE HoTen LIKE ? OR SDT LIKE ? ORDER BY HoTen";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String kw = "%" + (tuKhoa == null ? "" : tuKhoa) + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public PhuHuynh layTheoMa(String maPH) {
        String sql = "SELECT * FROM PhuHuynh WHERE MaPH = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maPH);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean them(PhuHuynh ph) {
        String sql = "INSERT INTO PhuHuynh(MaPH,HoTen,SDT,DiaChi,GhiChu,TrangThai) VALUES(?,?,?,?,?,?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ph.getMaPH());
            ps.setString(2, ph.getHoTen());
            ps.setString(3, ph.getSdt());
            ps.setString(4, ph.getDiaChi());
            ps.setString(5, ph.getGhiChu());
            ps.setString(6, ph.getTrangThai() != null ? ph.getTrangThai() : "HoatDong");
            return ps.executeUpdate() > 0;

        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean capNhat(PhuHuynh ph) {
        String sql = "UPDATE PhuHuynh SET HoTen=?,SDT=?,DiaChi=?,GhiChu=? WHERE MaPH=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ph.getHoTen());
            ps.setString(2, ph.getSdt());
            ps.setString(3, ph.getDiaChi());
            ps.setString(4, ph.getGhiChu());
            ps.setString(5, ph.getMaPH());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Soft-delete: chuyển trạng thái thành DaKhoa */
    public boolean khoaTaiKhoan(String maPH) {
        String sql = "UPDATE PhuHuynh SET TrangThai = N'DaKhoa' WHERE MaPH = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maPH);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Kiểm tra phụ huynh có lớp đang hoạt động không */
    public boolean dangCoLopHoatDong(String maPH) {
        String sql = "SELECT 1 FROM LopHoc WHERE MaPH = ? AND TrangThaiLop IN (N'ChoGiao', N'DaGiao')";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maPH);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Kiểm tra SĐT đã tồn tại chưa (dùng khi thêm/sửa) */
    public boolean sdtDaTonTai(String sdt, String maPH_loaiTru) {
        String sql = "SELECT 1 FROM PhuHuynh WHERE SDT = ? AND MaPH <> ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sdt);
            ps.setString(2, maPH_loaiTru == null ? "" : maPH_loaiTru);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Sinh mã PH tiếp theo */
    public String sinhMaPH() {
        String sql = "SELECT TOP 1 MaPH FROM PhuHuynh ORDER BY MaPH DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                int num = Integer.parseInt(rs.getString("MaPH").replaceAll("\\D", "")) + 1;
                return String.format("PH%03d", num);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "PH001";
    }

    private PhuHuynh mapRow(ResultSet rs) throws SQLException {
        return new PhuHuynh(
            rs.getString("MaPH"),
            rs.getString("HoTen"),
            rs.getString("SDT"),
            rs.getString("DiaChi"),
            rs.getString("GhiChu"),
            rs.getString("TrangThai")
        );
    }
}
