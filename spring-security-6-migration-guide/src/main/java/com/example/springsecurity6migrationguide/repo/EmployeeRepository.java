package com.example.springsecurity6migrationguide.repo;

import com.example.springsecurity6migrationguide.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee , Long> {
}
