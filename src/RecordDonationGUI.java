import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RecordDonationGUI extends JFrame {
    private JTextField donorIdField, requestIdField;

    public RecordDonationGUI() {
        setTitle("Record Donation");
        setSize(360, 180);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel form = new JPanel(new GridLayout(0,2,8,8));
        form.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        form.add(new JLabel("Donor ID:"));
        donorIdField = new JTextField(); form.add(donorIdField);

        form.add(new JLabel("Request ID:"));
        requestIdField = new JTextField(); form.add(requestIdField);

        JButton save = new JButton("Save");
        save.addActionListener(e -> recordDonation());

        add(form, BorderLayout.CENTER);
        add(save, BorderLayout.SOUTH);
    }

    private void recordDonation() {
        String sql = "INSERT INTO donation(donor_id, request_id) VALUES(?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(donorIdField.getText().trim()));
            ps.setInt(2, Integer.parseInt(requestIdField.getText().trim()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Donation recorded!");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
