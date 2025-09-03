import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegisterGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;

    public RegisterGUI() {
        setTitle("Register New User");
        setSize(360, 230);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        form.add(new JLabel("Username:"));
        usernameField = new JTextField();
        form.add(usernameField);

        form.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        form.add(passwordField);

        form.add(new JLabel("Role:"));
        roleCombo = new JComboBox<>(new String[]{"Donor", "Recipient", "Admin"});
        form.add(roleCombo);

        JButton registerBtn = new JButton("Create Account");
        add(form, BorderLayout.CENTER);
        add(registerBtn, BorderLayout.SOUTH);

        registerBtn.addActionListener(e -> doRegister());
    }

    private void doRegister() {
        String u = usernameField.getText().trim();
        String p = new String(passwordField.getPassword());
        String r = (String) roleCombo.getSelectedItem();

        if (u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        String sql = "INSERT INTO users(username, password, role) VALUES(?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u);
            ps.setString(2, p);
            ps.setString(3, r);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "User registered successfully!");
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }
}

