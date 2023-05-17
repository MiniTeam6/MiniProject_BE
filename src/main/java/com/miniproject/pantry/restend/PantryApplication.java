package com.miniproject.pantry.restend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableSentry
public class PantryApplication {
    public static void main(String[] args) {
        // Sentry.io 연결해서 !!
        SpringApplication.run(PantryApplication.class, args);
    }

}
