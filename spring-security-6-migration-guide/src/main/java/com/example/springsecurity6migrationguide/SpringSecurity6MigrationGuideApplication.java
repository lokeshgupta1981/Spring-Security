package com.example.springsecurity6migrationguide;

import com.example.springsecurity6migrationguide.entity.Employee;
import com.example.springsecurity6migrationguide.repo.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringSecurity6MigrationGuideApplication implements CommandLineRunner {

    @Autowired
    private EmployeeRepository employeeRepository ;

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurity6MigrationGuideApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Employee employee1 = Employee
                .builder()
                .id(null)
                .age(20)
                .job("SDE2")
                .name("Mr X")
                .build();
        Employee employee2 = Employee
                .builder()
                .id(null)
                .age(23)
                .job("SDE2")
                .name("Mr Y")
                .build();
        employeeRepository.save(employee2);
        employeeRepository.save(employee1);
    }
}
