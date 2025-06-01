package sql;
import java.sql.*;

public class DBUtil {
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/flightticketinfo?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf8&useSSL=true";
        String username = "root";
        String password = "Su#!2005-L1011";
        return DriverManager.getConnection(url, username, password);
    }
}
