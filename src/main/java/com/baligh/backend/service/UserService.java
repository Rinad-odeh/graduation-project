package com.baligh.backend.service;

import com.baligh.backend.dto.request.UpdateFcmTokenRequest;
import com.baligh.backend.dto.request.UpdateUserRequest;
import com.baligh.backend.dto.response.PageResponse;
import com.baligh.backend.dto.response.UserResponse;
import com.baligh.backend.exception.ResourceNotFoundException;
import com.baligh.backend.model.User;
import com.baligh.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getById(Long id) {
        return UserResponse.from(findOrThrow(id));
    }

    @Transactional
    public UserResponse updateProfile(Long id, UpdateUserRequest request) {
        User user = findOrThrow(id);
        user.setName(request.getName());
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public void updateFcmToken(Long id, UpdateFcmTokenRequest request) {
        User user = findOrThrow(id);
        user.setFcmToken(request.getFcmToken());
        userRepository.save(user);
    }

    public PageResponse<UserResponse> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return PageResponse.from(userRepository.findAll(pageable).map(UserResponse::from));
    }

    private User findOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("User", id));
    }
}
