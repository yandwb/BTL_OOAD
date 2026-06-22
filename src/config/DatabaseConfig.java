package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Lớp cấu hình kết nối SQL Server
 * Database: HTQLTTGS
 */
public class DatabaseConfig {

    // ===== CẤU HÌNH KẾT NỐI =====
    private static final String SERVER   = "localhost";      // hoặc tên máy chủ SQL Server
    private static final String PORT     = "1433";
    private static final String DATABASE = "HTQLTTGS";
    private static final String USERNAME = "sa";             // thay bằng username của bạn
    private static final String PASSWORD = "123456";         // thay bằng password của bạn

    private static final String URL =
        "jdbc:sqlserver://" + SERVER + ":" + PORT
        + ";databaseName=" + DATABASE
        + ";encrypt=false"
        + ";trustServerCertificate=true";

    private DatabaseConfig() {}

    /**
     * Lấy kết nối đến database
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Không tìm thấy driver SQL Server. Hãy thêm sqljdbc4.jar vào Build Path.", e);
        }
    }

    /**
     * Đóng kết nối an toàn
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try { conn.close(); } catch (SQLException ignored) {}
        }
    }
}
