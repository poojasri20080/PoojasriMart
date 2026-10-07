package com.poojamart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PoojaMartApplication {

    public static void main(String[] args) {
        SpringApplication.run(PoojaMartApplication.class, args);
        System.out.println("==================================================");
        System.out.println(" PoojaMart Toys Application is running successfully!");
        System.out.println(" Local URL: http://localhost:8080");
        System.out.println(" Admin Credentials: admin@poojamart.com / admin123");
        System.out.println(" User Credentials: user@poojamart.com / user123");
        System.out.println("==================================================");
    }
}
