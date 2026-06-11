package org.example;

import org.example.gui.MainSettingPanel;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static final int WINDOW_WIDTH = 1000;
    public static final int WINDOW_HEIGHT = 700;
    public static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Color DEEP_PINK = new Color(214, 51, 108);
    public static final Color SOFT_PINK = new Color(240, 98, 146);
    public static final Color LIGHT_PINK = new Color(253, 242, 244);
    public static final Color TEXT_DARK = new Color(60, 60, 60);
    public static final Color ERROR_COLOR = new Color(217, 48, 37);
    private static JFrame window;

    public static void styleButton(JButton button, Color bg) {
        button.setFont(LABEL_FONT);
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void showPanel(JPanel panel) {
        window.setContentPane(panel);
        window.revalidate();
        window.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            window = new JFrame("Maze Client");
            window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            window.setResizable(false);
            window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            window.setLocationRelativeTo(null);

            MainSettingPanel settingPanel = new MainSettingPanel(
                    0,
                    0,
                    WINDOW_WIDTH,
                    WINDOW_HEIGHT
            );

            showPanel(settingPanel);

            window.setVisible(true);
        });
    }
}