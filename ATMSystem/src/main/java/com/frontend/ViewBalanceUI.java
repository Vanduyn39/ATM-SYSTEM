package com.frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class ViewBalanceUI extends JFrame {
    JLabel l1, l2, l3, l4, accountNumberLabel, fullNameLabel, balanceLabel;
    JButton exitButton;
    private String accountNumber;
    private String authToken;

    public ViewBalanceUI(String accountNumber, String authToken) {
        this.accountNumber = accountNumber;
        this.authToken = authToken;

        setTitle("ATM - View Balance");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        l1 = new JLabel("View Balance Account");
        l1.setFont(new Font("Osward", Font.BOLD, 32));
        l1.setBounds(200, 50, 600, 30);
        add(l1);

        l2 = new JLabel("Account Number:");
        l2.setFont(new Font("Arial", Font.PLAIN, 20));
        l2.setBounds(150, 200, 200, 30);
        add(l2);

        accountNumberLabel = new JLabel(accountNumber);
        accountNumberLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        accountNumberLabel.setBounds(350, 200, 200, 30);
        add(accountNumberLabel);

        l4 = new JLabel("Current Balance:");
        l4.setFont(new Font("Arial", Font.PLAIN, 20));
        l4.setBounds(150, 300, 150, 30);
        add(l4);

        balanceLabel = new JLabel("Loading...");
        balanceLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        balanceLabel.setBounds(350, 300, 200, 30);
        add(balanceLabel);

        exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.BOLD, 20));
        exitButton.setBounds(450, 400, 150, 50);
        add(exitButton);

        getContentPane().setBackground(Color.WHITE);
        setSize(700, 600); // Điều chỉnh kích thước
        setLocation(250, 0);
        setVisible(true);

        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new TransactionsUI(accountNumber,authToken).setVisible(true);
                dispose();
            }
        });

        loadAccountDetails();
    }

    public void loadAccountDetails() {
        try {
            URL url = new URL("http://localhost:8080/api/balance/balance");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Cấu hình yêu cầu HTTP GET và truyền token qua header
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + authToken);

            // Kiểm tra mã phản hồi HTTP
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            // Đọc phản hồi từ API
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder response = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                response.append(output);
            }
            br.close();
            conn.disconnect();

            // Xử lý dữ liệu phản hồi
            double balance = Double.parseDouble(response.toString()); // API trả về số dư dạng Double

            // Cập nhật giao diện
            balanceLabel.setText(String.format("%.2f VND", balance));

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading account information! Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
