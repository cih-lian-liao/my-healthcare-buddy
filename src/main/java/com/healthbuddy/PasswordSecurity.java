package com.healthbuddy;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密碼安全處理類別
 * 提供密碼哈希、驗證和鹽值生成功能
 */
public class PasswordSecurity {
    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    
    /**
     * 生成隨機鹽值
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * 將密碼與鹽值進行哈希處理
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(Base64.getDecoder().decode(salt));
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("密碼哈希處理失敗", e);
        }
    }
    
    /**
     * 驗證密碼是否正確
     */
    public static boolean verifyPassword(String password, String hashedPassword, String salt) {
        String hashedInput = hashPassword(password, salt);
        return hashedInput.equals(hashedPassword);
    }
    
    /**
     * 檢查密碼強度
     */
    public static PasswordStrength checkPasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            return PasswordStrength.WEAK;
        }
        
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch -> "!@#$%^&*()_+-=[]{}|;:,.<>?".indexOf(ch) >= 0);
        
        int score = 0;
        if (hasUpper) score++;
        if (hasLower) score++;
        if (hasDigit) score++;
        if (hasSpecial) score++;
        if (password.length() >= 8) score++;
        
        if (score <= 2) return PasswordStrength.WEAK;
        if (score <= 3) return PasswordStrength.MEDIUM;
        if (score <= 4) return PasswordStrength.STRONG;
        return PasswordStrength.VERY_STRONG;
    }
    
    public enum PasswordStrength {
        WEAK("弱"),
        MEDIUM("中等"),
        STRONG("強"),
        VERY_STRONG("非常強");
        
        private final String description;
        
        PasswordStrength(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
