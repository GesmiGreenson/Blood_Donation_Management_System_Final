import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AddDonorGUI extends JFrame {
    private JTextField nameField, ageField, genderField, bloodGroupField, phoneField, emailField, cityField;

    public AddDonorGUI() {
        setTitle("Add Donor");
        setSize(420, 360);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel form = new JPanel(new GridLayout(0,2,8,8));
        form.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        form.add(new JLabel("Name:"));
        nameField = new JTextField(); form.add(nameField);

        form.add(new JLabel("Age:"));
        ageField = new JTextField(); form.add(ageField);

        form.add(new JLabel("Gender:"));
        genderField = new JTextField(); form.add(genderField);

        form.add(new JLabel("Blood Group:"));
        bloodGroupField = new JTextField(); form.add(bloodGroupField);

        form.add(new JLabel("Phone:"));
        phoneField = new JTextField(); form.add(phoneField);

        form.add(new JLabel("Email:"));
        emailField = new JTextField(); form.add(emailField);

        form.add(new JLabel("City:"));
        cityField = new JTextField(); form.add(cityField);

        JButton save = new JButton("Save");
        save.addActionListener(e -> saveDonor());

        add(form, BorderLayout.CENTER);
        add(save, BorderLayout.SOUTH);
    }

    private void saveDonor() {
        String sql = "INSERT INTO donor(name, age, gender, blood_group, phone, email, city) VALUES(?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nameField.getText().trim());
            ps.setInt(2, Integer.parseInt(ageField.getText().trim()));
            ps.setString(3, genderField.getText().trim());
            ps.setString(4, bloodGroupField.getText().trim());
            ps.setString(5, phoneField.getText().trim());
            ps.setString(6, emailField.getText().trim());
            ps.setString(7, cityField.getText().trim());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Donor added!");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}

