package model;

import java.sql.Timestamp;

public class LopHoc {
    private String    maLop;
    private String    maPH;
    private String    monHoc;
    private String    capHoc;
    private int       soBuoiTuan;
    private String    thoiGianHoc;
    private double    mucLuong;
    private String    diaChiHoc;
    private String    yeuCauKhac;
    private String    trangThaiLop; // ChoGiao | DaGiao | Huy | Hong
    private Timestamp ngayTao;

    // Thêm tên phụ huynh để hiện trên bảng (JOIN)
    private String tenPhuHuynh;
    private String sdtPhuHuynh;

    public LopHoc() {}

    public LopHoc(String maLop, String maPH, String monHoc, String capHoc,
                  int soBuoiTuan, String thoiGianHoc, double mucLuong,
                  String diaChiHoc, String yeuCauKhac, String trangThaiLop) {
        this.maLop       = maLop;
        this.maPH        = maPH;
        this.monHoc      = monHoc;
        this.capHoc      = capHoc;
        this.soBuoiTuan  = soBuoiTuan;
        this.thoiGianHoc = thoiGianHoc;
        this.mucLuong    = mucLuong;
        this.diaChiHoc   = diaChiHoc;
        this.yeuCauKhac  = yeuCauKhac;
        this.trangThaiLop = trangThaiLop;
    }

    // Getters & Setters
    public String    getMaLop()           { return maLop; }
    public void      setMaLop(String v)   { this.maLop = v; }

    public String    getMaPH()            { return maPH; }
    public void      setMaPH(String v)    { this.maPH = v; }

    public String    getMonHoc()          { return monHoc; }
    public void      setMonHoc(String v)  { this.monHoc = v; }

    public String    getCapHoc()          { return capHoc; }
    public void      setCapHoc(String v)  { this.capHoc = v; }

    public int       getSoBuoiTuan()      { return soBuoiTuan; }
    public void      setSoBuoiTuan(int v) { this.soBuoiTuan = v; }

    public String    getThoiGianHoc()         { return thoiGianHoc; }
    public void      setThoiGianHoc(String v) { this.thoiGianHoc = v; }

    public double    getMucLuong()          { return mucLuong; }
    public void      setMucLuong(double v)  { this.mucLuong = v; }

    public String    getDiaChiHoc()         { return diaChiHoc; }
    public void      setDiaChiHoc(String v) { this.diaChiHoc = v; }

    public String    getYeuCauKhac()         { return yeuCauKhac; }
    public void      setYeuCauKhac(String v) { this.yeuCauKhac = v; }

    public String    getTrangThaiLop()           { return trangThaiLop; }
    public void      setTrangThaiLop(String v)   { this.trangThaiLop = v; }

    public Timestamp getNgayTao()              { return ngayTao; }
    public void      setNgayTao(Timestamp v)   { this.ngayTao = v; }

    public String    getTenPhuHuynh()           { return tenPhuHuynh; }
    public void      setTenPhuHuynh(String v)   { this.tenPhuHuynh = v; }

    public String    getSdtPhuHuynh()           { return sdtPhuHuynh; }
    public void      setSdtPhuHuynh(String v)   { this.sdtPhuHuynh = v; }

    @Override
    public String toString() { return monHoc + " - " + capHoc + " (" + maLop + ")"; }
}
