package com.EMPMANAGE.Service;

import com.EMPMANAGE.Entity.Attendance;
import com.EMPMANAGE.Repository.AttendanceRepository;
import com.EMPMANAGE.Repository.EmployeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    public AttendanceService(AttendanceRepository attendanceRepository,
                             EmployeeRepository employeeRepository) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public Attendance checkIn(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found");
        }
        LocalDate today = LocalDate.now();
        if (attendanceRepository.existsByEmployeeIdAndDate(employeeId, today)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Attendance already marked today");
        }

        Attendance attendance = new Attendance();
        attendance.setEmployeeId(employeeId);
        attendance.setDate(today);
        attendance.setCheckInTime(LocalTime.now());
        attendance.setStatus("PRESENT");
        return attendanceRepository.save(attendance);
    }

    public List<Attendance> getEmployeeAttendance(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found");
        }
        return attendanceRepository.findByEmployeeId(employeeId);
    }
}
