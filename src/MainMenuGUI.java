import javax.swing.*;

public class MainMenuGUI {
    public MainMenuGUI() {
        JFrame frame = new JFrame("Blood Donation Management System");
        frame.setSize(400, 400);
        frame.setLayout(null);

        JButton addDonorBtn = new JButton("Add Donor");
        addDonorBtn.setBounds(100, 50, 200, 30);
        addDonorBtn.addActionListener(e -> new AddDonorGUI());

        JButton addRequestBtn = new JButton("Add Blood Request");
        addRequestBtn.setBounds(100, 100, 200, 30);
        addRequestBtn.addActionListener(e -> new AddRequestGUI());

        JButton recordDonationBtn = new JButton("Record Donation");
        recordDonationBtn.setBounds(100, 150, 200, 30);
        recordDonationBtn.addActionListener(e -> new RecordDonationGUI());

        JButton viewDataBtn = new JButton("View Data");
        viewDataBtn.setBounds(100, 200, 200, 30);
        viewDataBtn.addActionListener(e -> new ViewDataGUI());

        frame.add(addDonorBtn);
        frame.add(addRequestBtn);
        frame.add(recordDonationBtn);
        frame.add(viewDataBtn);

        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
