package view;

import config.DatabaseConfig;
import model.GiaSu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/**
 * UC02.3 – Xem chi tiết hồ sơ gia sư + lịch sử nhận lớp
 */
public class GiaSuChiTietDialog extends JDialog {

    public GiaSuChiTietDialog(JFrame parent, GiaSu gs) {
        super(parent, "Chi tiết hồ sơ: " + gs.getHoTen(), true);
        initUI(gs);
        setSize(680, 520);
        setLocationRelativeTo(parent);
    }

    private void initUI(GiaSu gs) {
        setLayout(new BorderLayout(8, 8));

        // ── Thông tin cá nhân ──
        JPanel info = new JPanel(new GridLayout(0, 2, 6, 6));
        info.setBorder(BorderFactory.createTitledBorder("Thông tin gia sư"));
        info.setBackground(new Color(248, 250, 255));

        addField(info, "Mã GS:",        gs.getMaGS());
        addField(info, "Họ tên:",       gs.getHoTen());
        addField(info, "Giới tính:",    gs.getGioiTinh());
        addField(info, "Ngày sinh:",    gs.getNgaySinh() != null ? gs.getNgaySinh().toString() : "");
        addField(info, "SĐT:",          gs.getSdt());
        addField(info, "Email:",        gs.getEmail());
        addField(info, "Trường ĐH:",   gs.getTruongDH());
        addField(info, "Chuyên ngành:", gs.getChuyenNganh());
        addField(info, "Trạng thái:",   gs.getTrangThaiDuyet());
        addField(info, "Tài khoản:",    gs.getTenDangNhap());

        add(info, BorderLayout.NORTH);

        // ── Lịch sử nhận lớp ──
        JPanel lichSuPanel = new JPanel(new BorderLayout());
        lichSuPanel.setBorder(BorderFactory.createTitledBorder("Lịch sử nhận lớp"));

        String[] cols = {"Mã phiếu","Mã lớp","Môn học","Ngày giao","Số tiền phí","Trạng thái"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(22);

        // Truy vấn lịch sử
        String sql = "SELECT p.MaPhieu, p.MaLop, l.MonHoc, p.NgayGiao, " +
                     "p.SoTienCanDong, p.TrangThaiThanhToan " +
                     "FROM PhieuGiaoLop p JOIN LopHoc l ON p.MaLop = l.MaLop " +
                     "WHERE p.MaGS = ? ORDER BY p.NgayGiao DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, gs.getMaGS());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("MaPhieu"),
                        rs.getString("MaLop"),
                        rs.getString("MonHoc"),
                        rs.getDate("NgayGiao"),
                        String.format("%,.0f đ", rs.getDouble("SoTienCanDong")),
                        rs.getString("TrangThaiThanhToan")
                    });
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }

        lichSuPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        add(lichSuPanel, BorderLayout.CENTER);

        JButton btnDong = new JButton("Đóng");
        btnDong.addActionListener(e -> dispose());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnDong);
        add(bottom, BorderLayout.SOUTH);
    }

    private void addField(JPanel p, String label, String value) {
        JLabel lbl = new JLabel(label); lbl.setFont(new Font("Arial", Font.BOLD, 12));
        p.add(lbl);
        p.add(new JLabel(value != null ? value : ""));
    }
}
