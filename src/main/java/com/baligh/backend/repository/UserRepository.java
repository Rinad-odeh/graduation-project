package com.baligh.backend.repository;

import com.baligh.backend.model.User;
import com.baligh.backend.model.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPhone(String phone);

    boolean existsByPhone(String phone);

    Page<User> findByRole(UserRole role, Pageable pageable);

    List<User> findAllByRole(UserRole role);

    long countByActiveTrue();
}
