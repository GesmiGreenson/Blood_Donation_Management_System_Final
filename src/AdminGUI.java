import javax.swing.*;
import java.awt.*;

public class AdminGUI extends JFrame {

    public AdminGUI() {
        setTitle("Admin Dashboard");
        setSize(520, 340);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel grid = new JPanel(new GridLayout(0, 1, 8, 8));
        grid.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JButton addDonorBtn        = new JButton("Add Donor");
        JButton addRequestBtn      = new JButton("Add Request");
        JButton viewDataBtn        = new JButton("View Data");
        JButton recordDonationBtn  = new JButton("Record Donation");
        JButton logoutBtn          = new JButton("Logout");

        addDonorBtn.addActionListener(e -> new AddDonorGUI().setVisible(true));
        addRequestBtn.addActionListener(e -> new AddRequestGUI().setVisible(true));
        viewDataBtn.addActionListener(e -> new ViewDataGUI().setVisible(true));
        recordDonationBtn.addActionListener(e -> new RecordDonationGUI().setVisible(true));
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginGUI().setVisible(true);
        });

        grid.add(addDonorBtn);
        grid.add(addRequestBtn);
        grid.add(viewDataBtn);
        grid.add(recordDonationBtn);
        grid.add(logoutBtn);

        add(new JLabel("Admin Functions", SwingConstants.CENTER), BorderLayout.NORTH);
        add(grid, BorderLayout.CENTER);
    }
}

