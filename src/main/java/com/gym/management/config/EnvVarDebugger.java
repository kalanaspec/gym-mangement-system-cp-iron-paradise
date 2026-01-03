package com.gym.management.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class EnvVarDebugger implements CommandLineRunner {
    @Override
    public void run(String... args) {
        System.out.println("========================================");
        System.out.println("DEBUGGING AZURE ENVIRONMENT VARIABLES");
        System.out.println("========================================");
        System.getenv().forEach((key, value) -> {
            // Filter out sensitive keys to avoid leaking passwords in logs
            if (key.contains("PASSWORD") || key.contains("SECRET") || key.contains("KEY")) {
                System.out.println(key + " = [HIDDEN]");
            } else {
                System.out.println(key + " = " + value);
            }
        });
        System.out.println("========================================");
    }
}