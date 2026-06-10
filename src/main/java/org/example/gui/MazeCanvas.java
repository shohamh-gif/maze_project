package org.example.gui;

import org.example.Main;

import javax.swing.*;
import java.awt.*;

// מחלקה זו אחראית אך ורק על הציור הפיזי של המבוך וקווי הרשת על גבי המסך
public class MazeCanvas extends JPanel {

    // המטריצה הלוגית והגדרות העיצוב
    private boolean[][] mazeMatrix;
    private Color wallColor;
    private Color pathColor;
    private boolean drawGrid;
    private Color gridColor;

    // בנאי המקבל את הגדרות העיצוב ושומר אותן
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

    // הפונקציה המובנית ב-Java שמציירת הכל על המסך
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (this.mazeMatrix == null || this.mazeMatrix.length == 0 || this.mazeMatrix[0].length == 0) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        int rows = this.mazeMatrix.length;
        int cols = this.mazeMatrix[0].length;

        // אזור בטוח (Safe Zone): שומרים פיקסל אחד מכל צד כדי שקווי הרשת החיצוניים לא ייחתכו!
        int availableWidth = this.getWidth() - 2;
        int availableHeight = this.getHeight() - 2;

        // חישוב גודל התא לפי האזור הבטוח
        double cellSize = Math.min(
                (double) availableWidth / cols,
                (double) availableHeight / rows
        );

        if (cellSize <= 0) return;

        double mazeWidth = cols * cellSize;
        double mazeHeight = rows * cellSize;

        // חישוב נקודת ההתחלה, כולל הסטה של פיקסל אחד פנימה (האזור הבטוח)
        double startX = 1 + (availableWidth - mazeWidth) / 2.0;
        double startY = 1 + (availableHeight - mazeHeight) / 2.0;

        // קריאה לפונקציות העזר שמבצעות את הציור בפועל
        this.drawCells(g2, startX, startY, cellSize, rows, cols);

        boolean shouldDrawGrid = this.drawGrid && cellSize >= 2;
        if (shouldDrawGrid) {
            this.drawGridLines(g2, startX, startY, cellSize, mazeWidth, mazeHeight, rows, cols);
        }
    }

    // פונקציית עזר: מציירת את הריבועים הפנימיים של המבוך בצורה אטומה
    private void drawCells(Graphics2D g2, double startX, double startY, double cellSize, int rows, int cols) {
        // כיבוי החלקת עקומות: מוודא שהריבועים יהיו חדים ויידבקו אחד לשני ללא חריצים שקופים
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {

                if (this.mazeMatrix[y][x]) {
                    g2.setColor(Color.WHITE);
                } else {
                    g2.setColor(this.wallColor);
                }

                // מתמטיקה מדויקת למניעת רווחים בין המשבצות
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

    // פונקציית עזר: מציירת את רשת הקווים הדקיקה מעל המבוך
    private void drawGridLines(Graphics2D g2, double startX, double startY, double cellSize, double mazeWidth, double mazeHeight, int rows, int cols) {
        // הדלקת החלקת עקומות: הופך את קווי הרשת לעדינים ודקים לעין
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(this.gridColor);
        g2.setStroke(new BasicStroke(0.5f)); // עובי קו דק מאוד ואסתטי

        // ציור שתי (קווים אנכיים)
        for (int x = 0; x <= cols; x++) {
            int lineX = (int) Math.round(startX + x * cellSize);
            g2.drawLine(
                    lineX,
                    (int) Math.round(startY),
                    lineX,
                    (int) Math.round(startY + mazeHeight)
            );
        }

        // ציור ערב (קווים אופקיים)
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