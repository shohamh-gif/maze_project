package org.example.gui;

import org.example.Main;

import javax.swing.*;
import java.awt.*;

// המחלקה הזאת מתפקדת בתור ה"ציירת" של הפרויקט - אחראית רק על הציור הגרפי של המבוך במסך
public class MazeCanvas extends JPanel {

    private boolean[][] mazeMatrix; // המבנה המקורי של המבוך
    private boolean[][] solutionMatrix; // מטריצה נפרדת המסמנת את הדרך החוצה (לצורך אנימציה)

    private Color wallColor;
    private Color pathColor;
    private boolean drawGrid;
    private Color gridColor;

    // בנאי המקבל את המבוך ואת הגדרות הציור שהגיעו מהשרת
    public MazeCanvas(boolean[][] mazeMatrix, Color wallColor, Color pathColor, boolean drawGrid, Color gridColor) {
        this.mazeMatrix = mazeMatrix;
        this.wallColor = wallColor;
        this.pathColor = pathColor;
        this.drawGrid = drawGrid;
        this.gridColor = gridColor;

        this.clearSolution(); // מאתחל את מטריצת הפתרון להיות ריקה בהתחלה
        this.setBackground(Main.LIGHT_PINK);
        this.setOpaque(true);
    }

    // מאפסת את סימון הפתרון. נקראת בתחילת התוכנית וגם כאשר רוצים להריץ אנימציה מחדש
    public void clearSolution() {
        if (this.mazeMatrix == null || this.mazeMatrix.length == 0 || this.mazeMatrix[0].length == 0) {
            return;
        }

        int rows = this.mazeMatrix.length;
        int cols = this.mazeMatrix[0].length;

        // מערך בוליאני חדש מאותחל אוטומטית ל-false בכל התאים
        this.solutionMatrix = new boolean[rows][cols];
        this.repaint(); // מורה לתוכנה לצייר מחדש את המסך כשהוא נקי
    }

    // מסמנת תא ספציפי (נקודת ציון) כחלק מהפתרון. משמשת לאנימציה צעד-אחר-צעד
    public void markSolutionCell(Point point) {
        if (point == null || this.solutionMatrix == null) {
            return;
        }
        int row = point.y;
        int col = point.x;

        // בדיקות בטיחות כדי לא לחרוג מגבולות המערך
        if (row < 0 || row >= this.solutionMatrix.length) {
            return;
        }

        if (col < 0 || col >= this.solutionMatrix[0].length) {
            return;
        }

        this.solutionMatrix[row][col] = true;
        this.repaint(); // מצייר מחדש את המסך, הפעם התא הזה יקבל צבע שונה (ראה drawCells)
    }

    // הפונקציה המובנית של Java שמציירת בפועל את המבוך על המסך
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (this.mazeMatrix == null || this.mazeMatrix.length == 0 || this.mazeMatrix[0].length == 0) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g;

        int rows = this.mazeMatrix.length;
        int cols = this.mazeMatrix[0].length;

        // חישוב אזור בטוח: שומרים מרווח של 2 פיקסלים כדי שקווי הרשת לא ייחתכו בקצוות המסך
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

        // דחיפת המבוך פיקסל אחד פנימה (לתוך האזור הבטוח) ומיקוד במרכז
        double startX = 1 + (availableWidth - mazeWidth) / 2.0;
        double startY = 1 + (availableHeight - mazeHeight) / 2.0;

        // ציור הריבועים
        this.drawCells(g2, startX, startY, cellSize, rows, cols);

        // ציור הרשת רק אם השרת אישר זאת, ורק אם התאים לא קטנים מדי (כדי לא לצבוע הכל ברשת)
        boolean shouldDrawGrid = this.drawGrid && cellSize >= 2;

        if (shouldDrawGrid) {
            this.drawGridLines(g2, startX, startY, cellSize, mazeWidth, mazeHeight, rows, cols);
        }
    }

    // מציירת את כל התאים של המבוך: קיר, מעבר לבן, או תא שהוא חלק מהפתרון
    private void drawCells(Graphics2D g2, double startX, double startY, double cellSize, int rows, int cols) {
        // כיבוי החלקת עקומות כדי שהריבועים ידבקו אחד לשני באופן אטום לחלוטין וללא שקיפויות ביניהם
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {

                // סדר העדיפויות לצביעה:
                // 1. קודם כל בודקים אם התא הזה הוא חלק ממסלול הפתרון (האנימציה)
                if (this.solutionMatrix != null && this.solutionMatrix[y][x]) {
                    g2.setColor(this.pathColor);
                }
                // 2. אם זה לא הפתרון, בודקים אם זה שביל רגיל
                else if (this.mazeMatrix[y][x]) {
                    g2.setColor(Color.WHITE);
                }
                // 3. אם זה לא זה ולא זה - זה קיר
                else {
                    g2.setColor(this.wallColor);
                }

                // מתמטיקה מדויקת לצביעת תאים בלי להשאיר חריצי רווחים
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

    // מציירת את קווי הרשת הדקים לאורך ולרוחב
    private void drawGridLines(Graphics2D g2, double startX, double startY, double cellSize,
                               double mazeWidth, double mazeHeight, int rows, int cols) {
        // הדלקת החלקת עקומות כדי שהקווים יהיו דקים ועדינים לעין
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(this.gridColor);
        g2.setStroke(new BasicStroke(0.5f)); // עובי קו דק ואסתטי של חצי פיקסל

        // ציור קווים אנכיים
        for (int x = 0; x <= cols; x++) {
            int lineX = (int) Math.round(startX + x * cellSize);

            g2.drawLine(
                    lineX,
                    (int) Math.round(startY),
                    lineX,
                    (int) Math.round(startY + mazeHeight)
            );
        }

        // ציור קווים אופקיים
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