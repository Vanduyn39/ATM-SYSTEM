package com.atm.controller;

import com.atm.model.ATMStatus;
import com.atm.model.Account;
import com.atm.model.User;
import com.atm.service.ATMService;
import com.atm.service.AccountService;
import com.atm.service.TransactionService;
import com.atm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class BackOfficeController {

    @Autowired
    private UserService userService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private AccountService accountService;

    @Autowired
    private ATMService atmService;

    @GetMapping("/admin")
    public String viewAdminPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        model.addAttribute("username", username);
        model.addAttribute("content", "fragments/main");
        return "index";
    }


    @GetMapping("/login")
    public String viewLoginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {

        if (error != null) {
            model.addAttribute("loginError", "Sai tên đăng nhập hoặc mật khẩu!");
        }
        if (logout != null) {
            model.addAttribute("loginError", "Đã đăng xuất thành công!");
        }
        return "login";
    }

    @GetMapping("/admin/customers")
    public String viewCustomerPage(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        model.addAttribute("username", username);

        List<User> userList = userService.getAllCustomers();
        model.addAttribute("customers", userList);
        model.addAttribute("content", "fragments/customer");
        return "index";
    }

    @GetMapping("/admin/accounts")
    public String viewAccountPage(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        model.addAttribute("username", username);


        List<Account> accounts = accountService.getAllCustomers();
        model.addAttribute("accounts", accounts);
        model.addAttribute("content", "fragments/account");
        return "index";
    }

    @GetMapping("/admin/atm")
    public ResponseEntity<Map<Integer,Integer>> getATMCash() {
        Map<Integer,Integer> map = new HashMap<>();
        map = atmService.getCashInATM();
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PostMapping("/admin/atm")
    public ResponseEntity<HttpStatus> updateATMCash(@RequestBody Map<Integer,Integer> map) {
        atmService.updateATMCash(map);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/admin/atmstatus")
    public ResponseEntity<HttpStatus> updateATMStatus(@RequestBody Map<String,String> map) {
        ATMStatus atmStatus = ATMStatus.valueOf(map.get("status"));
        atmService.updateATMStatus(atmStatus);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/admin/atmstatus")
    public ResponseEntity<ATMStatus> getATMStatus() {

        ATMStatus status = atmService.getATMStatus();

        if (status != null) {
            return new ResponseEntity<>(status, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/atmstatus")
    public ResponseEntity<ATMStatus> getPublicATMStatus() {

        ATMStatus status = atmService.getATMStatus();

        if (status != null) {
            return new ResponseEntity<>(status, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
