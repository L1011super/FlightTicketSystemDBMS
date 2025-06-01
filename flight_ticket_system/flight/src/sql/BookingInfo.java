package sql;

import javax.swing.*;

public class BookingInfo extends JFrame {
    //basic
    String uname;
    long uid;
    long telno;
    String email;
    long IDno;
    String realname;
    //unique
    String date;
    String flightno;
    String type;
    int seatno;
    double price;
    int distance;

    public BookingInfo(String flightNo) {
        setTitle("Booking - " + flightNo);
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel label = new JLabel("Booking page for flight: " + flightNo, JLabel.CENTER);
        add(label);

        setVisible(true);
    }
}
