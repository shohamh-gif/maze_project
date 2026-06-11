package org.example.gui;

import org.example.Main;

import javax.swing.*;
import java.awt.*;

// המחלקה הזאת אחראית רק על הציור של המבוך במסך
public class MazeCanvas extends JPanel {

    private boolean[][] mazeMatrix;
    private boolean[][] solutionMatrix;

    private Color wallColor;
    private Color pathColor;
    private boolean drawGrid;
    private Color gridColor;

    // מקבלת את המבוך ואת הגדרות הציור מהשרת
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

        this.clearSolution();

        this.setBackground(Main.LIGHT_PINK);
        this.setOpaque(true);
    }

    // מאפסת את סימון הפתרון כדי שאפשר יהיה להריץ אנימציה מחדש
    public void clearSolution() {
        if (this.mazeMatrix == null || this.mazeMatrix.length == 0 || this.mazeMatrix[0].length == 0) {
            return;
        }

        int rows = this.mazeMatrix.length;
        int cols = this.mazeMatrix[0].length;

        this.solutionMatrix = new boolean[rows][cols];

        repaint();
    }

    // מסמנת תא אחד כחלק מהפתרון וצובעת אותו מחדש
    public void markSolutionCell(Point point) {
        if (point == null || this.solutionMatrix == null) {
            return;
        }

        int row = point.y;
        int col = point.x;

        if (row < 0 || row >= this.solutionMatrix.length) {
            return;
        }

        if (col < 0 || col >= this.solutionMatrix[0].length) {
            return;
        }

        this.solutionMatrix[row][col] = true;
        repaint();
    }

    // הפונקציה שמציירת בפועל את המבוך על המסך
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (this.mazeMatrix == null || this.mazeMatrix.length == 0 || this.mazeMatrix[0].length == 0) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;

        int rows = this.mazeMatrix.length;
        int cols = this.mazeMatrix[0].length;

        int availableWidth = this.getWidth() - 2;
        int availableHeight = this.getHeight() - 2;

        double cellSize = Math.min(
                (double) availableWidth / cols,
                (double) availableHeight / rows
        );

        if (cellSize <= 0) {
            return;
        }

        double mazeWidth = cols * cellSize;
        double mazeHeight = rows * cellSize;

        double startX = 1 + (availableWidth - mazeWidth) / 2.0;
        double startY = 1 + (availableHeight - mazeHeight) / 2.0;

        this.drawCells(g2, startX, startY, cellSize, rows, cols);

        boolean shouldDrawGrid = this.drawGrid && cellSize >= 2;

        if (shouldDrawGrid) {
            this.drawGridLines(g2, startX, startY, cellSize, mazeWidth, mazeHeight, rows, cols);
        }
    }

    // מציירת את כל התאים של המבוך: קיר, מעבר או תא שהוא חלק מהפתרון
    private void drawCells(Graphics2D g2, double startX, double startY, double cellSize, int rows, int cols) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {

                if (this.solutionMatrix != null && this.solutionMatrix[y][x]) {
                    g2.setColor(this.pathColor);
                } else if (this.mazeMatrix[y][x]) {
                    g2.setColor(Color.WHITE);
                } else {
                    g2.setColor(this.wallColor);
                }

                int pixelX = (int) Math.round(startX + x * cellSize);
                int pixelY = (int) Math.round(startY + y * cellSize);

                int nextX = (int) Math.round(startX + (x + 1) * cellSize);
                int nextY = (int) Math.round(startY + (y + 1) * cellSize);

                int width = nextX - pixelX;
                int height = nextY - pixelY;

                g2.fillRect(pixelX, pixelY, width, height);
            }
        }
    }

    // מציירת את קווי הרשת אם השרת הגדיר שצריך להציג אותם
    private void drawGridLines(
            Graphics2D g2,
            double startX,
            double startY,
            double cellSize,
            double mazeWidth,
            double mazeHeight,
            int rows,
            int cols
    ) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(this.gridColor);
        g2.setStroke(new BasicStroke(0.5f));

        for (int x = 0; x <= cols; x++) {
            int lineX = (int) Math.round(startX + x * cellSize);

            g2.drawLine(
                    lineX,
                    (int) Math.round(startY),
                    lineX,
                    (int) Math.round(startY + mazeHeight)
            );
        }

        for (int y = 0; y <= rows; y++) {
            int lineY = (int) Math.round(startY + y * cellSize);

            g2.drawLine(
                    (int) Math.round(startX),
                    lineY,
                    (int) Math.round(startX + mazeWidth),
                    lineY
            );
        }
    }
}