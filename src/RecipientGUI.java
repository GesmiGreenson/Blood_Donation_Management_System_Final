import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RecipientGUI extends JFrame {
    private String username;

    public RecipientGUI(String username) {
        this.username = username;
        setTitle("Recipient Dashboard - " + username);
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton addRequestBtn = new JButton("Add Blood Request");
        JButton viewRequestBtn = new JButton("View My Requests");

        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(addRequestBtn);
        panel.add(viewRequestBtn);

        add(panel);

        addRequestBtn.addActionListener(e -> addRequest());
        viewRequestBtn.addActionListener(e -> viewRequests());
    }

    private void addRequest() {
        String bloodGroup = JOptionPane.showInputDialog(this, "Enter Blood Group:");
        String qtyStr = JOptionPane.showInputDialog(this, "Enter Quantity:");
        if (bloodGroup == null || qtyStr == null || bloodGroup.isEmpty() || qtyStr.isEmpty()) return;

        try {
            int quantity = Integer.parseInt(qtyStr);

            String sql = "INSERT INTO blood_request (requester_name, blood_group, quantity, contact_info, request_date) " +
                         "VALUES ((SELECT recipient_name FROM recipient WHERE username=?), ?, ?, " +
                         "(SELECT contact_info FROM recipient WHERE username=?), CURRENT_DATE)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, bloodGroup);
                stmt.setInt(3, quantity);
                stmt.setString(4, username);

                int rows = stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, rows > 0 ? "Request added!" : "Failed to add request.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding request: " + e.getMessage());
        }
    }

    private void viewRequests() {
        String sql = "SELECT * FROM blood_request WHERE requester_name = " +
                     "(SELECT recipient_name FROM recipient WHERE username=?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("Request ID: ").append(rs.getInt("request_id"))
                  .append(", Group: ").append(rs.getString("blood_group"))
                  .append(", Qty: ").append(rs.getInt("quantity"))
                  .append(", Date: ").append(rs.getDate("request_date"))
                  .append("\n");
            }
            JOptionPane.showMessageDialog(this, sb.length() > 0 ? sb.toString() : "No requests found.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}


