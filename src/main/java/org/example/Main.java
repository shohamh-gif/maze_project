package org.example;

import org.example.gui.MainSettingPanel;

import javax.swing.*;

public class Main {
    public static final int WINDOW_WIDTH = 1000;
    public static final int WINDOW_HEIGHT = 700;

    public static void main(String[] args) {
        JFrame window = new JFrame("Maze Client");
        window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        window.setResizable(false);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setLayout(null);

        // יצירת הפאנל מתוך תיקיית ה-gui
        MainSettingPanel settingPanel = new MainSettingPanel(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        window.add(settingPanel);
        window.setVisible(true);
    }
}