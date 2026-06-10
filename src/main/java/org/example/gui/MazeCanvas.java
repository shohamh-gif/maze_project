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

        int rows = mazeMatrix.length;
        int cols = mazeMatrix[0].length;

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        double cellSize = Math.min(
                (double) panelWidth / cols,
                (double) panelHeight / rows
        );

        if (cellSize <= 0) {
            return;
        }

        double mazeWidth = cols * cellSize;
        double mazeHeight = rows * cellSize;

        double startX = (panelWidth - mazeWidth) / 2.0;
        double startY = (panelHeight - mazeHeight) / 2.0;

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {

                if (mazeMatrix[y][x]) {
                    g.setColor(Color.WHITE);
                } else {
                    g.setColor(wallColor);
                }

                // --- התיקון לקווים הלבנים (עיגול מתמטי מושלם) ---
                // חישוב הפיקסל המדויק של תחילת המשבצת וסוף המשבצת (ללא חורים)
                int pixelX = (int) Math.round(startX + x * cellSize);
                int pixelY = (int) Math.round(startY + y * cellSize);
                int nextX = (int) Math.round(startX + (x + 1) * cellSize);
                int nextY = (int) Math.round(startY + (y + 1) * cellSize);

                int width = nextX - pixelX;
                int height = nextY - pixelY;

                g.fillRect(pixelX, pixelY, width, height);
            }
        }

        // ציור הרשת מותאם גם הוא לעיגול המדויק
        if (drawGrid && cellSize >= 4) {
            g.setColor(gridColor);

            for (int x = 0; x <= cols; x++) {
                int lineX = (int) Math.round(startX + x * cellSize);
                g.drawLine(
                        lineX,
                        (int) Math.round(startY),
                        lineX,
                        (int) Math.round(startY + mazeHeight)
                );
            }

            for (int y = 0; y <= rows; y++) {
                int lineY = (int) Math.round(startY + y * cellSize);
                g.drawLine(
                        (int) Math.round(startX),
                        lineY,
                        (int) Math.round(startX + mazeWidth),
                        lineY
                );
            }
        }
    }
}