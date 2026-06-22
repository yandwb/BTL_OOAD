package view;

import controller.GiaoLopController;
import model.GiaSu;
import model.LopHoc;
import model.PhieuGiaoLop;
import util.SessionManager;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class GiaoLopPanel extends JPanel {

    private JTextField txtTimKiem;
    private JComboBox<String> cbTrangThai;
    private JTable table;
    private DefaultTableModel tableModel;
    private GiaoLopController ctrl = new GiaoLopController();

    public GiaoLopPanel() {
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
        cbTrangThai = new JComboBox<>(new String[]{"Tất cả","ChuaThanhToan","DaThanhToan","DaHoanPhi"});
        top.add(cbTrangThai);

        JButton btnTim       = new JButton("🔍 Tìm");
        JButton btnGiaoLop   = new JButton("📋 Duyệt giao lớp");
        JButton btnThanhToan = new JButton("💰 Xác nhận thanh toán");
        JButton btnHoanPhi   = new JButton("↩ Hoàn phí");

        top.add(btnTim); top.add(btnGiaoLop); top.add(btnThanhToan); top.add(btnHoanPhi);
        add(top, BorderLayout.NORTH);

        btnTim.addActionListener(e -> taiDuLieu());
        btnGiaoLop.addActionListener(e -> moDialogGiaoLop());
        btnThanhToan.addActionListener(e -> xuLyThanhToan());
        btnHoanPhi.addActionListener(e   -> xuLyHoanPhi());
    }

    private void initTable() {
        String[] cols = {"Mã phiếu","Mã lớp","Môn học","Gia sư","Ngày giao","Tỉ lệ %","Số tiền","Trạng thái"};
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
                    String tt = (String) tableModel.getValueAt(row, 7);
                    if ("DaThanhToan".equals(tt))  c.setBackground(new Color(220, 255, 220));
                    else if ("DaHoanPhi".equals(tt)) c.setBackground(new Color(255, 220, 220));
                    else c.setBackground(new Color(255, 255, 200));
                }
                return c;
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void taiDuLieu() {
        tableModel.setRowCount(0);
        List<PhieuGiaoLop> list = ctrl.timKiem(txtTimKiem.getText().trim(), (String) cbTrangThai.getSelectedItem());
        for (PhieuGiaoLop p : list) {
            tableModel.addRow(new Object[]{
                p.getMaPhieu(), p.getMaLop(), p.getMonHoc(),
                p.getTenGiaSu(), p.getNgayGiao(),
                p.getTyLePhi() + "%",
                String.format("%,.0f đ", p.getSoTienCanDong()),
                p.getTrangThaiThanhToan()
            });
        }
    }

    /** Chọn lớp chờ giao → chọn gia sư → tạo phiếu */
    private void moDialogGiaoLop() {
        // Bước 1: Chọn lớp đang chờ giao
        List<LopHoc> dsLop = ctrl.danhSachChoGiao();
        if (dsLop.isEmpty()) { JOptionPane.showMessageDialog(this, "Không có lớp nào đang chờ giao!"); return; }
        String[] lopItems = dsLop.stream()
            .map(l -> l.getMaLop() + " | " + l.getMonHoc() + " | " + l.getCapHoc() + " | " + l.getTenPhuHuynh())
            .toArray(String[]::new);
        String selectedLop = (String) JOptionPane.showInputDialog(this,
            "Chọn lớp cần giao:", "Duyệt giao lớp",
            JOptionPane.QUESTION_MESSAGE, null, lopItems, lopItems[0]);
        if (selectedLop == null) return;
        LopHoc lop = dsLop.get(java.util.Arrays.asList(lopItems).indexOf(selectedLop));

        // Bước 2: Hiển thị danh sách gia sư đã đăng ký
        List<GiaSu> dsGS = ctrl.danhSachGiaSuDangKy(lop.getMaLop());
        if (dsGS.isEmpty()) { JOptionPane.showMessageDialog(this, "Chưa có gia sư nào đăng ký lớp này!"); return; }
        String[] gsItems = dsGS.stream()
            .map(g -> g.getMaGS() + " - " + g.getHoTen() + " | " + g.getTruongDH() + " | " + g.getSdt())
            .toArray(String[]::new);
        String selectedGS = (String) JOptionPane.showInputDialog(this,
            "Chọn gia sư để giao lớp:", "Chọn Gia sư",
            JOptionPane.QUESTION_MESSAGE, null, gsItems, gsItems[0]);
        if (selectedGS == null) return;
        GiaSu gs = dsGS.get(java.util.Arrays.asList(gsItems).indexOf(selectedGS));

        // Bước 3: Nhập tỉ lệ phí
        String tyLeStr = JOptionPane.showInputDialog(this, "Nhập tỉ lệ phí nhận lớp (%, VD: 30):", "Lập phiếu", JOptionPane.QUESTION_MESSAGE);
        if (tyLeStr == null) return;
        double tyLe;
        try { tyLe = Double.parseDouble(tyLeStr.trim()); } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Tỉ lệ phí không hợp lệ!"); return;
        }

        String maNV = SessionManager.getInstance().getMaNguoiDung();
        String ketQua = ctrl.giaoLop(lop.getMaLop(), gs.getMaGS(), maNV, tyLe);
        JOptionPane.showMessageDialog(this, ketQua);
        taiDuLieu();
    }

    private void xuLyThanhToan() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn phiếu trong danh sách!"); return; }
        String maPhieu = (String) tableModel.getValueAt(row, 0);
        String trangThai = (String) tableModel.getValueAt(row, 7);
        if (!"ChuaThanhToan".equals(trangThai)) {
            JOptionPane.showMessageDialog(this, "Phiếu này không ở trạng thái 'Chưa thanh toán'!"); return;
        }
        int c = JOptionPane.showConfirmDialog(this, "Xác nhận gia sư đã thanh toán phiếu " + maPhieu + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            String msg = ctrl.xacNhanThanhToan(maPhieu);
            JOptionPane.showMessageDialog(this, msg);
            taiDuLieu();
        }
    }

    private void xuLyHoanPhi() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn phiếu trong danh sách!"); return; }
        String maPhieu   = (String) tableModel.getValueAt(row, 0);
        String maLop     = (String) tableModel.getValueAt(row, 1);
        String trangThai = (String) tableModel.getValueAt(row, 7);
        if (!"DaThanhToan".equals(trangThai)) {
            JOptionPane.showMessageDialog(this, "Chỉ hoàn phí cho phiếu đã thanh toán!"); return;
        }
        String soTienStr = JOptionPane.showInputDialog(this, "Nhập số tiền hoàn (VNĐ):", "Hoàn phí", JOptionPane.QUESTION_MESSAGE);
        if (soTienStr == null) return;
        double soTien;
        try { soTien = Double.parseDouble(soTienStr.trim().replaceAll(",","")); } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ!"); return;
        }
        String lyDo = JOptionPane.showInputDialog(this, "Lý do hoàn phí:", "Hoàn phí", JOptionPane.QUESTION_MESSAGE);
        if (lyDo == null) return;
        String msg = ctrl.hoanPhi(maPhieu, maLop, soTien, lyDo);
        JOptionPane.showMessageDialog(this, msg);
        taiDuLieu();
    }
}
