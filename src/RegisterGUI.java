// RegisterGUI.java
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegisterGUI extends JFrame {
    private JTextField usernameField, nameField, contactField, bloodGroupField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;

    public RegisterGUI() {
        setTitle("Register User");
        setSize(420, 380);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel form = new JPanel(new GridLayout(6, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        form.add(new JLabel("Username:"));
        usernameField = new JTextField();
        form.add(usernameField);

        form.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        form.add(passwordField);

        form.add(new JLabel("Role:"));
        roleCombo = new JComboBox<>(new String[]{"Donor", "Recipient"}); // Admin is pre-seeded via SQL
        form.add(roleCombo);

        form.add(new JLabel("Full Name:"));
        nameField = new JTextField();
        form.add(nameField);

        form.add(new JLabel("Contact Info:"));
        contactField = new JTextField();
        form.add(contactField);

        form.add(new JLabel("Blood Group:"));
        bloodGroupField = new JTextField();
        form.add(bloodGroupField);

        JButton registerBtn = new JButton("Register");
        add(form, BorderLayout.CENTER);
        add(registerBtn, BorderLayout.SOUTH);

        registerBtn.addActionListener(e -> doRegister());
    }

    private void doRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role     = (String) roleCombo.getSelectedItem(); // "Donor" | "Recipient"
        String name     = nameField.getText().trim();
        String contact  = contactField.getText().trim();
        String blood    = bloodGroupField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || name.isEmpty() || blood.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill username, password, full name, and blood group.");
            return;
        }

        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                // 1) users
                String insUser = "INSERT INTO users (username, password, role, name) VALUES (?, ?, ?, ?)";
                try (PreparedStatement ps = c.prepareStatement(insUser)) {
                    ps.setString(1, username);
                    ps.setString(2, password);
                    ps.setString(3, role); // exact strings: Admin | Donor | Recipient
                    ps.setString(4, name);
                    ps.executeUpdate();
                }

                // 2) role profile
                if ("Donor".equals(role)) {
                    String insDonor = "INSERT INTO donor (username, contact_info, blood_group) VALUES (?, ?, ?)";
                    try (PreparedStatement ps = c.prepareStatement(insDonor)) {
                        ps.setString(1, username);
                        ps.setString(2, contact);
                        ps.setString(3, blood);
                        ps.executeUpdate();
                    }
                } else { // Recipient
                    String insRec = "INSERT INTO recipient (username, contact_info, blood_group) VALUES (?, ?, ?)";
                    try (PreparedStatement ps = c.prepareStatement(insRec)) {
                        ps.setString(1, username);
                        ps.setString(2, contact);
                        ps.setString(3, blood);
                        ps.executeUpdate();
                    }
                }

                c.commit();
                JOptionPane.showMessageDialog(this, "Registration successful!");
                dispose();
            } catch (SQLException ex) {
                c.rollback();
                JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }
}


