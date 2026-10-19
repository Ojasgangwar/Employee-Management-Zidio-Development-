package com.EMPMANAGE.Service;

import com.EMPMANAGE.Entity.Employee;
import com.EMPMANAGE.Entity.Payroll;
import com.EMPMANAGE.Repository.EmployeeRepository;
import com.EMPMANAGE.Repository.PayrollRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.time.YearMonth;

@Service
public class PayrollService {
    private final EmployeeRepository employeeRepository;
    private final PayrollRepository payrollRepository;

    public PayrollService(EmployeeRepository employeeRepository, PayrollRepository payrollRepository) {
        this.employeeRepository = employeeRepository;
        this.payrollRepository = payrollRepository;
    }

    @Transactional
    public Payroll generateForCurrentMonth(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));

        String month = YearMonth.now().toString();
        return payrollRepository.findByEmployeeIdAndPayrollMonth(employeeId, month)
                .orElseGet(() -> {
                    double base = employee.getSalary() == null ? 0.0 : employee.getSalary();
                    double allowances = 0.0;
                    double deductions = 0.0;

                    Payroll payroll = new Payroll();
                    payroll.setEmployeeId(employeeId);
                    payroll.setPayrollMonth(month);
                    payroll.setPaymentDate(LocalDate.now());
                    payroll.setStatus("GENERATED");
                    payroll.setBaseSalary(base);
                    payroll.setAllowances(allowances);
                    payroll.setDeductions(deductions);
                    payroll.setNetSalary(base + allowances - deductions);
                    return payrollRepository.save(payroll);
                });
    }
}
