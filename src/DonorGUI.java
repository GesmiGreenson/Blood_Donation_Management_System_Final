import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class DonorGUI extends JFrame {
    private String username;

    public DonorGUI(String username) {
        this.username = username;
        setTitle("Donor Dashboard - " + username);
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton viewBtn = new JButton("View Blood Requests");
        JButton recordBtn = new JButton("Record Donation");
        JButton historyBtn = new JButton("My Donation History");

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(viewBtn);
        panel.add(recordBtn);
        panel.add(historyBtn);

        add(panel);

        // Actions
        viewBtn.addActionListener(e -> viewBloodRequests());
        recordBtn.addActionListener(e -> recordDonation());
        historyBtn.addActionListener(e -> showDonationHistory());
    }

    private void viewBloodRequests() {
        String sql = "SELECT request_id, requester_name, blood_group, quantity, request_date " +
                     "FROM blood_request ORDER BY request_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("Request ID: ").append(rs.getInt("request_id"))
                  .append(", Name: ").append(rs.getString("requester_name"))
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

    private void recordDonation() {
        String requestId = JOptionPane.showInputDialog(this, "Enter Request ID to donate for:");
        if (requestId == null || requestId.isEmpty()) return;

        String sql = "INSERT INTO donation (donor_id, request_id, donation_date) " +
                     "VALUES ((SELECT donor_id FROM donor WHERE username=?), ?, CURRENT_TIMESTAMP)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setInt(2, Integer.parseInt(requestId));
            int rows = stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, rows > 0 ? "Donation recorded!" : "Failed to record donation.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void showDonationHistory() {
        String sql = "SELECT d.donation_id, d.request_id, d.donation_date " +
                     "FROM donation d JOIN donor o ON d.donor_id = o.donor_id WHERE o.username=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("Donation ID: ").append(rs.getInt("donation_id"))
                  .append(", Request ID: ").append(rs.getInt("request_id"))
                  .append(", Date: ").append(rs.getTimestamp("donation_date"))
                  .append("\n");
            }
            JOptionPane.showMessageDialog(this, sb.length() > 0 ? sb.toString() : "No donations found.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}


     