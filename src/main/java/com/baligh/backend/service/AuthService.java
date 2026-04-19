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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final OrganizationRepository orgRepository;

    public LoginResponse login(String phone) {
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
