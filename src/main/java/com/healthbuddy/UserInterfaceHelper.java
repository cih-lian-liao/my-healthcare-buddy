package com.healthbuddy;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * 用戶界面輔助類別
 * 提供統一的UI創建和樣式方法
 */
public class UserInterfaceHelper {
    
    /**
     * 創建統一的標題面板
     */
    public static JPanel createTitlePanel(String title) {
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIConfig.TITLE_FONT);
        titleLabel.setForeground(UIConfig.PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        return titlePanel;
    }
    
    /**
     * 創建帶有標題的邊框
     */
    public static TitledBorder createTitledBorder(String title) {
        Border lineBorder = BorderFactory.createLineBorder(UIConfig.PRIMARY_COLOR);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
            lineBorder, title, TitledBorder.LEFT, TitledBorder.TOP,
            UIConfig.REGULAR_FONT, UIConfig.PRIMARY_COLOR);
        return titledBorder;
    }
    
    /**
     * 創建帶有樣式的按鈕
     */
    public static JButton createStyledButton(String text, Color customColor) {
        Color buttonColor = customColor != null ? customColor : UIConfig.PRIMARY_COLOR;
        
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(buttonColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(buttonColor.brighter());
                } else {
                    g2.setColor(buttonColor);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                FontMetrics metrics = g2.getFontMetrics(getFont());
                int x = (getWidth() - metrics.stringWidth(getText())) / 2;
                int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
                
                g2.setColor(Color.WHITE);
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        
        button.setFont(UIConfig.BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(120, 35));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        
        return button;
    }
    
    /**
     * 創建帶有樣式的按鈕（使用默認顏色）
     */
    public static JButton createStyledButton(String text) {
        return createStyledButton(text, null);
    }
    
    /**
     * 創建帶有樣式的文本字段
     */
    public static JTextField createStyledTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setFont(UIConfig.REGULAR_FONT);
        textField.setBackground(Color.WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return textField;
    }
    
    /**
     * 創建帶有樣式的密碼字段
     */
    public static JPasswordField createStyledPasswordField(int columns) {
        JPasswordField passwordField = new JPasswordField(columns);
        passwordField.setFont(UIConfig.REGULAR_FONT);
        passwordField.setBackground(Color.WHITE);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return passwordField;
    }
    
    /**
     * 創建帶有樣式的標籤
     */
    public static JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConfig.REGULAR_FONT);
        label.setForeground(new Color(70, 70, 70));
        return label;
    }
    
    /**
     * 創建分隔線
     */
    public static JSeparator createSeparator() {
        JSeparator separator = new JSeparator();
        separator.setBackground(UIConfig.PRIMARY_COLOR);
        separator.setForeground(UIConfig.PRIMARY_COLOR);
        return separator;
    }
    
    /**
     * 居中對齊組件（僅適用於 JComponent）
     */
    public static void centerComponent(Component component) {
        if (component instanceof JComponent) {
            ((JComponent) component).setAlignmentX(Component.CENTER_ALIGNMENT);
        }
    }
    
    /**
     * 設置左對齊（僅適用於 JComponent）
     */
    public static void leftAlignComponent(Component component) {
        if (component instanceof JComponent) {
            ((JComponent) component).setAlignmentX(Component.LEFT_ALIGNMENT);
        }
    }
    
    /**
     * 創建信息面板
     */
    public static JPanel createInfoPanel(String iconText, String title, String description) {
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 圖標標籤
        JLabel iconLabel = new JLabel(iconText);
        iconLabel.setFont(new Font(UIConfig.REGULAR_FONT.getName(), UIConfig.REGULAR_FONT.getStyle(), 24));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // 內容面板
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIConfig.BUTTON_FONT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font(UIConfig.REGULAR_FONT.getName(), UIConfig.REGULAR_FONT.getStyle(), 10));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        contentPanel.add(descLabel);
        
        infoPanel.add(iconLabel, BorderLayout.WEST);
        infoPanel.add(contentPanel, BorderLayout.CENTER);
        
        return infoPanel;
    }
    
    /**
     * 創建工具提示
     */
    public static void setTooltip(JComponent component, String text) {
        component.setToolTipText(text);
    }
    
    /**
     * 顯示載入對話框
     */
    public static JDialog createLoadingDialog(JFrame parent, String message) {
        JDialog dialog = new JDialog(parent, "載入中...", true);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setSize(200, 100);
        dialog.setLocationRelativeTo(parent);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(message, SwingConstants.CENTER), BorderLayout.CENTER);
        dialog.add(panel);
        
        return dialog;
    }
}
