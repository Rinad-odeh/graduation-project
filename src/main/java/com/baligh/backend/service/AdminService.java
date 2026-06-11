package com.baligh.backend.service;

import com.baligh.backend.dto.response.UserResponse;
import com.baligh.backend.exception.BusinessException;
import com.baligh.backend.model.User;
import com.baligh.backend.model.enums.UserRole;
import com.baligh.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public List<UserResponse> getAllAdmins() {
        return userRepository.findAllByRole(UserRole.ADMIN)
                .stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponse promoteToAdmin(String phone, String name) {
        User user = userRepository.findByPhone(phone)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .name(name != null ? name : "Admin")
                            .phone(phone)
                            .role(UserRole.ADMIN)
                            .build();
                    return userRepository.save(newUser);
                });
        user.setRole(UserRole.ADMIN);
        if (name != null && !name.isBlank()) user.setName(name);
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public void demoteAdmin(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));
        if (user.getRole() != UserRole.ADMIN) {
            throw new BusinessException("User is not an admin", HttpStatus.BAD_REQUEST);
        }
        user.setRole(UserRole.USER);
        userRepository.save(user);
    }
}
