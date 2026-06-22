package view;

import controller.AuthController;
import util.SessionManager;

import javax.swing.*;
import java.awt.*;

/**
 * Cửa sổ chính – điều hướng theo vai trò
 */
public class MainForm extends JFrame {

    private JTabbedPane    tabs;
    private SessionManager session      = SessionManager.getInstance();
    private AuthController authCtrl;

    public MainForm(AuthController authCtrl) {
        this.authCtrl = authCtrl;
        initUI();
    }

    private void initUI() {
        String tenNguoiDung = session.getCurrentUser().getTenDangNhap();
        String vaiTro       = session.getCurrentUser().getVaiTro();

        setTitle("HTQLTTGS – " + vaiTro + "  [" + tenNguoiDung + "]");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 720);
        setLocationRelativeTo(null);

        // ── Toolbar ──────────────────────────────────
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        toolbar.setBackground(new Color(30, 80, 160));

        JLabel lblUser = new JLabel("  Xin chào: " + tenNguoiDung + "  (" + vaiTro + ")  ");
        lblUser.setForeground(Color.WHITE);
        lblUser.setFont(new Font("Arial", Font.BOLD, 12));
        toolbar.add(lblUser);

        JButton btnDoiMK = new JButton("🔑 Đổi mật khẩu");
        btnDoiMK.setFont(new Font("Arial", Font.PLAIN, 12));
        btnDoiMK.addActionListener(e -> new DoiMatKhauDialog(this, authCtrl).setVisible(true));
        toolbar.add(btnDoiMK);

        JButton btnDangXuat = new JButton("Đăng xuất");
        btnDangXuat.addActionListener(e -> {
            session.dangXuat();
            new DangNhapForm().setVisible(true);
            dispose();
        });
        toolbar.add(btnDangXuat);

        add(toolbar, BorderLayout.NORTH);

        // ── Tabs ─────────────────────────────────────
        tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.BOLD, 12));

        if (session.isNhanVien()) {
            tabs.addTab("🏠 Trang chủ",     new HomePanel());
            tabs.addTab("👨‍🏫 Gia sư",       new GiaSuPanel());
            tabs.addTab("👨‍👩‍👧 Phụ huynh",  new PhuHuynhPanel());
            tabs.addTab("📚 Lớp học",        new LopHocPanel());
            tabs.addTab("📋 Giao lớp",       new GiaoLopPanel());
        }

        if (session.isGiaSu()) {
            tabs.addTab("📚 Lớp chờ giao",   new LopChoGiaoPanel());
            tabs.addTab("📋 Lớp của tôi",    new LichSuNhanLopPanel());
        }

        if (session.isAdmin()) {
            tabs.addTab("📊 Thống kê",        new ThongKePanel());
        }

        add(tabs, BorderLayout.CENTER);
    }
}
