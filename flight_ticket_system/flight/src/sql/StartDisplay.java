package sql;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

public class StartDisplay extends JFrame {
    public StartDisplay() {
        setTitle("Flying Easily");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 背景图
        JLabel background = new JLabel(new ImageIcon("background1.jpg")); // 请放置图片同目录
        background.setLayout(new BorderLayout());
        setContentPane(background);

        // 中部文字
        JPanel centerPanel = new JPanel(new GridLayout(3, 1));
        centerPanel.setOpaque(false);
        JLabel title = new JLabel("Flying Easily Flight Booking System", JLabel.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 26));
        JLabel subtitle = new JLabel("Feel free to book tickets!", JLabel.CENTER);
        subtitle.setFont(new Font("SansSerif", Font.ITALIC, 18));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        JButton signIn = new JButton("Sign in");
        JButton logIn = new JButton("Log in");

        signIn.addActionListener(e -> {
            new RegistrationDisplay();
            dispose();
        });
        logIn.addActionListener(e -> {
            new LoginDisplay();
            dispose();
        });

        buttonPanel.add(signIn);
        buttonPanel.add(logIn);

        centerPanel.add(title);
        centerPanel.add(subtitle);
        centerPanel.add(buttonPanel);
        add(centerPanel, BorderLayout.CENTER);

        setVisible(true);
    }
}


