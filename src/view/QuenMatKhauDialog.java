package view;

import controller.AuthController;

import javax.swing.*;
import java.awt.*;

/**
 * UC01.3 – Quên mật khẩu
 */
public class QuenMatKhauDialog extends JDialog {

    private JTextField     txtTenDN, txtSDT, txtMatKhauMoi, txtXacNhan;
    private AuthController ctrl;

    public QuenMatKhauDialog(JFrame parent, AuthController ctrl) {
        super(parent, "Quên mật khẩu", true);
        this.ctrl = ctrl;
        initUI();
        setSize(400, 280);
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        JPanel p = new JPanel(new GridLayout(0, 2, 8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        p.add(new JLabel("Tên đăng nhập:"));  txtTenDN     = new JTextField(); p.add(txtTenDN);
        p.add(new JLabel("Số điện thoại:"));  txtSDT       = new JTextField(); p.add(txtSDT);
        p.add(new JLabel("Mật khẩu mới:"));   txtMatKhauMoi= new JPasswordField(); p.add(txtMatKhauMoi);
        p.add(new JLabel("Xác nhận MK mới:")); txtXacNhan  = new JPasswordField(); p.add(txtXacNhan);

        JPanel btn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton bDoi = new JButton("Đặt lại mật khẩu");
        JButton bHuy = new JButton("Hủy");
        btn.add(bDoi); btn.add(bHuy);

        add(p, BorderLayout.CENTER); add(btn, BorderLayout.SOUTH);

        bDoi.addActionListener(e -> xuLy());
        bHuy.addActionListener(e -> dispose());
    }

    private void xuLy() {
        String tenDN    = txtTenDN.getText().trim();
        String sdt      = txtSDT.getText().trim();
        String mk       = txtMatKhauMoi.getText().trim();
        String xacNhan  = txtXacNhan.getText().trim();

        if (tenDN.isEmpty() || sdt.isEmpty() || mk.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!"); return;
        }
        if (!mk.equals(xacNhan)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu mới và xác nhận không khớp!"); return;
        }

        String err = ctrl.quenMatKhau(tenDN, sdt, mk);
        if (err == null) {
            JOptionPane.showMessageDialog(this, "Đặt lại mật khẩu thành công!\nVui lòng đăng nhập lại.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, err, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
