package sql;

import javax.swing.*;
import java.awt.*;

public class UserDisplay extends JFrame {
    String uname;
    long uid;
    long telno;
    String email;
    long IDno;
    String realname;

    // 构造器接收 nickname 和 uid
    public UserDisplay(String uname, long uid, long telno, String email, long IDno,String realname) {
        this.uname = uname;
        this.uid = uid;
        this.telno = telno;
        this.email = email;
        this.IDno = IDno;
        this.realname=realname;

        setTitle("Welcome");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // 背景图
        setContentPane(new JLabel(new ImageIcon("780.jpg")));
        setLayout(new BorderLayout());

        // 欢迎语
        JLabel welcome = new JLabel("Hi " + uname + ", fly easily", JLabel.CENTER);
        welcome.setFont(new Font("Arial", Font.BOLD, 48));
        welcome.setForeground(Color.BLUE);
        add(welcome, BorderLayout.NORTH);

        // 按钮布局
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setOpaque(false);
        Dimension buttonSize = new Dimension(120, 30);

        JButton queryBtn = new JButton("Search");
        JButton orderBtn = new JButton("Bookings");
        JButton logoutBtn = new JButton("Log out");
        JButton frequentBtn = new JButton("Information");

        queryBtn.setPreferredSize(buttonSize);
        orderBtn.setPreferredSize(buttonSize);
        logoutBtn.setPreferredSize(buttonSize);
        frequentBtn.setPreferredSize(buttonSize);

        queryBtn.addActionListener(e -> {
            new UsersSearch( uname,  uid,  telno,  email,  IDno,realname); // 传入uid和IDno
            dispose();
        });

        frequentBtn.addActionListener(e -> {
            new UserInfo(uname,  uid,  telno,  email,  IDno,realname); // 传入uid和IDno
            dispose();
        });

        logoutBtn.addActionListener(e -> {
            new StartDisplay();
            dispose();
        });
        orderBtn.addActionListener(e -> {
            new BookingList(uname,  uid,  telno,  email,  IDno,realname);
            dispose();
        });

        panel.add(queryBtn);
        panel.add(orderBtn);
        panel.add(logoutBtn);
        panel.add(frequentBtn);

        add(panel, BorderLayout.SOUTH);

        setVisible(true);
    }
}
