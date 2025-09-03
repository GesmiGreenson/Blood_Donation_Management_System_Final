import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;

    public LoginGUI() {
        setTitle("Blood Donation System - Login");
        setSize(420, 260);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        roleCombo = new JComboBox<>(new String[]{"Admin", "Donor", "Recipient"});
        form.add(roleCombo);

        JPanel actions = new JPanel();
        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");
        actions.add(loginBtn);
        actions.add(registerBtn);

        add(form, BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);

        loginBtn.addActionListener(e -> doLogin());
        registerBtn.addActionListener(e -> new RegisterGUI().setVisible(true));
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role     = (String) roleCombo.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill username and password.");
            return;
        }

        String sql = "SELECT 1 FROM users WHERE username=? AND password=? AND role=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dispose();
                    switch (role) {
                        case "Admin"     -> new AdminGUI().setVisible(true);
                        case "Donor"     -> new DonorGUI(username).setVisible(true);
                        case "Recipient" -> new RecipientGUI(username).setVisible(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials / role combination.");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginGUI().setVisible(true));
    }
}
