package com.example.multitenant.repository;

import com.example.multitenant.entity.Otp;
import com.example.multitenant.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByOtpAndUser(String otp, User user);
    void deleteByUser(User user);
}

