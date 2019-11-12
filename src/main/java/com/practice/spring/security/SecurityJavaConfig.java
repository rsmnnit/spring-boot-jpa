package com.practice.spring.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityJavaConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("admin").password(encoder().encode("admin")).roles("USER", "ADMIN")
                .and()
                .withUser("user").password(encoder().encode("user")).roles("USER");
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }


//    @Bean
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        // ALTHOUGH THIS SEEMS LIKE USELESS CODE,
//        // IT'S REQUIRED TO PREVENT SPRING BOOT AUTO-CONFIGURATION
//        return super.authenticationManagerBean();
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .and()
                .authorizeRequests()
                .antMatchers("/practice/persons/getAll").hasRole("USER")//.authenticated()
                .antMatchers("/practice/persons/kafka").hasRole("ADMIN")
                .antMatchers("/practice/persons/putMessage").hasRole("ADMIN")
                .antMatchers("/practice/persons/get").hasRole("USER")
//                .antMatchers("/practice/persons/getLastMessages").authenticated()
                .and()
                .formLogin()
//                .successHandler(mySuccessHandler)
//                .failureHandler(myFailureHandler)
                .and()
                .logout();
    }
}
