package view;

import javax.swing.*;
import java.awt.*;

public class HomePanel extends JPanel {
    public HomePanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel lbl = new JLabel(
            "<html><center><h2>Chào mừng đến với<br/>" +
            "Hệ thống Quản lý Trung tâm Gia sư</h2>" +
            "<p>Sử dụng các tab ở trên để thao tác với hệ thống.</p></center></html>",
            SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.PLAIN, 15));
        add(lbl, BorderLayout.CENTER);
    }
}
