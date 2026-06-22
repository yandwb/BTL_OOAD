package view;

import controller.GiaSuController;
import model.GiaSu;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Màn hình Quản lý Gia sư
 */
public class GiaSuPanel extends JPanel {

    private JTextField  txtTimKiem;
    private JComboBox<String> cbTrangThai;
    private JTable      table;
    private DefaultTableModel tableModel;

    private GiaSuController controller = new GiaSuController();

    public GiaSuPanel() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initToolbar();
        initTable();
        taiDuLieu();
    }

    private void initToolbar() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));

        top.add(new JLabel("Tìm kiếm:"));
        txtTimKiem = new JTextField(16);
        top.add(txtTimKiem);

        top.add(new JLabel("Trạng thái:"));
        cbTrangThai = new JComboBox<>(new String[]{"Tất cả","ChoDuyet","DaDuyet","TuChoi","DaKhoa"});
        top.add(cbTrangThai);

        JButton btnTim   = new JButton("🔍 Tìm");
        JButton btnThem  = new JButton("➕ Thêm mới");
        JButton btnDuyet = new JButton("✔ Duyệt hồ sơ");
        JButton btnTuChoi = new JButton("✖ Từ chối");
        JButton btnKhoa  = new JButton("🔒 Khóa");
        JButton btnSua   = new JButton("✏ Sửa");

        top.add(btnTim); top.add(btnThem); top.add(btnDuyet);
        top.add(btnTuChoi); top.add(btnKhoa); top.add(btnSua);

        add(top, BorderLayout.NORTH);

        btnTim.addActionListener(e -> taiDuLieu());
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) taiDuLieu();
            }
        });
        btnThem.addActionListener(e   -> moFormThem());
        btnSua.addActionListener(e    -> moFormSua());
        btnDuyet.addActionListener(e  -> xuLyDuyet("DaDuyet"));
        btnTuChoi.addActionListener(e -> xuLyDuyet("TuChoi"));
        btnKhoa.addActionListener(e   -> xuLyKhoa());
    }

    private void initTable() {
        String[] cols = {"Mã GS","Họ tên","SĐT","Email","Trường ĐH","Chuyên ngành","Trạng thái","TK"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // Màu trạng thái
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) {
                    String tt = (String) tableModel.getValueAt(row, 6);
                    if ("DaDuyet".equals(tt))   c.setBackground(new Color(220, 255, 220));
                    else if ("TuChoi".equals(tt)) c.setBackground(new Color(255, 220, 220));
                    else if ("DaKhoa".equals(tt)) c.setBackground(new Color(220, 220, 220));
                    else                          c.setBackground(Color.WHITE);
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);
    }

    private void taiDuLieu() {
        tableModel.setRowCount(0);
        String kw = txtTimKiem.getText().trim();
        String tt = (String) cbTrangThai.getSelectedItem();
        List<GiaSu> list = controller.timKiem(kw, tt);
        for (GiaSu gs : list) {
            tableModel.addRow(new Object[]{
                gs.getMaGS(), gs.getHoTen(), gs.getSdt(), gs.getEmail(),
                gs.getTruongDH(), gs.getChuyenNganh(), gs.getTrangThaiDuyet(), gs.getTenDangNhap()
            });
        }
    }

    private GiaSu getGiaSuDuocChon() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn gia sư trong danh sách!"); return null; }
        String maGS = (String) tableModel.getValueAt(row, 0);
        return controller.layTheoMa(maGS);
    }

    private void moFormThem() {
        GiaSuFormDialog dlg = new GiaSuFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), null, controller);
        dlg.setVisible(true);
        taiDuLieu();
    }

    private void moFormSua() {
        GiaSu gs = getGiaSuDuocChon();
        if (gs == null) return;
        GiaSuFormDialog dlg = new GiaSuFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), gs, controller);
        dlg.setVisible(true);
        taiDuLieu();
    }

    private void xuLyDuyet(String trangThai) {
        GiaSu gs = getGiaSuDuocChon();
        if (gs == null) return;
        int confirm = JOptionPane.showConfirmDialog(this,
            "Xác nhận " + (trangThai.equals("DaDuyet") ? "DUYỆT" : "TỪ CHỐI") + " hồ sơ gia sư: " + gs.getHoTen() + "?",
            "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = controller.capNhatTrangThai(gs.getMaGS(), trangThai);
            JOptionPane.showMessageDialog(this, ok ? "Thực hiện thành công!" : "Thất bại!");
            taiDuLieu();
        }
    }

    private void xuLyKhoa() {
        GiaSu gs = getGiaSuDuocChon();
        if (gs == null) return;
        String tb = controller.kiemTraTruocKhoa(gs.getMaGS());
        if (tb != null) { JOptionPane.showMessageDialog(this, tb, "Không thể khóa", JOptionPane.WARNING_MESSAGE); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Khóa tài khoản gia sư: " + gs.getHoTen() + "?",
            "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.capNhatTrangThai(gs.getMaGS(), "DaKhoa");
            taiDuLieu();
        }
    }
}
