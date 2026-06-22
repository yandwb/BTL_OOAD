package model;

import java.sql.Date;

public class PhieuGiaoLop {
    private String maPhieu;
    private String maLop;
    private String maGS;
    private String maNV;
    private Date   ngayGiao;
    private double tyLePhi;
    private double soTienCanDong;
    private double soTienHoanLai;
    private String lyDoHoan;
    private String trangThaiThanhToan; // ChuaThanhToan | DaThanhToan | DaHoanPhi

    // Thông tin JOIN để hiển thị
    private String tenGiaSu;
    private String monHoc;

    public PhieuGiaoLop() {}

    public PhieuGiaoLop(String maPhieu, String maLop, String maGS, String maNV,
                        Date ngayGiao, double tyLePhi, double soTienCanDong, String trangThaiThanhToan) {
        this.maPhieu           = maPhieu;
        this.maLop             = maLop;
        this.maGS              = maGS;
        this.maNV              = maNV;
        this.ngayGiao          = ngayGiao;
        this.tyLePhi           = tyLePhi;
        this.soTienCanDong     = soTienCanDong;
        this.trangThaiThanhToan = trangThaiThanhToan;
    }

    // Getters & Setters
    public String getMaPhieu()              { return maPhieu; }
    public void   setMaPhieu(String v)      { this.maPhieu = v; }

    public String getMaLop()                { return maLop; }
    public void   setMaLop(String v)        { this.maLop = v; }

    public String getMaGS()                 { return maGS; }
    public void   setMaGS(String v)         { this.maGS = v; }

    public String getMaNV()                 { return maNV; }
    public void   setMaNV(String v)         { this.maNV = v; }

    public Date   getNgayGiao()             { return ngayGiao; }
    public void   setNgayGiao(Date v)       { this.ngayGiao = v; }

    public double getTyLePhi()              { return tyLePhi; }
    public void   setTyLePhi(double v)      { this.tyLePhi = v; }

    public double getSoTienCanDong()        { return soTienCanDong; }
    public void   setSoTienCanDong(double v){ this.soTienCanDong = v; }

    public double getSoTienHoanLai()        { return soTienHoanLai; }
    public void   setSoTienHoanLai(double v){ this.soTienHoanLai = v; }

    public String getLyDoHoan()             { return lyDoHoan; }
    public void   setLyDoHoan(String v)     { this.lyDoHoan = v; }

    public String getTrangThaiThanhToan()           { return trangThaiThanhToan; }
    public void   setTrangThaiThanhToan(String v)   { this.trangThaiThanhToan = v; }

    public String getTenGiaSu()             { return tenGiaSu; }
    public void   setTenGiaSu(String v)     { this.tenGiaSu = v; }

    public String getMonHoc()               { return monHoc; }
    public void   setMonHoc(String v)       { this.monHoc = v; }
}
