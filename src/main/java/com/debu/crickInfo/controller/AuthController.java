package com.debu.crickInfo.controller;

import com.debu.crickInfo.dto.AdminLoginRequest;
import com.debu.crickInfo.dto.ErrorResponse;
import com.debu.crickInfo.dto.LoginResponse;
import com.debu.crickInfo.dto.RegisterRequest;
import com.debu.crickInfo.dto.UserLoginRequest;
import com.debu.crickInfo.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/franchise/register")
    public ResponseEntity<?> registerFranchiseUser(@Valid @RequestBody RegisterRequest request, HttpServletRequest httpRequest) {
        return buildAuthResponse(
                authService.registerFranchiseUser(request),
                HttpStatus.CREATED,
                httpRequest,
                "Registration failed"
        );
    }

    @PostMapping("/users/register")
    public ResponseEntity<?> registerUserAlias(@Valid @RequestBody RegisterRequest request, HttpServletRequest httpRequest) {
        return registerFranchiseUser(request, httpRequest);
    }

    @PostMapping("/franchise/login")
    public ResponseEntity<?> loginFranchiseUser(@Valid @RequestBody UserLoginRequest request, HttpServletRequest httpRequest) {
        return buildAuthResponse(
                authService.loginFranchiseUser(request),
                HttpStatus.OK,
                httpRequest,
                "Franchise login failed"
        );
    }

    @PostMapping("/users/login")
    public ResponseEntity<?> loginUserAlias(@Valid @RequestBody UserLoginRequest request, HttpServletRequest httpRequest) {
        return loginFranchiseUser(request, httpRequest);
    }

    @PostMapping("/admin/login")
    public ResponseEntity<?> loginAdmin(@Valid @RequestBody AdminLoginRequest request, HttpServletRequest httpRequest) {
        return buildAuthResponse(authService.loginAdmin(request), HttpStatus.OK, httpRequest, "Admin login failed");
    }

    @GetMapping("/me")
    public ResponseEntity<LoginResponse> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(authService.getCurrentUser(authentication.getName()));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth service is running");
    }

    private ResponseEntity<?> buildAuthResponse(
            LoginResponse response,
            HttpStatus successStatus,
            HttpServletRequest request,
            String errorMessage
    ) {
        if (response.isSuccess()) {
            return ResponseEntity.status(successStatus).body(response);
        }

        HttpStatus failureStatus = successStatus == HttpStatus.CREATED ? HttpStatus.BAD_REQUEST : HttpStatus.UNAUTHORIZED;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(failureStatus.value())
                .message(errorMessage)
                .error(response.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(failureStatus).body(errorResponse);
    }
}
