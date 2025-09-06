// RecipientGUI.java
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RecipientGUI extends JFrame {
    private final String username;

    public RecipientGUI(String username) {
        this.username = username;
        setTitle("Recipient Dashboard - " + username);
        setSize(480, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(2, 1, 12, 12));
        JButton requestBtn = new JButton("Request Blood");
        JButton myReqBtn   = new JButton("My Requests");
        panel.add(requestBtn);
        panel.add(myReqBtn);
        add(panel, BorderLayout.CENTER);

        requestBtn.addActionListener(e -> requestBlood());
        myReqBtn.addActionListener(e -> viewMyRequests());
    }

    private void requestBlood() {
        String group = JOptionPane.showInputDialog(this, "Enter Blood Group (e.g., O+):");
        String qtyStr = JOptionPane.showInputDialog(this, "Enter Quantity (units):");

        if (group == null || group.isBlank() || qtyStr == null || qtyStr.isBlank()) {
            JOptionPane.showMessageDialog(this, "Please enter blood group and quantity.");
            return;
        }

        int qty;
        try { qty = Integer.parseInt(qtyStr); }
        catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Quantity must be a number."); return; }

        String qProfile =
            "SELECT u.name, r.contact_info " +
            "FROM users u JOIN recipient r ON u.username = r.username " +
            "WHERE u.username = ?";

        String ins =
            "INSERT INTO blood_request (requester_username, requester_name, blood_group, quantity, contact_info, request_date) " +
            "VALUES (?, ?, ?, ?, ?, CURDATE())";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement p1 = c.prepareStatement(qProfile)) {

            p1.setString(1, username);
            try (ResultSet rs = p1.executeQuery()) {
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "Recipient profile not found for " + username);
                    return;
                }
                String name = rs.getString("name");
                String contact = rs.getString("contact_info");

                try (PreparedStatement insPs = c.prepareStatement(ins)) {
                    insPs.setString(1, username);
                    insPs.setString(2, name);
                    insPs.setString(3, group);
                    insPs.setInt(4, qty);
                    insPs.setString(5, contact);
                    insPs.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Blood request submitted.");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }

    private void viewMyRequests() {
        String sql =
            "SELECT request_id, request_date, blood_group, quantity, contact_info " +
            "FROM blood_request " +
            "WHERE requester_username = ? " +
            "ORDER BY request_id DESC";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                StringBuilder sb = new StringBuilder("My Requests:\n");
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    sb.append("ID: ").append(rs.getInt("request_id"))
                      .append(", Date: ").append(rs.getDate("request_date"))
                      .append(", Group: ").append(rs.getString("blood_group"))
                      .append(", Qty: ").append(rs.getInt("quantity"))
                      .append(", Contact: ").append(rs.getString("contact_info")).append("\n");
                }
                if (!any) sb.append("No requests yet.");
                JOptionPane.showMessageDialog(this, sb.toString());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }
}




