package com.gym.management.service;

import com.gym.management.dto.AuthDtos;
import com.gym.management.entity.Members;
import com.gym.management.entity.Users;
import com.gym.management.repository.MemberRepository;
import com.gym.management.repository.UserRepository;
import com.gym.management.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, MemberRepository memberRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public void register(AuthDtos.RegisterRequest req) {
        Users user = new Users();
        user.setUsername(req.getUsername());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setRole("member");
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user = userRepository.save(user);

        Members member = new Members();
        member.setUser(user);
        member.setRegistrationDate(LocalDateTime.now());
        member.setStatus("pending");
        memberRepository.save(member);
    }

    @Transactional
    public void registerAdmin(AuthDtos.RegisterRequest req) {
        // Check if username already exists
        if (userRepository.findByUsername(req.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + req.getUsername());
        }
        
        // Check if email already exists
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + req.getEmail());
        }
        
        Users user = new Users();
        user.setUsername(req.getUsername());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setRole("admin");
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        userRepository.save(user);
    }

    public String login(String username) {
        // this method assumes authentication is already done by provider; we just create token
        var claims = new HashMap<String, Object>();
        claims.put("roles", "ROLE_MEMBER");
        return jwtUtil.generateToken(username, claims, 1000L * 60 * 60 * 24);
    }
}

