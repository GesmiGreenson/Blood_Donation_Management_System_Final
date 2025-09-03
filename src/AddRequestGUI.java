import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AddRequestGUI extends JFrame {
    private JTextField requesterField, bloodGroupField, quantityField, contactField;

    public AddRequestGUI() {
        setTitle("Add Blood Request");
        setSize(420, 260);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel form = new JPanel(new GridLayout(0,2,8,8));
        form.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        form.add(new JLabel("Requester Name:"));
        requesterField = new JTextField(); form.add(requesterField);

        form.add(new JLabel("Blood Group:"));
        bloodGroupField = new JTextField(); form.add(bloodGroupField);

        form.add(new JLabel("Quantity (units):"));
        quantityField = new JTextField(); form.add(quantityField);

        form.add(new JLabel("Contact Info:"));
        contactField = new JTextField(); form.add(contactField);

        JButton save = new JButton("Save");
        save.addActionListener(e -> saveRequest());

        add(form, BorderLayout.CENTER);
        add(save, BorderLayout.SOUTH);
    }

    private void saveRequest() {
        String sql = "INSERT INTO blood_request(requester_name, blood_group, quantity, contact_info) VALUES(?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, requesterField.getText().trim());
            ps.setString(2, bloodGroupField.getText().trim());
            ps.setInt(3, Integer.parseInt(quantityField.getText().trim()));
            ps.setString(4, contactField.getText().trim());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Request added!");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
