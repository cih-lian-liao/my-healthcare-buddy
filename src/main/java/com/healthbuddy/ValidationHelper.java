package com.healthbuddy;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * 輸入驗證輔助類別
 * 提供各種輸入欄位的驗證方法
 */
public class ValidationHelper {
    
    // 數字範圍限制
    public static final double MIN_WEIGHT = 20.0;
    public static final double MAX_WEIGHT = 300.0;
    public static final double MIN_HEIGHT = 100.0;
    public static final double MAX_HEIGHT = 250.0;
    public static final int MIN_AGE = 1;
    public static final int MAX_AGE = 150;
    public static final int MIN_STEPS = 0;
    public static final int MAX_STEPS = 100000;
    public static final int MIN_HEART_RATE = 30;
    public static final int MAX_HEART_RATE = 250;
    public static final int MIN_WATER_INTAKE = 0;
    public static final int MAX_WATER_INTAKE = 50;
    public static final int MIN_SLEEP_HOURS = 0;
    public static final int MAX_SLEEP_HOURS = 24;
    
    // 正則表達式模式
    private static final Pattern BLOOD_PRESSURE_PATTERN = 
        Pattern.compile("^\\d{1,3}/\\d{1,3}$");
    
    /**
     * 驗證用戶名
     */
    public static ValidationResult validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return new ValidationResult(false, "用戶名不能為空");
        }
        
        if (username.length() < 3) {
            return new ValidationResult(false, "用戶名至少需要3個字符");
        }
        
        if (username.length() > 20) {
            return new ValidationResult(false, "用戶名不能超過20個字符");
        }
        
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            return new ValidationResult(false, "用戶名只能包含字母、數字和下劃線");
        }
        
        return new ValidationResult(true, "用戶名有效");
    }
    
    /**
     * 驗證密碼
     */
    public static ValidationResult validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return new ValidationResult(false, "密碼不能為空");
        }
        
        if (password.length() < 6) {
            return new ValidationResult(false, "密碼至少需要6個字符");
        }
        
        if (password.length() > 50) {
            return new ValidationResult(false, "密碼不能超過50個字符");
        }
        
        PasswordSecurity.PasswordStrength strength = PasswordSecurity.checkPasswordStrength(password);
        if (strength == PasswordSecurity.PasswordStrength.WEAK) {
            return new ValidationResult(true, "密碼強度較弱，建議使用大小寫字母、數字和特殊字符");
        }
        
        return new ValidationResult(true, "密碼有效");
    }
    
    /**
     * 驗證姓名
     */
    public static ValidationResult validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ValidationResult(false, "用戶名稱不能為空");
        }
        
        if (name.length() > 50) {
            return new ValidationResult(false, "用戶名稱不能超過50個字符");
        }
        
        return new ValidationResult(true, "用戶名稱有效");
    }
    
    /**
     * 驗證年齡
     */
    public static ValidationResult validateAge(String ageText) {
        try {
            int age = Integer.parseInt(ageText.trim());
            if (age < MIN_AGE || age > MAX_AGE) {
                return new ValidationResult(false, String.format("年齡必須在 %d 到 %d 之間", MIN_AGE, MAX_AGE));
            }
            return new ValidationResult(true, "年齡有效");
        } catch (NumberFormatException e) {
            return new ValidationResult(false, "請輸入有效的年齡數字");
        }
    }
    
    /**
     * 驗證身高
     */
    public static ValidationResult validateHeight(String heightText) {
        try {
            double height = Double.parseDouble(heightText.trim());
            if (height < MIN_HEIGHT || height > MAX_HEIGHT) {
                return new ValidationResult(false, String.format("身高必須在 %.1f 到 %.1f 厘米之間", MIN_HEIGHT, MAX_HEIGHT));
            }
            return new ValidationResult(true, "身高有效");
        } catch (NumberFormatException e) {
            return new ValidationResult(false, "請輸入有效的身高數字");
        }
    }
    
    /**
     * 驗證體重
     */
    public static ValidationResult validateWeight(String weightText) {
        try {
            double weight = Double.parseDouble(weightText.trim());
            if (weight < MIN_WEIGHT || weight > MAX_WEIGHT) {
                return new ValidationResult(false, String.format("體重必須在 %.1f 到 %.1f 公斤之間", MIN_WEIGHT, MAX_WEIGHT));
            }
            return new ValidationResult(true, "體重有效");
        } catch (NumberFormatException e) {
            return new ValidationResult(false, "請輸入有效的體重數字");
        }
    }
    
    /**
     * 驗證步數
     */
    public static ValidationResult validateSteps(String stepsText) {
        try {
            int steps = Integer.parseInt(stepsText.trim());
            if (steps < MIN_STEPS || steps > MAX_STEPS) {
                return new ValidationResult(false, String.format("步數必須在 %d 到 %d 之間", MIN_STEPS, MAX_STEPS));
            }
            return new ValidationResult(true, "步數有效");
        } catch (NumberFormatException e) {
            return new ValidationResult(false, "請輸入有效的步數");
        }
    }
    
    /**
     * 驗證心率
     */
    public static ValidationResult validateHeartRate(String heartRateText) {
        try {
            int heartRate = Integer.parseInt(heartRateText.trim());
            if (heartRate < MIN_HEART_RATE || heartRate > MAX_HEART_RATE) {
                return new ValidationResult(false, String.format("心率必須在 %d 到 %d 之間", MIN_HEART_RATE, MAX_HEART_RATE));
            }
            return new ValidationResult(true, "心率有效");
        } catch (NumberFormatException e) {
            return new ValidationResult(false, "請輸入有效的心率數字");
        }
    }
    
    /**
     * 驗證血壓
     */
    public static ValidationResult validateBloodPressure(String bloodPressure) {
        if (bloodPressure == null || bloodPressure.trim().isEmpty()) {
            return new ValidationResult(false, "血壓不能為空");
        }
        
        if (!BLOOD_PRESSURE_PATTERN.matcher(bloodPressure).matches()) {
            return new ValidationResult(false, "血壓格式必須為 XXX/XXX（如：120/80）");
        }
        
        String[] parts = bloodPressure.split("/");
        try {
            int systolic = Integer.parseInt(parts[0]);
            int diastolic = Integer.parseInt(parts[1]);
            
            if (systolic < 50 || systolic > 250) {
                return new ValidationResult(false, "收縮壓必須在 50-250 之間");
            }
            
            if (diastolic < 30 || diastolic > 180) {
                return new ValidationResult(false, "舒張壓必須在 30-180 之間");
            }
            
            if (systolic <= diastolic) {
                return new ValidationResult(false, "收縮壓必須大於舒張壓");
            }
            
            return new ValidationResult(true, "血壓有效");
        } catch (NumberFormatException e) {
            return new ValidationResult(false, "請輸入有效的血壓數字");
        }
    }
    
    /**
     * 驗證水分攝取量
     */
    public static ValidationResult validateWaterIntake(String waterText) {
        try {
            int water = Integer.parseInt(waterText.trim());
            if (water < MIN_WATER_INTAKE || water > MAX_WATER_INTAKE) {
                return new ValidationResult(false, String.format("水分攝取量必須在 %d 到 %d 杯之間", MIN_WATER_INTAKE, MAX_WATER_INTAKE));
            }
            return new ValidationResult(true, "水分攝取量有效");
        } catch (NumberFormatException e) {
            return new ValidationResult(false, "請輸入有效的水分攝取量");
        }
    }
    
    /**
     * 驗證睡眠時數
     */
    public static ValidationResult validateSleepHours(String sleepText) {
        try {
            int sleep = Integer.parseInt(sleepText.trim());
            if (sleep < MIN_SLEEP_HOURS || sleep > MAX_SLEEP_HOURS) {
                return new ValidationResult(false, String.format("睡眠時數必須在 %d 到 %d 小時之間", MIN_SLEEP_HOURS, MAX_SLEEP_HOURS));
            }
            return new ValidationResult(true, "睡眠時數有效");
        } catch (NumberFormatException e) {
            return new ValidationResult(false, "請輸入有效的睡眠時數");
        }
    }
    
    /**
     * 驗證日期
     */
    public static ValidationResult validateDate(String dateText, String format) {
        if (dateText == null || dateText.trim().isEmpty()) {
            return new ValidationResult(false, "日期不能為空");
        }
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            LocalDate date = LocalDate.parse(dateText, formatter);
            
            // 檢查日期不能是未來日期
            if (date.isAfter(LocalDate.now())) {
                return new ValidationResult(false, "日期不能是未來日期");
            }
            
            return new ValidationResult(true, "日期有效");
        } catch (DateTimeParseException e) {
            return new ValidationResult(false, String.format("請輸入正確的日期格式 (%s)", format));
        }
    }
    
    /**
     * 驗證結果類別
     */
    public static class ValidationResult {
        private final boolean isValid;
        private final String message;
        
        public ValidationResult(boolean isValid, String message) {
            this.isValid = isValid;
            this.message = message;
        }
        
        public boolean isValid() {
            return isValid;
        }
        
        public String getMessage() {
            return message;
        }
    }
}
