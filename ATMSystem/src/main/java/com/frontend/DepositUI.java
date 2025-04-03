package com.frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DepositUI extends JFrame {
    private JLabel l1;
    private JTextField amountText;
    private JButton btnexit, btndeposit;
    private String accountNumber;
    private String authToken;

    public DepositUI(String accountNumber, String authToken) {
        this.accountNumber = accountNumber;
        this.authToken = authToken;
        setTitle("ATM - Deposit");
        initializeComponents();
        addComponentsToFrame();
        addActionListeners();
        configureFrame();
    }

    private void initializeComponents() {
        l1 = new JLabel("Enter The Amount To Deposit");
        l1.setFont(new Font("Osward", Font.BOLD, 32));

        amountText = new JTextField(15);
        amountText.setFont(new Font("Arial", Font.BOLD, 24));

        btnexit = new JButton("Exit");
        btnexit.setFont(new Font("Arial", Font.BOLD, 24));

        btndeposit = new JButton("Deposit");
        btndeposit.setFont(new Font("Arial", Font.BOLD, 24));
    }

    private void addComponentsToFrame() {
        setLayout(null);

        l1.setBounds(100, 50, 800, 40);
        add(l1);

        amountText.setBounds(150, 200, 400, 60);
        add(amountText);

        btnexit.setBounds(300, 350, 150, 50);
        add(btnexit);

        btndeposit.setBounds(500, 350, 150, 50);
        add(btndeposit);
    }

    private void addActionListeners() {
        btnexit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                navigateToTransactions();
            }
        });

        btndeposit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                processDeposit(authToken);
            }
        });
    }

    private void processDeposit(String authToken) {
        String amountStr = amountText.getText();

        if (!amountStr.isEmpty()) {
            try {
                double amount = Double.parseDouble(amountStr);

                if (amount <= 0) {
                    JOptionPane.showMessageDialog(null, "Please enter an amount greater than 0");
                    return;
                }

                // Gọi API nạp tiền
                try {
                    URL url = new URL("http://localhost:8080/api/transactions/deposit");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    // Cấu hình yêu cầu HTTP POST
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Authorization", "Bearer " + authToken); // Thêm token vào header
                    conn.setDoOutput(true);

                    // Tạo JSON body cho yêu cầu
                    String jsonBody = String.format("{\"amount\": %.2f}", amount);

                    // Log JSON body gửi đi
                    System.out.println("Sending JSON Body: " + jsonBody);
                    System.out.println("Authorization Token: " + authToken);

                    try (OutputStream os = conn.getOutputStream()) {
                        os.write(jsonBody.getBytes("UTF-8"));
                        os.flush();
                    }

                    // Kiểm tra phản hồi từ server
                    int responseCode = conn.getResponseCode();
                    System.out.println("Response Code: " + responseCode);

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Xử lý phản hồi thành công
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String output;
                        while ((output = br.readLine()) != null) {
                            response.append(output);
                        }
                        br.close();

                        System.out.println("Response: " + response.toString());
                        JOptionPane.showMessageDialog(null, "Deposit Successful! Amount: " + amount);
                        navigateToTransactions(); // Điều hướng sau khi thành công
                    } else {
                        // Log lỗi từ server
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                        StringBuilder errorResponse = new StringBuilder();
                        String output;
                        while ((output = br.readLine()) != null) {
                            errorResponse.append(output);
                        }
                        br.close();
                        System.out.println("Error Response: " + errorResponse.toString());

                        if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                            JOptionPane.showMessageDialog(null, "Invalid Request: Please check your inputs", "Error", JOptionPane.ERROR_MESSAGE);
                        } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                            JOptionPane.showMessageDialog(null, "Unauthorized: Invalid token or token expired", "Error", JOptionPane.ERROR_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to deposit: HTTP Error Code " + responseCode, "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (Exception e) {
                    // Xử lý lỗi khi gọi API
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error processing deposit: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Please enter a valid amount");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Amount field cannot be empty");
        }
    }

    private void navigateToTransactions() {
        new TransactionsUI(accountNumber,authToken).setVisible(true);  // Quay về màn hình giao dịch
        dispose();
    }

    private void configureFrame() {
        getContentPane().setBackground(Color.WHITE);
        setSize(700, 600);
        setLocation(250, 0);
        setVisible(true);
    }

}
