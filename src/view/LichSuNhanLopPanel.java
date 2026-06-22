package view;

import controller.GiaoLopController;
import model.PhieuGiaoLop;
import util.SessionManager;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/** Màn hình Gia sư: lịch sử lớp đã nhận */
public class LichSuNhanLopPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private GiaoLopController ctrl = new GiaoLopController();

    public LichSuNhanLopPanel() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnLamMoi = new JButton("🔄 Làm mới");
        top.add(btnLamMoi);
        add(top, BorderLayout.NORTH);

        String[] cols = {"Mã phiếu","Mã lớp","Môn học","Ngày giao","Tỉ lệ %","Số tiền phải đóng","Đã hoàn","Trạng thái"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        add(new JScrollPane(table), BorderLayout.CENTER);

        btnLamMoi.addActionListener(e -> taiDuLieu());
        taiDuLieu();
    }

    private void taiDuLieu() {
        tableModel.setRowCount(0);
        String maGS = SessionManager.getInstance().getMaNguoiDung();
        List<PhieuGiaoLop> list = ctrl.lichSuNhanLop(maGS);
        for (PhieuGiaoLop p : list) {
            tableModel.addRow(new Object[]{
                p.getMaPhieu(), p.getMaLop(), p.getMonHoc(),
                p.getNgayGiao(), p.getTyLePhi() + "%",
                String.format("%,.0f đ", p.getSoTienCanDong()),
                String.format("%,.0f đ", p.getSoTienHoanLai()),
                p.getTrangThaiThanhToan()
            });
        }
    }
}
