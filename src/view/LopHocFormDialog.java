package view;

import controller.LopHocController;
import model.LopHoc;
import model.PhuHuynh;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LopHocFormDialog extends JDialog {

    private JTextField txtMaLop, txtMonHoc, txtCapHoc, txtBuoi, txtLuong, txtDiaChi, txtYeuCau, txtThoiGian;
    private JComboBox<String> cbPhuHuynh;
    private List<PhuHuynh>   dsPhuHuynh;

    private LopHoc         lop;
    private LopHocController ctrl;

    public LopHocFormDialog(JFrame parent, LopHoc lop, LopHocController ctrl) {
        super(parent, lop == null ? "Tạo lớp mới" : "Cập nhật lớp", true);
        this.lop  = lop;
        this.ctrl = ctrl;
        initUI();
        if (lop != null) dieuKien();
        setSize(480, 480);
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        JPanel p = new JPanel(new GridLayout(0, 2, 8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        p.add(new JLabel("Mã lớp:")); txtMaLop = new JTextField();
        if (lop == null) txtMaLop.setText(ctrl.sinhMaLop());
        txtMaLop.setEditable(false);
        p.add(txtMaLop);

        p.add(new JLabel("Phụ huynh (*):"));
        dsPhuHuynh = ctrl.danhSachPhuHuynh();
        String[] dsStr = dsPhuHuynh.stream().map(ph -> ph.getMaPH() + " - " + ph.getHoTen() + " (" + ph.getSdt() + ")")
                         .toArray(String[]::new);
        cbPhuHuynh = new JComboBox<>(dsStr);
        p.add(cbPhuHuynh);

        p.add(new JLabel("Môn học (*):")); txtMonHoc = new JTextField(); p.add(txtMonHoc);
        p.add(new JLabel("Cấp học (*):")); txtCapHoc = new JTextField(); p.add(txtCapHoc);
        p.add(new JLabel("Số buổi/tuần (*):")); txtBuoi = new JTextField(); p.add(txtBuoi);
        p.add(new JLabel("Thời gian học:")); txtThoiGian = new JTextField(); p.add(txtThoiGian);
        p.add(new JLabel("Lương/tháng (VNĐ) (*):")); txtLuong = new JTextField(); p.add(txtLuong);
        p.add(new JLabel("Địa chỉ dạy (*):")); txtDiaChi = new JTextField(); p.add(txtDiaChi);
        p.add(new JLabel("Yêu cầu khác:")); txtYeuCau = new JTextField(); p.add(txtYeuCau);

        JPanel btn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton bLuu = new JButton("💾 Lưu"); JButton bHuy = new JButton("Hủy");
        btn.add(bLuu); btn.add(bHuy);

        add(p, BorderLayout.CENTER); add(btn, BorderLayout.SOUTH);
        bLuu.addActionListener(e -> xuLyLuu());
        bHuy.addActionListener(e -> dispose());
    }

    private void dieuKien() {
        txtMonHoc.setText(lop.getMonHoc());
        txtCapHoc.setText(lop.getCapHoc());
        txtBuoi.setText(String.valueOf(lop.getSoBuoiTuan()));
        txtThoiGian.setText(lop.getThoiGianHoc());
        txtLuong.setText(String.valueOf((long) lop.getMucLuong()));
        txtDiaChi.setText(lop.getDiaChiHoc());
        txtYeuCau.setText(lop.getYeuCauKhac());
        // Chọn phụ huynh tương ứng
        for (int i = 0; i < dsPhuHuynh.size(); i++) {
            if (dsPhuHuynh.get(i).getMaPH().equals(lop.getMaPH())) {
                cbPhuHuynh.setSelectedIndex(i); break;
            }
        }
    }

    private void xuLyLuu() {
        String monHoc = txtMonHoc.getText().trim();
        String capHoc = txtCapHoc.getText().trim();
        String diaChiHoc = txtDiaChi.getText().trim();
        if (monHoc.isEmpty() || capHoc.isEmpty() || diaChiHoc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ các trường bắt buộc (*)!"); return;
        }
        int soBuoi; double luong;
        try {
            soBuoi = Integer.parseInt(txtBuoi.getText().trim());
            luong  = Double.parseDouble(txtLuong.getText().trim().replaceAll(",",""));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số buổi và lương phải là số hợp lệ!"); return;
        }

        PhuHuynh ph = dsPhuHuynh.get(cbPhuHuynh.getSelectedIndex());
        boolean ok;
        if (lop == null) {
            LopHoc newLop = new LopHoc(txtMaLop.getText(), ph.getMaPH(), monHoc, capHoc,
                soBuoi, txtThoiGian.getText().trim(), luong, diaChiHoc,
                txtYeuCau.getText().trim(), "ChoGiao");
            ok = ctrl.them(newLop);
        } else {
            lop.setMaPH(ph.getMaPH());
            lop.setMonHoc(monHoc); lop.setCapHoc(capHoc);
            lop.setSoBuoiTuan(soBuoi); lop.setThoiGianHoc(txtThoiGian.getText().trim());
            lop.setMucLuong(luong); lop.setDiaChiHoc(diaChiHoc);
            lop.setYeuCauKhac(txtYeuCau.getText().trim());
            ok = ctrl.capNhat(lop);
        }
        JOptionPane.showMessageDialog(this, ok ? "Lưu thành công!" : "Có lỗi xảy ra!");
        if (ok) dispose();
    }
}
