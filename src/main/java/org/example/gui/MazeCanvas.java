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

        this.setBackground(Main.LIGHT_PINK);
        this.setOpaque(true);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (this.mazeMatrix == null || this.mazeMatrix.length == 0 || this.mazeMatrix[0].length == 0) {
            return;
        }

        int rows = this.mazeMatrix.length;
        int cols = this.mazeMatrix[0].length;

        int panelWidth = this.getWidth();
        int panelHeight = this.getHeight();

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

        Graphics2D g2 = (Graphics2D) g;

        // --- שלב 1: צביעת התאים (fillRect) - עם החלקת עקומות כבויה! ---
        // זה התיקון הקריטי: אנחנו מכבים את ה-AA כדי שהריבועים יהיו חדים ויידבקו מושלם.
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {

                if (this.mazeMatrix[y][x]) {
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

                // צביעת התא (הם יידבקו אחד לשני בצורה מושלמת, בלי זיגזגים)
                g2.fillRect(pixelX, pixelY, width, height);
            }
        }

        // --- שלב 2: ציור הרשת - עם החלקת עקומות מופעלת! ---
        boolean shouldDrawGrid = this.drawGrid && cellSize >= 2;

        if (shouldDrawGrid) {
            // עכשיו, ורק עכשיו, אנחנו מדליקים את ה-AA כדי לקבל קווים דקים ויפים.
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(this.gridColor);
            g2.setStroke(new BasicStroke(0.5f)); // העובי הדק והיפה שרצינו

            // ציור קווים אנכיים (מלמעלה למטה)
            for (int x = 0; x <= cols; x++) {
                int lineX = (int) Math.round(startX + x * cellSize);
                g2.drawLine(
                        lineX,
                        (int) Math.round(startY),
                        lineX,
                        (int) Math.round(startY + mazeHeight)
                );
            }

            // ציור קווים אופקיים (משמאל לימין)
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
}