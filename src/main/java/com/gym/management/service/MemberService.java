package com.gym.management.service;

import com.gym.management.dto.AddMemberDto;
import com.gym.management.entity.Members;
import com.gym.management.entity.Users;
import com.gym.management.repository.MemberRepository;
import com.gym.management.repository.UserRepository;
import com.gym.management.repository.AttendanceRepository;
import com.gym.management.repository.PaymentRepository;
import com.gym.management.repository.MealPlanRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AttendanceRepository attendanceRepository;
    private final PaymentRepository paymentRepository;
    private final MealPlanRepository mealPlanRepository;

    public MemberService(MemberRepository memberRepository, UserRepository userRepository, PasswordEncoder passwordEncoder,
                         AttendanceRepository attendanceRepository, PaymentRepository paymentRepository, MealPlanRepository mealPlanRepository) {
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.attendanceRepository = attendanceRepository;
        this.paymentRepository = paymentRepository;
        this.mealPlanRepository = mealPlanRepository;
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
        // Check if email already exists
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + dto.getEmail());
        }

        // Generate admission number (format: ADM + year + sequential number)
        String admissionNumber = generateAdmissionNumber();

        // Create user without username/password (members don't have login credentials)
        Users user = new Users();
        user.setUsername("null"); // No username for admin-created members
        // Set empty password hash (cannot be null due to database constraint)
        user.setPasswordHash(""); // Empty password for admin-created members (they can't login)
        user.setRole("member");
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user = userRepository.save(user);

        // Create member record
        Members member = new Members();
        member.setUser(user);
        member.setAdmissionNumber(admissionNumber);
        member.setAddress(dto.getAddress());
        member.setDateOfBirth(dto.getDateOfBirth());
        member.setHeight(dto.getHeight());
        member.setWeight(dto.getWeight());
        member.setGender(dto.getGender());
        member.setPhoneNumber(dto.getPhoneNumber());
        member.setRegistrationDate(LocalDateTime.now());
        member.setStatus("active"); // Auto-approve admin-created members
        
        // Set payment information
        String paymentStatus = (dto.getPaymentStatus() != null && !dto.getPaymentStatus().isEmpty()) 
            ? dto.getPaymentStatus() 
            : "UNPAID";
        member.setPaymentStatus(paymentStatus);
        
        // Set payment plan type and amount if provided
        if (dto.getPaymentPlanType() != null) {
            member.setPaymentPlanType(dto.getPaymentPlanType());
        }
        if (dto.getPaymentAmount() != null) {
            member.setPaymentAmount(dto.getPaymentAmount());
        }
        
        // If payment status is PAID, set payment dates
        if ("PAID".equalsIgnoreCase(paymentStatus)) {
            LocalDateTime now = LocalDateTime.now();
            member.setLastPaymentDate(now);
            
            // Calculate next payment date based on plan type
            String planType = dto.getPaymentPlanType();
            if (planType != null) {
                if ("YEARLY".equalsIgnoreCase(planType)) {
                    member.setNextPaymentDate(now.plusYears(1));
                } else if ("MONTHLY".equalsIgnoreCase(planType)) {
                    member.setNextPaymentDate(now.plusMonths(1));
                }
            }
        }

        return memberRepository.save(member);
    }

    private String generateAdmissionNumber() {
        int currentYear = LocalDateTime.now().getYear();
        long memberCount = memberRepository.count();
        // Format: ADM + YYYY + sequential number (e.g., ADM202601, ADM202602)
        return String.format("ADM%d%04d", currentYear, memberCount + 1);
    }

    @Transactional
    public void deleteMember(Long id) {
        Members member = memberRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + id));
        
        // Get user reference before deleting member
        Users user = member.getUser();
        
        // Delete all related records first (to avoid foreign key constraint violations)
        // Delete attendance records
        attendanceRepository.deleteByMember(member);
        
        // Delete payment records
        paymentRepository.deleteByMember(member);
        
        // Delete meal plan records
        mealPlanRepository.deleteByMember(member);
        
        // Delete the member record (this removes the foreign key reference)
        memberRepository.delete(member);
        
        // Delete the associated user record
        if (user != null) {
            userRepository.delete(user);
        }
    }

    public Members updateMemberPayment(Long id, String paymentStatus, String planType, BigDecimal amount) {
        Members member = memberRepository.findById(id).orElseThrow();
        member.setPaymentStatus(paymentStatus);
        member.setPaymentPlanType(planType);
        member.setPaymentAmount(amount);
        
        // If payment status is PAID, set payment dates
        if ("PAID".equalsIgnoreCase(paymentStatus)) {
            LocalDateTime now = LocalDateTime.now();
            member.setLastPaymentDate(now);
            
            // Calculate next payment date based on plan type
            if (planType != null) {
                if ("YEARLY".equalsIgnoreCase(planType)) {
                    member.setNextPaymentDate(now.plusYears(1));
                } else if ("MONTHLY".equalsIgnoreCase(planType)) {
                    member.setNextPaymentDate(now.plusMonths(1));
                }
            }
        } else {
            // For UNPAID or other statuses, clear payment dates
            member.setLastPaymentDate(null);
            member.setNextPaymentDate(null);
        }
        
        return memberRepository.save(member);
    }
}

