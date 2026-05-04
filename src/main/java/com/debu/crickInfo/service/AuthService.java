package com.debu.crickInfo.service;

import com.debu.crickInfo.dto.AdminCredentialUpdateRequest;
import com.debu.crickInfo.dto.AdminLoginRequest;
import com.debu.crickInfo.dto.ApprovalUpdateRequest;
import com.debu.crickInfo.dto.FranchiseUserSummaryResponse;
import com.debu.crickInfo.dto.LoginResponse;
import com.debu.crickInfo.dto.RegisterRequest;
import com.debu.crickInfo.dto.UserLoginRequest;
import com.debu.crickInfo.dto.UserSummaryResponse;
import com.debu.crickInfo.model.User;
import com.debu.crickInfo.repository.UserRepository;
import com.debu.crickInfo.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class AuthService {

    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_FRANCHISE = "ROLE_FRANCHISE";
    private static final String ROLE_USER = "ROLE_USER";
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_APPROVED = "APPROVED";
    private static final String STATUS_REJECTED = "REJECTED";

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse registerFranchiseUser(RegisterRequest request) {
        String normalizedEmail = normalizeEmail(request.getEmail());
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            return failure("User with this email already exists");
        }

        User user = User.builder()
                .name(request.getName().trim())
                .email(normalizedEmail)
                .username(normalizedEmail)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(ROLE_FRANCHISE)
                .franchiseName(request.getFranchiseName().trim())
                .accountStatus(STATUS_PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);
        return LoginResponse.builder()
                .success(true)
                .message("Registration submitted. Your account is pending admin approval.")
                .userId(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .username(savedUser.getUsername())
                .franchiseName(savedUser.getFranchiseName())
                .role(savedUser.getRole())
                .accountStatus(savedUser.getAccountStatus())
                .build();
    }

    public LoginResponse loginFranchiseUser(UserLoginRequest request) {
        String normalizedEmail = normalizeEmail(request.getEmail());
        Optional<User> userOptional = userRepository.findByEmailIgnoreCase(normalizedEmail);

        if (userOptional.isEmpty()) {
            return failure("Invalid email or password");
        }

        User user = userOptional.get();
        if (!ROLE_FRANCHISE.equals(user.getRole())) {
            return failure("Invalid email or password");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return failure("Invalid email or password");
        }
        if (!STATUS_APPROVED.equalsIgnoreCase(user.getAccountStatus())) {
            return failure("Your account is " + user.getAccountStatus().toLowerCase(Locale.ROOT) + ". Please contact admin.");
        }

        user.setUpdatedAt(LocalDateTime.now());
        return success(userRepository.save(user), "Login successful");
    }

    public LoginResponse loginAdmin(AdminLoginRequest request) {
        Optional<User> adminOptional = userRepository.findByUsernameIgnoreCase(request.getUsername().trim());

        if (adminOptional.isEmpty()) {
            return failure("Invalid username or password");
        }

        User admin = adminOptional.get();
        if (!ROLE_ADMIN.equals(admin.getRole())) {
            return failure("Invalid username or password");
        }
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            return failure("Invalid username or password");
        }

        admin.setUpdatedAt(LocalDateTime.now());
        return success(userRepository.save(admin), "Admin login successful");
    }

    public LoginResponse updateAdminCredentials(String currentUsername, AdminCredentialUpdateRequest request) {
        Optional<User> adminOptional = userRepository.findByUsernameIgnoreCase(currentUsername);

        if (adminOptional.isEmpty()) {
            return failure("Admin not found");
        }

        User admin = adminOptional.get();
        if (!ROLE_ADMIN.equals(admin.getRole())) {
            return failure("Only admins can update credentials");
        }
        if (!passwordEncoder.matches(request.getCurrentPassword(), admin.getPassword())) {
            return failure("Current password is incorrect");
        }

        String newUsername = request.getNewUsername().trim();
        Optional<User> existingUser = userRepository.findByUsernameIgnoreCase(newUsername);
        if (existingUser.isPresent() && !existingUser.get().getId().equals(admin.getId())) {
            return failure("Username already taken");
        }

        admin.setUsername(newUsername);
        admin.setPassword(passwordEncoder.encode(request.getNewPassword()));
        admin.setUpdatedAt(LocalDateTime.now());

        return success(userRepository.save(admin), "Admin credentials updated successfully");
    }

    public List<UserSummaryResponse> getAllFranchiseUsers() {
        return getFranchiseLikeUsers().stream()
                .map(this::toUserSummary)
                .toList();
    }

    public List<UserSummaryResponse> getPendingFranchiseUsers() {
        return getFranchiseLikeUsersByStatus(STATUS_PENDING).stream()
                .map(this::toUserSummary)
                .toList();
    }

    public List<UserSummaryResponse> getFranchiseUsersByStatus(String status) {
        String normalizedStatus = normalizeStatus(status);
        return getFranchiseLikeUsersByStatus(normalizedStatus).stream()
                .map(this::toUserSummary)
                .toList();
    }

    public UserSummaryResponse getFranchiseUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Franchise user not found"));

        if (!isFranchiseLikeUser(user)) {
            throw new IllegalArgumentException("User is not a franchise user");
        }

        return toUserSummary(user);
    }

    public FranchiseUserSummaryResponse getFranchiseUserSummary() {
        List<User> allUsers = getFranchiseLikeUsers();
        List<UserSummaryResponse> allResponses = allUsers.stream().map(this::toUserSummary).toList();
        List<UserSummaryResponse> pendingResponses = allResponses.stream()
                .filter(user -> STATUS_PENDING.equalsIgnoreCase(user.getAccountStatus()))
                .toList();

        long approvedCount = allResponses.stream().filter(user -> STATUS_APPROVED.equalsIgnoreCase(user.getAccountStatus())).count();
        long rejectedCount = allResponses.stream().filter(user -> STATUS_REJECTED.equalsIgnoreCase(user.getAccountStatus())).count();

        return FranchiseUserSummaryResponse.builder()
                .totalRequests(allResponses.size())
                .pendingRequests(pendingResponses.size())
                .approvedRequests(approvedCount)
                .rejectedRequests(rejectedCount)
                .allUsers(allResponses)
                .pendingUsers(pendingResponses)
                .build();
    }

    public UserSummaryResponse updateFranchiseApproval(String userId, String adminUsername, ApprovalUpdateRequest request) {
        String normalizedStatus = normalizeStatus(request.getStatus());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Franchise user not found"));

        if (!isFranchiseLikeUser(user)) {
            throw new IllegalArgumentException("Only franchise users can be approved or rejected");
        }

        if (ROLE_USER.equals(user.getRole())) {
            user.setRole(ROLE_FRANCHISE);
        }
        if (user.getFranchiseName() == null || user.getFranchiseName().isBlank()) {
            user.setFranchiseName("Unassigned Franchise");
        }
        user.setAccountStatus(normalizedStatus);
        user.setApprovedBy(adminUsername);
        user.setApprovedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return toUserSummary(userRepository.save(user));
    }

    public LoginResponse getCurrentUser(String username) {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return success(user, "Current user fetched successfully");
    }

    public void initializeDefaultAdmin() {
        Optional<User> existingDefaultAdmin = userRepository.findByUsernameIgnoreCase(DEFAULT_ADMIN_USERNAME);
        if (existingDefaultAdmin.isPresent()) {
            User admin = existingDefaultAdmin.get();
            boolean updated = false;

            if (!ROLE_ADMIN.equals(admin.getRole())) {
                admin.setRole(ROLE_ADMIN);
                updated = true;
            }
            if (!STATUS_APPROVED.equals(admin.getAccountStatus())) {
                admin.setAccountStatus(STATUS_APPROVED);
                updated = true;
            }
            if (updated) {
                admin.setUpdatedAt(LocalDateTime.now());
                userRepository.save(admin);
            }
            return;
        }

        User defaultAdmin = User.builder()
                .name("Administrator")
                .username(DEFAULT_ADMIN_USERNAME)
                .password(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD))
                .role(ROLE_ADMIN)
                .accountStatus(STATUS_APPROVED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(defaultAdmin);
    }

    private LoginResponse success(User user, String message) {
        return LoginResponse.builder()
                .token(jwtUtil.generateToken(user.getUsername(), user.getRole()))
                .username(user.getUsername())
                .role(user.getRole())
                .message(message)
                .success(true)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .franchiseName(user.getFranchiseName())
                .accountStatus(user.getAccountStatus())
                .build();
    }

    private LoginResponse failure(String message) {
        return LoginResponse.builder()
                .success(false)
                .message(message)
                .build();
    }

    private UserSummaryResponse toUserSummary(User user) {
        return UserSummaryResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .franchiseName(user.getFranchiseName())
                .role(user.getRole())
                .accountStatus(user.getAccountStatus())
                .approvedBy(user.getApprovedBy())
                .approvedAt(user.getApprovedAt())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private List<User> getFranchiseLikeUsers() {
        List<User> users = new ArrayList<>(userRepository.findByRoleInOrderByCreatedAtDesc(Arrays.asList(ROLE_FRANCHISE, ROLE_USER)));
        users.sort(Comparator.comparing(User::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())));
        return users;
    }

    private List<User> getFranchiseLikeUsersByStatus(String status) {
        List<User> users = new ArrayList<>(
                userRepository.findByRoleInAndAccountStatusOrderByCreatedAtDesc(Arrays.asList(ROLE_FRANCHISE, ROLE_USER), status)
        );
        users.sort(Comparator.comparing(User::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())));
        return users;
    }

    private boolean isFranchiseLikeUser(User user) {
        return ROLE_FRANCHISE.equals(user.getRole()) || ROLE_USER.equals(user.getRole());
    }

    private String normalizeStatus(String status) {
        String normalizedStatus = status.trim().toUpperCase(Locale.ROOT);
        if (!STATUS_APPROVED.equals(normalizedStatus) && !STATUS_REJECTED.equals(normalizedStatus) && !STATUS_PENDING.equals(normalizedStatus)) {
            throw new IllegalArgumentException("Status must be PENDING, APPROVED or REJECTED");
        }
        return normalizedStatus;
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
