package gui;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hashing {
    public boolean comparePassword(String password, String hash) {
        String passHash = createPassword(password);
        return passHash.equals(hash);
    }

    public String createPassword(String password) {
        MessageDigest md = null;
        try { md = MessageDigest.getInstance("SHA-256"); }
        catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
        byte[] hashVal = md.digest(password.getBytes(StandardCharsets.UTF_8));
        BigInteger number = new BigInteger(1, hashVal);

        StringBuilder hexString = new StringBuilder(number.toString(16));
        while (hexString.length() < 32) { hexString.insert(0, '0'); }

        return hexString.toString();
    }
}
