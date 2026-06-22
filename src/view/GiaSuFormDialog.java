package view;

import controller.GiaSuController;
import model.GiaSu;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;

public class GiaSuFormDialog extends JDialog {

    private JTextField  txtMaGS, txtHoTen, txtSDT, txtEmail, txtTruong, txtNganh;
    private JComboBox<String> cbGioiTinh;
    private JSpinner    spNgaySinh;
    private JButton     btnLuu, btnHuy;

    private GiaSu            giaSu;      // null = thêm mới
    private GiaSuController  ctrl;

    public GiaSuFormDialog(JFrame parent, GiaSu gs, GiaSuController ctrl) {
        super(parent, gs == null ? "Thêm mới Gia sư" : "Cập nhật Gia sư", true);
        this.giaSu = gs;
        this.ctrl  = ctrl;
        initUI();
        if (gs != null) dieuKien();
        setSize(450, 420);
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        panel.add(new JLabel("Mã GS:")); txtMaGS = new JTextField();
        if (giaSu == null) txtMaGS.setText(ctrl.sinhMaGS());
        txtMaGS.setEditable(false);
        panel.add(txtMaGS);

        panel.add(new JLabel("Họ tên (*):"));  txtHoTen = new JTextField(); panel.add(txtHoTen);
        panel.add(new JLabel("Giới tính:"));
        cbGioiTinh = new JComboBox<>(new String[]{"Nam","Nữ"}); panel.add(cbGioiTinh);

        panel.add(new JLabel("SĐT (*):"));     txtSDT   = new JTextField(); panel.add(txtSDT);
        panel.add(new JLabel("Email:"));        txtEmail = new JTextField(); panel.add(txtEmail);
        panel.add(new JLabel("Trường ĐH:"));   txtTruong= new JTextField(); panel.add(txtTruong);
        panel.add(new JLabel("Chuyên ngành:")); txtNganh = new JTextField(); panel.add(txtNganh);

        // Nút
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnLuu = new JButton("💾 Lưu");
        btnHuy = new JButton("Hủy");
        btnPanel.add(btnLuu); btnPanel.add(btnHuy);

        add(panel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        btnLuu.addActionListener(e -> xuLyLuu());
        btnHuy.addActionListener(e -> dispose());
    }

    private void dieuKien() {
        txtHoTen.setText(giaSu.getHoTen());
        cbGioiTinh.setSelectedItem(giaSu.getGioiTinh());
        txtSDT.setText(giaSu.getSdt());
        txtEmail.setText(giaSu.getEmail());
        txtTruong.setText(giaSu.getTruongDH());
        txtNganh.setText(giaSu.getChuyenNganh());
    }

    private void xuLyLuu() {
        String hoTen = txtHoTen.getText().trim();
        String sdt   = txtSDT.getText().trim();

        if (hoTen.isEmpty() || sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Họ tên và SĐT không được để trống!");
            return;
        }

        boolean ok;
        if (giaSu == null) {
            // Thêm mới
            GiaSu gs = new GiaSu();
            gs.setMaGS(txtMaGS.getText());
            gs.setHoTen(hoTen);
            gs.setGioiTinh((String) cbGioiTinh.getSelectedItem());
            gs.setSdt(sdt);
            gs.setEmail(txtEmail.getText().trim());
            gs.setTruongDH(txtTruong.getText().trim());
            gs.setChuyenNganh(txtNganh.getText().trim());
            gs.setTrangThaiDuyet("ChoDuyet");
            // Tạo tài khoản mặc định = MaGS, mật khẩu = sdt
            gs.setTenDangNhap(gs.getMaGS().toLowerCase());
            ok = ctrl.themMoi(gs, gs.getTenDangNhap(), sdt);
        } else {
            // Cập nhật
            giaSu.setHoTen(hoTen);
            giaSu.setGioiTinh((String) cbGioiTinh.getSelectedItem());
            giaSu.setSdt(sdt);
            giaSu.setEmail(txtEmail.getText().trim());
            giaSu.setTruongDH(txtTruong.getText().trim());
            giaSu.setChuyenNganh(txtNganh.getText().trim());
            ok = ctrl.capNhat(giaSu);
        }

        JOptionPane.showMessageDialog(this, ok ? "Lưu thành công!" : "Có lỗi xảy ra!");
        if (ok) dispose();
    }
}
