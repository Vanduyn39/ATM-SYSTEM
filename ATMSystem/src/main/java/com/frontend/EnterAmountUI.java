package com.frontend;

import javax.swing.*;
import java.awt.*;

public class EnterAmountUI extends JFrame {
    private JLabel l1, l2;
    private JTextField amountText;
    private JButton btnexit, btnWithdraw;
    private String accountNumber;
    private String authToken;

    public EnterAmountUI(String accountNumber, String authToken) {
        this.accountNumber = accountNumber;
        this.authToken = authToken;
        setTitle("ATM - Enter Amount");
        initializeComponents();
        addComponentsToFrame();
        addActionListeners();
        configureFrame();
    }

    private void initializeComponents() {
        l1 = new JLabel("Enter The Amount To Withdraw");
        l1.setFont(new Font("Osward", Font.BOLD, 32));

        amountText = new JTextField(15);
        amountText.setFont(new Font("Arial", Font.BOLD, 24));

        l2 = new JLabel("Enter multiples of 50.000");
        l2.setFont(new Font("Arial", Font.ITALIC, 18));

        btnexit = new JButton("Exit");
        btnexit.setFont(new Font("Arial", Font.BOLD, 24));

        btnWithdraw = new JButton("Withdraw");
        btnWithdraw.setFont(new Font("Arial", Font.BOLD, 24));
    }

    private void addComponentsToFrame() {
        setLayout(null);

        l1.setBounds(100, 50, 800, 40);
        add(l1);

        amountText.setBounds(150, 200, 400, 60);
        add(amountText);

        btnexit.setBounds(300, 350, 150, 50);
        add(btnexit);

        btnWithdraw.setBounds(500, 350, 150, 50);
        add(btnWithdraw);
    }

    private void addActionListeners() {
        btnexit.addActionListener(ae -> {
            new CashWithdrawUI(accountNumber, authToken).setVisible(true);
            dispose();
        });

        btnWithdraw.addActionListener(ae -> {
            String inputAmount = amountText.getText().trim();
            try {
                double amount = Double.parseDouble(inputAmount);
                if (amount > 0 && amount % 50000 == 0) {
                    new TransactionConfirmationUI(accountNumber, amount, authToken).setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Amount must be a positive number and a multiple of 50.000!");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input! Please enter a valid number.");
            }
        });
    }

    private void configureFrame() {
        getContentPane().setBackground(Color.WHITE);
        setSize(700, 600);
        setLocation(250, 0);
        setVisible(true);
    }

}