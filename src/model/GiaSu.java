package model;

import java.sql.Date;

public class GiaSu {
    private String maNV;        // dùng chung tên field maNV = MaGS
    private String maGS;
    private String hoTen;
    private Date   ngaySinh;
    private String gioiTinh;
    private String sdt;
    private String email;
    private String truongDH;
    private String chuyenNganh;
    private String hinhAnh;
    private String trangThaiDuyet; // ChoDuyet | DaDuyet | TuChoi | DaKhoa
    private String tenDangNhap;

    public GiaSu() {}

    public GiaSu(String maGS, String hoTen, Date ngaySinh, String gioiTinh,
                 String sdt, String email, String truongDH, String chuyenNganh,
                 String hinhAnh, String trangThaiDuyet, String tenDangNhap) {
        this.maGS           = maGS;
        this.hoTen          = hoTen;
        this.ngaySinh       = ngaySinh;
        this.gioiTinh       = gioiTinh;
        this.sdt            = sdt;
        this.email          = email;
        this.truongDH       = truongDH;
        this.chuyenNganh    = chuyenNganh;
        this.hinhAnh        = hinhAnh;
        this.trangThaiDuyet = trangThaiDuyet;
        this.tenDangNhap    = tenDangNhap;
    }

    // Getters & Setters
    public String getMaGS()              { return maGS; }
    public void   setMaGS(String v)      { this.maGS = v; }

    public String getHoTen()             { return hoTen; }
    public void   setHoTen(String v)     { this.hoTen = v; }

    public Date   getNgaySinh()          { return ngaySinh; }
    public void   setNgaySinh(Date v)    { this.ngaySinh = v; }

    public String getGioiTinh()          { return gioiTinh; }
    public void   setGioiTinh(String v)  { this.gioiTinh = v; }

    public String getSdt()               { return sdt; }
    public void   setSdt(String v)       { this.sdt = v; }

    public String getEmail()             { return email; }
    public void   setEmail(String v)     { this.email = v; }

    public String getTruongDH()          { return truongDH; }
    public void   setTruongDH(String v)  { this.truongDH = v; }

    public String getChuyenNganh()       { return chuyenNganh; }
    public void   setChuyenNganh(String v){ this.chuyenNganh = v; }

    public String getHinhAnh()           { return hinhAnh; }
    public void   setHinhAnh(String v)   { this.hinhAnh = v; }

    public String getTrangThaiDuyet()        { return trangThaiDuyet; }
    public void   setTrangThaiDuyet(String v){ this.trangThaiDuyet = v; }

    public String getTenDangNhap()           { return tenDangNhap; }
    public void   setTenDangNhap(String v)   { this.tenDangNhap = v; }

    @Override
    public String toString() { return hoTen + " (" + maGS + ")"; }
}
