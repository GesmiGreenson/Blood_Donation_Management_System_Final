import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestDB {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/blood_donation";
        String user = "root";   // use your MySQL username
        String password = "root10";  // the password you set

        try {
            // Connect
            Connection con = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Connected to database!");

            // Query donors
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM donors");

            while (rs.next()) {
                System.out.println(
                    rs.getInt("donor_id") + " | " +
                    rs.getString("name") + " | " +
                    rs.getString("blood_group")
                );
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
