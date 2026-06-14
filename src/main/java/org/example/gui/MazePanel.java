package org.example.gui;

import org.example.Main;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List; // הורדנו את Queue ו-LinkedList!

// המחלקה הזאת מייצגת את החלון השלם של המבוך: הכותרת למעלה, הקנבס באמצע, והכפתורים למטה
public class MazePanel extends JPanel {

    // השינוי לאפשרות 2: שומרים פעולה (Runnable) במקום לשמור את הפאנל הקודם
    private final Runnable onBackAction;
    private final int animationDelay; // זמן ההמתנה בין צעד לצעד באנימציה
    private final boolean[][] mazeMatrix;

    private JLabel statusLabel;
    private MazeCanvas mazeCanvas;
    private JButton checkSolutionButton;
    private Timer animationTimer; // אחראי על ניהול הזמנים של אנימציית הפתרון

    // הבנאי מקבל את הפעולה (מה לעשות כשלוחצים חזור) ואת ההגדרות, ובונה את הממשק
    public MazePanel(
            Runnable onBackAction,
            boolean[][] mazeMatrix,
            Color wallColor,
            Color pathColor,
            boolean drawGrid,
            Color gridColor,
            int animationDelay
    ) {
        this.onBackAction = onBackAction;
        this.animationDelay = animationDelay;
        this.mazeMatrix = mazeMatrix;

        this.setupUI(wallColor, pathColor, drawGrid, gridColor);
    }

    // בונה את כל המבנה של המסך: עליון, מרכזי ותחתון
    private void setupUI(Color wallColor, Color pathColor, boolean drawGrid, Color gridColor) {
        this.setLayout(new BorderLayout());
        this.setBackground(Main.LIGHT_PINK);
        this.setOpaque(true);

        this.add(this.createTopPanel(), BorderLayout.NORTH);

        this.mazeCanvas = new MazeCanvas(this.mazeMatrix, wallColor, pathColor, drawGrid, gridColor);
        this.add(this.mazeCanvas, BorderLayout.CENTER);

        this.add(this.createButtonPanel(), BorderLayout.SOUTH);
    }

    // יוצר את החלק העליון שבו מוצגת הודעת סטטוס דינמית למשתמש (במקום חלונות קופצים)
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Main.LIGHT_PINK);
        // שמירת גובה קבוע מבטיחה שהמבוך לא "יידבק" ללמעלה כשהתווית ריקה
        topPanel.setPreferredSize(new Dimension(Main.WINDOW_WIDTH, 45));

        this.statusLabel = new JLabel(" ", JLabel.CENTER);
        this.statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        this.statusLabel.setForeground(Main.TEXT_DARK);

        topPanel.add(this.statusLabel, BorderLayout.CENTER);

        return topPanel;
    }

    // יוצר את כפתור החזרה ואת כפתור פתרון המבוך בתחתית המסך
    private JPanel createButtonPanel() {
        JButton backButton = new JButton("חזור");
        Main.styleButton(backButton, Main.SOFT_PINK);
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        backButton.addActionListener(e -> this.handleBackButton());

        this.checkSolutionButton = new JButton("בדוק פתרון");
        Main.styleButton(this.checkSolutionButton, Main.DEEP_PINK);
        this.checkSolutionButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        this.checkSolutionButton.addActionListener(e -> this.handleCheckSolution());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Main.LIGHT_PINK);
        buttonPanel.add(backButton);
        buttonPanel.add(this.checkSolutionButton);

        return buttonPanel;
    }

    // פעולה בעת לחיצה על "חזור" - עוצרת אנימציות רצות ומריצה את פעולת החזרה שקיבלנו מהמסך הקודם
    private void handleBackButton() {
        this.stopAnimationIfNeeded();
        // מריצים את ה-Runnable! קוד סופר-נקי בלי שום צורך ב-instanceof
        this.onBackAction.run();
    }

    // פעולה בעת לחיצה על "בדוק פתרון" - מחשבת מסלול ומפעילה את האנימציה
    private void handleCheckSolution() {
        // מניעת לחיצות כפולות
        this.checkSolutionButton.setEnabled(false);
        this.mazeCanvas.clearSolution();

        this.statusLabel.setText("מחשב מסלול...");
        this.statusLabel.setForeground(Main.TEXT_DARK);

        // הפעלת אלגוריתם מציאת המסלול
        List<Point> path = this.findPathBFS();

        // טיפול במקרה של ללא מוצא
        if (path == null) {
            this.statusLabel.setForeground(Main.ERROR_COLOR);
            this.statusLabel.setText("אין פתרון");
            this.checkSolutionButton.setEnabled(true);
            return;
        }

        // אם יש פתרון, מתחילים להריץ את האנימציה על המסך
        this.statusLabel.setForeground(Main.DEEP_PINK);
        this.statusLabel.setText("נמצא פתרון, מציג אנימציה...");
        this.animateSolution(path);
    }

    // מציגה את הפתרון שלב אחרי שלב בעזרת Timer מובנה
    private void animateSolution(List<Point> path) {
        // שימוש במערך של תא אחד כדי שיהיה אפשר לעדכן אותו מתוך אובייקט ה-Timer
        final int[] index = {0};

        this.animationTimer = new Timer(this.animationDelay, event -> {
            // תנאי עצירה - אם סיימנו לעבור על כל הדרך
            if (index[0] >= path.size()) {
                this.animationTimer.stop();
                this.checkSolutionButton.setEnabled(true);
                this.statusLabel.setText("הפתרון הוצג. אורך המסלול: " + path.size() + " צעדים");
                return;
            }

            // שולחים כל פעם צעד אחד בודד לקנבס כדי שיצייר אותו
            Point currentPoint = path.get(index[0]);
            this.mazeCanvas.markSolutionCell(currentPoint);
            index[0]++;
        });

        this.animationTimer.start();
    }

    // עוצרת את האנימציה כדי שלא תמשיך לרוץ ברקע ולזלול משאבים אם המשתמש חזר אחורה
    private void stopAnimationIfNeeded() {
        if (this.animationTimer != null && this.animationTimer.isRunning()) {
            this.animationTimer.stop();
        }
    }

    /**
     * אלגוריתם חיפוש לרוחב (Breadth-First Search).
     * סורק את המבוך "שכבה אחרי שכבה" מנקודת ההתחלה.
     * עבר התאמה לעבוד רק עם List (רשימה) במקום Queue (תור) לפי הדרישות.
     */
    private List<Point> findPathBFS() {
        int rows = this.mazeMatrix.length;
        int cols = this.mazeMatrix[0].length;

        // מקרה קצה: חוסך זמן חישוב - אם ההתחלה או הסוף הם קירות, בלתי אפשרי לפתור
        if (!this.mazeMatrix[0][0] || !this.mazeMatrix[rows - 1][cols - 1]) {
            return null;
        }

        boolean[][] visited = new boolean[rows][cols]; // מעקב איפה כבר היינו
        Point[][] parent = new Point[rows][cols]; // מעקב מאיפה הגענו לכל תא (לצורך שחזור הדרך)

        // יצירת רשימה רגילה שתתפקד עבורנו כמו תור!
        List<Point> searchList = new ArrayList<>();

        searchList.add(new Point(0, 0)); // הוספה לסוף הרשימה
        visited[0][0] = true;

        // מערכי עזר לתנועה (למעלה, למטה, שמאלה, ימינה). לא כולל אלכסונים!
        int[] dy = {-1, 1, 0, 0};
        int[] dx = {0, 0, -1, 1};

        Point endPoint = this.searchMaze(searchList, visited, parent, rows, cols, dy, dx);

        // אם המבוך חסום
        if (endPoint == null) {
            return null;
        }

        // קריאה לפונקציית העזר כדי להפוך את אוסף הנתונים למסלול מסודר
        return this.reconstructPath(parent, cols - 1, rows - 1);
    }

    // הלולאה המרכזית של האלגוריתם (מופרדת לפונקציה כדי לשמור על קוד נקי)
    private Point searchMaze(
            List<Point> searchList,
            boolean[][] visited,
            Point[][] parent,
            int rows,
            int cols,
            int[] dy,
            int[] dx
    ) {
        while (!searchList.isEmpty()) {
            // ה"קסם": במקום poll של תור, אנחנו פשוט מוציאים תמיד את האיבר הראשון ברשימה!
            Point current = searchList.remove(0);

            // אם הגענו לסוף המבוך (למטה-ימינה), מחזירים את התא המנצח
            if (current.y == rows - 1 && current.x == cols - 1) {
                return current;
            }

            // אחרת, ממשיכים לבדוק לאן אפשר להתקדם
            this.checkNeighbors(current, searchList, visited, parent, rows, cols, dy, dx);
        }

        return null;
    }

    // בודקת את ארבעת השכנים של התא הנוכחי ומוסיפה אותם לרשימה אם מותר לעבור אליהם
    private void checkNeighbors(
            Point current,
            List<Point> searchList,
            boolean[][] visited,
            Point[][] parent,
            int rows,
            int cols,
            int[] dy,
            int[] dx
    ) {
        for (int i = 0; i < 4; i++) {
            int nextY = current.y + dy[i];
            int nextX = current.x + dx[i];

            if (this.isLegalMove(nextY, nextX, rows, cols, visited)) {
                visited[nextY][nextX] = true;
                parent[nextY][nextX] = current; // שמירת תא המוצא ("האבא") של התא החדש

                // הוספת התא החדש לסוף הרשימה (עובד בדיוק כמו הכנסה לתור)
                searchList.add(new Point(nextX, nextY));
            }
        }
    }

    // פונקציית עזר לוגית: בודקת שלא יצאנו מגבולות המסך, ושזה מעבר לבן שעוד לא היינו בו
    private boolean isLegalMove(int row, int col, int rows, int cols, boolean[][] visited) {
        if (row < 0 || row >= rows) {
            return false;
        }

        if (col < 0 || col >= cols) {
            return false;
        }

        return this.mazeMatrix[row][col] && !visited[row][col];
    }

    // משחזרת את המסלול צעד אחר צעד על ידי הליכה אחורה במערך ה"אבות"
    private List<Point> reconstructPath(Point[][] parent, int endX, int endY) {
        List<Point> path = new ArrayList<>();
        Point current = new Point(endX, endY);

        while (current != null) {
            path.add(current);
            current = parent[current.y][current.x]; // טיפוס צעד אחד אחורה
        }

        // כיוון שאספנו מהסוף להתחלה, חובה להפוך את הרשימה כדי שהאנימציה תרוץ מההתחלה לסוף!
        Collections.reverse(path);

        return path;
    }
}