import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewDataGUI extends JFrame {

    public ViewDataGUI() {
        setTitle("View Data");
        setSize(700, 420);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Donors", new JScrollPane(
            buildTable("SELECT donor_id, username, blood_group, contact_info FROM donor")
        ));
        tabs.addTab("Requests", new JScrollPane(
            buildTable("SELECT request_id, requester_name, blood_group, quantity, contact_info, request_date FROM blood_request")
        ));
        tabs.addTab("Donations", new JScrollPane(
            buildTable("SELECT donation_id, donor_id, request_id, donation_date FROM donation")
        ));

        add(tabs);
    }

    private JTable buildTable(String query) {
        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            int cols = rs.getMetaData().getColumnCount();
            String[] headers = new String[cols];
            for (int i = 1; i <= cols; i++) {
                headers[i - 1] = rs.getMetaData().getColumnLabel(i);
            }
            model.setColumnIdentifiers(headers);

            while (rs.next()) {
                Object[] row = new Object[cols];
                for (int i = 1; i <= cols; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                model.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
        return table;
    }
}
