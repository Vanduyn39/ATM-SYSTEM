package com.frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendOTPUI extends JFrame {
    private JLabel l1, l2, l3;
    private JTextField accountNumText;
    private JButton btnexit, btnsendOTP;

    public SendOTPUI() {
        initializeComponents();
        addComponentsToFrame();
        addActionListeners();
        configureFrame();
    }

    private void initializeComponents() {
        l1 = new JLabel("Send OTP for Transaction");
        l1.setFont(new Font("Osward", Font.BOLD, 32));

        l2 = new JLabel("Account Number:");
        l2.setFont(new Font("Raleway", Font.BOLD, 22));

        accountNumText = new JTextField(15);
        accountNumText.setFont(new Font("Arial", Font.BOLD, 24));

        btnexit = new JButton("Exit");
        btnexit.setFont(new Font("Arial", Font.BOLD, 24));

        btnsendOTP = new JButton("Send OTP");
        btnsendOTP.setFont(new Font("Arial", Font.BOLD, 24));
    }

    private void addComponentsToFrame() {
        setLayout(null);

        l1.setBounds(125, 50, 600, 40);
        add(l1);

        l2.setBounds(100, 200, 300, 30);
        add(l2);

        accountNumText.setBounds(300, 200, 230, 40);
        add(accountNumText);

        btnexit.setBounds(175, 350, 150, 50);
        add(btnexit);

        btnsendOTP.setBounds(375, 350, 150, 50);
        add(btnsendOTP);
    }

    private void addActionListeners() {
        btnexit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                new LoginUI().setVisible(true);
                dispose();
            }
        });
        // Send OTP button logic
        btnsendOTP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String accountNumber = accountNumText.getText();

                if (accountNumber.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter your account number.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Call the send OTP API
                boolean otpSent = sendOTP(accountNumber);
                if (otpSent) {
                    JOptionPane.showMessageDialog(null, "OTP has been sent to your registered phone number.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    new OTPUI().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to send OTP. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private boolean sendOTP(String accountNumber) {
        try {
            // URL of the send OTP API
            String apiUrl = "http://localhost:8080/api/transactions/send-otp";

            // Create HttpURLConnection
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Prepare JSON payload
            String jsonPayload = String.format("{\"accountNumber\": \"%s\"}", accountNumber);

            // Send request
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Check response code
            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void configureFrame() {
        setSize(700, 600);
        setLocation(250, 0);
        getContentPane().setBackground(Color.WHITE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        new SendOTPUI();
    }
}
