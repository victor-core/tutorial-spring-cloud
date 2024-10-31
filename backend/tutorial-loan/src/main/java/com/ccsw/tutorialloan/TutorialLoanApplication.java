package com.ccsw.tutorialloan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TutorialLoanApplication {

    public static void main(String[] args) {
        SpringApplication.run(TutorialLoanApplication.class, args);
    }

}
