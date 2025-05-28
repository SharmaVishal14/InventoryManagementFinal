package com.cts.service;

import java.util.Optional;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cts.entity.UserCredentials;
import com.cts.repository.UserCredentialRepo;

import jakarta.transaction.Transactional;

@Service
public class AuthService {

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private UserCredentialRepo credentialRepo;
	
    @Autowired
    private JwtService jwtService;
    
   
	public String saveUser(UserCredentials userCredentials)
	{
		userCredentials.setPassword(passwordEncoder.encode(userCredentials.getPassword()));
		credentialRepo.save(userCredentials);
		return "user added to the system";
	}
	
	public String generateToken(UserCredentials userCredentials)
	{
		return jwtService.generateToken(userCredentials);
	}
	
	public void validateToken(String token)
	{
		jwtService.validateToken(token);
	}
	
	public String verifyUser(String email)
	{
		Optional<UserCredentials> userCredentials=credentialRepo.findByEmail(email);
		
		if(userCredentials.isEmpty())
		{
			return "not verified";
		}
		return "verified";
	}
}
