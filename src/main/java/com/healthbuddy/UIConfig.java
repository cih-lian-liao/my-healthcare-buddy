package com.healthbuddy;

import javax.swing.*;
import java.awt.*;

public class UIConfig {
    public static final Color PRIMARY_COLOR = new Color(100, 149, 237);
    public static final Color BACKGROUND_COLOR = new Color(240, 248, 255);

    public static final Font TITLE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 24);
    public static final Font SUBTITLE_FONT = new Font(Font.SANS_SERIF, Font.ITALIC, 14);
    public static final Font REGULAR_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 14);

    public static void setupUIDefaults() {
        // Set default fonts
        UIManager.put("Label.font", REGULAR_FONT);
        UIManager.put("TextField.font", REGULAR_FONT);
        UIManager.put("PasswordField.font", REGULAR_FONT);
        UIManager.put("Button.font", BUTTON_FONT);

        // Set default colors
        UIManager.put("Panel.background", BACKGROUND_COLOR);
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("PasswordField.background", Color.WHITE);
    }
}
