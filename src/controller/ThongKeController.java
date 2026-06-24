package controller;

import config.DatabaseConfig;
import dao.PhieuGiaoLopDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java.io.FileOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

// Apache POI
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// OpenPDF
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;

public class ThongKeController {

    private PhieuGiaoLopDAO phieuDAO = new PhieuGiaoLopDAO();

    public Map<String, Object> thongKe(Date tuNgay, Date denNgay) {
        Map<String, Object> result = new LinkedHashMap<>();

        double tongDoanhThu = phieuDAO.tongDoanhThu(tuNgay, denNgay);
        result.put("tongDoanhThu", tongDoanhThu);

        int[] lopStats = thongKeLop(tuNgay, denNgay);
        result.put("tongLop", lopStats[0]);
        result.put("lopThanhCong", lopStats[1]);
        result.put("lopHong", lopStats[2]);

        result.put("chiTiet", chiTietDoanhThu(tuNgay, denNgay));

        return result;
    }

    public List<Map<String, Object>> topGiaSu(Date tuNgay, Date denNgay) {

        List<Map<String, Object>> list = new ArrayList<>();

        String sql =
                "SELECT g.MaGS, g.HoTen, COUNT(p.MaPhieu) AS SoLopNhan " +
                "FROM PhieuGiaoLop p " +
                "JOIN GiaSu g ON p.MaGS = g.MaGS " +
                "WHERE CAST(p.NgayGiao AS DATE) BETWEEN ? AND ? " +
                "GROUP BY g.MaGS, g.HoTen " +
                "ORDER BY SoLopNhan DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, tuNgay);
            ps.setDate(2, denNgay);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Map<String, Object> row = new LinkedHashMap<>();

                    row.put("maGS", rs.getString("MaGS"));
                    row.put("hoTen", rs.getString("HoTen"));
                    row.put("soLop", rs.getInt("SoLopNhan"));

                    list.add(row);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    private int[] thongKeLop(Date tuNgay, Date denNgay) {

        int[] counts = {0, 0, 0};

        String sql =
                "SELECT TrangThaiLop, COUNT(*) AS SoLuong " +
                "FROM LopHoc " +
                "WHERE CAST(NgayTao AS DATE) BETWEEN ? AND ? " +
                "GROUP BY TrangThaiLop";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, tuNgay);
            ps.setDate(2, denNgay);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    int cnt = rs.getInt("SoLuong");

                    counts[0] += cnt;

                    String tt = rs.getString("TrangThaiLop");

                    if ("DaGiao".equals(tt))
                        counts[1] += cnt;

                    if ("Hong".equals(tt))
                        counts[2] += cnt;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return counts;
    }

    private List<Map<String, Object>> chiTietDoanhThu(Date tuNgay, Date denNgay) {

        List<Map<String, Object>> list = new ArrayList<>();

        String sql =
                "SELECT p.MaPhieu, g.HoTen AS TenGiaSu, l.MonHoc, " +
                "p.NgayGiao, p.SoTienCanDong, p.TrangThaiThanhToan " +
                "FROM PhieuGiaoLop p " +
                "JOIN GiaSu g ON p.MaGS = g.MaGS " +
                "JOIN LopHoc l ON p.MaLop = l.MaLop " +
                "WHERE p.TrangThaiThanhToan = N'DaThanhToan' " +
                "AND CAST(p.NgayGiao AS DATE) BETWEEN ? AND ? " +
                "ORDER BY p.NgayGiao DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, tuNgay);
            ps.setDate(2, denNgay);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Map<String, Object> row = new LinkedHashMap<>();

                    row.put("maPhieu", rs.getString("MaPhieu"));
                    row.put("tenGiaSu", rs.getString("TenGiaSu"));
                    row.put("monHoc", rs.getString("MonHoc"));
                    row.put("ngayGiao", rs.getDate("NgayGiao"));
                    row.put("soTien", rs.getDouble("SoTienCanDong"));
                    row.put("trangThai", rs.getString("TrangThaiThanhToan"));

                    list.add(row);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ==========================
    // XUẤT EXCEL
    // ==========================

    public void xuatExcel(Date tuNgay, Date denNgay) {

        Map<String, Object> data = thongKe(tuNgay, denNgay);

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Lưu báo cáo Excel");

        chooser.setFileFilter(
                new FileNameExtensionFilter(
                        "Excel Files (*.xlsx)",
                        "xlsx")
        );

        int option = chooser.showSaveDialog(null);

        if (option != JFileChooser.APPROVE_OPTION) {
            return;
        }

        String filePath =
                chooser.getSelectedFile().getAbsolutePath();

        if (!filePath.toLowerCase().endsWith(".xlsx")) {
            filePath += ".xlsx";
        }

        try (Workbook wb = new XSSFWorkbook()) {

            Sheet sheet = wb.createSheet("ThongKe");

            int rowNum = 0;

            Row row1 = sheet.createRow(rowNum++);
            row1.createCell(0).setCellValue("Tong doanh thu");
            row1.createCell(1).setCellValue(
                    Double.parseDouble(
                            data.get("tongDoanhThu").toString()));

            Row row2 = sheet.createRow(rowNum++);
            row2.createCell(0).setCellValue("Tong lop");
            row2.createCell(1).setCellValue(
                    Integer.parseInt(
                            data.get("tongLop").toString()));

            rowNum++;

            Row header = sheet.createRow(rowNum++);
            header.createCell(0).setCellValue("Ma phieu");
            header.createCell(1).setCellValue("Gia su");
            header.createCell(2).setCellValue("Mon hoc");
            header.createCell(3).setCellValue("So tien");

            List<Map<String, Object>> ds =
                    (List<Map<String, Object>>) data.get("chiTiet");

            for (Map<String, Object> item : ds) {

                Row r = sheet.createRow(rowNum++);

                r.createCell(0).setCellValue(
                        item.get("maPhieu").toString());

                r.createCell(1).setCellValue(
                        item.get("tenGiaSu").toString());

                r.createCell(2).setCellValue(
                        item.get("monHoc").toString());

                r.createCell(3).setCellValue(
                        Double.parseDouble(
                                item.get("soTien").toString()));
            }

            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }

            FileOutputStream fos =
                    new FileOutputStream(filePath);

            wb.write(fos);
            fos.close();

            JOptionPane.showMessageDialog(
                    null,
                    "Xuất Excel thành công!\n" + filePath);

        } catch (Exception e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    null,
                    "Lỗi xuất Excel:\n" + e.getMessage());
        }
    }

    // ==========================
    // XUẤT PDF
    // ==========================

    public void xuatPDF(Date tuNgay, Date denNgay) {

        Map<String, Object> data = thongKe(tuNgay, denNgay);

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Lưu báo cáo PDF");

        chooser.setFileFilter(
                new FileNameExtensionFilter(
                        "PDF Files (*.pdf)",
                        "pdf")
        );

        int option = chooser.showSaveDialog(null);

        if (option != JFileChooser.APPROVE_OPTION) {
            return;
        }

        String filePath =
                chooser.getSelectedFile().getAbsolutePath();

        if (!filePath.toLowerCase().endsWith(".pdf")) {
            filePath += ".pdf";
        }

        try {

            Document document = new Document();

            PdfWriter.getInstance(
                    document,
                    new FileOutputStream(filePath));

            document.open();

            document.add(new Paragraph("BAO CAO THONG KE"));
            document.add(new Paragraph(" "));

            document.add(new Paragraph(
                    "Tong doanh thu: " +
                            data.get("tongDoanhThu")));

            document.add(new Paragraph(
                    "Tong lop: " +
                            data.get("tongLop")));

            document.add(new Paragraph(
                    "Lop thanh cong: " +
                            data.get("lopThanhCong")));

            document.add(new Paragraph(
                    "Lop hong: " +
                            data.get("lopHong")));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("CHI TIET DOANH THU"));
            document.add(new Paragraph(" "));

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> ds =
                    (List<Map<String, Object>>) data.get("chiTiet");

            PdfPTable table = new PdfPTable(6);

            table.setWidthPercentage(100);

            table.addCell("Ma phieu");
            table.addCell("Gia su");
            table.addCell("Mon hoc");
            table.addCell("Ngay giao");
            table.addCell("So tien");
            table.addCell("Trang thai");

            for (Map<String, Object> item : ds) {

                table.addCell(item.get("maPhieu").toString());
                table.addCell(item.get("tenGiaSu").toString());
                table.addCell(item.get("monHoc").toString());
                table.addCell(item.get("ngayGiao").toString());
                table.addCell(item.get("soTien").toString());
                table.addCell(item.get("trangThai").toString());
            }

            document.add(table);

            document.close();

            JOptionPane.showMessageDialog(
                    null,
                    "Xuất PDF thành công!\n" + filePath);

        } catch (Exception e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    null,
                    "Lỗi xuất PDF:\n" + e.getMessage());
        }
    }
}