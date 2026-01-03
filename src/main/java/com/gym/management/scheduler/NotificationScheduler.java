package com.gym.management.scheduler;

import com.gym.management.entity.Members;
import com.gym.management.repository.MemberRepository;
import com.gym.management.service.EmailService;
import com.gym.management.service.PaymentService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@EnableScheduling
public class NotificationScheduler {
    private final PaymentService paymentService;
    private final EmailService emailService;
    private final MemberRepository memberRepository;

    public NotificationScheduler(PaymentService paymentService, EmailService emailService, MemberRepository memberRepository) {
        this.paymentService = paymentService;
        this.emailService = emailService;
        this.memberRepository = memberRepository;
    }

    @Scheduled(cron = "0 0 9 * * ?") // Runs daily at 9 AM
    public void sendMembershipExpirationReminders() {
        try {
            LocalDateTime now = LocalDateTime.now();
            // Find members whose nextPaymentDate is between 2 and 4 days from now
            // This sends reminders 2-4 days before expiration (e.g., Jan 28-29 for Feb 1 expiration)
            LocalDateTime startDate = now.plusDays(2).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endDate = now.plusDays(4).withHour(23).withMinute(59).withSecond(59);
            
            List<Members> membersToNotify = memberRepository.findMembersWithUpcomingPaymentDate(startDate, endDate);
            
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
                            System.out.println("Sent expiration reminder to: " + email + " for expiration date: " + member.getNextPaymentDate());
                        }
                    }
                } catch (Exception e) {
                    // Log error for individual member processing
                    System.err.println("Error processing member " + (member != null ? member.getMemberId() : "unknown") + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            System.out.println("Membership expiration reminder job completed. Sent " + membersToNotify.size() + " reminders.");
            
        } catch (Exception e) {
            // Log error for scheduler
            System.err.println("Error in membership expiration reminder scheduler: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 0 9 * * ?") // Runs daily at 9 AM (same time, but different method)
    public void sendExpirationReminders() {
        try {
            paymentService.expiringWithinDays(7).forEach(payment -> {
                try {
                    if (payment != null && 
                        payment.getMember() != null && 
                        payment.getMember().getUser() != null) {
                        
                        String email = payment.getMember().getUser().getEmail();
                        if (email != null && !email.trim().isEmpty()) {
                            emailService.sendExpirationReminder(email, "Your subscription is expiring soon.");
                        }
                    }
                } catch (Exception e) {
                    // Log error for individual payment processing
                    System.err.println("Error processing payment " + payment.getPaymentId() + ": " + e.getMessage());
                }
            });
        } catch (Exception e) {
            // Log error for scheduler
            System.err.println("Error in expiration reminder scheduler: " + e.getMessage());
        }
    }
}

