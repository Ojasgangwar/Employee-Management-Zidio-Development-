package com.EMPMANAGE.Controller;

import com.EMPMANAGE.Entity.Payroll;
import com.EMPMANAGE.Service.PayrollService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payroll")
public class PayrollController {
    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @PostMapping("/generate/{employeeId}")
    public ResponseEntity<Payroll> generatePayroll(@PathVariable Long employeeId) {
        return ResponseEntity.ok(payrollService.generateForCurrentMonth(employeeId));
    }
}
