package com.cts.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cts.entity.UserCredentials;

public interface UserCredentialRepo extends JpaRepository<UserCredentials, Integer> {

	Optional<UserCredentials> findByEmail(String email);
}
