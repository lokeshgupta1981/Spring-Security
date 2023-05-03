package com.example.springsecurity6migrationguide.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        // httpSecurity.csrf().ignoringRequestMatchers("/h2-console/**");
       //  httpSecurity.headers().frameOptions().disable();
        httpSecurity.authorizeHttpRequests()
                .requestMatchers("/public/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .httpBasic();
        return  httpSecurity.build();
    }

    // In Memory Authentication
     @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager(){
     UserDetails userDetails = User.withDefaultPasswordEncoder().username("admin").password("1234").roles("ADMIN").build();
    return new InMemoryUserDetailsManager(userDetails);
     }

    // JDBC Authentication

    // @Autowired
    // private DataSource dataSource ;

  //@Bean
    //public DataSource dataSource() {
    //  return new EmbeddedDatabaseBuilder()
    //      .setType(EmbeddedDatabaseType.H2)
    //      .addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION)
    //       .build();
    //}

    //@Bean
    //JdbcUserDetailsManager jdbcUserDetailsManager(DataSource dataSource){
    //  UserDetails user = User.withDefaultPasswordEncoder()
    //          .username("user")
    //          .password("password")
    //          .roles("USER")
    //          .build();
    //  JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
    //  jdbcUserDetailsManager.createUser(user);
    //  return jdbcUserDetailsManager ;
    //}


}
