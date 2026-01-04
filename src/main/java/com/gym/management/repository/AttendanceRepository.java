package com.gym.management.repository;

import com.gym.management.entity.Attendance;
import com.gym.management.entity.Members;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByMember(Members member);
    void deleteByMember(Members member);
}

