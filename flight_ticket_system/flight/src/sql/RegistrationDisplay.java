package sql;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegistrationDisplay extends JFrame {
    public RegistrationDisplay() {
        setTitle("User Registration");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        String[] labels = {"Real Name", "Username", "Telephone", "Email", "ID Number", "Password", "Confirm Password"};
        JTextField[] fields = new JTextField[labels.length - 2];
        JPasswordField pwd1 = new JPasswordField();
        JPasswordField pwd2 = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(8, 2, 5, 5));

        for (int i = 0; i < labels.length - 2; i++) {
            panel.add(new JLabel(labels[i],JLabel.CENTER));
            fields[i] = new JTextField();
            panel.add(fields[i]);
        }

        panel.add(new JLabel(labels[5],JLabel.CENTER));
        panel.add(pwd1);
        panel.add(new JLabel(labels[6],JLabel.CENTER));
        panel.add(pwd2);

        JButton submit = new JButton("Register");
        submit.addActionListener(e -> {
            String password1 = new String(pwd1.getPassword());
            String password2 = new String(pwd2.getPassword());
            if (!password1.equals(password2)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match!");
                return;
            }

            // 验证输入格式
            String telText = fields[2].getText().trim();
            String email = fields[3].getText().trim();
            String idText = fields[4].getText().trim();
/*
            if (telText.length() != 11 || !telText.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Telephone must be 11 digits!");
                return;
            }
            if (!email.contains("@")) {
                JOptionPane.showMessageDialog(this, "Email must contain '@'!");
                return;
            }
            if (idText.length() != 18 || !idText.matches("\\d{18}")) {
                JOptionPane.showMessageDialog(this, "ID number must be 18 digits!");
                return;
            }
*/
            try {
                long telno = Long.parseLong(telText);
                long IDno = Long.parseLong(idText);

                Connection conn = DBUtil.getConnection();
                Statement stmt = conn.createStatement();

                // 获取最大uid
                ResultSet rs = stmt.executeQuery("SELECT MAX(uid) FROM user");
                long newUid = 100000001L;
                if (rs.next() && rs.getLong(1) > 0) {
                    newUid = rs.getLong(1) + 1;
                }

                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO user(uid, realname, uname, telno, email, IDno, password) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?)"
                );
                ps.setLong(1, newUid);
                ps.setString(2, fields[0].getText());
                ps.setString(3, fields[1].getText());
                ps.setLong(4, telno);
                ps.setString(5, email);
                ps.setLong(6, IDno);
                ps.setString(7, password1);
                String realname=fields[0].getText();

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Registration successful! UID: " + newUid);


                new UserDisplay(
                        fields[1].getText(), // nickname
                        newUid,
                        telno,
                        email,
                        IDno,
                        realname
                );

                dispose();

                ps.close();
                stmt.close();
                conn.close();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number format: " + ex.getMessage());
            } catch (SQLException ex) {
                // 捕获数据库触发器的错误信息
                String errorMsg = ex.getMessage();
                if (errorMsg.contains("telno_length")) {
                    JOptionPane.showMessageDialog(this, "Database error: Telephone must be 11 digits!");
                } else if (errorMsg.contains("email_format")) {
                    JOptionPane.showMessageDialog(this, "Database error: Invalid email format!");
                } else if (errorMsg.contains("idno_length")) {
                    JOptionPane.showMessageDialog(this, "Database error: ID number must be 18 digits!");
                } else {
                    JOptionPane.showMessageDialog(this, "Database error: " + errorMsg);
                }
                ex.printStackTrace();
            }
        });

        add(panel, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(submit);

        JButton backBtn = new JButton("back");
        backBtn.addActionListener(e -> {
            new StartDisplay();
            dispose();
        });
        bottomPanel.add(backBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
}