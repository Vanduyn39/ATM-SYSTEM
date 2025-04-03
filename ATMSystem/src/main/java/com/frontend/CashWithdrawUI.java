package com.frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class
CashWithdrawUI extends JFrame {

    private JLabel l1;
    private JButton btn500, btn1000, btn2000, btn3000, btn5000, btnenterAmount, btnexit;
    private String accountNumber;
    private String authToken;

    public CashWithdrawUI(String accountNumber, String authToken) {
        this.accountNumber = accountNumber;
        this.authToken = authToken;
        setTitle("ATM - Cash Withdrawal");
        initializeComponents();
        addComponentsToFrame();
        addActionListeners();
        configureFrame();
    }

    private void initializeComponents() {
        l1 = new JLabel("Please Select Withdrawal Amount");
        l1.setForeground(Color.BLACK);
        l1.setFont(new Font("Osward", Font.BOLD, 32));

        btn500 = createButton("500.000");
        btn1000 = createButton("1.000.000");
        btn2000 = createButton("2.000.000");
        btn3000 = createButton("3.000.000");
        btn5000 = createButton("5.000.000");
        btnenterAmount = createButton("Enter Amount");
        btnexit = createButton("Exit");
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        return button;
    }

    private void addComponentsToFrame() {
        setLayout(null);

        l1.setBounds(75, 50, 700, 50);
        add(l1);

        addButton(btn500, 50, 175);
        addButton(btn1000, 50, 275);
        addButton(btn2000, 50, 375);
        addButton(btn3000, 400, 175);
        addButton(btn5000, 400, 275);
        addButton(btnenterAmount, 400, 375);
        addButton(btnexit, 400, 475);
    }

    private void addButton(JButton button, int x, int y) {
        button.setBounds(x, y, 250, 50);
        add(button);
    }

    private void addActionListeners() {
        btn500.addActionListener(createWithdrawListener(500000));
        btn1000.addActionListener(createWithdrawListener(1000000));
        btn2000.addActionListener(createWithdrawListener(2000000));
        btn3000.addActionListener(createWithdrawListener(3000000));
        btn5000.addActionListener(createWithdrawListener(5000000));

        btnenterAmount.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                new EnterAmountUI(accountNumber,authToken).setVisible(true);  // Pass account number to EnterAmountUI
                dispose();
            }
        });

        btnexit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                new TransactionsUI(accountNumber,authToken).setVisible(true);  // Return to main transaction screen
                dispose();
            }
        });
    }

    private ActionListener createWithdrawListener(double amount) {
        return new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                new TransactionConfirmationUI(accountNumber, amount,authToken).setVisible(true);
                dispose();
            }
        };
    }

    private void configureFrame() {
        setSize(700, 600);
        setLocation(250, 0);
        getContentPane().setBackground(Color.WHITE);
        setVisible(true);
    }


}
