package view;

import controller.LopHocController;
import model.LopHoc;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class LopHocPanel extends JPanel {

    private JTextField txtTimKiem;
    private JComboBox<String> cbTrangThai;
    private JTable table;
    private DefaultTableModel tableModel;
    private LopHocController ctrl = new LopHocController();

    public LopHocPanel() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initToolbar();
        initTable();
        taiDuLieu();
    }

    private void initToolbar() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        top.add(new JLabel("Tìm:"));
        txtTimKiem = new JTextField(14);
        top.add(txtTimKiem);
        top.add(new JLabel("Trạng thái:"));
        cbTrangThai = new JComboBox<>(new String[]{"Tất cả","ChoGiao","DaGiao","Huy","Hong"});
        top.add(cbTrangThai);

        JButton btnTim  = new JButton("🔍 Tìm");
        JButton btnThem = new JButton("➕ Thêm lớp");
        JButton btnSua  = new JButton("✏ Sửa");
        JButton btnHuy  = new JButton("🚫 Hủy lớp");

        top.add(btnTim); top.add(btnThem); top.add(btnSua); top.add(btnHuy);
        add(top, BorderLayout.NORTH);

        btnTim.addActionListener(e -> taiDuLieu());
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) taiDuLieu();
            }
        });
        btnThem.addActionListener(e -> moForm(null));
        btnSua.addActionListener(e  -> { LopHoc lop = getDuocChon(); if (lop != null) moForm(lop); });
        btnHuy.addActionListener(e  -> xuLyHuy());
    }

    private void initTable() {
        String[] cols = {"Mã Lớp","Phụ huynh","SĐT PH","Môn học","Cấp","Buổi/tuần","Lương/tháng","Địa chỉ","Trạng thái"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) {
                    String tt = (String) tableModel.getValueAt(row, 8);
                    if ("DaGiao".equals(tt))  c.setBackground(new Color(220, 255, 220));
                    else if ("Huy".equals(tt)||"Hong".equals(tt)) c.setBackground(new Color(255, 220, 220));
                    else c.setBackground(Color.WHITE);
                }
                return c;
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void taiDuLieu() {
        tableModel.setRowCount(0);
        List<LopHoc> list = ctrl.timKiem(txtTimKiem.getText().trim(), (String) cbTrangThai.getSelectedItem());
        for (LopHoc lop : list) {
            tableModel.addRow(new Object[]{
                lop.getMaLop(), lop.getTenPhuHuynh(), lop.getSdtPhuHuynh(),
                lop.getMonHoc(), lop.getCapHoc(), lop.getSoBuoiTuan() + " buổi",
                String.format("%,.0f đ", lop.getMucLuong()),
                lop.getDiaChiHoc(), lop.getTrangThaiLop()
            });
        }
    }

    private LopHoc getDuocChon() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn lớp học trong danh sách!"); return null; }
        return ctrl.layTheoMa((String) tableModel.getValueAt(row, 0));
    }

    private void moForm(LopHoc lop) {
        LopHocFormDialog dlg = new LopHocFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), lop, ctrl);
        dlg.setVisible(true);
        taiDuLieu();
    }

    private void xuLyHuy() {
        LopHoc lop = getDuocChon();
        if (lop == null) return;
        if (!"ChoGiao".equals(lop.getTrangThaiLop())) {
            JOptionPane.showMessageDialog(this, "Chỉ có thể hủy lớp đang ở trạng thái 'Chờ giao'!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int c = JOptionPane.showConfirmDialog(this, "Hủy lớp: " + lop.getMaLop() + " - " + lop.getMonHoc() + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            ctrl.capNhatTrangThai(lop.getMaLop(), "Huy");
            taiDuLieu();
        }
    }
}
