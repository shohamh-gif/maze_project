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

        // הגדרת פריסת המסך לחלקים (מרכז ותחתית)
        this.setLayout(new BorderLayout());

        // יצירת קנבס הציור מהמחלקה החיצונית והעברת כל הנתונים בבנאי
        MazeCanvas mazeCanvas = new MazeCanvas(mazeMatrix, wallColor, pathColor, drawGrid, gridColor);

        // הוספת הקנבס למרכז - הוא יתקווץ ויתרחב אוטומטית לפי גודל החלון!
        this.add(mazeCanvas, BorderLayout.CENTER);

        // יצירת אזור הכפתורים למטה
        JButton checkSolution = new JButton("בדוק פיתרון");
        Main.styleButton(checkSolution, Main.DEEP_PINK);
        checkSolution.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(checkSolution);

        // הוספת פאנל הכפתור לחלק התחתון של המסך
        this.add(buttonPanel, BorderLayout.SOUTH);
    }
}