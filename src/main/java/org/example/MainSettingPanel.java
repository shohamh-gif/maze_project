package org.example;

import javax.swing.*;
import java.awt.*;

public class MainSettingPanel extends JPanel {
    private JTextField widthField;
    private JTextField heightField;
    private JButton getMazeButton;
    private JButton refreshButton;
    private JLabel configLabel;

    public MainSettingPanel(int x, int y, int windowWidth, int windowHeight) {
        this.setBounds(x, y, windowWidth, windowHeight);
        this.setLayout(null);
        // --- אזור הגדרות שרת (שלב 1: טקסט זמני וכפתור רענון) ---
        this.configLabel = new JLabel("הגדרות מהשרת יופיעו כאן...");
        this.add(configLabel);

        this.refreshButton = new JButton("Refresh Setting");
        this.add(refreshButton);

        this.add(new JLabel("Width:"));
        this.widthField = new JTextField("30", 5); // 30 זה טקסט התחלתי, 5 זה רוחב התיבה
        this.add(widthField);

        this.add(new JLabel("Height:"));
        this.heightField = new JTextField("30", 5);
        this.add(heightField);

        this.getMazeButton = new JButton("GET MAZE");
        this.add(getMazeButton);
    }
}