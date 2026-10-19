package com.EMPMANAGE.Repository;

import com.EMPMANAGE.Entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Long> {
    Optional<Payroll> findByEmployeeIdAndPayrollMonth(Long employeeId, String payrollMonth);
}
