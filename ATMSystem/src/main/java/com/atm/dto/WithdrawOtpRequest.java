package com.atm.dto;

public class WithdrawOtpRequest {
    private String accountNumber;
//    private String phoneNumber;
    private String otp;
    private Long amount;

    // Constructor mặc định hoặc có tham số
    public WithdrawOtpRequest() {}

    public WithdrawOtpRequest(String accountNumber, String phoneNumber, String otp, Long amount) {
        this.accountNumber = accountNumber;
//        this.phoneNumber = phoneNumber;
        this.otp = otp;
        this.amount = amount;
    }

    // Getter và Setter cho các thuộc tính
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

//    public String getPhoneNumber() {
//        return phoneNumber;
//    }
//
//    public void setPhoneNumber(String phoneNumber) {
//        this.phoneNumber = phoneNumber;
//    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}