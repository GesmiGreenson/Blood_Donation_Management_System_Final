import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RecipientGUI extends JFrame {
    private String username;

    public RecipientGUI(String username) {
        this.username = username;
        setTitle("Recipient Dashboard - " + username);
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));

        JButton requestBloodBtn = new JButton("Request Blood");
        JButton viewRequestsBtn = new JButton("My Requests");

        panel.add(requestBloodBtn);
        panel.add(viewRequestsBtn);

        add(panel, BorderLayout.CENTER);

        // Action Listeners
        requestBloodBtn.addActionListener(e -> requestBlood());
        viewRequestsBtn.addActionListener(e -> viewMyRequests(username));
    }

    // --- Request Blood ---
    private void requestBlood() {
        String bloodGroup = JOptionPane.showInputDialog(this, "Enter Blood Group:");
        String qtyStr = JOptionPane.showInputDialog(this, "Enter Quantity:");
        if (bloodGroup == null || bloodGroup.isEmpty() || qtyStr == null || qtyStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all fields.");
            return;
        }

        try {
            int quantity = Integer.parseInt(qtyStr);

            // fetch recipient info using username
            String sqlRecipient = "SELECT recipient_id, name, contact_info FROM recipient WHERE username = ?";
            try (Connection c = DBConnection.getConnection();
                 PreparedStatement ps1 = c.prepareStatement(sqlRecipient)) {

                ps1.setString(1, username);
                try (ResultSet rs = ps1.executeQuery()) {
                    if (rs.next()) {
                        String name = rs.getString("name");
                        String contact = rs.getString("contact_info");

                        String insertSql = "INSERT INTO blood_request (requester_name, blood_group, quantity, contact_info, request_date) " +
                                           "VALUES (?, ?, ?, ?, CURDATE())";
                        try (PreparedStatement ps2 = c.prepareStatement(insertSql)) {
                            ps2.setString(1, name);
                            ps2.setString(2, bloodGroup);
                            ps2.setInt(3, quantity);
                            ps2.setString(4, contact);
                            ps2.executeUpdate();
                            JOptionPane.showMessageDialog(this, "Blood request submitted successfully!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Recipient not found for this username.");
                    }
                }
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantity must be a number.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }

    // --- View My Requests ---
    private void viewMyRequests(String username) {
        String sql = "SELECT r.request_id, r.request_date, r.blood_group, r.quantity, r.contact_info " +
                     "FROM blood_request r " +
                     "JOIN recipient u ON r.requester_name = u.name " +
                     "WHERE u.username = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                StringBuilder sb = new StringBuilder("My Requests:\n");
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    sb.append("ID: ").append(rs.getInt("request_id"))
                      .append(", Date: ").append(rs.getDate("request_date"))
                      .append(", Blood Group: ").append(rs.getString("blood_group"))
                      .append(", Qty: ").append(rs.getInt("quantity"))
                      .append(", Contact: ").append(rs.getString("contact_info"))
                      .append("\n");
                }
                if (!found) {
                    sb.append("No requests found.");
                }
                JOptionPane.showMessageDialog(this, sb.toString());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }
}



