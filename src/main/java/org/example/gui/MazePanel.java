package org.example.gui;

import org.example.Main;

import javax.swing.*;
import java.awt.*;

public class MazePanel extends JPanel {
    private static final Color TRANSITION_COLOR = Color.WHITE;
    private static final int CELL_SIZE = 20;

    private boolean[][] mazeMatrix;
    private Color wallColor;
    private Color pathColor;
    private boolean drawGrid;
    private Color gridColor;
    private int animationDelay;

    public MazePanel( boolean[][] mazeMatrix, Color wallColor, Color pathColor,boolean drawGrid,
                     Color gridColor,  int animationDelay){
        this.mazeMatrix = mazeMatrix;
        this.wallColor = wallColor;
        this.pathColor = pathColor;
        this.drawGrid = drawGrid;
        this.gridColor = gridColor;
        this.animationDelay = animationDelay;

        JButton checkSolution = new JButton("צור מבוך חדש");
        Main.styleButton(checkSolution, Main.DEEP_PINK);
        checkSolution.setFont(new Font("Segoe UI", Font.BOLD, 15));
        checkSolution.setBounds(100, 50, 20, 10);
        this.add(checkSolution);


    }
}
