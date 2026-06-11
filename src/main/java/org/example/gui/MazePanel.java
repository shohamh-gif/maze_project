package org.example.gui;

import org.example.Main;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

// המחלקה הזאת מייצגת את מסך המבוך עצמו
public class MazePanel extends JPanel {

    private final JPanel previousPanel;
    private final int animationDelay;
    private final boolean[][] mazeMatrix;

    private JLabel statusLabel;
    private MazeCanvas mazeCanvas;
    private JButton checkSolutionButton;
    private Timer animationTimer;

    // מקבלת את המבוך, את המסך הקודם ואת הגדרות הציור
    public MazePanel(
            JPanel previousPanel,
            boolean[][] mazeMatrix,
            Color wallColor,
            Color pathColor,
            boolean drawGrid,
            Color gridColor,
            int animationDelay
    ) {
        this.previousPanel = previousPanel;
        this.animationDelay = animationDelay;
        this.mazeMatrix = mazeMatrix;

        this.setupUI(wallColor, pathColor, drawGrid, gridColor);
    }

    // בונה את כל המסך: כותרת, ציור המבוך וכפתורים
    private void setupUI(Color wallColor, Color pathColor, boolean drawGrid, Color gridColor) {
        this.setLayout(new BorderLayout());
        this.setBackground(Main.LIGHT_PINK);
        this.setOpaque(true);

        this.add(this.createTopPanel(), BorderLayout.NORTH);

        this.mazeCanvas = new MazeCanvas(this.mazeMatrix, wallColor, pathColor, drawGrid, gridColor);
        this.add(this.mazeCanvas, BorderLayout.CENTER);

        this.add(this.createButtonPanel(), BorderLayout.SOUTH);
    }

    // יוצר את החלק העליון שבו מוצגת הודעת מצב למשתמש
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Main.LIGHT_PINK);
        topPanel.setPreferredSize(new Dimension(Main.WINDOW_WIDTH, 45));

        this.statusLabel = new JLabel(" ", JLabel.CENTER);
        this.statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        this.statusLabel.setForeground(Main.TEXT_DARK);

        topPanel.add(this.statusLabel, BorderLayout.CENTER);

        return topPanel;
    }

    // יוצר את כפתור החזור ואת כפתור בדיקת הפתרון
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

    // מחזירה למסך ההגדרות ועוצרת אנימציה אם היא עדיין רצה
    private void handleBackButton() {
        this.stopAnimationIfNeeded();

        if (this.previousPanel instanceof MainSettingPanel) {
            MainSettingPanel settingPanel = (MainSettingPanel) this.previousPanel;
            settingPanel.prepareForNewMaze();
        }

        Main.showPanel(this.previousPanel);
    }

    // מתחילה בדיקת פתרון למבוך ומפעילה אנימציה אם נמצא פתרון
    private void handleCheckSolution() {
        this.checkSolutionButton.setEnabled(false);
        this.mazeCanvas.clearSolution();

        this.statusLabel.setText("מחשב מסלול...");
        this.statusLabel.setForeground(Main.TEXT_DARK);

        List<Point> path = this.findPathBFS();

        if (path == null) {
            this.statusLabel.setForeground(Main.ERROR_COLOR);
            this.statusLabel.setText("אין פתרון");
            this.checkSolutionButton.setEnabled(true);
            return;
        }

        this.statusLabel.setForeground(Main.DEEP_PINK);
        this.statusLabel.setText("נמצא פתרון, מציג אנימציה...");
        this.animateSolution(path);
    }

    // מציגה את הפתרון שלב אחרי שלב לפי זמן ההמתנה שהגיע מהשרת
    private void animateSolution(List<Point> path) {
        final int[] index = {0};

        this.animationTimer = new Timer(this.animationDelay, event -> {
            if (index[0] >= path.size()) {
                this.animationTimer.stop();
                this.checkSolutionButton.setEnabled(true);
                this.statusLabel.setText("הפתרון הוצג. אורך המסלול: " + path.size() + " צעדים");
                return;
            }

            Point currentPoint = path.get(index[0]);
            this.mazeCanvas.markSolutionCell(currentPoint);
            index[0]++;
        });

        this.animationTimer.start();
    }

    // עוצרת את האנימציה אם המשתמש חוזר אחורה באמצע
    private void stopAnimationIfNeeded() {
        if (this.animationTimer != null && this.animationTimer.isRunning()) {
            this.animationTimer.stop();
        }
    }

    // מחפשת מסלול מהפינה השמאלית העליונה לפינה הימנית התחתונה בעזרת BFS
    private List<Point> findPathBFS() {
        int rows = this.mazeMatrix.length;
        int cols = this.mazeMatrix[0].length;

        if (!this.mazeMatrix[0][0] || !this.mazeMatrix[rows - 1][cols - 1]) {
            return null;
        }

        boolean[][] visited = new boolean[rows][cols];
        Point[][] parent = new Point[rows][cols];
        Queue<Point> queue = new LinkedList<>();

        queue.add(new Point(0, 0));
        visited[0][0] = true;

        int[] dy = {-1, 1, 0, 0};
        int[] dx = {0, 0, -1, 1};

        Point endPoint = this.searchMaze(queue, visited, parent, rows, cols, dy, dx);

        if (endPoint == null) {
            return null;
        }

        return this.reconstructPath(parent, cols - 1, rows - 1);
    }

    // מבצעת את הסריקה עצמה ועוברת על התאים במבוך
    private Point searchMaze(
            Queue<Point> queue,
            boolean[][] visited,
            Point[][] parent,
            int rows,
            int cols,
            int[] dy,
            int[] dx
    ) {
        while (!queue.isEmpty()) {
            Point current = queue.poll();

            if (current.y == rows - 1 && current.x == cols - 1) {
                return current;
            }

            this.checkNeighbors(current, queue, visited, parent, rows, cols, dy, dx);
        }

        return null;
    }

    // בודקת את ארבעת השכנים של התא הנוכחי
    private void checkNeighbors(
            Point current,
            Queue<Point> queue,
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
                parent[nextY][nextX] = current;
                queue.add(new Point(nextX, nextY));
            }
        }
    }

    // בודקת אם מותר לעבור לתא מסוים
    private boolean isLegalMove(int row, int col, int rows, int cols, boolean[][] visited) {
        if (row < 0 || row >= rows) {
            return false;
        }

        if (col < 0 || col >= cols) {
            return false;
        }

        return this.mazeMatrix[row][col] && !visited[row][col];
    }

    // משחזרת את המסלול שנמצא מהסוף להתחלה ואז הופכת אותו
    private List<Point> reconstructPath(Point[][] parent, int endX, int endY) {
        List<Point> path = new ArrayList<>();
        Point current = new Point(endX, endY);

        while (current != null) {
            path.add(current);
            current = parent[current.y][current.x];
        }

        Collections.reverse(path);

        return path;
    }
}