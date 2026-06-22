package view;

import controller.PhuHuynhController;
import model.PhuHuynh;

import javax.swing.*;
import java.awt.*;

public class PhuHuynhFormDialog extends JDialog {

    private JTextField txtMaPH, txtHoTen, txtSDT, txtDiaChi, txtGhiChu;
    private PhuHuynh            ph;
    private PhuHuynhController  ctrl;

    public PhuHuynhFormDialog(JFrame parent, PhuHuynh ph, PhuHuynhController ctrl) {
        super(parent, ph == null ? "Thêm mới Phụ huynh" : "Sửa Phụ huynh", true);
        this.ph   = ph;
        this.ctrl = ctrl;
        initUI();
        if (ph != null) dieuKien();
        setSize(400, 320);
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        JPanel p = new JPanel(new GridLayout(0, 2, 8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        p.add(new JLabel("Mã PH:")); txtMaPH = new JTextField();
        if (ph == null) txtMaPH.setText(ctrl.sinhMaPH());
        txtMaPH.setEditable(false);
        p.add(txtMaPH);

        p.add(new JLabel("Họ tên (*):")); txtHoTen = new JTextField(); p.add(txtHoTen);
        p.add(new JLabel("SĐT (*):")); txtSDT = new JTextField(); p.add(txtSDT);
        p.add(new JLabel("Địa chỉ:")); txtDiaChi = new JTextField(); p.add(txtDiaChi);
        p.add(new JLabel("Ghi chú:")); txtGhiChu = new JTextField(); p.add(txtGhiChu);

        JPanel btn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton bLuu = new JButton("💾 Lưu"); JButton bHuy = new JButton("Hủy");
        btn.add(bLuu); btn.add(bHuy);

        add(p, BorderLayout.CENTER); add(btn, BorderLayout.SOUTH);
        bLuu.addActionListener(e -> xuLyLuu());
        bHuy.addActionListener(e -> dispose());
    }

    private void dieuKien() {
        txtHoTen.setText(ph.getHoTen());
        txtSDT.setText(ph.getSdt());
        txtDiaChi.setText(ph.getDiaChi());
        txtGhiChu.setText(ph.getGhiChu());
    }

    private void xuLyLuu() {
        String hoTen = txtHoTen.getText().trim();
        String sdt   = txtSDT.getText().trim();
        if (hoTen.isEmpty() || sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Họ tên và SĐT!"); return;
        }
        boolean ok;
        if (ph == null) {
            PhuHuynh newPH = new PhuHuynh(txtMaPH.getText(), hoTen, sdt,
                txtDiaChi.getText().trim(), txtGhiChu.getText().trim(), "HoatDong");
            ok = ctrl.them(newPH);
        } else {
            ph.setHoTen(hoTen); ph.setSdt(sdt);
            ph.setDiaChi(txtDiaChi.getText().trim());
            ph.setGhiChu(txtGhiChu.getText().trim());
            ok = ctrl.capNhat(ph);
        }
        JOptionPane.showMessageDialog(this, ok ? "Lưu thành công!" : "Có lỗi xảy ra!");
        if (ok) dispose();
    }
}
