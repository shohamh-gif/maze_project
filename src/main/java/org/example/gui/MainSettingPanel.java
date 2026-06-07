package org.example.gui;

import org.example.api.MazeApiService;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Scanner;


public class MainSettingPanel extends JPanel {

    private static final int DEFAULT_MAZE_SIZE = 30;
    private static final int MIN_MAZE_SIZE = 5;
    private static final int MAX_MAZE_SIZE = 100;

    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    private static final Color WINDOW_BG = new Color(253, 242, 244);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color DEEP_PINK = new Color(214, 51, 108);
    private static final Color SOFT_PINK = new Color(240, 98, 146);
    private static final Color TEXT_DARK = new Color(60, 60, 60);
    private static final Color ERROR_COLOR = new Color(217, 48, 37);

    private JTextField widthField;
    private JTextField heightField;
    private JLabel configLabel;
    private JLabel errorLabel;

    private int mazeWidth;
    private int mazeHeight;
    private boolean[][] mazePixel;

    private final MazeApiService apiService;

    public MainSettingPanel(int x, int y, int windowWidth, int windowHeight) {
        this.apiService = new MazeApiService();

        // אתחול ערכי ברירת מחדל לרוחב ולגובה המבוך
        this.mazeWidth = DEFAULT_MAZE_SIZE;
        this.mazeHeight = DEFAULT_MAZE_SIZE;

        // קובע את המיקום והגודל של הפאנל בתוך החלון
        this.setBounds(x, y, windowWidth, windowHeight);

        // קובע צבע רקע לכל הפאנל
        this.setBackground(WINDOW_BG);

        // מנהל פריסה שממקם את הכרטיס במרכז הפאנל הראשי
        this.setLayout(new GridBagLayout());

        // חישוב גדלים לפי גודל החלון, כדי לשמור על מראה מסודר
        int cardWidth = windowWidth - 90;
        int cardHeight = 520;
        int serverBoxWidth = cardWidth - 100;
        int serverBoxHeight = 105;
        int configLabelWidth = serverBoxWidth - 40;

        // יצירת הכרטיס הלבן המרכזי
        JPanel card = new JPanel();
        card.setBackground(CARD_BG);

        // קביעת גודל קבוע לכרטיס
        // preferred הוא הגודל המבוקש
        // maximum מונע התרחבות
        // minimum מונע כיווץ
        card.setPreferredSize(new Dimension(cardWidth, cardHeight));
        card.setMaximumSize(new Dimension(cardWidth, cardHeight));
        card.setMinimumSize(new Dimension(cardWidth, cardHeight));

        // BoxLayout מסדר את רכיבי הכרטיס מלמעלה למטה
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        // יצירת מסגרת ורודה לכרטיס עם רווח פנימי
        // המסגרת החיצונית נותנת גבול
        // ה EmptyBorder יוצר מרווח בין תוכן הכרטיס לקצוות
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(243, 213, 220), 1),
                new EmptyBorder(30, 40, 30, 40)
        ));

        // יצירת כותרת ראשית
        JLabel titleLabel = new JLabel("הגדרות ליצירת מבוך");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(DEEP_PINK);

        // ממרכז את הכותרת בתוך BoxLayout
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLabel);

        // רווח אנכי בין הכותרת לחלק הבא
        card.add(Box.createVerticalStrut(20));

        // יצירת קופסה פנימית להצגת הגדרות מהשרת
        JPanel serverBox = new JPanel();

        // סידור הטקסט והכפתור אחד מתחת לשני
        serverBox.setLayout(new BoxLayout(serverBox, BoxLayout.Y_AXIS));

        serverBox.setBackground(new Color(255, 247, 248));

        // מסגרת ורודה ורווח פנימי לקופסת השרת
        serverBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(248, 222, 228), 1),
                new EmptyBorder(12, 15, 12, 15)
        ));

        // ממרכז את קופסת השרת בתוך הכרטיס
        serverBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        // קביעת גודל קבוע לקופסת השרת
        // כך טקסט ארוך לא ידחוף את גבולות הכרטיס
        serverBox.setPreferredSize(new Dimension(serverBoxWidth, serverBoxHeight));
        serverBox.setMaximumSize(new Dimension(serverBoxWidth, serverBoxHeight));
        serverBox.setMinimumSize(new Dimension(serverBoxWidth, serverBoxHeight));

        // תווית שתציג את הגדרות העיצוב שחוזרות מהשרת
        this.configLabel = new JLabel("הגדרות העיצוב מהשרת יופיעו כאן לאחר טעינה", JLabel.CENTER);
        this.configLabel.setFont(MAIN_FONT);
        this.configLabel.setForeground(TEXT_DARK);
        this.configLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // הגבלת רוחב התווית, כדי שהיא תישאר בתוך קופסת השרת
        this.configLabel.setPreferredSize(new Dimension(configLabelWidth, 30));
        this.configLabel.setMaximumSize(new Dimension(configLabelWidth, 30));
        this.configLabel.setMinimumSize(new Dimension(configLabelWidth, 30));

        // כפתור מקומי, כי אין צורך לגשת אליו מפונקציות אחרות
        JButton refreshButton = new JButton("טען הגדרות שרת");
        styleButton(refreshButton, SOFT_PINK);
        refreshButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // בלחיצה על הכפתור נטענות הגדרות מהשרת
        refreshButton.addActionListener(e -> loadServerConfig());

        serverBox.add(configLabel);
        serverBox.add(Box.createVerticalStrut(10));
        serverBox.add(refreshButton);

        card.add(serverBox);
        card.add(Box.createVerticalStrut(25));

        // כותרת לאזור הקלט של גודל המבוך
        JLabel sizeTitle = new JLabel("קביעת גודל המבוך (בין 5 ל-100):");
        sizeTitle.setFont(LABEL_FONT);
        sizeTitle.setForeground(TEXT_DARK);
        sizeTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(sizeTitle);
        card.add(Box.createVerticalStrut(10));

        // פאנל שמסדר את שדות הרוחב והגובה אחד ליד השני
        JPanel fieldsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        fieldsPanel.setBackground(CARD_BG);

        // קופסה לשדה הרוחב, הכותרת מעל השדה
        JPanel widthBox = new JPanel();
        widthBox.setLayout(new BoxLayout(widthBox, BoxLayout.Y_AXIS));
        widthBox.setBackground(CARD_BG);

        JLabel widthLabel = new JLabel("רוחב");
        widthLabel.setFont(LABEL_FONT);
        widthLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // שדה קלט שמתחיל מערך ברירת המחדל
        this.widthField = new JTextField(String.valueOf(mazeWidth), 5);
        styleTextField(widthField);

        widthBox.add(widthLabel);
        widthBox.add(Box.createVerticalStrut(5));
        widthBox.add(widthField);

        // קופסה לשדה הגובה, הכותרת מעל השדה
        JPanel heightBox = new JPanel();
        heightBox.setLayout(new BoxLayout(heightBox, BoxLayout.Y_AXIS));
        heightBox.setBackground(CARD_BG);

        JLabel heightLabel = new JLabel("גובה");
        heightLabel.setFont(LABEL_FONT);
        heightLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // שדה קלט שמתחיל מערך ברירת המחדל
        this.heightField = new JTextField(String.valueOf(mazeHeight), 5);
        styleTextField(heightField);

        heightBox.add(heightLabel);
        heightBox.add(Box.createVerticalStrut(5));
        heightBox.add(heightField);

        // הוספת שתי הקופסאות לפאנל השדות
        fieldsPanel.add(widthBox);
        fieldsPanel.add(heightBox);

        card.add(fieldsPanel);
        card.add(Box.createVerticalStrut(25));

        // כפתור מקומי ליצירת מבוך
        JButton getMazeButton = new JButton("צור מבוך חדש");
        styleButton(getMazeButton, DEEP_PINK);
        getMazeButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        getMazeButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // בלחיצה, הקלט נבדק ונשמר רק אם הוא תקין
        getMazeButton.addActionListener(e -> {
            this.errorLabel.setText(" ");

            this.mazeWidth = validateInput(widthField, "הרוחב");
            this.mazeHeight = validateInput(heightField, "הגובה");

            // עדכון השדות לפי הערכים התקינים שנשמרו
            widthField.setText(String.valueOf(this.mazeWidth));
            heightField.setText(String.valueOf(this.mazeHeight));

            fetchAndReadMazeImage();
        });

        card.add(getMazeButton);
        card.add(Box.createVerticalStrut(15));

        // תווית להצגת הודעות שגיאה למשתמש
        this.errorLabel = new JLabel(" ", JLabel.CENTER);
        this.errorLabel.setForeground(ERROR_COLOR);
        this.errorLabel.setFont(LABEL_FONT);
        this.errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(errorLabel);

        // הוספת הכרטיס לפאנל הראשי
        this.add(card);
    }

    private void loadServerConfig() {
        this.configLabel.setText("מתחבר לשרת ומביא הגדרות...");

        new Thread(() -> {
            try {
                JSONObject dataAsJson = apiService.fetchRenderConfig();

                String wallColor = dataAsJson.getString("wallCellColor");
                String pathColor = dataAsJson.getString("pathColor");
                boolean drawGrid = dataAsJson.getBoolean("drawGrid");
                String gridColor = dataAsJson.getString("gridColor");
                int animationDelay = dataAsJson.getInt("animationDelayMs");

                String configText = String.format(
                        "צבע הקירות: %s | צבע הנתיב: %s | האם יהיו קווי רשת? %b | צבע קווי הרשת: %s | זמן השהיה: %d ms",
                        wallColor,
                        pathColor,
                        drawGrid,
                        gridColor,
                        animationDelay
                );

                SwingUtilities.invokeLater(() -> this.configLabel.setText(configText));

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> this.configLabel.setText("שגיאה בהבאת נתונים מהשרת."));
                ex.printStackTrace();
            }
        }).start();
    }
    // שולחת בקשה לשרת, מקבלת תמונת מבוך, ואז קוראת את הפיקסלים שלה
// שולחת בקשה לשרת, מקבלת תמונת מבוך, ממירה למערך ומציגה את התמונה בדפדפן
    private void fetchAndReadMazeImage() {
        this.errorLabel.setText("טוען וקורא מבוך מהשרת...");

        new Thread(() -> {
            try {
                BufferedImage mazeImage = apiService.fetchMazeImage(mazeWidth, mazeHeight);

                this.mazePixel = convertImageToBooleanMatrix(mazeImage);

                openMazeImageInBrowser(mazeImage);

                SwingUtilities.invokeLater(() ->
                        this.errorLabel.setText("המבוך נקרא בהצלחה מהשרת.")
                );

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() ->
                        this.errorLabel.setText("שגיאה: לא הצלחנו לקרוא את תמונת המבוך.")
                );

                ex.printStackTrace();
            }
        }).start();
    }

    // שומרת את תמונת המבוך כקובץ זמני ופותחת אותה בדפדפן ברירת המחדל
    private void openMazeImageInBrowser(BufferedImage mazeImage) throws Exception {
        File mazeFile = File.createTempFile("maze-", ".png");

        ImageIO.write(mazeImage, "png", mazeFile);

        Desktop.getDesktop().browse(mazeFile.toURI());
    }

    // ממירה את תמונת המבוך למערך דו ממדי של true ו false
    private boolean[][] convertImageToBooleanMatrix(BufferedImage mazeImage) {
        int imageWidth = mazeImage.getWidth();
        int imageHeight = mazeImage.getHeight();

        boolean[][] matrix = new boolean[imageHeight][imageWidth];

        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {
                int currentRgb = mazeImage.getRGB(x, y);
                Color pixelColor = new Color(currentRgb);
                matrix[y][x] = isWhite(pixelColor);
                System.out.print(matrix[y][x] + "  ");
            }
            System.out.println();
        }

        return matrix;
    }
    // בודקת אם צבע הפיקסל לבן
    private boolean isWhite(Color color) {
        return color.getRed() == 255 &&
                color.getGreen() == 255 &&
                color.getBlue() == 255;
    }


    private void styleButton(JButton button, Color bg) {
        button.setFont(LABEL_FONT);
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleTextField(JTextField textField) {
        textField.setFont(MAIN_FONT);
        textField.setHorizontalAlignment(JTextField.CENTER);
        textField.setPreferredSize(new Dimension(70, 30));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 190, 200), 1),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
    }

    private int validateInput(JTextField field, String fieldName) {
        String text = field.getText().trim();

        try {
            int value = Integer.parseInt(text);

            if (value >= MIN_MAZE_SIZE && value <= MAX_MAZE_SIZE) {
                return value;
            }

            this.errorLabel.setText(
                    "שגיאה: " + fieldName + " מחוץ לטווח (" +
                            MIN_MAZE_SIZE + "-" + MAX_MAZE_SIZE +
                            "). שונה ל-" + DEFAULT_MAZE_SIZE + "."
            );

            return DEFAULT_MAZE_SIZE;

        } catch (NumberFormatException e) {
            this.errorLabel.setText(
                    "שגיאה: הקלט בתוך " + fieldName +
                            " אינו מספר חוקי! שונה ל-" + DEFAULT_MAZE_SIZE + "."
            );

            return DEFAULT_MAZE_SIZE;
        }
    }

    public int getMazeWidth() {
        return mazeWidth;
    }

    public int getMazeHeight() {
        return mazeHeight;
    }
}