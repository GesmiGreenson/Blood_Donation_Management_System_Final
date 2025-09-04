import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class DonorGUI extends JFrame {
    private String username;

    public DonorGUI(String username) {
        this.username = username;
        setTitle("Donor Dashboard - " + username);
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));

        JButton recordDonationBtn = new JButton("Record Donation");
        JButton viewHistoryBtn = new JButton("My Donation History");
        JButton viewRequestsBtn = new JButton("View Blood Requests");

        panel.add(recordDonationBtn);
        panel.add(viewHistoryBtn);
        panel.add(viewRequestsBtn);

        add(panel, BorderLayout.CENTER);

        // Action Listeners
        recordDonationBtn.addActionListener(e -> recordDonationDialog());
        viewHistoryBtn.addActionListener(e -> viewDonationHistory(username));
        viewRequestsBtn.addActionListener(e -> viewBloodRequests());
    }

    // --- Record Donation ---
    private void recordDonationDialog() {
        String requestIdStr = JOptionPane.showInputDialog(this, "Enter Request ID to donate:");
        if (requestIdStr != null && !requestIdStr.isEmpty()) {
            try {
                int requestId = Integer.parseInt(requestIdStr);
                recordDonation(username, requestId);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Request ID.");
            }
        }
    }

    private void recordDonation(String username, int requestId) {
        String donorSql = "SELECT donor_id FROM donor WHERE username = ?";
        String insertSql = "INSERT INTO donation (donor_id, request_id, donation_date) VALUES (?, ?, CURDATE())";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps1 = c.prepareStatement(donorSql)) {

            ps1.setString(1, username);
            try (ResultSet rs = ps1.executeQuery()) {
                if (rs.next()) {
                    int donorId = rs.getInt("donor_id");

                    try (PreparedStatement ps2 = c.prepareStatement(insertSql)) {
                        ps2.setInt(1, donorId);
                        ps2.setInt(2, requestId);
                        ps2.executeUpdate();
                        JOptionPane.showMessageDialog(this, "Donation recorded successfully!");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Donor not found for this username.");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }

    // --- View Donation History ---
    private void viewDonationHistory(String username) {
        String sql = "SELECT d.donation_id, d.donation_date, r.requester_name, r.blood_group, r.quantity " +
                     "FROM donation d " +
                     "JOIN donor o ON d.donor_id = o.donor_id " +
                     "JOIN blood_request r ON d.request_id = r.request_id " +
                     "WHERE o.username = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                StringBuilder sb = new StringBuilder("Donation History:\n");
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    sb.append("ID: ").append(rs.getInt("donation_id"))
                      .append(", Date: ").append(rs.getDate("donation_date"))
                      .append(", To: ").append(rs.getString("requester_name"))
                      .append(", Blood Group: ").append(rs.getString("blood_group"))
                      .append(", Qty: ").append(rs.getInt("quantity"))
                      .append("\n");
                }
                if (!found) {
                    sb.append("No donations found.");
                }
                JOptionPane.showMessageDialog(this, sb.toString());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }

    // --- View Blood Requests ---
    private void viewBloodRequests() {
        String sql = "SELECT request_id, requester_name, blood_group, quantity, contact_info, request_date FROM blood_request";

        try (Connection c = DBConnection.getConnection();
             Statement stmt = c.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            StringBuilder sb = new StringBuilder("Blood Requests:\n");
            while (rs.next()) {
                sb.append("ID: ").append(rs.getInt("request_id"))
                  .append(", Name: ").append(rs.getString("requester_name"))
                  .append(", Blood Group: ").append(rs.getString("blood_group"))
                  .append(", Qty: ").append(rs.getInt("quantity"))
                  .append(", Contact: ").append(rs.getString("contact_info"))
                  .append(", Date: ").append(rs.getDate("request_date"))
                  .append("\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }
}



     