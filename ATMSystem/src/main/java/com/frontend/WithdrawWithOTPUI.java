package com.frontend;

import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class WithdrawWithOTPUI extends JFrame {
    private JLabel l1, l2, l3;
    private JTextField tf1;
    private JPasswordField pf2;
    private JButton b1, b2, b3;

    public WithdrawWithOTPUI() {
        setTitle("ATM - Withdraw With OTP");
        initializeComponents();
        addComponentsToFrame();
        addActionListeners();
        configureFrame();
    }

    private void initializeComponents() {
        l1 = new JLabel("Withdraw With OTP");
        l1.setFont(new Font("Osward", Font.BOLD, 32));

        l2 = new JLabel("Amount:");
        l2.setFont(new Font("Raleway", Font.BOLD, 22));

        tf1 = new JTextField(15);
        tf1.setFont(new Font("Arial", Font.BOLD, 14));

        l3 = new JLabel("OTP:");
        l3.setFont(new Font("Raleway", Font.BOLD, 22));

        pf2 = new JPasswordField(15);
        pf2.setFont(new Font("Arial", Font.BOLD, 14));

        b1 = new JButton("Exit");
        b1.setFont(new Font("Arial", Font.BOLD, 20));

        b2 = new JButton("Confirm");
        b2.setFont(new Font("Arial", Font.BOLD, 20));

    }

    private void addComponentsToFrame() {
        setLayout(null);

        l1.setBounds(200, 50, 450, 40);
        add(l1);

        l2.setBounds(100, 175, 300, 30);
        add(l2);

        tf1.setBounds(375, 175, 230, 40);
        add(tf1);

        l3.setBounds(100, 270, 300, 30);
        add(l3);

        pf2.setBounds(375, 270, 230, 40);
        add(pf2);

        b1.setBounds(175, 350, 150, 50);
        add(b1);

        b2.setBounds(375, 350, 150, 50);
        add(b2);

    }

    private void addActionListeners() {
        b1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                new LoginUI().setVisible(true);
                dispose();
            }
        });

        b2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String amount = tf1.getText(); // Get the entered amount
                String otp = new String(pf2.getPassword()); // Get the entered OTP

                try {
                    // Create URL connection
                    URL url = new URL("http://localhost:8080/api/transactions/process-with-otp");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    // Create JSON object with amount and OTP
                    JSONObject json = new JSONObject();
                    json.put("amount", amount);
                    json.put("otp", otp);

                    // Send JSON payload to the server
                    OutputStream os = conn.getOutputStream();
                    os.write(json.toString().getBytes());
                    os.flush();
                    os.close();

                    // Read the server response
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) { // Success
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        // Show success message
                        JOptionPane.showMessageDialog(null, "Transaction Successful: " + response.toString());
                        new LoginUI().setVisible(true);
                    } else {
                        // Show error message
                        JOptionPane.showMessageDialog(null, "Transaction Failed: " + conn.getResponseMessage());
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                }
            }
        });
    }


    private void configureFrame() {
        getContentPane().setBackground(Color.WHITE);
        setSize(700, 600);
        setLocation(250, 0);
        setVisible(true);
    }

    public static void main(String[] args) {
        new WithdrawWithOTPUI();
    }
}
