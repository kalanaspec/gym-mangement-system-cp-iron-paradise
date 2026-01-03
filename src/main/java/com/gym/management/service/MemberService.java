package com.gym.management.service;

import com.gym.management.dto.AddMemberDto;
import com.gym.management.entity.Members;
import com.gym.management.entity.Users;
import com.gym.management.repository.MemberRepository;
import com.gym.management.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Members> findAll() {
        return memberRepository.findAll();
    }

    public Members approve(Long id) {
        Members m = memberRepository.findById(id).orElseThrow();
        m.setStatus("active");
        return memberRepository.save(m);
    }

    public Members updateStatus(Long id, String status) {
        Members m = memberRepository.findById(id).orElseThrow();
        m.setStatus(status);
        return memberRepository.save(m);
    }

    public Members createMember(AddMemberDto dto) {
        // Check if username already exists
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + dto.getUsername());
        }

        // Create user first
        Users user = new Users();
        user.setUsername(dto.getUsername());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setRole("member");
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user = userRepository.save(user);

        // Create member record
        Members member = new Members();
        member.setUser(user);
        member.setAddress(dto.getAddress());
        member.setDateOfBirth(dto.getDateOfBirth());
        member.setHeight(dto.getHeight());
        member.setWeight(dto.getWeight());
        member.setRegistrationDate(LocalDateTime.now());
        member.setStatus("active"); // Auto-approve admin-created members

        return memberRepository.save(member);
    }

    public Members updateMemberPayment(Long id, String paymentStatus, String planType, BigDecimal amount) {
        Members member = memberRepository.findById(id).orElseThrow();
        member.setPaymentStatus(paymentStatus);
        member.setPaymentPlanType(planType);
        member.setPaymentAmount(amount);
        member.setLastPaymentDate(LocalDateTime.now());
        
        // Calculate next payment date based on plan type
        if (planType != null && planType.equals("YEARLY")) {
            member.setNextPaymentDate(LocalDateTime.now().plusYears(1));
        } else if (planType != null && planType.equals("MONTHLY")) {
            member.setNextPaymentDate(LocalDateTime.now().plusMonths(1));
        }
        
        return memberRepository.save(member);
    }
}

