package com.pro.service.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.pro.service.entity.Users;
import com.pro.service.respository.UserRepo;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class UserService {

    @Autowired
    private JWTService jwtService;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    private UserRepo repo;


    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    
	@Autowired
    private JavaMailSender mailSender;
    
	private final Map<String, String> otpStorage = new HashMap<>();
    private final Map<String, LocalDateTime> otpExpiry = new HashMap<>();

    public UserService(JavaMailSender mailSender) {
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
            if ("berrypopindia@gmail.com".equals(email)){
            	 helper.setTo(email);
            }else {
            	return false;
            }
            helper.setSubject("Verify Your Email to Complete Registration");

            // HTML content for the email body (registration-specific)
            String emailContent = "<html><body>"
                    + "<h2>Welcome to BerryPop!</h2>"
                    + "<p>We're excited to have you onboard. To complete your registration, please verify your email address using the OTP below:</p>"
                    + "<h3 style='color: #FF6600;'>Your OTP: " + otp + "</h3>"
                    + "<p>This OTP is valid for <strong>5 minutes</strong>. Do not share it with anyone.</p>"
                    + "<p>If you did not initiate this registration, please ignore this email.</p>"
                    + "<br/>"
                    + "<p>Cheers,<br/><strong>The BerryPop Team</strong></p>"
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

    public Users register(Users user) {
        user.setPassword(encoder.encode(user.getPassword()));
        repo.save(user);
        return user;
    }

    public String verify(Users user) {
    	try {
    		 System.out.println("token1");
            Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );
            if (authentication.isAuthenticated()) {
            	 System.out.println("token");
                return jwtService.generateToken(user.getUsername());
               
            }
        } catch (Exception e) {
        	 System.out.println("token3");
            e.printStackTrace(); // Log the error for debugging
        }
        return "fail";
    }
}