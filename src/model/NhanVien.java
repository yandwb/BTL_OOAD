package model;

public class NhanVien {
    private String maNV;
    private String hoTen;
    private String sdt;
    private String chucVu;       // Admin | NhanVien
    private String tenDangNhap;

    public NhanVien() {}

    public NhanVien(String maNV, String hoTen, String sdt, String chucVu, String tenDangNhap) {
        this.maNV        = maNV;
        this.hoTen       = hoTen;
        this.sdt         = sdt;
        this.chucVu      = chucVu;
        this.tenDangNhap = tenDangNhap;
    }

    public String getMaNV()        { return maNV; }
    public void   setMaNV(String v){ this.maNV = v; }

    public String getHoTen()        { return hoTen; }
    public void   setHoTen(String v){ this.hoTen = v; }

    public String getSdt()        { return sdt; }
    public void   setSdt(String v){ this.sdt = v; }

    public String getChucVu()        { return chucVu; }
    public void   setChucVu(String v){ this.chucVu = v; }

    public String getTenDangNhap()        { return tenDangNhap; }
    public void   setTenDangNhap(String v){ this.tenDangNhap = v; }

    @Override
    public String toString() { return hoTen + " (" + maNV + ")"; }
}
