package com.frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TransferUI extends JFrame {
    private JLabel l1, l2, l3;
    private JButton btnexit, btnTransfer;
    private JTextField accountNumText,amountText;
    private String accountNumber;
    private String authToken;
    public TransferUI(String accountNumber,String authToken) {
        this.accountNumber = accountNumber;
        this.authToken = authToken;
        initializeComponents();
        addComponentsToFrame();
        addActionListeners();
        configureFrame();
    }
    private void initializeComponents() {
        l1 = new JLabel("Enter The Information To Transfer");
        l1.setFont(new Font("Osward", Font.BOLD, 32));

        l2 = new JLabel("Account Number:");
        l2.setFont(new Font("Raleway", Font.BOLD, 22));

        accountNumText = new JTextField(15);
        accountNumText.setFont(new Font("Arial", Font.BOLD, 24));

        l3 = new JLabel("Amount:");
        l3.setFont(new Font("Raleway", Font.BOLD, 22));

        amountText = new JTextField(15);
        amountText.setFont(new Font("Arial", Font.BOLD, 24));

        btnexit = new JButton("Exit");
        btnexit.setFont(new Font("Arial", Font.BOLD, 24));

        btnTransfer = new JButton("Transfer");
        btnTransfer.setFont(new Font("Arial", Font.BOLD, 24));
    }

    private void addComponentsToFrame() {
        setLayout(null);

        l1.setBounds(50, 50, 800, 40);
        add(l1);

        l2.setBounds(100, 175, 300, 30);
        add(l2);

        accountNumText.setBounds(375, 175, 230, 40);
        add(accountNumText);

        l3.setBounds(100, 270, 300, 30);
        add(l3);

        amountText.setBounds(375, 270, 230, 40);
        add(amountText);

        btnexit.setBounds(300, 350, 150, 50);
        add(btnexit);

        btnTransfer.setBounds(500, 350, 150, 50);
        add(btnTransfer);
    }
    private void configureFrame() {
        setSize(700, 600);
        setLocation(250, 0);
        getContentPane().setBackground(Color.WHITE);
        setVisible(true);
    }
    private void addActionListeners() {
        // Exit button functionality
        btnexit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new TransactionsUI(accountNumber,authToken).setVisible(true);
                dispose();
            }
        });

        // Transfer button functionality
        btnTransfer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String receiverAccountNumber = accountNumText.getText();
                String amount = amountText.getText();

                // Simple validation
                if (receiverAccountNumber.isEmpty() || amount.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    double transferAmount = Double.parseDouble(amount);
                    if (transferAmount <= 0) {
                        JOptionPane.showMessageDialog(null, "Amount must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Call the API (replace with actual API call)
                    boolean success = callTransferAPI(receiverAccountNumber, transferAmount);
                    if (success) {
                        JOptionPane.showMessageDialog(null, "Transfer successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        new TransactionsUI(accountNumber,authToken).setVisible(true);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(null, "Transfer failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid amount. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
    private boolean callTransferAPI(String targetAccountNumber, double amount) {
        try {
            // URL của API
            String apiUrl = "http://localhost:8080/api/transactions/transfer";

            // Tạo HttpURLConnection
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + authToken); // Token cần được xác thực
            connection.setDoOutput(true);

            // Tạo payload JSON theo định dạng yêu cầu của API
            String jsonPayload = String.format("{\"targetAccountNumber\": \"%s\", \"amount\": %.2f}", targetAccountNumber, amount);

            // Gửi payload JSON
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Đọc phản hồi từ API
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Gọi API thành công, đọc phản hồi chi tiết nếu cần
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line.trim());
                    }
                    System.out.println("Success Response: " + response.toString());
                }
                return true;
            } else {
                // Đọc phản hồi lỗi
                try (InputStream errorStream = connection.getErrorStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream, "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line.trim());
                    }
                    System.err.println("Error Response: " + response.toString());
                }
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
