package com.rawa.cloud.helper;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.DigestUtils;

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

    public static String MD5(String origin) {
        return DigestUtils.md5DigestAsHex(origin.getBytes());
    }
}
