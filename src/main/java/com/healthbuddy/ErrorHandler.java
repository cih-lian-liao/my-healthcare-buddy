package com.healthbuddy;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 統一的錯誤處理類別
 * 提供錯誤日誌記錄和用戶友好的錯誤提示
 */
public class ErrorHandler {
    
    private static final Logger logger = Logger.getLogger("MyHealthcareBuddy");
    private static boolean loggerInitialized = false;
    
    /**
     * 初始化日誌系統
     */
    private static void initializeLogger() {
        if (!loggerInitialized) {
            try {
                FileHandler fileHandler = new FileHandler("healthcare-buddy.log", true);
                SimpleFormatter formatter = new SimpleFormatter();
                fileHandler.setFormatter(formatter);
                logger.addHandler(fileHandler);
                logger.setUseParentHandlers(false); // 防止輸出到控制台
                loggerInitialized = true;
            } catch (Exception e) {
                // 如果無法創建文件日誌，至少記錄到控制台
                System.err.println("無法初始化文件日誌: " + e.getMessage());
            }
        }
    }
    
    /**
     * 處理數據庫錯誤
     */
    public static void handleDatabaseError(Component parent, SQLException e, String operation) {
        initializeLogger();
        
        String message = "數據庫錯誤: " + getDatabaseErrorMessage(e);
        String title = "數據庫錯誤";
        
        logger.severe(String.format("[%s] 數據庫錯誤 [%s]: %s", 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            operation, e.getMessage()));
        
        showErrorDialog(parent, message, title);
    }
    
    /**
     * 處理用戶輸入錯誤
     */
    public static void handleValidationError(Component parent, String message) {
        initializeLogger();
        
        String title = "輸入錯誤";
        
        logger.warning(String.format("[%s] 驗證錯誤: %s", 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            message));
        
        showWarningDialog(parent, message, title);
    }
    
    /**
     * 處理系統錯誤
     */
    public static void handleSystemError(Component parent, Exception e, String operation) {
        initializeLogger();
        
        String message = "系統錯誤: " + e.getMessage();
        String title = "系統錯誤";
        
        logger.severe(String.format("[%s] 系統錯誤 [%s]: %s", 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            operation, e.getMessage()));
        
        // 開發模式顯示詳細錯誤，生產模式只顯示一般訊息
        if (isDevelopmentMode()) {
            showErrorDialog(parent, message, title);
            e.printStackTrace();
        } else {
            showErrorDialog(parent, "發生系統錯誤，請稍後再試。", title);
        }
    }
    
    /**
     * 顯示成功訊息
     */
    public static void showSuccessMessage(Component parent, String message) {
        initializeLogger();
        
        logger.info(String.format("[%s] 操作成功: %s", 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            message));
        
        JOptionPane.showMessageDialog(parent,
            message,
            "成功",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * 顯示警告訊息
     */
    public static void showWarningDialog(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent,
            message,
            title,
            JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * 顯示詢問對話框
     */
    public static int showConfirmDialog(Component parent, String message, String title) {
        return JOptionPane.showConfirmDialog(parent,
            message,
            title,
            JOptionPane.YES_NO_OPTION);
    }
    
    /**
     * 獲取數據庫錯誤的用戶友好訊息
     */
    private static String getDatabaseErrorMessage(SQLException e) {
        String sqlState = e.getSQLState();
        int errorCode = e.getErrorCode();
        
        // 根據錯誤代碼返回用戶友好的訊息
        switch (errorCode) {
            case 1: // SQLite constraint violation
                return "數據違反完整性約束，請檢查輸入的數據";
            case 2067: // SQLite cannot read database
                return "無法讀取數據庫，請檢查數據庫文件";
            case 38503: // SQLite lock timeout
                return "數據庫正在被使用，請稍後再試";
            default:
                return "數據庫操作失敗，錯誤代碼: " + errorCode;
        }
    }
    
    /**
     * 檢查是否為開發模式
     */
    private static boolean isDevelopmentMode() {
        String mode = System.getProperty("app.mode", "production");
        return "development".equalsIgnoreCase(mode);
    }
    
    /**
     * 顯示帶有詳細信息的錯誤對話框
     */
    private static void showErrorDialog(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent,
            message,
            title,
            JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * 記錄應用程式啟動信息
     */
    public static void logApplicationStart() {
        initializeLogger();
        logger.info(String.format("[%s] 應用程式啟動成功", 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
    }
    
    /**
     * 記錄應用程式關閉信息
     */
    public static void logApplicationShutdown() {
        initializeLogger();
        logger.info(String.format("[%s] 應用程式正常關閉", 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
    }
}
