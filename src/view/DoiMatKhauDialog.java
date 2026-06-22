package view;

import controller.AuthController;

import javax.swing.*;
import java.awt.*;

/**
 * UC01.4 – Đổi mật khẩu (khi đã đăng nhập)
 */
public class DoiMatKhauDialog extends JDialog {

    private JPasswordField txtCu, txtMoi, txtXacNhan;
    private AuthController ctrl;

    public DoiMatKhauDialog(JFrame parent, AuthController ctrl) {
        super(parent, "Đổi mật khẩu", true);
        this.ctrl = ctrl;
        initUI();
        setSize(380, 240);
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        JPanel p = new JPanel(new GridLayout(0, 2, 8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        p.add(new JLabel("Mật khẩu hiện tại:")); txtCu      = new JPasswordField(); p.add(txtCu);
        p.add(new JLabel("Mật khẩu mới:"));      txtMoi     = new JPasswordField(); p.add(txtMoi);
        p.add(new JLabel("Xác nhận MK mới:"));   txtXacNhan = new JPasswordField(); p.add(txtXacNhan);

        JPanel btn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton bLuu = new JButton("💾 Lưu thay đổi");
        JButton bHuy = new JButton("Hủy");
        btn.add(bLuu); btn.add(bHuy);

        add(p, BorderLayout.CENTER); add(btn, BorderLayout.SOUTH);
        bLuu.addActionListener(e -> xuLy());
        bHuy.addActionListener(e -> dispose());
    }

    private void xuLy() {
        String cu      = new String(txtCu.getPassword()).trim();
        String moi     = new String(txtMoi.getPassword()).trim();
        String xacNhan = new String(txtXacNhan.getPassword()).trim();

        String err = ctrl.doiMatKhau(cu, moi, xacNhan);
        if (err == null) {
            JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, err, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
