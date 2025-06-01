package sql;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class BookingList extends JFrame {
    private String uname;
    private long uid;
    private long telno;
    private String email;
    private long IDno;
    private String realname;

    public BookingList(String uname, long uid, long telno, String email, long IDno, String realname) {
        this.uname = uname;
        this.uid = uid;
        this.telno = telno;
        this.email = email;
        this.IDno = IDno;
        this.realname = realname;

        setTitle(uname + "'s Bookings");
        setSize(1200, 550); // 减小窗口尺寸
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5)); // 减小布局间距

        // 创建紧凑的表头
        JPanel headerPanel = new JPanel(new GridLayout(1, 10, 3, 3)); // 10列，小间距
        headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        headerPanel.setBackground(new Color(240, 245, 255));

        // 新增Aircraft列
        String[] headers = {"Date", "Flight No", "Route", "Departure", "Arrival",
                "Aircraft", "Price", "Seat", "Reservation", "Action"};

        for (String h : headers) {
            JLabel label = new JLabel(h);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("SansSerif", Font.BOLD, 11)); // 减小字体
            label.setForeground(new Color(50, 70, 110));
            headerPanel.add(label);
        }
        add(headerPanel, BorderLayout.NORTH);

        // 内容区
        JPanel bookingPanel = new JPanel();
        bookingPanel.setLayout(new BoxLayout(bookingPanel, BoxLayout.Y_AXIS));
        bookingPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5)); // 减小内边距

        JScrollPane scrollPane = new JScrollPane(bookingPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // 更快滚动
        add(scrollPane, BorderLayout.CENTER);

        // 返回按钮
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        backBtn.setBackground(new Color(70, 130, 180));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setPreferredSize(new Dimension(150, 30));

        backBtn.addActionListener(e -> {
            new UserDisplay(uname, uid, telno, email, IDno, realname);
            dispose();
        });

        bottomPanel.add(backBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        try (Connection conn = DBUtil.getConnection()) {
            // 使用视图查询（新增aircraft_type字段）
            String sql = """
                SELECT fldate, flightno, route, distance, price, seatno, rsvno,
                       departuretime, arrivaltime, aircraft_regno, aircraft_type
                FROM v_user_bookings
                WHERE uid = ?
                ORDER BY fldate DESC
                """;
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, uid);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String fldate = rs.getString("fldate");
                String flightNo = rs.getString("flightno");
                String route = rs.getString("route");
                int distance = rs.getInt("distance");
                double price = rs.getDouble("price");
                int seatNo = rs.getInt("seatno");
                String rsvno = rs.getString("rsvno");
                String depTime = rs.getString("departuretime");
                String arrTime = rs.getString("arrivaltime");
                String aircraftReg = rs.getString("aircraft_regno");
                String aircraftType = rs.getString("aircraft_type");

                // 创建紧凑的行面板
                JPanel row = new JPanel(new GridLayout(1, 10, 3, 3)); // 10列，小间距
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28)); // 减小行高
                row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 225, 230)));
                row.setBackground(Color.WHITE);

                // 日期 (保持原始格式)
                String formattedDate = fldate.substring(0, 4) + "/" +
                        fldate.substring(4, 6) + "/" +
                        fldate.substring(6);
                row.add(createCenteredLabel(formattedDate));

                // 航班号
                row.add(createCenteredLabel(flightNo));

                // 航线
                row.add(createCenteredLabel(route));

                // 起飞时间 (只显示时间部分)
                row.add(createCenteredLabel(depTime.substring(0, 5)));

                // 到达时间 (只显示时间部分)
                row.add(createCenteredLabel(arrTime.substring(0, 5)));

                // 机型 (新增列)
                JLabel aircraftLabel = createCenteredLabel(aircraftType);
                aircraftLabel.setToolTipText("Aircraft: " + aircraftReg);
                row.add(aircraftLabel);

                // 价格
                JLabel priceLabel = createCenteredLabel(String.format("%.2f", price));
                priceLabel.setForeground(new Color(40, 120, 60));
                priceLabel.setFont(new Font("SansSerif", Font.BOLD, 10)); // 减小字体
                row.add(priceLabel);

                // 座位号
                row.add(createCenteredLabel(String.valueOf(seatNo)));

                // 预订号 (保持完整显示)
                JLabel rsvLabel = createCenteredLabel(rsvno);
                rsvLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10)); // 减小字体
                row.add(rsvLabel);

                // 取消按钮
                JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
                btnPanel.setOpaque(false);

                JButton cancelBtn = new JButton("Refund");
                cancelBtn.setFont(new Font("SansSerif", Font.PLAIN, 10)); // 减小字体
                cancelBtn.setBackground(new Color(220, 80, 60));
                cancelBtn.setForeground(Color.WHITE);
                cancelBtn.setFocusPainted(false);
                cancelBtn.setMargin(new Insets(1, 5, 1, 5)); // 减小按钮内边距
                cancelBtn.setPreferredSize(new Dimension(80, 24)); // 减小按钮尺寸
                btnPanel.add(cancelBtn);

                row.add(btnPanel);

                cancelBtn.addActionListener(e -> showCancellationDialog(
                        fldate, flightNo, route, depTime, arrTime,
                        aircraftType, seatNo, price, rsvno,
                        distance
                ));

                bookingPanel.add(row);
            }

            // 如果没有预订记录
            if (bookingPanel.getComponentCount() == 0) {
                JPanel noBookingPanel = new JPanel(new BorderLayout());
                noBookingPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));

                JLabel noBookingLabel = new JLabel("No bookings found", SwingConstants.CENTER);
                noBookingLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
                noBookingLabel.setForeground(new Color(150, 150, 150));

                noBookingPanel.add(noBookingLabel, BorderLayout.CENTER);
                bookingPanel.add(noBookingPanel);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to load booking records: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        setVisible(true);
    }

    private JLabel createCenteredLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.PLAIN, 10)); // 减小字体
        return label;
    }

    private void showCancellationDialog(
            String fldate, String flightNo, String route,
            String depTime, String arrTime, String aircraftType,
            int seatNo, double price, String rsvno,
            int distance
    ) {
        // 弹出确认窗口
        JPanel confirmPanel = new JPanel(new GridLayout(0, 1, 3, 3)); // 减小间距
        confirmPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        confirmPanel.add(createDialogLabel("Are you sure to cancel this booking?", Font.BOLD));
        confirmPanel.add(Box.createVerticalStrut(8));
        confirmPanel.add(createDialogLabel("Date: " + fldate, Font.PLAIN));
        confirmPanel.add(createDialogLabel("Flight: " + flightNo + " (" + route + ")", Font.PLAIN));
        confirmPanel.add(createDialogLabel("Departure: " + depTime + " | Arrival: " + arrTime, Font.PLAIN));
        confirmPanel.add(createDialogLabel("Aircraft: " + aircraftType, Font.PLAIN)); // 新增飞机类型
        confirmPanel.add(createDialogLabel("Seat: " + seatNo + " | Price: $" + String.format("%.2f", price), Font.PLAIN));
        confirmPanel.add(createDialogLabel("Reservation: " + rsvno, Font.PLAIN));
        confirmPanel.add(Box.createVerticalStrut(8));
        confirmPanel.add(createDialogLabel("Refunds will be processed within 7 business days", Font.ITALIC));

        int choice = JOptionPane.showConfirmDialog(
                this,
                confirmPanel,
                "Cancel Booking",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            processCancellation(rsvno, distance);
        }
    }

    private JLabel createDialogLabel(String text, int style) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", style, 12));
        return label;
    }

    private void processCancellation(String rsvno, int distance) {
        try (Connection conn2 = DBUtil.getConnection()) {
            // 调用存储过程
            CallableStatement cs = conn2.prepareCall("{CALL cancel_booking(?, ?, ?)}");
            cs.setString(1, rsvno);
            cs.setLong(2, uid);
            cs.setInt(3, distance);
            cs.execute();
            cs.close();

            JOptionPane.showMessageDialog(
                    this,
                    "Ticket cancelled successfully.\nRefund will be processed within 7 days.",
                    "Cancellation Confirmed",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();
            new BookingList(uname, uid, telno, email, IDno, realname); // 刷新页面
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Cancellation failed: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}