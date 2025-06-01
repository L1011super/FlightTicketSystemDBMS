package sql;

import java.sql.*;

public class JDBC {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        /*
//        1.加载驱动
        Class.forName("com.mysql.cj.jdbc.Driver");
//        2.用户信息和url
// jdbc:mysql://localhost:3306/?user=root
        String url = "jdbc:mysql://localhost:3306/flightticketinfo?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf8&useSSL=true";
        String username="root";
        String password="Su#!2005-L1011";
//        3.连接成功，数据库对象 Connection
        Connection connection = DriverManager.getConnection(url,username,password);
//        4.执行SQL对象Statement，执行SQL的对象
        Statement statement = connection.createStatement();
//        5.执行SQL的对象去执行SQL，返回结果集
        String sql = "SELECT *FROM airport;";
        ResultSet resultSet = statement.executeQuery(sql);
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                String value = resultSet.getString(i);
                System.out.print(columnName + "=" + value + "\t");
            }
            System.out.println();
        }


//        6.释放连接
        resultSet.close();
        statement.close();
        connection.close();*/
    }
}

