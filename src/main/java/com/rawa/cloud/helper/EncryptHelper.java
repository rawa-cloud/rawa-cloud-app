package com.rawa.cloud.helper;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncryptHelper {

    private static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static String encrypt(String password) {
        String prefix = "{bcrypt}";
        return prefix  + passwordEncoder.encode(password);
    }

    public static boolean check(String rawPassword, String encodedPassword) {
        encodedPassword = encodedPassword.replace("{bcrypt}", "");
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
