package com.baligh.backend.service;

import com.baligh.backend.dto.request.RegisterOrgFullRequest;
import com.baligh.backend.dto.request.RegisterUserRequest;
import com.baligh.backend.dto.response.LoginResponse;
import com.baligh.backend.dto.response.RegisterOrgResponse;
import com.baligh.backend.exception.BusinessException;
import com.baligh.backend.model.Organization;
import com.baligh.backend.model.User;
import com.baligh.backend.model.enums.UserRole;
import com.baligh.backend.repository.OrganizationRepository;
import com.baligh.backend.repository.UserRepository;
import com.twilio.Twilio;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final OrganizationRepository orgRepository;

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.verify-service-sid}")
    private String verifyServiceSid;
    private final Map <String, String> otpStore = new java.util.concurrent.ConcurrentHashMap<>();

 public void sendOtp(String phone) {
    try {
        Twilio.init(accountSid, authToken);
       String formattedPhone = "whatsapp:+972" + phone.substring(1);
        System.out.println(">>> Sending OTP to: " + formattedPhone);
        
        String digits = "0123456789";
StringBuilder otpBuilder = new StringBuilder();
java.util.Random random = new java.util.Random();
for (int i = 0; i < 6; i++) {
    otpBuilder.append(digits.charAt(random.nextInt(10)));
}
String otp = otpBuilder.toString();
        
        com.twilio.rest.api.v2010.account.Message.creator(
            new com.twilio.type.PhoneNumber(formattedPhone),
            new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),
            "رمز التحقق الخاص بك هو: " + otp
        ).create();
        
        otpStore.put(phone, otp);
        System.out.println(">>> OTP sent successfully: " + otp);
    } catch (Exception e) {
        System.out.println(">>> ERROR: " + e.getMessage());
        throw new RuntimeException(e.getMessage());
    }
}

   public LoginResponse login(String phone, String otp) {
    String stored = otpStore.get(phone);
    if (stored == null || !stored.equals(otp)) {
        throw new BusinessException("Invalid OTP", HttpStatus.UNAUTHORIZED);
    }
    otpStore.remove(phone);
    return userRepository.findByPhone(phone)
            .map(LoginResponse::found)
            .orElseGet(LoginResponse::notFound);
}

    @Transactional
    public LoginResponse registerUser(RegisterUserRequest request) {
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException("Phone number already registered", HttpStatus.CONFLICT);
        }
        User user = User.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .role(UserRole.USER)
                .build();
        return LoginResponse.found(userRepository.save(user));
    }

    @Transactional
    public RegisterOrgResponse registerOrg(RegisterOrgFullRequest request) {
        if (userRepository.existsByPhone(request.getRepPhone())) {
            throw new BusinessException("Phone number already registered", HttpStatus.CONFLICT);
        }
        User rep = User.builder()
                .name(request.getRepName())
                .phone(request.getRepPhone())
                .role(UserRole.ORG_MEMBER)
                .build();
        rep = userRepository.save(rep);
        Organization org = Organization.builder()
                .name(request.getOrgName())
                .category(request.getCategory())
                .description(request.getDescription())
                .contactPhone(request.getContactPhone())
                .contactEmail(request.getContactEmail())
                .representative(rep)
                .build();
        org = orgRepository.save(org);
        return new RegisterOrgResponse(rep.getId(), org.getId(), rep.getName(), rep.getPhone());
    }
}
