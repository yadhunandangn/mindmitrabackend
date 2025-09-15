package com.MindMitra.Rolebased.Service;

import com.MindMitra.Rolebased.Config.JwtUtil;
import com.MindMitra.Rolebased.DTO.Request.LoginReq;
import com.MindMitra.Rolebased.DTO.Request.docProfileReq;
import com.MindMitra.Rolebased.DTO.Response.LoginRes;
import com.MindMitra.Rolebased.Entity.Doctor;
import com.MindMitra.Rolebased.Entity.OTP;
import com.MindMitra.Rolebased.Entity.Role;
import com.MindMitra.Rolebased.Entity.User;
import com.MindMitra.Rolebased.Exception.AccountExistsException;
import com.MindMitra.Rolebased.Exception.OtpExpiredException;
import com.MindMitra.Rolebased.Exception.OtpMismatchException;
import com.MindMitra.Rolebased.Repository.OTP_Repo;
import com.MindMitra.Rolebased.Repository.UsersRepo;
import com.MindMitra.Rolebased.Repository.docRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.print.Doc;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class profileService {

    private final JwtUtil jwtUtil;
    private final UsersRepo userRepo;
    private final OTP_Repo otpRepo;
    private final JavaMailSender mailSender;
    private final docRepo DocRepo;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public String sendOTP(String email) {
        if (userRepo.existsByEmail(email)) {
            throw new AccountExistsException("Email already registered: " + email);
        }

        OTP otpEntity = otpRepo.findByEmail(email).orElse(new OTP());
        String generatedOtp = generateOTP();

        otpEntity.setEmail(email);
        otpEntity.setOtp(generatedOtp);
        otpEntity.setExpiry(LocalDateTime.now().plusMinutes(5));
        otpEntity.setVerified(false);

        otpRepo.save(otpEntity);

        String subject = "Email Verification OTP";
        String message = "Your OTP for registration is: " + generatedOtp + "\nThis OTP is valid for 5 minutes.";

        sendMail(email, subject, message);

        return "OTP sent to " + email;
    }


    private String generateOTP() {
        return new DecimalFormat("000000").format(new SecureRandom().nextInt(999999));
    }


    private void sendMail(String toEmail, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("yadhunandan2002@gmail.com"); // better: configure spring.mail.username
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailSender.send(mailMessage);
    }


    public void verifyOTP(String otp, String email) {
        OTP otpEntity = otpRepo.findByEmail(email)
                .orElseThrow(() -> new OtpMismatchException("OTP not found for email: " + email));

        if (!otpEntity.getOtp().equals(otp)) {
            throw new OtpMismatchException("Invalid OTP for email: " + email);
        }

        if (LocalDateTime.now().isAfter(otpEntity.getExpiry())) {
            throw new OtpExpiredException("OTP has expired for email: " + email);
        }

        otpEntity.setVerified(true);
        otpRepo.save(otpEntity);
    }


    public void createProfile(String email, String username, String fullName, String password) {
        if (userRepo.existsByEmail(email)) {
            throw new AccountExistsException("Your email " + email + " is already registered");
        }

        OTP exists = otpRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email " + email + " not verified"));

        if (!exists.getVerified()) {
            throw new RuntimeException("Email not verified. Please verify OTP first.");
        }
        User user = User.builder()
                .FullName(fullName)
                .username(username)
                .email(email)
                .role(Role.PATIENT)
                .password(passwordEncoder.encode(password))
                .build();

        userRepo.save(user);
        otpRepo.delete(exists);
    }

    public void createDocProfile(docProfileReq request)
    {
        Boolean doctorEntity = DocRepo.existsByEmail(request.getEmail());
        if(doctorEntity) throw new AccountExistsException("Account Already Exists for "+request.getEmail());
        Doctor doc= Doctor.builder()
                //.photo(request.getPhoto())
                .doctorName(request.getDoctorName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .Specialization(request.getSpecialization())
                .role(Role.DOCTOR)
                .build();
        DocRepo.save(doc);
    }

    public LoginRes loginAuth(LoginReq request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        // Generate JWT based on username
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        return new LoginRes(token, user.getUsername(),user.getId(),user.getRole().name());
    }

    // Add this method inside the profileService class

    public void changeDoctorPassword(String doctorUsername, String oldPassword, String newPassword) {


        Doctor doctor = DocRepo.findByDoctorName(doctorUsername) // Assumes findByDoctorName exists
                .orElseThrow(() -> new UsernameNotFoundException("Doctor not found with username: " + doctorUsername));

        if (!passwordEncoder.matches(oldPassword, doctor.getPassword())) {
            throw new BadCredentialsException("Invalid old password.");
        }

        // You might want to add validation for the new password format here
        doctor.setPassword(passwordEncoder.encode(newPassword));
        DocRepo.save(doctor);
    }

    public LoginRes DocLoginAuth(LoginReq request) {
        Doctor doc = DocRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Doctor not found"));

        if (!passwordEncoder.matches(request.getPassword(), doc.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        // Generate JWT based on username
        String token = jwtUtil.generateToken(doc.getDoctorName(), doc.getRole().name());

        return new LoginRes(token, doc.getDoctorName(),doc.getId(),doc.getRole().name());
    }
    public String initiatePasswordReset(String email) {
        // 1. Check if user exists (either User or Doctor)
        Optional<User> userOpt = userRepo.findByEmail(email);
        Optional<Doctor> docOpt = DocRepo.findByEmail(email);

        if (userOpt.isEmpty() && docOpt.isEmpty()) {
            throw new UsernameNotFoundException("No account found with email: " + email);
        }

        // 2. Create or update OTP entity
        OTP otpEntity = otpRepo.findByEmail(email).orElse(new OTP());
        String generatedOtp = generateOTP();

        otpEntity.setEmail(email);
        otpEntity.setOtp(generatedOtp);
        otpEntity.setExpiry(LocalDateTime.now().plusMinutes(5));
        otpEntity.setVerified(false);

        otpRepo.save(otpEntity);

        // 3. Send OTP mail
        String subject = "Password Reset OTP - MindMitra";
        String message = "Your OTP for password reset is: " + generatedOtp
                + "\nThis OTP is valid for 5 minutes."
                + "\nIf you did not request a reset, please ignore this email.";

        sendMail(email, subject, message);

        return "Password reset OTP sent to " + email;
    }

    public String resetPassword(String email, String otp, String newPassword) {
        // 1. Validate OTP
        OTP otpEntity = otpRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No OTP request found for this email"));

        if (!otpEntity.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        if (otpEntity.getExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired");
        }



        // 2. Mark OTP as used
        otpEntity.setVerified(true);
        otpRepo.save(otpEntity);

        // 3. Update password (for User or Doctor)
        Optional<User> userOpt = userRepo.findByEmail(email);
        Optional<Doctor> docOpt = DocRepo.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepo.save(user);
        } else if (docOpt.isPresent()) {
            Doctor doctor = docOpt.get();
            doctor.setPassword(passwordEncoder.encode(newPassword));
            DocRepo.save(doctor);
        } else {
            throw new RuntimeException("No account found with this email");
        }

        return "Password reset successful. You can now login with your new password.";
    }


}
