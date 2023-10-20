package com.example.teamproject.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Component;

@Component
public class Encrypt {
    public String encode(String raw) {
        String hex = null;
        try {
            // String raw = "password1234";
            MessageDigest md;
            md = MessageDigest.getInstance("SHA-256");
            md.update(raw.getBytes());
            hex = String.format("%064x", new BigInteger(1, md.digest()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hex;
    }
}

// hex = 해시값 반환