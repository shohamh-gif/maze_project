package org.example.gui;

import org.example.Main;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MazePanel extends JPanel {

    private final JPanel previousPanel;
    private int animationDelay;
    private boolean[][] mazeMatrix;
    private MazeCanvas mazeCanvas;

    // התווית החדשה שלנו שתחליף את החלון הקופץ!
    private JLabel statusLabel;

    public MazePanel(JPanel previousPanel, boolean[][] mazeMatrix, Color wallColor, Color pathColor, boolean drawGrid, Color gridColor, int animationDelay) {
        this.previousPanel = previousPanel;
        this.mazeMatrix = mazeMatrix;
        this.animationDelay = animationDelay;

        this.setLayout(new BorderLayout());

        // --- 1. שדרוג הפאנל העליון ---
        // שינינו ל-BorderLayout כדי שנוכל לשים את הכפתור בצד ואת הטקסט באמצע
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(true);
        topPanel.setBackground(Main.LIGHT_PINK);
        // מוסיף קצת מרווח כדי שזה לא יידבק לקצוות
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JButton backButton = new JButton("חזור להגדרות");
        Main.styleButton(backButton, Main.DEEP_PINK);
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));

        backButton.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (topFrame != null) {
                topFrame.setContentPane(this.previousPanel);
                topFrame.revalidate();
                topFrame.repaint();
            }
        });

        // הוספת הכפתור לצד שמאל (WEST)
        topPanel.add(backButton, BorderLayout.WEST);

        // יצירת ועיצוב תווית הסטטוס (שתתחיל ריקה)
        this.statusLabel = new JLabel(" ", JLabel.CENTER);
        this.statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        this.statusLabel.setForeground(Main.TEXT_DARK);

        // הוספת התווית למרכז הפאנל העליון (CENTER)
        topPanel.add(this.statusLabel, BorderLayout.CENTER);

        this.add(topPanel, BorderLayout.NORTH);

        // --- 2. אזור המבוך ---
        this.mazeCanvas = new MazeCanvas(this.mazeMatrix, wallColor, pathColor, drawGrid, gridColor);
        this.add(this.mazeCanvas, BorderLayout.CENTER);

        // --- 3. אזור כפתור הפתרון ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(true);
        buttonPanel.setBackground(Main.LIGHT_PINK);

        JButton checkSolution = new JButton("בדוק פיתרון");
        Main.styleButton(checkSolution, Main.DEEP_PINK);
        checkSolution.setFont(new Font("Segoe UI", Font.BOLD, 15));

        checkSolution.addActionListener(e -> this.handleCheckSolution());

        buttonPanel.add(checkSolution);
        this.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleCheckSolution() {
        // מנקים הודעות קודמות בזמן שהוא מחשב
        this.statusLabel.setText("מחשב מסלול...");
        this.statusLabel.setForeground(Main.TEXT_DARK);

        // קוראים לאלגוריתם שלנו (הלוגיקה נשארה זהה לחלוטין)
        List<Point> path = this.findPathBFS();

        if (path == null) {
            // עיצוב ההודעה במקרה שאין פתרון
            this.statusLabel.setForeground(Main.ERROR_COLOR);
            this.statusLabel.setText("No solution found");
        } else {
            // עיצוב ההודעה במקרה שיש פתרון
            this.statusLabel.setForeground(Main.DEEP_PINK);
            this.statusLabel.setText("Solution found! (אורך המסלול: " + path.size() + " צעדים)");

            // המשתנה path מחזיק כרגע את כל המסלול המדויק מההתחלה לסוף!
            // ברגע שתהיו מוכנות, כאן נוסיף את הקריאה לציור האנימציה.
        }
    }

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

        boolean found = false;

        while (!queue.isEmpty()) {
            Point current = queue.poll();
            int y = current.y;
            int x = current.x;

            if (y == rows - 1 && x == cols - 1) {
                found = true;
                break;
            }

            for (int i = 0; i < 4; i++) {
                int ny = y + dy[i];
                int nx = x + dx[i];

                if (ny >= 0 && ny < rows && nx >= 0 && nx < cols) {
                    if (this.mazeMatrix[ny][nx] && !visited[ny][nx]) {
                        visited[ny][nx] = true;
                        parent[ny][nx] = current;
                        queue.add(new Point(nx, ny));
                    }
                }
            }
        }

        if (!found) {
            return null;
        }

        List<Point> path = new ArrayList<>();
        Point curr = new Point(cols - 1, rows - 1);

        while (curr != null) {
            path.add(curr);
            curr = parent[curr.y][curr.x];
        }

        Collections.reverse(path);
        return path;
    }
}