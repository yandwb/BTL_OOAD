package view;

import controller.GiaoLopController;
import model.LopHoc;
import util.SessionManager;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/** Màn hình dành cho Gia sư: xem lớp chờ giao & đăng ký */
public class LopChoGiaoPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private GiaoLopController ctrl = new GiaoLopController();

    public LopChoGiaoPanel() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnLamMoi = new JButton("🔄 Làm mới");
        JButton btnDangKy = new JButton("✋ Đăng ký nhận lớp");
        top.add(btnLamMoi); top.add(btnDangKy);
        add(top, BorderLayout.NORTH);

        String[] cols = {"Mã lớp","Môn học","Cấp","Buổi/tuần","Thời gian","Lương/tháng","Địa chỉ","Yêu cầu"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        add(new JScrollPane(table), BorderLayout.CENTER);

        btnLamMoi.addActionListener(e -> taiDuLieu());
        btnDangKy.addActionListener(e -> xuLyDangKy());

        taiDuLieu();
    }

    private void taiDuLieu() {
        tableModel.setRowCount(0);
        List<LopHoc> list = ctrl.danhSachChoGiao();
        for (LopHoc lop : list) {
            tableModel.addRow(new Object[]{
                lop.getMaLop(), lop.getMonHoc(), lop.getCapHoc(),
                lop.getSoBuoiTuan() + " buổi", lop.getThoiGianHoc(),
                String.format("%,.0f đ", lop.getMucLuong()),
                lop.getDiaChiHoc(), lop.getYeuCauKhac()
            });
        }
    }

    private void xuLyDangKy() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn lớp muốn đăng ký!"); return; }
        String maLop = (String) tableModel.getValueAt(row, 0);
        String maGS  = SessionManager.getInstance().getMaNguoiDung();
        int c = JOptionPane.showConfirmDialog(this,
            "Bạn muốn đăng ký lớp " + maLop + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            String msg = ctrl.dangKyNhanLop(maLop, maGS);
            JOptionPane.showMessageDialog(this, msg);
        }
    }
}
