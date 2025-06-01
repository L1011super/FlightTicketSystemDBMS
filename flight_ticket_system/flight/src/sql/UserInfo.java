package sql;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class UserInfo extends JFrame {
    private String uname, realname, email;
    private long uid, telno, IDno;

    public UserInfo(String uname, long uid, long telno, String email, long IDno,String realname) {
        this.uname = uname;
        this.uid = uid;
        this.realname = realname;
        this.IDno = IDno;
        this.telno = telno;
        this.email = email;

        setTitle("User Information");
        setSize(550, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 主信息区域
        JPanel infoPanel = new JPanel(new GridLayout(7, 3, 10, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        addInfoRow(infoPanel, "UID", String.valueOf(uid), null);
        addInfoRow(infoPanel, "Username", uname, "uname");
        addInfoRow(infoPanel, "Real Name", realname, "realname");
        addInfoRow(infoPanel, "ID Number", String.valueOf(IDno), "IDno");
        addInfoRow(infoPanel, "Telephone", String.valueOf(telno), "telno");
        addInfoRow(infoPanel, "Email", email, "email");

        JPanel pwdPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pwdPanel.setOpaque(false);
        pwdPanel.add(new JLabel("Password:"));
        JLabel pwdMask = new JLabel("*********");
        pwdPanel.add(pwdMask);

        JButton changePwdBtn = new JButton("Change");
        changePwdBtn.setPreferredSize(new Dimension(80, 25));
        pwdPanel.add(changePwdBtn);

        infoPanel.add(pwdPanel);
        changePwdBtn.addActionListener(e -> {
            JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
            JPasswordField oldPwd = new JPasswordField();
            JPasswordField newPwd1 = new JPasswordField();
            JPasswordField newPwd2 = new JPasswordField();

            inputPanel.add(new JLabel("Old Password:"));
            inputPanel.add(oldPwd);
            inputPanel.add(new JLabel("New Password:"));
            inputPanel.add(newPwd1);
            inputPanel.add(new JLabel("Confirm New Password:"));
            inputPanel.add(newPwd2);

            int result = JOptionPane.showConfirmDialog(this, inputPanel, "Change Password", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    Connection conn = DBUtil.getConnection();
                    PreparedStatement ps = conn.prepareStatement("SELECT password FROM user WHERE uid = ?");
                    ps.setLong(1, uid);
                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        String currentPwd = rs.getString(1);
                        if (!currentPwd.equals(new String(oldPwd.getPassword()))) {
                            JOptionPane.showMessageDialog(this, "Incorrect old password.");
                            return;
                        }

                        String np1 = new String(newPwd1.getPassword());
                        String np2 = new String(newPwd2.getPassword());
                        if (!np1.equals(np2)) {
                            JOptionPane.showMessageDialog(this, "New passwords do not match.");
                            return;
                        }

                        // 修改密码
                        PreparedStatement update = conn.prepareStatement("UPDATE user SET password = ? WHERE uid = ?");
                        update.setString(1, np1);
                        update.setLong(2, uid);
                        update.executeUpdate();
                        update.close();

                        JOptionPane.showMessageDialog(this, "Password updated successfully.");
                    }

                    rs.close();
                    ps.close();
                    conn.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        });


        add(infoPanel, BorderLayout.CENTER);

        // 航旅会员区域
        JPanel alliancePanel = new JPanel();
        alliancePanel.setLayout(new BoxLayout(alliancePanel, BoxLayout.Y_AXIS));
        alliancePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        try {
            Connection conn = DBUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM frequentpassenger WHERE uid = ?");
            ps.setLong(1, uid);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                alliancePanel.add(centeredLabel("FLYING ALLIANCE MEMBER"));
                alliancePanel.add(centeredLabel("Urange: " + rs.getString("Urange")));
                alliancePanel.add(centeredLabel("Discount: " + rs.getString("personaldiscount")));
                alliancePanel.add(centeredLabel("Credits: " + rs.getInt("credits")));
            } else {
                alliancePanel.add(centeredLabel("Join in FLYING ALLIANCE"));
                JButton joinBtn = new JButton("Join");
                joinBtn.setPreferredSize(new Dimension(80, 25));
                joinBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
                joinBtn.addActionListener(e -> openJoinDialog());
                alliancePanel.add(joinBtn);
            }

            rs.close();
            ps.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        add(alliancePanel, BorderLayout.SOUTH);

        // 返回按钮
        JButton backBtn = new JButton("Back");
        backBtn.setPreferredSize(new Dimension(80, 25));
        backBtn.addActionListener(e -> {
            new UserDisplay(uname, uid, telno, email, IDno,realname);
            dispose();
        });

        JPanel bottom = new JPanel();
        bottom.add(backBtn);
        add(bottom, BorderLayout.NORTH);

        setVisible(true);
    }

    private void addInfoRow(JPanel panel, String label, String value, String field) {
        JLabel l1 = new JLabel(label + ":");
        l1.setHorizontalAlignment(JLabel.CENTER);
        panel.add(l1);

        JLabel l2 = new JLabel(value);
        l2.setHorizontalAlignment(JLabel.CENTER);
        panel.add(l2);

        if (field != null) {
            JButton btn = new JButton("Change");
            btn.setPreferredSize(new Dimension(80, 25));
            btn.addActionListener(e -> openEditDialog(field, label));
            panel.add(btn);
        } else {
            panel.add(new JLabel(""));
        }
    }

    private JLabel centeredLabel(String text) {
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }


    private void openEditDialog(String field, String label) {
        JDialog dialog = new JDialog(this, "Edit " + label, true);
        dialog.setLayout(new GridLayout(3, 2, 10, 10));
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);

        JPasswordField pwdField = new JPasswordField();
        JTextField newField = new JTextField();

        dialog.add(new JLabel("Password:"));
        dialog.add(pwdField);
        dialog.add(new JLabel("New " + label + ":"));
        dialog.add(newField);

        JButton ok = new JButton("Confirm");
        JButton cancel = new JButton("Cancel");

        ok.addActionListener(e -> {
            try {
                String password = new String(pwdField.getPassword());
                String newValue = newField.getText().trim();
                Connection conn = DBUtil.getConnection();

                PreparedStatement ps = conn.prepareStatement("SELECT password FROM user WHERE uid = ?");
                ps.setLong(1, uid);
                ResultSet rs = ps.executeQuery();

                if (rs.next() && rs.getString("password").equals(password)) {
                    String sql = "UPDATE user SET " + field + " = ? WHERE uid = ?";
                    PreparedStatement update = conn.prepareStatement(sql);

                    if (field.equals("telno") || field.equals("IDno")) {
                        update.setLong(1, Long.parseLong(newValue));
                    } else {
                        update.setString(1, newValue);
                    }

                    update.setLong(2, uid);
                    update.executeUpdate();

                    JOptionPane.showMessageDialog(dialog, "Updated successfully!");
                    dispose();
                    new UserInfo(
                            field.equals("uname") ? newValue : uname,
                            uid,


                            field.equals("telno") ? Long.parseLong(newValue) : telno,
                            field.equals("email") ? newValue : email,
                            field.equals("IDno") ? Long.parseLong(newValue) : IDno,
                            field.equals("realname") ? newValue : realname
                    );

                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Incorrect password.");
                }

                rs.close();
                ps.close();
                conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        cancel.addActionListener(e -> dialog.dispose());

        dialog.add(ok);
        dialog.add(cancel);

        dialog.setVisible(true);
    }

    private void openJoinDialog() {
        JDialog dialog = new JDialog(this, "Join FLYING ALLIANCE", true);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));
        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(this);

        JLabel urangeField = new JLabel("Lv1");
        JLabel discountField = new JLabel();
        JLabel creditField = new JLabel("0");

        dialog.add(new JLabel("Urange:"));
        dialog.add(urangeField);
        dialog.add(new JLabel("Discount:"));
        dialog.add(discountField);
        dialog.add(new JLabel("Credits:"));
        dialog.add(creditField);

        JButton confirm = new JButton("Confirm");
        JButton cancel = new JButton("Cancel");

        confirm.addActionListener(e -> {
            try {
                Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO frequentpassenger(uid, Urange, personaldiscount, credits) VALUES (?, ?, ?, ?)"
                );
                ps.setLong(1, uid);
                ps.setString(2, urangeField.getText().trim());
                ps.setString(3, discountField.getText().trim());
                ps.setInt(4, Integer.parseInt(creditField.getText().trim()));
                ps.executeUpdate();

                JOptionPane.showMessageDialog(dialog, "Successfully joined!");
                dialog.dispose();
                dispose();
                new UserInfo(uname, uid, telno, email, IDno,realname);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        cancel.addActionListener(e -> dialog.dispose());

        dialog.add(confirm);
        dialog.add(cancel);
        dialog.setVisible(true);
    }
}
