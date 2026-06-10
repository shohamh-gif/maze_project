package org.example.gui;

import org.example.Main;
import javax.swing.*;
import java.awt.*;

public class MazePanel extends JPanel {

    private final JPanel previousPanel;
    private int animationDelay; // נשמור לשלב האנימציה של הפתרון

    public MazePanel(JPanel previousPanel, boolean[][] mazeMatrix, Color wallColor, Color pathColor, boolean drawGrid, Color gridColor, int animationDelay) {
        this.previousPanel = previousPanel;
        this.animationDelay = animationDelay;

        // הגדרת פריסת המסך לחלקים (למעלה, מרכז ותחתית)
        this.setLayout(new BorderLayout());

        // --- 1. יצירת אזור עליון משמאל לכפתור החזור ---
        // FlowLayout.LEFT דוחף את כל מה שבתוכו לצד שמאל
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(true);
        topPanel.setBackground(Main.LIGHT_PINK); // צבע רקע תואם לפאנל התחתון

        JButton backButton = new JButton("חזור להגדרות");
        Main.styleButton(backButton, Main.DEEP_PINK);
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // פעולת הלחיצה על כפתור חזור
        backButton.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (topFrame != null) {
                topFrame.setContentPane(previousPanel);
                topFrame.revalidate();
                topFrame.repaint();
            }
        });

        topPanel.add(backButton);
        // הוספת הפאנל העליון לחלק הצפוני של המסך
        this.add(topPanel, BorderLayout.NORTH);


        // --- 2. קנבס הציור של המבוך ---
        MazeCanvas mazeCanvas = new MazeCanvas(mazeMatrix, wallColor, pathColor, drawGrid, gridColor);
        // הוספת הקנבס למרכז - יתפוס את כל השטח שבין הלמעלה ללמטה
        this.add(mazeCanvas, BorderLayout.CENTER);


        // --- 3. יצירת אזור הכפתורים למטה ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(true);
        buttonPanel.setBackground(Main.LIGHT_PINK);

        JButton checkSolution = new JButton("בדוק פיתרון");
        Main.styleButton(checkSolution, Main.DEEP_PINK);
        checkSolution.setFont(new Font("Segoe UI", Font.BOLD, 15));

        buttonPanel.add(checkSolution);
        // הוספת פאנל הכפתור לחלק התחתון של המסך
        this.add(buttonPanel, BorderLayout.SOUTH);
    }
}