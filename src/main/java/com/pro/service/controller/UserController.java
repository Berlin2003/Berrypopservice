package com.pro.service.controller;




import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pro.service.entity.Users;
import com.pro.service.service.UserService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api") // Or a more appropriate path
public class UserController {

	@Autowired
    private UserService service;
	


    @PostMapping("/register")
    public Users register(@RequestBody Users user) {
        return service.register(user);

    }

    @PostMapping("/login")
    public String login(@RequestBody Users user) {

        return service.verify(user);
    }
    @PostMapping("/regotp")
    public ResponseEntity<Map<String, String>> sendOtp(@RequestParam String email) {
        Map<String, String> response = new HashMap<>();
        try {
            boolean isOtpSent = service.sendOTP(email);
            if (isOtpSent) {
                response.put("message", "OTP sent to " + email);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("message", "Failed to send OTP");
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            response.put("message", "Error occurred while sending OTP: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/verfiyotp")
    public ResponseEntity<Map<String, String>> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        Map<String, String> response = new HashMap<>();

        if (email == null || otp == null) {
            response.put("message", "Email and OTP are required.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            boolean isVerified = service.verifyOTP(email, otp);
            if (isVerified) {
                response.put("message", "OTP verified successfully!");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("message", "Invalid or expired OTP");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            response.put("message", "Error occurred during OTP verification: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
   
    
}
