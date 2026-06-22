package model;

public class PhuHuynh {
    private String maPH;
    private String hoTen;
    private String sdt;
    private String diaChi;
    private String ghiChu;
    private String trangThai; // HoatDong | DaKhoa

    public PhuHuynh() {}

    public PhuHuynh(String maPH, String hoTen, String sdt,
                    String diaChi, String ghiChu, String trangThai) {
        this.maPH     = maPH;
        this.hoTen    = hoTen;
        this.sdt      = sdt;
        this.diaChi   = diaChi;
        this.ghiChu   = ghiChu;
        this.trangThai = trangThai;
    }

    public String getMaPH()            { return maPH; }
    public void   setMaPH(String v)    { this.maPH = v; }

    public String getHoTen()           { return hoTen; }
    public void   setHoTen(String v)   { this.hoTen = v; }

    public String getSdt()             { return sdt; }
    public void   setSdt(String v)     { this.sdt = v; }

    public String getDiaChi()          { return diaChi; }
    public void   setDiaChi(String v)  { this.diaChi = v; }

    public String getGhiChu()          { return ghiChu; }
    public void   setGhiChu(String v)  { this.ghiChu = v; }

    public String getTrangThai()        { return trangThai; }
    public void   setTrangThai(String v){ this.trangThai = v; }

    @Override
    public String toString() { return hoTen + " - " + sdt; }
}
