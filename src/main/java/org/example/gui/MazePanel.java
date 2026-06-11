package org.example.gui;

import org.example.Main;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

// מחלקה זו מייצגת את המסך (פאנל) השלם שמכיל את המבוך עצמו ואת כפתורי הפעולה שמתחתיו
public class MazePanel extends JPanel {

    // שומרים הפניה למסך הקודם (מסך ההגדרות) כדי שנוכל לחזור אליו בלחיצת כפתור
    private final JPanel previousPanel;
    // משתנה שישמש אותנו בהמשך לשלב האנימציה של הפתרון (השהיה בין צעד לצעד)
    private int animationDelay;

    // שומרים את המטריצה הלוגית כדי שהאלגוריתם יוכל לנתח אותה, ואת התווית להצגת התוצאה
    private boolean[][] mazeMatrix;
    private JLabel statusLabel;
    private MazeCanvas mazeCanvas;

    // בנאי המחלקה - עכשיו הוא נקי וקצר, ורק קורא לפונקציית עזר שבונה את המסך
    public MazePanel(JPanel previousPanel, boolean[][] mazeMatrix, Color wallColor, Color pathColor, boolean drawGrid, Color gridColor, int animationDelay) {
        this.previousPanel = previousPanel;
        this.animationDelay = animationDelay;
        this.mazeMatrix = mazeMatrix;

        // קריאה לפונקציה שבונה את ממשק המשתמש
        this.setupUI(wallColor, pathColor, drawGrid, gridColor);
    }

    // --- פונקציות עזר לבניית המסך (UI) ---

    // פונקציה המרכזת את כל בניית חלקי המסך השונים (עליון, מרכז, תחתון)
    private void setupUI(Color wallColor, Color pathColor, boolean drawGrid, Color gridColor) {
        this.setLayout(new BorderLayout());
        this.setBackground(Main.LIGHT_PINK);
        this.setOpaque(true);

        // הוספת הפאנל העליון (עם הטקסט)
        this.add(this.createTopPanel(), BorderLayout.NORTH);

        // הוספת קנבס הציור למרכז המסך
        this.mazeCanvas = new MazeCanvas(this.mazeMatrix, wallColor, pathColor, drawGrid, gridColor);
        this.add(this.mazeCanvas, BorderLayout.CENTER);

        // הוספת אזור הכפתורים למטה
        this.add(this.createButtonPanel(), BorderLayout.SOUTH);
    }

    // פונקציה היוצרת את הפאנל העליון עם תווית הסטטוס ומרווח קבוע מראש
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Main.LIGHT_PINK);
        topPanel.setPreferredSize(new Dimension(Main.WINDOW_WIDTH, 45)); // שומר מקום כדי שהמבוך לא יידבק לתקרה

        this.statusLabel = new JLabel(" ", JLabel.CENTER);
        this.statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        this.statusLabel.setForeground(Main.TEXT_DARK);

        topPanel.add(this.statusLabel, BorderLayout.CENTER);
        return topPanel;
    }

    // פונקציה היוצרת את אזור הכפתורים התחתון ומגדירה את הפעולות שלהם
    private JPanel createButtonPanel() {
        JButton backButton = new JButton("חזור");
        Main.styleButton(backButton, Main.SOFT_PINK);
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        backButton.addActionListener(e -> this.handleBackButton());

        JButton checkSolution = new JButton("בדוק פתרון");
        Main.styleButton(checkSolution, Main.DEEP_PINK);
        checkSolution.setFont(new Font("Segoe UI", Font.BOLD, 15));
        checkSolution.addActionListener(e -> this.handleCheckSolution());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Main.LIGHT_PINK);
        buttonPanel.add(backButton);
        buttonPanel.add(checkSolution);

        return buttonPanel;
    }

    // --- פונקציות עזר לטיפול באירועים (לחיצות כפתורים) ---

    // הפעולה שתקרה בעת לחיצה על כפתור "חזור"
    private void handleBackButton() {
        if (this.previousPanel instanceof MainSettingPanel) {
            MainSettingPanel settingPanel = (MainSettingPanel) this.previousPanel;
            settingPanel.prepareForNewMaze();
        }
        Main.showPanel(this.previousPanel);
    }

    // הפעולה שתקרה בעת לחיצה על "בדוק פיתרון"
    private void handleCheckSolution() {
        this.statusLabel.setText("מחשב מסלול...");
        this.statusLabel.setForeground(Main.TEXT_DARK);

        // הפעלת אלגוריתם ה-BFS
        List<Point> path = this.findPathBFS();

        // עדכון המסך לפי התוצאה
        if (path == null) {
            this.statusLabel.setForeground(Main.ERROR_COLOR);
            this.statusLabel.setText("אין פיתרון");
        } else {
            this.statusLabel.setForeground(Main.DEEP_PINK);
            this.statusLabel.setText("נמצא פיתרון! (אורך המסלול: " + path.size() + " צעדים)");
            // הערה: המשתנה path מכיל את המסלול ומוכן לשימוש עתידי באנימציה!
        }
    }

    // --- פונקציות האלגוריתם (BFS) ---

    // אלגוריתם מציאת המסלול שלנו - מנהל את תהליך החיפוש הכולל
    private List<Point> findPathBFS() {
        int rows = this.mazeMatrix.length;
        int cols = this.mazeMatrix[0].length;

        // מקרה קצה: אי אפשר להתחיל או לסיים על קיר
        if (!this.mazeMatrix[0][0] || !this.mazeMatrix[rows - 1][cols - 1]) {
            return null;
        }

        boolean[][] visited = new boolean[rows][cols];
        Point[][] parent = new Point[rows][cols];
        Queue<Point> queue = new LinkedList<>();

        queue.add(new Point(0, 0));
        visited[0][0] = true;

        // מערכים המייצגים תנועה למעלה, למטה, שמאלה וימינה (ללא אלכסונים)
        int[] dy = {-1, 1, 0, 0};
        int[] dx = {0, 0, -1, 1};

        // הפעלת פונקציית הסריקה שמחפשת את משבצת הסיום
        Point endPoint = this.searchMaze(queue, visited, parent, rows, cols, dy, dx);

        if (endPoint == null) {
            return null; // המבוך חסום לחלוטין
        }

        // אם מצאנו - משחזרים את הדרך חזרה
        return this.reconstructPath(parent, cols - 1, rows - 1);
    }

    // פונקציית עזר ל-BFS: מבצעת את סריקת המבוך עצמה (הלולאה המרכזית)
    private Point searchMaze(Queue<Point> queue, boolean[][] visited, Point[][] parent, int rows, int cols, int[] dy, int[] dx) {
        while (!queue.isEmpty()) {
            Point current = queue.poll();
            int y = current.y;
            int x = current.x;

            // אם הגענו למשבצת התחתונה-ימנית - סיימנו בהצלחה!
            if (y == rows - 1 && x == cols - 1) {
                return current;
            }

            // בדיקת 4 השכנים הישירים
            for (int i = 0; i < 4; i++) {
                int ny = y + dy[i];
                int nx = x + dx[i];

                // מוודאים שלא חרגנו מהגבולות ושמדובר במעבר חוקי שעוד לא ביקרנו בו
                if (ny >= 0 && ny < rows && nx >= 0 && nx < cols) {
                    if (this.mazeMatrix[ny][nx] && !visited[ny][nx]) {
                        visited[ny][nx] = true;
                        parent[ny][nx] = current; // שמירת "האבא" כדי שנדע מאיפה באנו
                        queue.add(new Point(nx, ny));
                    }
                }
            }
        }
        return null;
    }

    // פונקציית עזר ל-BFS: משחזרת את המסלול המדויק מהסוף להתחלה
    private List<Point> reconstructPath(Point[][] parent, int endX, int endY) {
        List<Point> path = new ArrayList<>();
        Point curr = new Point(endX, endY);

        // הליכה אחורה בעץ המשפחה עד שמגיעים להתחלה
        while (curr != null) {
            path.add(curr);
            curr = parent[curr.y][curr.x];
        }

        // הופכים את הרשימה כדי שהמסלול יתחיל מההתחלה ויסתיים בסוף
        Collections.reverse(path);
        return path;
    }
}