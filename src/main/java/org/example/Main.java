package org.example;

import org.example.gui.MainSettingPanel;

import javax.swing.*;
import java.awt.*;

// מחלקה זו משמשת כנקודת הכניסה (Entry Point) של התוכנית שלנו
// היא מגדירה את חלון האפליקציה המרכזי ומרכזת קבועים גרפיים לשימוש כללי
public class Main {

    // הגדרות כלליות לחלון התוכנה
    public static final int WINDOW_WIDTH = 1000;
    public static final int WINDOW_HEIGHT = 700;

    // הגדרת פונט אחיד לכל כפתורי האפליקציה
    public static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);

    // פלטת הצבעים המרכזית של הפרויקט
    public static final Color DEEP_PINK = new Color(214, 51, 108);
    public static final Color SOFT_PINK = new Color(240, 98, 146);
    public static final Color LIGHT_PINK = new Color(253, 242, 244);
    public static final Color TEXT_DARK = new Color(60, 60, 60);
    public static final Color ERROR_COLOR = new Color(217, 48, 37);

    private static JFrame window;

    // פונקציית עזר סטטית לעיצוב אחיד של כפתורים בכל חלקי האפליקציה
    public static void styleButton(JButton button, Color bg) {
        button.setFont(LABEL_FONT);
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false); // מבטל את הריבוע המכוער שמופיע סביב הטקסט בלחיצה
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // משנה את סמן העכבר לאצבע מצביעה
    }

    // פונקציה להחלפת פאנלים (מסכים) על גבי החלון הראשי בקלות
    public static void showPanel(JPanel panel) {
        window.setContentPane(panel);
        window.revalidate(); // מרענן את המבנה של החלון
        window.repaint();    // מצייר מחדש את המסך
    }

    public static void main(String[] args) {
        // מריצים את בניית הממשק בתהליך (Thread) המיועד ל-GUI כדי למנוע תקיעות
        SwingUtilities.invokeLater(() -> {
            window = new JFrame("Maze Client");
            window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            window.setResizable(false);
            window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // סוגר את התוכנית כשהחלון נסגר
            window.setLocationRelativeTo(null); // ממקם את החלון בדיוק באמצע המסך

            // יצירת המסך הראשון (מסך ההגדרות) וטעינתו לחלון
            MainSettingPanel settingPanel = new MainSettingPanel(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
            showPanel(settingPanel);
            window.setVisible(true); // הצגת החלון למשתמש
        });
    }
}