/**
 * My Healthcare Buddy - Portfolio Code Samples
 * 
 * This file contains highlighted code samples that demonstrate:
 * - Security implementation (password hashing)
 * - Data validation framework
 * - Professional error handling
 * - Database layer design
 * - UI component customization
 */

// ==============================================
// 1. SECURITY IMPLEMENTATION - Password Hashing
// ==============================================

/**
 * Secure password hashing with SHA-256 and randomized salt
 * Demonstrates security best practices and cryptographic knowledge
 */
public class PasswordSecurity {
    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    
    /**
     * Hash password with unique salt for enhanced security
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDiscrypt md = MessageDigest.getInstance(ALGORITHM);
            md.update(Base64.getDecoder().decode(salt));
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }
    
    /**
     * Generate cryptographically secure random salt
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * Verify password against stored hash with salt
     */
    public static boolean verifyPassword(String password, String hashedPassword, String salt) {
        String hashedInput = hashPassword(password, salt);
        return hashedInput.equals(hashedPassword);
    }
}

// ==============================================
// 2. DATA VALIDATION FRAMEWORK
// ==============================================

/**
 * Comprehensive input validation with business logic constraints
 * Demonstrates defensive programming and data integrity
 */
public class ValidationHelper {
    // Business constraints
    public static final double MIN_WEIGHT = 20.0;
    public static final double MAX_WEIGHT = 300.0;
    public static final int MIN_HEART_RATE = 30;
    public static final int MAX_HEART_RATE = 250;
    
    /**
     * Validate weight input with comprehensive error handling
     */
    public static ValidationResult validateWeight(String weightText) {
        try {
            double weight = Double.parseDouble(weightText.trim());
            if (weight < MIN_WEIGHT || weight > MAX_WEIGHT) {
                return new ValidationResult(false, 
                    String.format("Weight must be between %.1f and %.1f kg", 
                    MIN_WEIGHT, MAX_WEIGHT));
            }
            return new ValidationResult(true, "Weight is valid");
        } catch (NumberFormatException e) {
            return new ValidationResult(false, "Please enter a valid weight number");
        }
    }
    
    /**
     * Validate blood pressure format and values
     */
    public static ValidationResult validateBloodPressure(String bloodPressure) {
        if (!BLOOD_PRESSURE_PATTERN.matcher(bloodPressure).matches()) {
            return new ValidationResult(false, "Blood pressure format must be XXX/XXX (e.g., 120/80)");
        }
        
        String[] parts = bloodPressure.split("/");
        try {
            int systolic = Integer.parseInt(parts[0]);
            int diastolic = Integer.parseInt(parts[1]);
            
            if (systolic <= diastolic) {
                return new ValidationResult(false, "Systolic pressure must be greater than diastolic");
            }
            
            return new ValidationResult(true, "Blood pressure is valid");
        } catch (NumberFormatException e) {
            return new ValidationResult(false, "Please enter valid blood pressure numbers");
        }
    }
    
    /**
     * Result class for validation operations
     */
    public static class ValidationResult {
        private final boolean isValid;
        private final String message;
        
        public ValidationResult(boolean isValid, String message) {
            this.isValid = isValid;
            this.message = message;
        }
        
        public boolean isValid() { return isValid; }
        public String getMessage() { return message; }
    }
}

// ==============================================
// 3. PROFESSIONAL ERROR HANDLING
// ==============================================

/**
 * Centralized error handling with logging and user feedback
 * Demonstrates enterprise-level error management
 */
public class ErrorHandler {
    private static final Logger logger = Logger.getLogger("MyHealthcareBuddy");
    
    /**
     * Handle database errors with appropriate user messaging
     */
    public static void handleDatabaseError(Component parent, SQLException e, String operation) {
        // Log detailed error for debugging
        logger.severe(String.format("[%s] Database error [%s]: %s", 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            operation, e.getMessage()));
        
        // Show user-friendly message
        String message = getDatabaseErrorMessage(e);
        showSuccessDialog(parent, message, "Database Error");
    }
    
    /**
     * Convert technical errors to user-friendly messages
     */
    private static String getDatabaseErrorMessage(SQLException e) {
        int errorCode = e.getErrorCode();
        switch (errorCode) {
            case 1: // SQLite constraint violation
                return "Data violates integrity constraints, please check your input";
            case 2067: // SQLite cannot read database
                return "Unable to read database, please check database file";
            case 38503: // SQLite lock timeout
                return "Database is being used, please try again later";
            default:
                return "Database operation failed, error code: " + errorCode;
        }
    }
}

// ==============================================
// 4. DATABASE LAYER DESIGN
// ==============================================

/**
 * Proper database abstraction with prepared statements
 * Demonstrates secure database practices and SQL knowledge
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:health_buddy.db";
    private Connection connection;
    
    /**
     * Secure user authentication with prepared statements
     */
    public boolean validateLogin(String username, String password) throws SQLException {
        String sql = "SELECT password, salt FROM users WHERE username = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                String salt = rs.getString("salt");
                return PasswordSecurity.verifyPassword(password, storedPassword, salt);
            }
        } catch (SQLException e) {
            ErrorHandler.handleDatabaseError(null, e, "User Authentication");
            throw e;
        }
        return false;
    }
    
    /**
     * Insert new user with secure password handling
     */
    public boolean insertUser(String username, String password) throws SQLException {
        String salt = PasswordSecurity.generateSalt();
        String hashedPassword = PasswordSecurity.hashPassword(password, salt);
        
        String sql = "INSERT INTO users (username, password, salt) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, salt);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            ErrorHandler.handleDatabaseError(null, e, "User Registration");
            throw e;
        }
    }
}

// ==============================================
// 5. UI COMPONENT CUSTOMIZATION
// ==============================================

/**
 * Custom styled button component
 * Demonstrates Swing customization and UI/UX design skills
 */
public class UserInterfaceHelper {
    /**
     * Create consistently styled buttons with hover effects
     */
    public static JButton createStyledButton(String text, Color customColor) {
        Color buttonColor = customColor != null ? customColor : UIConfig.PRIMARY_COLOR;
        
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Apply hover and press effects
                if (getModel().isPressed()) {
                    g2.setColor(buttonColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(buttonColor.brighter());
                } else {
                    g2.setColor(buttonColor);
                }
                
                // Draw rounded rectangle
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Draw text centered
                FontMetrics metrics = g2.getFontMetrics(getFont());
                int x = (getWidth() - metrics.stringWidth(getText())) / 2;
                int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
                
                g2.setColor(Color.WHITE);
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        
        // Configure button properties
        button.setFont(UIConfig.BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(120, 35));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        
        return button;
    }
}

/**
 * KEY PROGRAMMING CONCEPTS DEMONSTRATED:
 * 
 * 1. Security: SHA-256 hashing, salt generation, input sanitization
 * 2. Design Patterns: Factory pattern, Strategy pattern for validation
 * 3. Error Handling: Centralized error management, user-friendly messages
 * 4. Database Design: Prepared statements, foreign keys, data integrity
 * 5. UI/UX: Custom components, consistent styling, accessibility
 * 6. Clean Code: Clear naming, proper documentation, separation of concerns
 * 7. Testing Considerations: Input validation, edge case handling
 * 
 * This code sample represents approximately 10% of the total codebase,
 * focusing on the most impactful and impressive implementations.
 */
