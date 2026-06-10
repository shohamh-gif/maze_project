package org.example.gui;

import org.example.Main;

import javax.swing.*;
import java.awt.*;

public class MazeCanvas extends JPanel {

    private boolean[][] mazeMatrix;
    private Color wallColor;
    private Color pathColor;
    private boolean drawGrid;
    private Color gridColor;

    public MazeCanvas(
            boolean[][] mazeMatrix,
            Color wallColor,
            Color pathColor,
            boolean drawGrid,
            Color gridColor
    ) {
        this.mazeMatrix = mazeMatrix;
        this.wallColor = wallColor;
        this.pathColor = pathColor;
        this.drawGrid = drawGrid;
        this.gridColor = gridColor;

        setBackground(Main.LIGHT_PINK);
        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (mazeMatrix == null || mazeMatrix.length == 0 || mazeMatrix[0].length == 0) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;

        int rows = mazeMatrix.length;
        int cols = mazeMatrix[0].length;

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int cellSize = Math.min(
                panelWidth / cols,
                panelHeight / rows
        );

        if (cellSize < 1) {
            return;
        }

        int mazeWidth = cols * cellSize;
        int mazeHeight = rows * cellSize;

        int startX = (panelWidth - mazeWidth) / 2;
        int startY = (panelHeight - mazeHeight) / 2;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {

                int x = startX + col * cellSize;
                int y = startY + row * cellSize;

                if (mazeMatrix[row][col]) {
                    g2.setColor(Color.WHITE);
                } else {
                    g2.setColor(wallColor);
                }

                g2.fillRect(x, y, cellSize, cellSize);
            }
        }

        if (drawGrid) {
            g2.setColor(gridColor);
            g2.setStroke(new BasicStroke(1));

            for (int col = 0; col <= cols; col++) {
                int x = startX + col * cellSize;
                g2.drawLine(x, startY, x, startY + mazeHeight);
            }

            for (int row = 0; row <= rows; row++) {
                int y = startY + row * cellSize;
                g2.drawLine(startX, y, startX + mazeWidth, y);
            }
        }
    }
}