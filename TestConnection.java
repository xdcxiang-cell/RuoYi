import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        String url = "jdbc:mysql://192.168.1.8:23306/ry?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8";
        String username = "root";
        String password = "123456";
        
        System.out.println("Attempting to connect to: " + url);
        System.out.println("User: " + username);
        
        try {
            // 加载驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver loaded successfully");
            
            // 尝试连接
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connection successful!");
            
            // 检查连接信息
            System.out.println("Database: " + conn.getCatalog());
            System.out.println("Driver: " + conn.getMetaData().getDriverName());
            System.out.println("Driver version: " + conn.getMetaData().getDriverVersion());
            
            conn.close();
        } catch (ClassNotFoundException e) {
            System.err.println("Driver not found: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}