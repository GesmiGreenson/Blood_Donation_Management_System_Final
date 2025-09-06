// DBConnection.java
import java.sql.*;

public class DBConnection {
    private static final String URL  = "jdbc:mysql://localhost:3306/bloodbank?useSSL=false&serverTimezone=UTC";
    private static final String USER = "your_user";      // <- change to yours
    private static final String PASS = "your_password";  // <- change to yours

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}

