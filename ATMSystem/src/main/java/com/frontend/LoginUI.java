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

public class LoginUI extends JFrame {
    private JLabel l1, l2, l3;
    private JTextField accountNum;
    private JPasswordField passwordText;
    private JButton btnSignIn, btnClear, btnOTP;

    public LoginUI() {
        setTitle("LOGIN ACCOUNT");
        initializeComponents();
        addComponentsToFrame();
        addActionListeners();
        configureFrame();
    }

    private void initializeComponents() {
        l1 = new JLabel("WELCOME TO ATM");
        l1.setFont(new Font("Osward", Font.BOLD, 32));

        l2 = new JLabel("Account Number:");
        l2.setFont(new Font("Raleway", Font.BOLD, 22));

        accountNum = new JTextField(15);
        accountNum.setFont(new Font("Arial", Font.BOLD, 14));

        l3 = new JLabel("PIN:");
        l3.setFont(new Font("Raleway", Font.BOLD, 22));

        passwordText = new JPasswordField(15);
        passwordText.setFont(new Font("Arial", Font.BOLD, 14));

        btnSignIn = new JButton("Sign In");
        btnSignIn.setFont(new Font("Arial", Font.BOLD, 20));

        btnClear = new JButton("Clear");
        btnClear.setFont(new Font("Arial", Font.BOLD, 20));

        btnOTP = new JButton("Withdraw by OTP");
        btnOTP.setFont(new Font("Arial", Font.BOLD, 20));
    }

    private void addComponentsToFrame() {
        setLayout(null);

        l1.setBounds(200, 50, 450, 40);
        add(l1);

        l2.setBounds(100, 175, 300, 30);
        add(l2);

        accountNum.setBounds(375, 175, 230, 40);
        add(accountNum);

        l3.setBounds(100, 270, 300, 30);
        add(l3);

        passwordText.setBounds(375, 270, 230, 40);
        add(passwordText);

        btnSignIn.setBounds(175, 350, 150, 50);
        add(btnSignIn);

        btnClear.setBounds(375, 350, 150, 50);
        add(btnClear);

        btnOTP.setBounds(200, 420, 300, 50);
        add(btnOTP);
    }

    private void addActionListeners() {
        btnSignIn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                handleLogin();
            }
        });

        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                clearFields();
            }
        });
        btnOTP.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {

                new SendOTPUI().setVisible(true);
                dispose();

            }
        });
    }

    private void handleLogin() {
        String accountNumber = accountNum.getText();
        String pin = new String(passwordText.getPassword());

        try {
            URL url = new URL("http://localhost:8080/api/transactions/login");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String jsonBody = String.format("{\"accountNumber\":\"%s\", \"pin\":\"%s\"}", accountNumber, pin);
            try (OutputStream os = connection.getOutputStream()) {
                os.write(jsonBody.getBytes("UTF-8"));
                os.flush();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String output;
                while ((output = br.readLine()) != null) {
                    response.append(output);
                }
                br.close();
                connection.disconnect();

                JSONObject jsonResponse = new JSONObject(response.toString());
                String authToken = jsonResponse.getString("token");

                JOptionPane.showMessageDialog(null, "Login Successful!");

                new TransactionsUI(accountNumber, authToken).setVisible(true); // Truyền token
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Invalid Account Number or PIN", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void clearFields() {
        accountNum.setText("");
        passwordText.setText("");
    }

    private void configureFrame() {
        getContentPane().setBackground(Color.WHITE);
        setSize(700, 600);
        setLocation(250, 0);
        setVisible(true);
    }

    public static void main(String[] args) {
        new LoginUI();
    }
}
