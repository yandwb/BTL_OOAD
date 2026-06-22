package view;

import controller.AuthController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Màn hình đăng nhập – UC01.1 / UC01.3
 */
public class DangNhapForm extends JFrame {

    private JTextField     txtTenDangNhap;
    private JPasswordField txtMatKhau;
    private JButton        btnDangNhap, btnQuenMK;
    private JLabel         lblThongBao;
    private AuthController authController = new AuthController();

    public DangNhapForm() { initUI(); }

    private void initUI() {
        setTitle("HTQLTTGS – Đăng nhập");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(440, 340);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 35, 20, 35));
        panel.setBackground(new Color(245, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Logo/Tiêu đề
        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ TRUNG TÂM GIA SƯ", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitle.setForeground(new Color(30, 80, 160));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitle, gbc);

        JSeparator sep = new JSeparator(); gbc.gridy = 1; panel.add(sep, gbc);

        gbc.gridwidth = 1; gbc.gridy = 2; gbc.gridx = 0;
        panel.add(new JLabel("Tên đăng nhập:"), gbc);
        txtTenDangNhap = new JTextField(18); gbc.gridx = 1; panel.add(txtTenDangNhap, gbc);

        gbc.gridy = 3; gbc.gridx = 0; panel.add(new JLabel("Mật khẩu:"), gbc);
        txtMatKhau = new JPasswordField(18); gbc.gridx = 1; panel.add(txtMatKhau, gbc);

        lblThongBao = new JLabel(" ", SwingConstants.CENTER);
        lblThongBao.setForeground(Color.RED);
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2; panel.add(lblThongBao, gbc);

        btnDangNhap = new JButton("Đăng nhập");
        btnDangNhap.setBackground(new Color(30, 80, 160));
        btnDangNhap.setForeground(Color.WHITE);
        btnDangNhap.setFont(new Font("Arial", Font.BOLD, 13));
        btnDangNhap.setFocusPainted(false);
        gbc.gridy = 5; panel.add(btnDangNhap, gbc);

        btnQuenMK = new JButton("Quên mật khẩu?");
        btnQuenMK.setBorderPainted(false); btnQuenMK.setContentAreaFilled(false);
        btnQuenMK.setForeground(new Color(30, 80, 160)); btnQuenMK.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridy = 6; panel.add(btnQuenMK, gbc);

        add(panel);

        btnDangNhap.addActionListener(e -> xuLyDangNhap());
        txtMatKhau.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) xuLyDangNhap();
            }
        });
        btnQuenMK.addActionListener(e -> {
            new QuenMatKhauDialog(this, authController).setVisible(true);
        });
    }

    private void xuLyDangNhap() {
        String tenDN = txtTenDangNhap.getText().trim();
        String mk    = new String(txtMatKhau.getPassword()).trim();
        if (tenDN.isEmpty() || mk.isEmpty()) { lblThongBao.setText("Vui lòng nhập đầy đủ!"); return; }
        String err = authController.dangNhap(tenDN, mk);
        if (err == null) {
            new MainForm(authController).setVisible(true);
            dispose();
        } else {
            lblThongBao.setText(err);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
            new DangNhapForm().setVisible(true);
        });
    }
}
