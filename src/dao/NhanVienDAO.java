package dao;

import config.DatabaseConfig;
import model.NhanVien;

import java.sql.*;

public class NhanVienDAO {

    public NhanVien layTheoTenDangNhap(String tenDangNhap) {
        String sql = "SELECT * FROM NhanVien WHERE TenDangNhap = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public NhanVien layTheoMa(String maNV) {
        String sql = "SELECT * FROM NhanVien WHERE MaNV = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    private NhanVien mapRow(ResultSet rs) throws SQLException {
        return new NhanVien(
            rs.getString("MaNV"),
            rs.getString("HoTen"),
            rs.getString("SDT"),
            rs.getString("ChucVu"),
            rs.getString("TenDangNhap")
        );
    }
}
