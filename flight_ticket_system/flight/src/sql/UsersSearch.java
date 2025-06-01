package sql;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class UsersSearch extends JFrame {
    String uname;
    long uid;
    long telno;
    String email;
    long IDno;
    String realname;

    public UsersSearch(String uname, long uid, long telno, String email, long IDno, String realname) {
        this.uname = uname;
        this.uid = uid;
        this.telno = telno;
        this.email = email;
        this.IDno = IDno;
        this.realname = realname;

        setTitle("Flight Search");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new FlowLayout());
        JTextField fromField = new JTextField(5);
        JTextField toField = new JTextField(5);
        JTextField monthField = new JTextField(2);
        JTextField dayField = new JTextField(2);
        JButton searchBtn = new JButton("Search");
        JButton backBtn = new JButton("Back");

        inputPanel.add(new JLabel("From:"));
        inputPanel.add(fromField);
        inputPanel.add(new JLabel("To:"));
        inputPanel.add(toField);
        inputPanel.add(new JLabel("Month:"));
        inputPanel.add(monthField);
        inputPanel.add(new JLabel("Day:"));
        inputPanel.add(dayField);
        inputPanel.add(searchBtn);
        inputPanel.add(backBtn);
        add(inputPanel, BorderLayout.NORTH);

        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(resultPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // 更快滚动
        add(scrollPane, BorderLayout.CENTER);

        searchBtn.addActionListener(e -> {
            resultPanel.removeAll();

            String from = fromField.getText().toUpperCase().trim();
            String to = toField.getText().toUpperCase().trim();
            String month = String.format("%02d", Integer.parseInt(monthField.getText().trim()));
            String day = String.format("%02d", Integer.parseInt(dayField.getText().trim()));
            String date = "2025" + month + day;

            boolean hasResult = false;

            try (Connection conn = DBUtil.getConnection()) {

                String sql = """
                    SELECT * FROM flight 
                    WHERE fldate =? AND DEP_IATAcode =? AND ARV_IATAcode =?
                    ORDER BY departuretime ASC
                    """;

                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, date);
                ps.setString(2, from);
                ps.setString(3, to);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    hasResult = true;
                    JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
                    row.setAlignmentX(Component.LEFT_ALIGNMENT); // 左对齐
                    row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                    row.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10)); // 控制上下间距

                    row.add(new JLabel("Date: " + rs.getString("fldate")));
                    row.add(new JLabel("Flight: " + rs.getString("flightno")));
                    row.add(new JLabel("From: " + rs.getString("DEP_IATAcode")));
                    row.add(new JLabel("To: " + rs.getString("ARV_IATAcode")));
                    row.add(new JLabel("Time: " + rs.getString("departuretime") + "-" + rs.getString("arrivaltime")));

                    JButton bookBtn = new JButton("Book");
                    String flightNo = rs.getString("flightno");
                    String departureTime = rs.getString("departuretime");
                    String arrivalTime = rs.getString("arrivaltime");

                    bookBtn.addActionListener(ev -> {
                        try (Connection conn2 = DBUtil.getConnection()) {
                            conn2.setAutoCommit(false); // 开启事务
                            try {
                                PreparedStatement regnoStmt = conn2.prepareStatement(
                                        "SELECT * FROM aircraft WHERE regno = (SELECT regNo FROM flight WHERE flightno =? AND fldate =?)");
                                regnoStmt.setString(1, flightNo);
                                regnoStmt.setString(2, date);
                                ResultSet regnoRs = regnoStmt.executeQuery();

                                String type = "", regno = "", age = "";
                                int capacity = 0;

                                if (regnoRs.next()) {
                                    type = regnoRs.getString("type");
                                    regno = regnoRs.getString("regno");
                                    age = regnoRs.getString("age");
                                    capacity = regnoRs.getInt("seatcapacity");
                                }
                                regnoRs.close();
                                regnoStmt.close();

                                PreparedStatement seatStmt = conn2.prepareStatement(
                                        "SELECT seatno FROM booking WHERE flightno =? AND fldate =?");
                                seatStmt.setString(1, flightNo);
                                seatStmt.setString(2, date);
                                ResultSet seatRs = seatStmt.executeQuery();

                                boolean[] seatUsed = new boolean[capacity + 1];
                                while (seatRs.next()) {
                                    seatUsed[seatRs.getInt("seatno")] = true;
                                }
                                seatRs.close();
                                seatStmt.close();

                                int seatno = -1;
                                for (int i = 1; i <= capacity; i++) {
                                    if (!seatUsed[i]) {
                                        seatno = i;
                                        break;
                                    }
                                }

                                if (seatno == -1) {
                                    JOptionPane.showMessageDialog(this, "No seat available.");
                                    return;
                                }

                                PreparedStatement distStmt = conn2.prepareStatement(
                                        "SELECT distance FROM flight WHERE flightno =? AND fldate =?");
                                distStmt.setString(1, flightNo);
                                distStmt.setString(2, date);
                                ResultSet distRs = distStmt.executeQuery();

                                int distance = 0;
                                if (distRs.next()) distance = distRs.getInt("distance");
                                distRs.close();
                                distStmt.close();

                                double price = distance * 1.15;
                                String rsvno = generateRsvno(date, flightNo, IDno, seatno);

                                JPanel panel = new JPanel(new GridLayout(0, 1, 10, 5));
                                panel.add(new JLabel("Flight No: " + flightNo));
                                panel.add(new JLabel("Date: " + date));
                                panel.add(new JLabel("From: " + from));
                                panel.add(new JLabel("To: " + to));
                                panel.add(new JLabel("Departure - Arrival: " + departureTime + " - " + arrivalTime));
                                panel.add(new JLabel("RegNo: " + regno + "  Type: " + type + "  Age: " + age));
                                panel.add(new JLabel("Seat No: " + seatno + "  Price: " + String.format("%.2f", price)));

                                int option = JOptionPane.showConfirmDialog(this, panel, "Confirm Booking", JOptionPane.OK_CANCEL_OPTION);
                                if (option == JOptionPane.OK_OPTION) {
                                    // 插入预订记录
                                    PreparedStatement ins = conn2.prepareStatement("INSERT INTO booking VALUES (?,?,?,?,?,?)");
                                    ins.setString(1, rsvno);
                                    ins.setString(6, date);
                                    ins.setString(2, flightNo);
                                    ins.setLong(3, uid);
                                    ins.setInt(5, seatno);
                                    ins.setDouble(4, price);
                                    ins.executeUpdate();
                                    ins.close();

                                    // 调用存储过程更新积分
                                    CallableStatement call = conn2.prepareCall("{CALL update_credits1(?,?)}");
                                    call.setLong(1, uid);
                                    call.setInt(2, distance);
                                    call.execute();
                                    call.close();

                                    conn2.commit(); // 事务提交
                                    JOptionPane.showMessageDialog(this,
                                            "Booking Success! Your Reservation No: " + rsvno);
                                }
                            } catch (SQLException ex) {
                                conn2.rollback(); // 出现异常回滚事务
                                // 处理存储过程抛出的自定义错误
                                String errorMsg = ex.getMessage();

                                // 提取并简化错误信息
                                if (errorMsg.contains("Distance must be positive")) {
                                    errorMsg = "Invalid flight distance";
                                } else if (errorMsg.contains("User does not exist")) {
                                    errorMsg = "User account not found";
                                }

                                JOptionPane.showMessageDialog(this,
                                        "Booking failed: " + errorMsg,
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
                            } finally {
                                try {
                                    conn2.setAutoCommit(true);
                                } catch (SQLException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(this,
                                    "Booking failed: " + ex.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    });


                    row.add(bookBtn);
                    resultPanel.add(row);
                }

                if (!hasResult) {
                    resultPanel.add(new JLabel("Sorry, No Matching Flights."));
                }

                resultPanel.revalidate();
                resultPanel.repaint();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "查询失败：" + ex.getMessage());
            }
        });

        backBtn.addActionListener(e -> {
            new UserDisplay(uname, uid, telno, email, IDno, realname);
            dispose();
        });

        setVisible(true);
    }

    private String generateRsvno(String date, String flightno, long IDno, int seatno) {
        try {
            String input = date + flightno + IDno + seatno;
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString().toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
            return "ERRRRSVNUM000000";
        }
    }
}