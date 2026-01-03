package com.gym.management.service;

import com.gym.management.dto.AttendanceDto;
import com.gym.management.entity.Attendance;
import com.gym.management.entity.Members;
import com.gym.management.repository.AttendanceRepository;
import com.gym.management.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final MemberRepository memberRepository;

    public AttendanceService(AttendanceRepository attendanceRepository, MemberRepository memberRepository) {
        this.attendanceRepository = attendanceRepository;
        this.memberRepository = memberRepository;
    }

    public void saveAttendance(AttendanceDto dto) {
        Members member = memberRepository.findById(dto.getMemberId()).orElseThrow();
        Attendance att = new Attendance();
        att.setMember(member);
        att.setTimestamp(dto.getTimestamp());
        att.setSource(dto.getSource());
        attendanceRepository.save(att);
    }
}

