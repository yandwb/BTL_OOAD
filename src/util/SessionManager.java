package util;

import model.TaiKhoan;

/**
 * Quản lý phiên đăng nhập (Singleton)
 */
public class SessionManager {
    private static SessionManager instance;
    private TaiKhoan currentUser;
    private String   maNguoiDung; // MaNV hoặc MaGS

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    public void dangNhap(TaiKhoan tk, String maNguoiDung) {
        this.currentUser   = tk;
        this.maNguoiDung   = maNguoiDung;
    }

    public void dangXuat() {
        this.currentUser   = null;
        this.maNguoiDung   = null;
    }

    public TaiKhoan getCurrentUser()   { return currentUser; }
    public String   getMaNguoiDung()   { return maNguoiDung; }
    public boolean  isDangNhap()       { return currentUser != null; }
    public boolean  isAdmin()          { return isDangNhap() && "Admin".equals(currentUser.getVaiTro()); }
    public boolean  isNhanVien()       { return isDangNhap() && ("Admin".equals(currentUser.getVaiTro())
                                                                  || "NhanVien".equals(currentUser.getVaiTro())); }
    public boolean  isGiaSu()          { return isDangNhap() && "GiaSu".equals(currentUser.getVaiTro()); }
}
