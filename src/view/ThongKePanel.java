package view;

import controller.ThongKeController;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class ThongKePanel extends JPanel {

    private JSpinner spTuNgay, spDenNgay;
    private JLabel   lblTongDoanhThu, lblTongLop, lblLopThanhCong, lblLopHong;
    private JTable   table;
    private DefaultTableModel tableModel;
    private ThongKeController ctrl = new ThongKeController();

    public ThongKePanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        initFilterPanel();
        initSummaryPanel();
        initTable();
    }

    private void initFilterPanel() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        top.setBorder(BorderFactory.createTitledBorder("Chọn khoảng thời gian"));

        spTuNgay = new JSpinner(new SpinnerDateModel());
        spTuNgay.setEditor(new JSpinner.DateEditor(spTuNgay, "dd/MM/yyyy"));
        spDenNgay = new JSpinner(new SpinnerDateModel());
        spDenNgay.setEditor(new JSpinner.DateEditor(spDenNgay, "dd/MM/yyyy"));

        top.add(new JLabel("Từ ngày:")); top.add(spTuNgay);
        top.add(new JLabel("Đến ngày:")); top.add(spDenNgay);
        JButton btnThongKe = new JButton("📊 Thống kê");
        top.add(btnThongKe);
        add(top, BorderLayout.NORTH);

        btnThongKe.addActionListener(e -> thongKe());
    }

    private void initSummaryPanel() {
        JPanel sum = new JPanel(new GridLayout(1, 4, 10, 0));
        sum.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        lblTongDoanhThu  = taoCard("Tổng doanh thu", "0 đ",        new Color(40, 120, 200));
        lblTongLop       = taoCard("Tổng số lớp",    "0",          new Color(80, 160, 80));
        lblLopThanhCong  = taoCard("Lớp thành công", "0",          new Color(60, 180, 120));
        lblLopHong       = taoCard("Lớp bị hỏng",    "0",          new Color(200, 80, 80));

        sum.add(lblTongDoanhThu.getParent());
        sum.add(lblTongLop.getParent());
        sum.add(lblLopThanhCong.getParent());
        sum.add(lblLopHong.getParent());
        add(sum, BorderLayout.CENTER);
    }

    private JLabel taoCard(String title, String value, Color color) {
        JPanel card = new JPanel(new GridLayout(2, 1));
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 12));
        JLabel lblValue = new JLabel(value, SwingConstants.CENTER);
        lblValue.setForeground(Color.WHITE);
        lblValue.setFont(new Font("Arial", Font.BOLD, 18));
        card.add(lblTitle); card.add(lblValue);
        return lblValue;
    }

    private void initTable() {
        String[] cols = {"Mã phiếu","Gia sư","Môn học","Ngày giao","Số tiền","Trạng thái"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(22);
        add(new JScrollPane(table), BorderLayout.SOUTH);
    }

    private void thongKe() {
        java.util.Date tuNgayUtil  = (java.util.Date) spTuNgay.getValue();
        java.util.Date denNgayUtil = (java.util.Date) spDenNgay.getValue();
        if (tuNgayUtil.after(denNgayUtil)) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu không thể sau ngày kết thúc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Date tuNgay  = new Date(tuNgayUtil.getTime());
        Date denNgay = new Date(denNgayUtil.getTime());

        Map<String, Object> ketQua = ctrl.thongKe(tuNgay, denNgay);

        double tongDT = (double) ketQua.get("tongDoanhThu");
        int    tongLop = (int) ketQua.get("tongLop");
        int    lopTC   = (int) ketQua.get("lopThanhCong");
        int    lopHong  = (int) ketQua.get("lopHong");

        lblTongDoanhThu.setText(String.format("%,.0f đ", tongDT));
        lblTongLop.setText(String.valueOf(tongLop));
        lblLopThanhCong.setText(String.valueOf(lopTC));
        lblLopHong.setText(String.valueOf(lopHong));

        // Bảng chi tiết
        tableModel.setRowCount(0);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> chiTiet = (List<Map<String, Object>>) ketQua.get("chiTiet");
        for (Map<String, Object> row : chiTiet) {
            tableModel.addRow(new Object[]{
                row.get("maPhieu"), row.get("tenGiaSu"), row.get("monHoc"),
                row.get("ngayGiao"),
                String.format("%,.0f đ", row.get("soTien")),
                row.get("trangThai")
            });
        }
    }
}
