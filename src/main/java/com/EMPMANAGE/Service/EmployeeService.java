package com.EMPMANAGE.Service;

import com.EMPMANAGE.Entity.Employee;
import com.EMPMANAGE.Repository.EmployeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
    }

    @Transactional
    public Employee saveEmployee(Employee employee) {
        employee.setId(null);
        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee updateEmployee(Long id, Employee input) {
        Employee employee = getEmployeeById(id);
        employee.setFirstName(input.getFirstName());
        employee.setLastName(input.getLastName());
        employee.setEmail(input.getEmail());
        employee.setDepartment(input.getDepartment());
        employee.setDesignation(input.getDesignation());
        employee.setSalary(input.getSalary());
        employee.setJoiningDate(input.getJoiningDate());
        return employeeRepository.save(employee);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = getEmployeeById(id);
        employeeRepository.delete(employee);
    }
}
