package controller;

import config.DatabaseConfig;
import dao.PhieuGiaoLopDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller xử lý UC06 – Quản lý báo cáo thống kê
 */
public class ThongKeController {

    private PhieuGiaoLopDAO phieuDAO = new PhieuGiaoLopDAO();

    /**
     * UC06.1 + UC06.2 – Thống kê tổng hợp theo khoảng thời gian
     * @return Map gồm: tongDoanhThu, tongLop, lopThanhCong, lopHong, chiTiet (List)
     */
    public Map<String, Object> thongKe(Date tuNgay, Date denNgay) {
        Map<String, Object> result = new LinkedHashMap<>();

        // UC06.1 – Tổng doanh thu
        double tongDoanhThu = phieuDAO.tongDoanhThu(tuNgay, denNgay);
        result.put("tongDoanhThu", tongDoanhThu);

        // UC06.2 – Thống kê lớp học
        int[] lopStats = thongKeLop(tuNgay, denNgay);
        result.put("tongLop",      lopStats[0]);
        result.put("lopThanhCong", lopStats[1]);
        result.put("lopHong",       lopStats[2]);

        // Chi tiết các phiếu đã thanh toán
        result.put("chiTiet", chiTietDoanhThu(tuNgay, denNgay));

        return result;
    }

    /**
     * UC06.3 – Top gia sư tích cực nhất (theo số phiếu nhận lớp)
     */
    public List<Map<String, Object>> topGiaSu(Date tuNgay, Date denNgay) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT g.MaGS, g.HoTen, COUNT(p.MaPhieu) AS SoLopNhan " +
                     "FROM PhieuGiaoLop p JOIN GiaSu g ON p.MaGS = g.MaGS " +
                     "WHERE p.NgayGiao BETWEEN ? AND ? " +
                     "GROUP BY g.MaGS, g.HoTen " +
                     "ORDER BY SoLopNhan DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, tuNgay); ps.setDate(2, denNgay);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("maGS",    rs.getString("MaGS"));
                    row.put("hoTen",   rs.getString("HoTen"));
                    row.put("soLop",   rs.getInt("SoLopNhan"));
                    list.add(row);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ──────────────────────────────────────────────
    // Private helpers
    // ──────────────────────────────────────────────

    /** Đếm số lớp tổng / thành công / hỏng trong kỳ */
    private int[] thongKeLop(Date tuNgay, Date denNgay) {
        int[] counts = {0, 0, 0}; // [tong, thanhCong, hong]
        String sql = "SELECT TrangThaiLop, COUNT(*) AS SoLuong FROM LopHoc " +
                     "WHERE NgayTao BETWEEN ? AND ? " +
                     "GROUP BY TrangThaiLop";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, tuNgay); ps.setDate(2, denNgay);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int cnt = rs.getInt("SoLuong");
                    counts[0] += cnt;
                    String tt = rs.getString("TrangThaiLop");
                    if ("DaGiao".equals(tt))  counts[1] += cnt;
                    if ("Hong".equals(tt))    counts[2] += cnt;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return counts;
    }

    /** Danh sách phiếu đã thanh toán trong kỳ (cho bảng chi tiết) */
    private List<Map<String, Object>> chiTietDoanhThu(Date tuNgay, Date denNgay) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT p.MaPhieu, g.HoTen AS TenGiaSu, l.MonHoc, " +
                     "p.NgayGiao, p.SoTienCanDong, p.TrangThaiThanhToan " +
                     "FROM PhieuGiaoLop p " +
                     "JOIN GiaSu g ON p.MaGS = g.MaGS " +
                     "JOIN LopHoc l ON p.MaLop = l.MaLop " +
                     "WHERE p.TrangThaiThanhToan = N'DaThanhToan' " +
                     "AND p.NgayGiao BETWEEN ? AND ? " +
                     "ORDER BY p.NgayGiao DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, tuNgay); ps.setDate(2, denNgay);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("maPhieu",  rs.getString("MaPhieu"));
                    row.put("tenGiaSu", rs.getString("TenGiaSu"));
                    row.put("monHoc",   rs.getString("MonHoc"));
                    row.put("ngayGiao", rs.getDate("NgayGiao"));
                    row.put("soTien",   rs.getDouble("SoTienCanDong"));
                    row.put("trangThai",rs.getString("TrangThaiThanhToan"));
                    list.add(row);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
