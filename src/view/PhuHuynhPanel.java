package view;

import controller.PhuHuynhController;
import model.PhuHuynh;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class PhuHuynhPanel extends JPanel {

    private JTextField txtTimKiem;
    private JTable     table;
    private DefaultTableModel tableModel;
    private PhuHuynhController ctrl = new PhuHuynhController();

    public PhuHuynhPanel() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initToolbar();
        initTable();
        taiDuLieu();
    }

    private void initToolbar() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        top.add(new JLabel("Tìm (tên/SĐT):"));
        txtTimKiem = new JTextField(16);
        top.add(txtTimKiem);

        JButton btnTim  = new JButton("🔍 Tìm");
        JButton btnThem = new JButton("➕ Thêm");
        JButton btnSua  = new JButton("✏ Sửa");
        JButton btnKhoa = new JButton("🔒 Khóa");

        top.add(btnTim); top.add(btnThem); top.add(btnSua); top.add(btnKhoa);
        add(top, BorderLayout.NORTH);

        btnTim.addActionListener(e -> taiDuLieu());
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) taiDuLieu();
            }
        });
        btnThem.addActionListener(e -> moForm(null));
        btnSua.addActionListener(e  -> {
            PhuHuynh ph = getDuocChon();
            if (ph != null) moForm(ph);
        });
        btnKhoa.addActionListener(e -> xuLyKhoa());
    }

    private void initTable() {
        String[] cols = {"Mã PH","Họ tên","SĐT","Địa chỉ","Ghi chú","Trạng thái"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void taiDuLieu() {
        tableModel.setRowCount(0);
        List<PhuHuynh> list = ctrl.timKiem(txtTimKiem.getText().trim());
        for (PhuHuynh ph : list) {
            tableModel.addRow(new Object[]{
                ph.getMaPH(), ph.getHoTen(), ph.getSdt(),
                ph.getDiaChi(), ph.getGhiChu(), ph.getTrangThai()
            });
        }
    }

    private PhuHuynh getDuocChon() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn phụ huynh trong danh sách!"); return null; }
        return ctrl.layTheoMa((String) tableModel.getValueAt(row, 0));
    }

    private void moForm(PhuHuynh ph) {
        PhuHuynhFormDialog dlg = new PhuHuynhFormDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), ph, ctrl);
        dlg.setVisible(true);
        taiDuLieu();
    }

    private void xuLyKhoa() {
        PhuHuynh ph = getDuocChon();
        if (ph == null) return;
        String msg = ctrl.kiemTraTruocKhoa(ph.getMaPH());
        if (msg != null) { JOptionPane.showMessageDialog(this, msg, "Không thể khóa", JOptionPane.WARNING_MESSAGE); return; }
        int c = JOptionPane.showConfirmDialog(this, "Khóa tài khoản: " + ph.getHoTen() + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) { ctrl.khoa(ph.getMaPH()); taiDuLieu(); }
    }
}
