package com.pro.service.service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.pro.service.entity.Details;
import com.pro.service.respository.DetailsRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;


@Service
public class DetailsService {
	
	@Autowired
	private DetailsRepository detailsRepository;
	
	@Autowired
    private JavaMailSender mailSender;
    
	private final Map<String, String> otpStorage = new HashMap<>();
    private final Map<String, LocalDateTime> otpExpiry = new HashMap<>();

    public DetailsService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Generate a random OTP
    public String generateOTP() {
        int otp = (int) (Math.random() * 1000000);
        return String.format("%06d", otp); // Generate a 6-digit OTP
    }


    // Send OTP to user's email
    public boolean sendOTP(String email) {
        String otp = generateOTP();
        otpStorage.put(email, otp);
        otpExpiry.put(email, LocalDateTime.now().plusMinutes(5)); // OTP valid for 5 minutes

        try {
            // Create a MimeMessage
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("berrypopindia@gmail.com");
            helper.setTo(email);
            helper.setSubject("Your OTP for Email Verification");

            // HTML content for the email body
            String emailContent = "<html><body>"
                    + "<h2>Hello,</h2>"
                    + "<p>We received a request to verify your email address. Please use the following One-Time Password (OTP) to complete your verification process:</p>"
                    + "<h3 style='color: #4CAF50;'>OTP: " + otp + "</h3>"
                    + "<p>This OTP is valid for 5 minutes. If you did not request this, please ignore this email.</p>"
                    + "<p>Thank you for using our service.</p>"
                    + "<p><strong>Best regards,</strong><br/>The BerryPop Team</p>"
                    + "</body></html>";

            helper.setText(emailContent, true);
            mailSender.send(message);

            return true; // OTP sent successfully
        } catch (MessagingException e) {
            e.printStackTrace();
            return false; // Failed to send OTP
        }
    }

    // Verify OTP entered by user
    public boolean verifyOTP(String email, String otp) {
        String storedOtp = otpStorage.get(email);
        LocalDateTime expiryTime = otpExpiry.get(email);

        if (storedOtp != null && storedOtp.equals(otp) && LocalDateTime.now().isBefore(expiryTime)) {
            otpStorage.remove(email); // OTP is verified, remove it
            otpExpiry.remove(email);
            return true; // OTP is valid and within expiration time
        }
        return false; // Invalid OTP or expired
    }
	 // Create
    public Details createDetails(Details details) {
        return detailsRepository.save(details);
    }
    
 // Read
    public List<Details> getAllDetails() {
        return detailsRepository.findAll();
    }
    
 // Read by ID
    public Optional<Details> getDetailsById(int id) {
        return detailsRepository.findById(id);
    }
    
 // Update
    public Details updateDetails(int id, Details details) {
        if (detailsRepository.existsById(id)) {
            details.setId(id);  // Ensure the id is set for update
            return detailsRepository.save(details);
        } else {
            return null;  // Return null if the entity doesn't exist
        }
    }
    
 // Delete
    public boolean deleteDetails(int id) {
        if (detailsRepository.existsById(id)) {
            detailsRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
    
    public Details changeStatusOrPayment(int id, Details.Status status, Details.Payment payment) {
        Optional<Details> optionalDetails = detailsRepository.findById(id);

        if (optionalDetails.isPresent()) {
            Details details = optionalDetails.get();

            if (status != null) {
                details.setStatus(status);
            }

            if (payment != null) {
                details.setPayment(payment);
            }

            return detailsRepository.save(details);
        }

        return null;
    }

    

}
