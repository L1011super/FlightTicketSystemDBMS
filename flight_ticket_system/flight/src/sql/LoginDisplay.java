package sql;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginDisplay extends JFrame {
    public LoginDisplay() {
        setTitle("User Login");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTextField uidField = new JTextField();
        JPasswordField pwdField = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("UID:",JLabel.CENTER));
        panel.add(uidField);
        panel.add(new JLabel("Password:",JLabel.CENTER));
        panel.add(pwdField);

        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> {
            long uid = Long.parseLong(uidField.getText().trim());
            String inputPassword = new String(pwdField.getPassword());

            try {
                Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT * FROM user WHERE uid = ?"
                );
                ps.setLong(1, uid);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    String uname = rs.getString("uname"); // 也可以是 "realname"
                    long telno = rs.getLong("telno");
                    String email = rs.getString("email");
                    long IDno = rs.getLong("IDno");
                    String realname=rs.getString("realname");

                    if (storedPassword.equals(inputPassword)) {
                        JOptionPane.showMessageDialog(this, "Login successful!");
                        new UserDisplay(uname, uid, telno, email, IDno,realname); // 👈 传入全部参数
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Incorrect password.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "User ID not found.");
                }

                rs.close();
                ps.close();
                conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Login error: " + ex.getMessage());
            }
        });


        add(panel, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(loginBtn);

        JButton backBtn = new JButton("返回");
        backBtn.addActionListener(e -> {
            new StartDisplay();
            dispose();
        });
        bottomPanel.add(backBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
}

