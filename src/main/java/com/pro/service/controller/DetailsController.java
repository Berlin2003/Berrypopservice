package com.pro.service.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pro.service.entity.Details;
import com.pro.service.service.DetailsService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/details")
public class DetailsController {
	
	@Autowired
    private DetailsService detailsService;

	@PostMapping("/admins")
	public ResponseEntity<Map<String, Object>> createDetail(@RequestBody Details details) {
	    try {
	        Details createdDetails = detailsService.createDetails(details);

	        // Generate token or placeholder (replace with actual logic)
	        String token = "Bearer " + UUID.randomUUID().toString(); // Example token, replace with JWT or your logic

	        // Build response
	        Map<String, Object> response = new HashMap<>();
	        response.put("message", "Details created successfully");
	        response.put("id", createdDetails.getId());

	        // Set token in headers
	        HttpHeaders headers = new HttpHeaders();
	        headers.set("Authorization", token);

	        return new ResponseEntity<>(response, headers, HttpStatus.CREATED);
	    } catch (Exception e) {
	        Map<String, Object> response = new HashMap<>();
	        response.put("message", "Failed to create details: " + e.getMessage());
	        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	    }
	}
	
	


    @GetMapping("/admins")
    public ResponseEntity<List<Details>> getAllDetails() {
        List<Details> detailsList = detailsService.getAllDetails();
        return new ResponseEntity<>(detailsList, HttpStatus.OK);
    }

    @GetMapping("admins/{id}")
    public ResponseEntity<Details> getDetailsById(@PathVariable int id) {
        Optional<Details> details = detailsService.getDetailsById(id);
        return details.map(ResponseEntity::ok)
                      .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    @PutMapping("admins/{id}")
    public ResponseEntity<Details> updateDetails(@PathVariable int id, @RequestBody Details details) {
        Details updatedDetails = detailsService.updateDetails(id, details);
        return updatedDetails != null
                ? new ResponseEntity<>(updatedDetails, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("admins/{id}")
    public ResponseEntity<Void> deleteDetails(@PathVariable int id) {
        boolean isDeleted = detailsService.deleteDetails(id);
        return isDeleted
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    @PostMapping("/admins/send-otp")
    public ResponseEntity<Map<String, String>> sendOtp(@RequestParam String email) {
        Map<String, String> response = new HashMap<>();
        try {
            boolean isOtpSent = detailsService.sendOTP(email);
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


 // Verify OTP for the provided email and OTP
    @PostMapping("/admins/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        Map<String, String> response = new HashMap<>();

        if (email == null || otp == null) {
            response.put("message", "Email and OTP are required.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            boolean isVerified = detailsService.verifyOTP(email, otp);
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

    @PostMapping("/admins/change")
    public ResponseEntity<Map<String, Object>> changeStatus(
            @RequestParam int id,
            @RequestParam Details.Status status,
            @RequestParam Details.Payment payment) {

        Details updated = detailsService.changeStatusOrPayment(id, status, payment);

        Map<String, Object> response = new HashMap<>();
        if (updated != null) {
            response.put("message", "Details updated successfully.");
            response.put("data", updated);
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Details with ID " + id + " not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }



}
