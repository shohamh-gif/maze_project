package org.example;

import javax.swing.*;
import java.awt.*;

public class MainSettingPanel extends JPanel {
    private JTextField widthField;
    private JTextField heightField;
    private JButton getMazeButton;
    private JButton refreshButton;
    private JLabel configLabel;

    public MainSettingPanel(int x, int y, int windowWidth, int windowHeight) {
        this.setBounds(x, y, windowWidth, windowHeight);
        this.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        // --- אזור הגדרות שרת (שלב 1: טקסט זמני וכפתור רענון) ---
        this.configLabel = new JLabel("הגדרות מהשרת יופיעו כאן...");
        this.add(configLabel);

        this.refreshButton = new JButton("Refresh Setting");
        this.refreshButton.addActionListener(e -> {
            // משנים את הטקסט כדי שהמשתמש ידע שאנחנו טוענים
            this.configLabel.setText("מתחבר לשרת מביא הגדרות...");

            // פותחים תהליך רקע (Thread) כדי לא לתקוע את המסך
            new Thread(() -> {
                try {
                    // 1. יצירת הלקוח והבקשה
                    okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .url("https://backend-qcf9.onrender.com/fm1/get-render-config")
                            .build();

                    // 2. ביצוע הבקשה
                    okhttp3.Response response = client.newCall(request).execute();
                    String data = response.body().string();

                    // 3. הפיכת הטקסט לאובייקט JSON
                    org.json.JSONObject dataAsJson = new org.json.JSONObject(data);

                    // 4. שליפת הנתונים לפי השמות שלהם ב-JSON של המבוך
                    String wallColor = dataAsJson.getString("wallCellColor");
                    String pathColor = dataAsJson.getString("pathColor");
                    boolean drawGrid = dataAsJson.getBoolean("drawGrid");
                    int animationDelay = dataAsJson.getInt("animationDelayMs");

                    // 5. בניית הטקסט שנרצה להציג למשתמש
                    String configText = String.format("קירות: %s | נתיב: %s | רשת: %b | השהיה: %d ms",
                            wallColor, pathColor, drawGrid, animationDelay);

                    // 6. עדכון המסך מתוך תהליך הרקע (חובה להשתמש ב-SwingUtilities)
                    SwingUtilities.invokeLater(() -> {
                        this.configLabel.setText(configText);
                    });

                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        this.configLabel.setText("שגיאה בהבאת נתונים מהשרת.");
                    });
                    ex.printStackTrace();
                }
            }).start();
        });
        this.add(refreshButton);


        this.add(new JLabel("Width:"));
        this.widthField = new JTextField("30", 5); // 30 זה טקסט התחלתי, 5 זה רוחב התיבה
        this.add(widthField);

        this.add(new JLabel("Height:"));
        this.heightField = new JTextField("30", 5);
        this.add(heightField);

        this.getMazeButton = new JButton("GET MAZE");
        this.getMazeButton.addActionListener(e -> {
            System.out.println("הכפתור נלחץ!");
        });
        this.add(getMazeButton);
    }
}