package com.gym.management.controller;

import com.gym.management.entity.Members;
import com.gym.management.repository.MemberRepository;
import com.gym.management.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@PreAuthorize("hasRole('ADMIN')")
public class TestController {
    
    private final EmailService emailService;
    private final MemberRepository memberRepository;

    public TestController(EmailService emailService, MemberRepository memberRepository) {
        this.emailService = emailService;
        this.memberRepository = memberRepository;
    }

    /**
     * Test endpoint to manually trigger expiration reminder emails
     * This sends emails to members whose nextPaymentDate is 2-4 days away
     */
    @PostMapping("/send-expiration-reminders")
    public ResponseEntity<?> testSendExpirationReminders() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startDate = now.plusDays(2).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endDate = now.plusDays(4).withHour(23).withMinute(59).withSecond(59);
            
            List<Members> membersToNotify = memberRepository.findMembersWithUpcomingPaymentDate(startDate, endDate);
            
            int emailsSent = 0;
            for (Members member : membersToNotify) {
                try {
                    if (member != null && 
                        member.getUser() != null && 
                        member.getNextPaymentDate() != null) {
                        
                        String email = member.getUser().getEmail();
                        String memberName = member.getUser().getName();
                        
                        if (email != null && !email.trim().isEmpty()) {
                            emailService.sendMembershipExpirationReminder(
                                email,
                                memberName,
                                member.getNextPaymentDate()
                            );
                            emailsSent++;
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error sending email to member " + member.getMemberId() + ": " + e.getMessage());
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Test email sending completed");
            response.put("emailsSent", emailsSent);
            response.put("membersFound", membersToNotify.size());
            response.put("dateRange", Map.of(
                "startDate", startDate.toString(),
                "endDate", endDate.toString()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error testing email notifications: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Test endpoint to send a single test email to a specific email address
     */
    @PostMapping("/send-test-email")
    public ResponseEntity<?> testSendEmail(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String memberName = request.getOrDefault("memberName", "Test Member");
            
            if (email == null || email.trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Email address is required");
                return ResponseEntity.badRequest().body(error);
            }
            
            // Send test email with expiration date 3 days from now
            LocalDateTime testExpirationDate = LocalDateTime.now().plusDays(3);
            emailService.sendMembershipExpirationReminder(email, memberName, testExpirationDate);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Test email sent successfully");
            response.put("email", email);
            response.put("testExpirationDate", testExpirationDate.toString());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error sending test email: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Get list of members who would receive expiration reminders
     */
    @GetMapping("/upcoming-expirations")
    public ResponseEntity<?> getUpcomingExpirations() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startDate = now.plusDays(2).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endDate = now.plusDays(4).withHour(23).withMinute(59).withSecond(59);
            
            List<Members> members = memberRepository.findMembersWithUpcomingPaymentDate(startDate, endDate);
            
            List<Map<String, Object>> memberInfo = members.stream()
                .map(m -> {
                    Map<String, Object> info = new HashMap<>();
                    info.put("memberId", m.getMemberId());
                    info.put("name", m.getUser() != null ? m.getUser().getName() : "N/A");
                    info.put("email", m.getUser() != null ? m.getUser().getEmail() : "N/A");
                    info.put("nextPaymentDate", m.getNextPaymentDate());
                    info.put("paymentStatus", m.getPaymentStatus());
                    info.put("status", m.getStatus());
                    return info;
                })
                .toList();
            
            Map<String, Object> response = new HashMap<>();
            response.put("count", memberInfo.size());
            response.put("dateRange", Map.of(
                "startDate", startDate.toString(),
                "endDate", endDate.toString()
            ));
            response.put("members", memberInfo);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error fetching upcoming expirations: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(error);
        }
    }
}

