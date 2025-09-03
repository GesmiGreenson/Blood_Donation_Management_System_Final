import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL  = "jdbc:mysql://localhost:3306/bloodbank"; // ← set your DB name
    private static final String USER = "root";                                   // ← set your user
    private static final String PASS = "root10";                                       // ← set your password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
