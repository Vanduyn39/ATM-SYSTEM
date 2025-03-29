package com.atm.service;

import com.atm.dto.AccountDTO;
import com.atm.model.Account;
import com.atm.model.Credential;
import com.atm.model.User;
import com.atm.repository.AccountRepository;
import com.atm.repository.BalanceRepository;
import com.atm.repository.CredentialRepository;
import com.atm.repository.UserRepository;
import com.atm.model.Balance;
import com.atm.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;
    private final CredentialRepository credentialRepository;
    private final BalanceRepository balanceRepository;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AccountService(AccountRepository accountRepository,
                          CredentialRepository credentialRepository,
                          BalanceRepository balanceRepository,
                          UserRepository userRepository,
                          JdbcTemplate jdbcTemplate,
                          JwtUtil jwtUtil,
                          PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.credentialRepository = credentialRepository;
        this.balanceRepository = balanceRepository;
        this.userRepository = userRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public Account getAccount(String accountNumber) {
        Optional<Account> accountOpt = accountRepository.findByAccountNumber(accountNumber);

        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            logger.info("🔍 Tài khoản tìm thấy: {}, Role: {}", account.getAccountNumber(), account.getRole());
            return account;
        }

        logger.warn("⚠ Không tìm thấy tài khoản: {}", accountNumber);
        return null;
    }

    // Đăng ký tài khoản mới
    @Transactional
    public Account register(Account account) {
        logger.info("🔍 Đang vào phương thức register...");
        logger.info("Received request to register account: {}", account.getAccountNumber());

        // Kiểm tra xem tài khoản đã tồn tại hay chưa
        if (accountRepository.existsById(account.getAccountNumber())) {
            logger.error("Account already exists: {}", account.getAccountNumber());
            throw new IllegalArgumentException("Tài khoản đã tồn tại!");
        }

        // Kiểm tra User của tài khoản
        User user = account.getUser();
        if (user == null) {
            // Nếu không có thông tin người dùng trong account, lấy thông tin người dùng từ DB
            logger.info("User của tài khoản là null, kiểm tra lại từ DB...");
            user = userRepository.findByUserId(account.getUser().getUserId()).orElse(null);

            if (user != null) {
                // Kiểm tra ràng buộc 1 userId chỉ có 1 name
                if (!user.getName().equals(account.getFullName())) {
                    logger.error("User with ID {} already exists with a different name: {}", account.getUser().getUserId(), user.getName());
                    throw new IllegalArgumentException("Tên người dùng không khớp với userId!");
                }
            }
        }

        if (user == null) {
            // Nếu không tìm thấy User trong DB, tạo User mới và gán cho tài khoản
            logger.info("Không tìm thấy User, tạo User mới...");

            // Lấy full name từ tài khoản
            String fullName = account.getFullName();  // Tên người dùng từ Account

            // Lấy userId người dùng nhập vào (nếu có)
            String userId = account.getUser().getUserId();  // Giả sử người dùng đã nhập userId khi tạo account

            // Kiểm tra tính hợp lệ của userId (CCCD phải là 12 số)
            if (userId == null || !userId.matches("\\d{12}")) {
                logger.error("Invalid userId: {}", userId);
                throw new IllegalArgumentException("userId phải là 12 số (CCCD)");
            }

            // Tạo user mới từ userId và tên người dùng
            user = new User();
            user.setUserId(userId);  // Lưu userId người dùng nhập vào
            user.setName(fullName);  // Lưu tên người dùng nhập vào (tương ứng với full_name trong Account)

            // Lưu User mới vào DB
            userRepository.save(user);
            logger.info("User mới được tạo với ID: {}", user.getUserId());

            // Gán User cho tài khoản
            account.setUser(user);
        } else {
            logger.info("User đã tồn tại: {}", user.getUserId());
        }

        // Lưu tài khoản vào bảng Account
        Account savedAccount = accountRepository.save(account);
        accountRepository.flush(); // Đảm bảo tài khoản được commit vào DB

        // Tạo và lưu thông tin Balance
        Balance balance = new Balance();
        balance.setAccount(savedAccount); // Liên kết Balance với tài khoản
        balance.setBalance(0.0); // Số dư mặc định
        balance.setLastUpdated(LocalDateTime.now());
        balanceRepository.save(balance);
        logger.info("Balance record created for account: {}", savedAccount.getAccountNumber());

        // Tạo thông tin Credential với PIN mặc định
        Credential credential = new Credential();
        credential.setAccount(savedAccount);
        credential.setPin(passwordEncoder.encode("000000")); // Mã hóa PIN mặc định
        credential.setFailedAttempts(0);
        credential.setLockTime(null);
        credential.setUpdateAt(LocalDateTime.now());
        credentialRepository.save(credential);

        logger.info("Successfully registered account: {}", savedAccount.getAccountNumber());
        return savedAccount;
    }

    // Kiểm tra user có tồn tại không
    public boolean isUserExists(String userId) {
        String sql = "SELECT COUNT(*) FROM user WHERE user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null && count > 0;
    }

    // Tạo mới User nếu chưa tồn tại
    // Trong AccountService
    public void createUser(User user) {
        logger.info("Creating user with id: {}", user.getUserId());

        String sqlCheck = "SELECT user_id FROM `User` WHERE user_id = ?";
        String existingUserId = null;

        try {
            existingUserId = jdbcTemplate.queryForObject(sqlCheck, String.class, user.getUserId());
        } catch (EmptyResultDataAccessException e) {
            // Nếu không tìm thấy, tiếp tục tạo mới
        }

        if (existingUserId != null) {
            logger.info("User already exists with ID: {}", existingUserId);
            return; // Hoặc bạn có thể ném ra ngoại lệ nếu cần
        } else {
            logger.info("User does not exist, creating user with id: {}", user.getUserId());

            // Chèn user mới vào cơ sở dữ liệu
            String sqlInsert = "INSERT INTO `User` (user_id, name) VALUES (?, ?)";
            int rows = jdbcTemplate.update(sqlInsert, user.getUserId(), user.getName());

            if (rows > 0) {
                logger.info("User created with ID: {}", user.getUserId());
            } else {
                logger.error("Failed to create user with id: {}", user.getUserId());
                throw new RuntimeException("Failed to create user");
            }
        }
    }

    @Transactional
    public void updateAccount(AccountDTO accountDTO, String accountNumber) {
        Optional<Account> optionalAccount = accountRepository.findById(accountDTO.getAccountNumber());

        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();

            // Kiểm tra quyền cập nhật
            String userRole = getUserRole(accountNumber);
            if (!"ADMIN".equals(userRole) && !accountNumber.equals(accountDTO.getAccountNumber())) {
                throw new RuntimeException("Bạn không có quyền cập nhật tài khoản này!");
            }

            // Cập nhật thông tin Account nếu có thay đổi
            if (accountDTO.getFullName() != null) {
                account.setFullName(accountDTO.getFullName());
            }

            // Cập nhật Balance nếu tồn tại, hoặc tạo mới
            if (accountDTO.getBalance() != null) {
                if (account.getBalanceEntity() == null) {
                    // Tạo mới Balance nếu chưa có
                    Balance newBalance = new Balance();
                    newBalance.setBalance(accountDTO.getBalance());  // Sử dụng balance thay vì available_balance
                    newBalance.setAccount(account);  // Liên kết Balance với Account
                    account.setBalanceEntity(newBalance);
                    balanceRepository.save(newBalance);  // Lưu Balance mới
                } else {
                    // Cập nhật Balance nếu đã tồn tại
                    account.getBalanceEntity().setBalance(accountDTO.getBalance());
                    balanceRepository.save(account.getBalanceEntity());  // Lưu Balance đã cập nhật
                }
            }

            // Cập nhật Pin nếu có thay đổi
            if (accountDTO.getPin() != null) {
                Optional<Credential> optionalCredential = credentialRepository.findById(accountDTO.getAccountNumber());
                if (optionalCredential.isPresent()) {
                    Credential credential = optionalCredential.get();
                    credential.setPin(passwordEncoder.encode(accountDTO.getPin())); // Mã hóa pin mới
                    credential.setUpdateAt(LocalDateTime.now());
                    credentialRepository.save(credential);  // Lưu Credential đã cập nhật
                } else {
                    throw new RuntimeException("Không tìm thấy thông tin Credential cho tài khoản này.");
                }
            }

            // Cập nhật Role nếu có thay đổi
            if (accountDTO.getRole() != null && !accountDTO.getRole().isEmpty()) {
                account.setRole(accountDTO.getRole());
            }

            // Cập nhật User nếu có thay đổi
            if (account.getUser() != null) {
                User user = account.getUser();

                // Cập nhật số điện thoại trong User nếu có thay đổi
                if (accountDTO.getPhoneNumber() != null && !accountDTO.getPhoneNumber().equals(user.getPhone())) {
                    user.setPhone(accountDTO.getPhoneNumber());
                }

                // Cập nhật tên đầy đủ trong User nếu có thay đổi
                if (accountDTO.getFullName() != null && !accountDTO.getFullName().equals(user.getName())) {
                    user.setName(accountDTO.getFullName());
                }

                // Lưu thông tin User đã cập nhật
                try {
                    userRepository.save(user);  // Lưu thông tin User đã cập nhật
                } catch (Exception e) {
                    throw new RuntimeException("Có lỗi khi lưu thông tin người dùng: " + e.getMessage());
                }
            } else {
                throw new RuntimeException("Không tìm thấy người dùng liên kết với tài khoản này.");
            }

            // Lưu Account (Hibernate sẽ tự động lưu Balance khi Account được lưu)
            accountRepository.save(account);

        } else {
            throw new RuntimeException("Tài khoản không tồn tại.");
        }
    }

    public Double getBalance(String accountNumber) {
        // Lấy tài khoản đang đăng nhập
        String loggedInAccountNumber = getLoggedInAccountNumber();

        // Kiểm tra xem tài khoản yêu cầu có phải của người dùng đang đăng nhập hay không
        if (!accountNumber.equals(loggedInAccountNumber)) {
            throw new SecurityException("Bạn không có quyền truy cập số dư của tài khoản này.");
        }

        return accountRepository.findByAccountNumber(accountNumber)
                .map(Account::getBalance)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại."));
    }

    // Hàm lấy số tài khoản của người dùng hiện tại
    public String getLoggedInAccountNumber() {
        System.out.println("🔍 Kiểm tra SecurityContextHolder: " + SecurityContextHolder.getContext().getAuthentication());

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("❌ SecurityContextHolder is NULL!");
            return null;
        }

        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    // Lấy tất cả khách hàng (dành cho nhân viên ngân hàng)
    public List<Account> getAllCustomers() {
        return accountRepository.findAll();
    }
    public String getUserRole(String accountNumber) {
        return accountRepository.findRoleByAccountNumber(accountNumber);
    }
}