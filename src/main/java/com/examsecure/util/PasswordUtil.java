package com.examsecure.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PasswordUtil {

    // SHA-256 of (salt + password)
    public static String hash(String salt, String password) {

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            byte[] bytes = md.digest(
                    (salt + password).getBytes(StandardCharsets.UTF_8)
            );

            StringBuilder sb = new StringBuilder();

            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    // Generate a random 16-character salt
    public static String generateSalt() {

        String chars =
                "abcdefghijklmnopqrstuvwxyz0123456789";

        SecureRandom rnd = new SecureRandom();

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 16; i++) {
            sb.append(chars.charAt(
                    rnd.nextInt(chars.length())
            ));
        }

        return sb.toString();
    }
}