import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class DonorGUI extends JFrame {
    private final String username;
    private JTable table;

    public DonorGUI(String username) {
        this.username = username;
        setTitle("Donor Dashboard - " + username);
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel topPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        JButton viewRequestsBtn = new JButton("View Blood Requests");
        JButton recordDonationBtn = new JButton("Record Donation");
        JButton viewHistoryBtn = new JButton("My Donation History");
        topPanel.add(viewRequestsBtn);
        topPanel.add(recordDonationBtn);
        topPanel.add(viewHistoryBtn);

        // Table setup
        String[] columns = {"ID", "Requester", "Blood Group", "Quantity", "Contact", "Date", "Compatible"};
        table = new JTable(new DefaultTableModel(columns, 0));
        JScrollPane scrollPane = new JScrollPane(table);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Event handlers
        viewRequestsBtn.addActionListener(e -> loadRequests());
        recordDonationBtn.addActionListener(e -> recordDonationDialog());
        viewHistoryBtn.addActionListener(e -> viewDonationHistory());

        // Custom cell renderer for coloring
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, val, isSelected, hasFocus, row, col);

                String compat = (String) tbl.getModel().getValueAt(row, 6); // "Yes"/"No"
                if ("Yes".equals(compat)) {
                    c.setBackground(new Color(200, 255, 200)); // light green
                } else {
                    c.setBackground(new Color(255, 200, 200)); // light red
                }

                if (isSelected) {
                    c.setBackground(c.getBackground().darker());
                }
                return c;
            }
        });
    }

    // ✅ Load requests into JTable with compatibility check
    private void loadRequests() {
        String donorGroup = getDonorBloodGroup();
        if (donorGroup == null) {
            JOptionPane.showMessageDialog(this, "Donor profile not found.");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // clear old data

        String sql = "SELECT request_id, requester_name, blood_group, quantity, contact_info, request_date " +
                     "FROM blood_request ORDER BY request_id DESC";

        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("request_id");
                String name = rs.getString("requester_name");
                String group = rs.getString("blood_group");
                int qty = rs.getInt("quantity");
                String contact = rs.getString("contact_info");
                Date date = rs.getDate("request_date");

                boolean ok = isCompatible(donorGroup, group);
                model.addRow(new Object[]{id, name, group, qty, contact, date, ok ? "Yes" : "No"});
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }

    private void recordDonationDialog() {
        String idStr = JOptionPane.showInputDialog(this, "Enter Request ID to donate:");
        if (idStr == null || idStr.isBlank()) return;

        try {
            int requestId = Integer.parseInt(idStr);
            recordDonation(requestId);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Request ID.");
        }
    }

    private void recordDonation(int requestId) {
        String qDonor = "SELECT donor_id, blood_group FROM donor WHERE username = ?";
        String qReq   = "SELECT blood_group FROM blood_request WHERE request_id = ?";
        String ins    = "INSERT INTO donation (donor_id, request_id, donation_date) VALUES (?, ?, CURDATE())";

        try (Connection c = DBConnection.getConnection()) {
            String donorGroup = null;
            int donorId = -1;

            try (PreparedStatement ps = c.prepareStatement(qDonor)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        donorId = rs.getInt("donor_id");
                        donorGroup = rs.getString("blood_group");
                    } else {
                        JOptionPane.showMessageDialog(this, "Donor profile not found.");
                        return;
                    }
                }
            }

            String recipientGroup = null;
            try (PreparedStatement ps = c.prepareStatement(qReq)) {
                ps.setInt(1, requestId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        recipientGroup = rs.getString("blood_group");
                    } else {
                        JOptionPane.showMessageDialog(this, "Request not found.");
                        return;
                    }
                }
            }

            if (!isCompatible(donorGroup, recipientGroup)) {
                JOptionPane.showMessageDialog(this,
                    "Donation not possible: Donor group " + donorGroup +
                    " is incompatible with Recipient group " + recipientGroup);
                return;
            }

            try (PreparedStatement ps = c.prepareStatement(ins)) {
                ps.setInt(1, donorId);
                ps.setInt(2, requestId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Donation recorded successfully. Thank you!");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }

    private void viewDonationHistory() {
        String sql =
            "SELECT d.donation_id, d.donation_date, r.requester_name, r.blood_group, r.quantity " +
            "FROM donation d " +
            "JOIN donor o        ON d.donor_id = o.donor_id " +
            "JOIN blood_request r ON d.request_id = r.request_id " +
            "WHERE o.username = ? " +
            "ORDER BY d.donation_id DESC";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                StringBuilder sb = new StringBuilder("My Donations:\n");
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    sb.append("ID: ").append(rs.getInt("donation_id"))
                      .append(", Date: ").append(rs.getDate("donation_date"))
                      .append(", For: ").append(rs.getString("requester_name"))
                      .append(", Group: ").append(rs.getString("blood_group"))
                      .append(", Qty: ").append(rs.getInt("quantity")).append("\n");
                }
                if (!any) sb.append("No donations yet.");
                JOptionPane.showMessageDialog(this, sb.toString());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }

    private String getDonorBloodGroup() {
        String sql = "SELECT blood_group FROM donor WHERE username = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("blood_group");
            }
        } catch (SQLException ignored) {}
        return null;
    }

    private boolean isCompatible(String donor, String recipient) {
        donor = donor.toUpperCase();
        recipient = recipient.toUpperCase();

        switch (donor) {
            case "O-": return true;
            case "O+": return recipient.endsWith("+");
            case "A-": return recipient.startsWith("A") || recipient.startsWith("AB");
            case "A+": return recipient.equals("A+") || recipient.equals("AB+");
            case "B-": return recipient.startsWith("B") || recipient.startsWith("AB");
            case "B+": return recipient.equals("B+") || recipient.equals("AB+");
            case "AB-": return recipient.startsWith("AB");
            case "AB+": return recipient.equals("AB+");
            default: return false;
        }
    }
}




     