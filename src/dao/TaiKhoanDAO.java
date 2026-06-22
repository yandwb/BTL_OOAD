package dao;

import config.DatabaseConfig;
import model.TaiKhoan;

import java.sql.*;

/**
 * DAO xử lý xác thực tài khoản
 */
public class TaiKhoanDAO {

    /** Đăng nhập */
    public TaiKhoan dangNhap(String tenDangNhap, String matKhau) {
        String sql = "SELECT * FROM TaiKhoan WHERE TenDangNhap = ? AND MatKhau = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap); ps.setString(2, matKhau);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TaiKhoan tk = new TaiKhoan();
                    tk.setTenDangNhap(rs.getString("TenDangNhap"));
                    tk.setMatKhau(rs.getString("MatKhau"));
                    tk.setVaiTro(rs.getString("VaiTro"));
                    tk.setTrangThai(rs.getString("TrangThai"));
                    return tk;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /** Đổi mật khẩu */
    public boolean doiMatKhau(String tenDangNhap, String matKhauMoi) {
        String sql = "UPDATE TaiKhoan SET MatKhau = ? WHERE TenDangNhap = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, matKhauMoi); ps.setString(2, tenDangNhap);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Kiểm tra tài khoản tồn tại */
    public boolean tonTai(String tenDangNhap) {
        String sql = "SELECT 1 FROM TaiKhoan WHERE TenDangNhap = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Thêm tài khoản mới */
    public boolean themTaiKhoan(TaiKhoan tk) {
        String sql = "INSERT INTO TaiKhoan(TenDangNhap, MatKhau, VaiTro, TrangThai) VALUES(?,?,?,?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tk.getTenDangNhap()); ps.setString(2, tk.getMatKhau());
            ps.setString(3, tk.getVaiTro());
            ps.setString(4, tk.getTrangThai() != null ? tk.getTrangThai() : "HoatDong");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Khóa / mở tài khoản */
    public boolean capNhatTrangThai(String tenDangNhap, String trangThai) {
        String sql = "UPDATE TaiKhoan SET TrangThai = ? WHERE TenDangNhap = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThai); ps.setString(2, tenDangNhap);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /**
     * Xác thực tên đăng nhập & SĐT (dùng cho UC01.3 – Quên mật khẩu)
     */
    public boolean xacThucSdtVaTenDN(String tenDangNhap, String sdt) {
        String sql = "SELECT 1 FROM GiaSu   WHERE TenDangNhap = ? AND SDT = ? " +
                     "UNION " +
                     "SELECT 1 FROM NhanVien WHERE TenDangNhap = ? AND SDT = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap); ps.setString(2, sdt);
            ps.setString(3, tenDangNhap); ps.setString(4, sdt);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
