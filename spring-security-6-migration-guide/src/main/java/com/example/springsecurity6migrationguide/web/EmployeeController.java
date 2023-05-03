package com.example.springsecurity6migrationguide.web;

import com.example.springsecurity6migrationguide.entity.Employee;
import com.example.springsecurity6migrationguide.repo.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository ;

    @GetMapping("/employees")
    public ResponseEntity< List<Employee>> getAllEmployees(){
       return ResponseEntity.ok().body( employeeRepository.findAll());
    }

    @GetMapping("/public")
    public ResponseEntity<String> publicRessource(){
        return ResponseEntity.ok().body("public ressource ");
    }



}
