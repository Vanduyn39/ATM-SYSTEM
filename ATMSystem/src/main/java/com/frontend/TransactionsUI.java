package com.frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TransactionsUI extends JFrame {

    private JLabel l1;
    private JButton btnWithdraw, btnBalance, btnTransfer, btnDeposit, btnPin, btnOut;
    private String accountNumber;
    private String authToken;

    public TransactionsUI(String accountNumber,String authToken) {
        this.accountNumber = accountNumber;
        this.authToken = authToken;
        initializeUI();
        addEventListeners();
    }

    private void initializeUI() {
        l1 = new JLabel("Please Select Your Transaction");
        l1.setForeground(Color.BLACK);
        l1.setFont(new Font("Osward", Font.BOLD, 32));
        l1.setBounds(100, 50, 700, 50);

        btnWithdraw = createButton("Cash Withdraw");
        btnBalance = createButton("View Balance");
        btnTransfer = createButton("Transfer");
        btnDeposit = createButton("Deposit");
        btnPin = createButton("Pin Change");
        btnOut = createButton("Log Out");

        setLayout(null);
        addButton(btnWithdraw, 50, 175);
        addButton(btnBalance, 50, 275);
        addButton(btnTransfer, 50, 375);
        addButton(btnDeposit, 400, 175);
        addButton(btnPin, 400, 275);
        addButton(btnOut, 400, 375);

        add(l1);

        setSize(700, 600);
        setLocation(250, 0);
        getContentPane().setBackground(Color.WHITE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    private void addButton(JButton button, int x, int y) {
        button.setBounds(x, y, 250, 50);
        add(button);
    }
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        return button;
    }
    private void addEventListeners() {
        btnWithdraw.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                new CashWithdrawUI(accountNumber,authToken).setVisible(true);
                dispose();
            }
        });

        btnBalance.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                new ViewBalanceUI(accountNumber, authToken).setVisible(true);
                dispose();
            }
        });

        btnTransfer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                new TransferUI(accountNumber,authToken).setVisible(true);  // Assuming TransferUI is implemented
                dispose();
            }
        });

        btnDeposit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                new DepositUI(accountNumber,authToken).setVisible(true);
                dispose();
            }
        });

        btnPin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                new PinChangeUI(accountNumber,authToken).setVisible(true);
                dispose();
            }
        });

        btnOut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                new LoginUI().setVisible(true);  // Log out and go back to login screen
                dispose();
            }
        });
    }

}
