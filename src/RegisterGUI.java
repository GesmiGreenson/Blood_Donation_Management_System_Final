import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegisterGUI extends JFrame {
    public RegisterGUI() {
        setTitle("Register User");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();

        JLabel nameLabel = new JLabel("Full Name:");
        JTextField nameField = new JTextField();

        JLabel contactLabel = new JLabel("Contact Info:");
        JTextField contactField = new JTextField();

        JLabel bloodLabel = new JLabel("Blood Group:");
        JTextField bloodField = new JTextField();

        JLabel roleLabel = new JLabel("Role:");
        String[] roles = {"Donor", "Recipient"};
        JComboBox<String> roleBox = new JComboBox<>(roles);

        JButton registerBtn = new JButton("Register");

        panel.add(userLabel);
        panel.add(userField);
        panel.add(passLabel);
        panel.add(passField);
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(contactLabel);
        panel.add(contactField);
        panel.add(bloodLabel);
        panel.add(bloodField);
        panel.add(roleLabel);
        panel.add(roleBox);
        panel.add(new JLabel());
        panel.add(registerBtn);

        add(panel, BorderLayout.CENTER);

        registerBtn.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            String name = nameField.getText();
            String contactInfo = contactField.getText();
            String bloodGroup = bloodField.getText();
            String role = roleBox.getSelectedItem().toString();

            if (username.isEmpty() || password.isEmpty() || name.isEmpty() || contactInfo.isEmpty() || bloodGroup.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }

            try (Connection c = DBConnection.getConnection()) {
                // Insert into users table
                String sqlUser = "INSERT INTO users (username, password, role, name) VALUES (?, ?, ?, ?)";
                try (PreparedStatement ps = c.prepareStatement(sqlUser)) {
                    ps.setString(1, username);
                    ps.setString(2, password);
                    ps.setString(3, role.toLowerCase());
                    ps.setString(4, name);
                    ps.executeUpdate();
                }

                if (role.equalsIgnoreCase("donor")) {
                    // Insert into donor table
                    String sqlDonor = "INSERT INTO donor (username, contact_info, blood_group) VALUES (?, ?, ?)";
                    try (PreparedStatement ps2 = c.prepareStatement(sqlDonor)) {
                        ps2.setString(1, username);
                        ps2.setString(2, contactInfo);
                        ps2.setString(3, bloodGroup);
                        ps2.executeUpdate();
                    }
                } else {
                    // Insert into recipient table
                    String sqlRecipient = "INSERT INTO recipient (username, contact_info, blood_group) VALUES (?, ?, ?)";
                    try (PreparedStatement ps3 = c.prepareStatement(sqlRecipient)) {
                        ps3.setString(1, username);
                        ps3.setString(2, contactInfo);
                        ps3.setString(3, bloodGroup);
                        ps3.executeUpdate();
                    }
                }

                JOptionPane.showMessageDialog(this, "Registration successful!");
                dispose();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
            }
        });
    }
}


