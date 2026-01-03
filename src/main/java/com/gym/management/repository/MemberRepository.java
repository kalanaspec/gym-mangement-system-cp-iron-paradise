package com.gym.management.repository;

import com.gym.management.entity.Members;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MemberRepository extends JpaRepository<Members, Long> {
    List<Members> findByStatus(String status);
    
    @Query("SELECT m FROM Members m WHERE m.nextPaymentDate IS NOT NULL " +
           "AND m.nextPaymentDate BETWEEN :startDate AND :endDate " +
           "AND m.paymentStatus = 'PAID' " +
           "AND m.status = 'active'")
    List<Members> findMembersWithUpcomingPaymentDate(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}

