package org.example.gui;

import org.example.Main;
import org.example.api.MazeApiService;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

// מחלקה זו מנהלת את מסך פתיחת האפליקציה: קבלת הגדרות, קליטת מידות למבוך, ופניה לשרת
public class MainSettingPanel extends JPanel {

    // גבולות וקבועים לגודל המבוך המותר
    private static final int DEFAULT_MAZE_SIZE = 30;
    private static final int MIN_MAZE_SIZE = 5;
    private static final int MAX_MAZE_SIZE = 100;

    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    private static final Color WINDOW_BG = Main.LIGHT_PINK;
    private static final Color CARD_BG = Color.WHITE;

    private JTextField widthField;
    private JTextField heightField;
    private JLabel configLabel;
    private JLabel errorLabel;
    private JButton getMazeButton;

    // הגדרות עיצוב שיישמרו לאחר קבלתן מהשרת
    private String wallColor;
    private String pathColor;
    private boolean drawGrid;
    private String gridColor;
    private int animationDelay;

    // הגדרות המבוך הנוכחיות
    private int mazeWidth;
    private int mazeHeight;
    private boolean[][] mazePixel; // ייצוג המבוך כמטריצה בוליאנית (אמת=מעבר, שקר=קיר)

    private final MazeApiService apiService;

    public MainSettingPanel(int x, int y, int windowWidth, int windowHeight) {
        this.apiService = new MazeApiService();
        this.mazeWidth = DEFAULT_MAZE_SIZE;
        this.mazeHeight = DEFAULT_MAZE_SIZE;

        this.setupPanel(x, y, windowWidth, windowHeight);
        this.add(this.createMainCard(windowWidth));
    }

    private void setupPanel(int x, int y, int windowWidth, int windowHeight) {
        this.setBounds(x, y, windowWidth, windowHeight);
        this.setBackground(WINDOW_BG);
        this.setLayout(new GridBagLayout());
    }

    // יצירת ה"כרטיסייה" הלבנה שמרכזת את כל האלמנטים במסך ההגדרות
    private JPanel createMainCard(int windowWidth) {
        int cardWidth = windowWidth - 90;
        int cardHeight = 520;
        int serverBoxWidth = cardWidth - 100;
        int serverBoxHeight = 105;
        int configLabelWidth = serverBoxWidth - 40;

        JPanel card = new JPanel();
        card.setBackground(CARD_BG);
        card.setPreferredSize(new Dimension(cardWidth, cardHeight));
        card.setMaximumSize(new Dimension(cardWidth, cardHeight));
        card.setMinimumSize(new Dimension(cardWidth, cardHeight));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(this.createCardBorder());

        card.add(this.createTitleLabel());
        card.add(Box.createVerticalStrut(20));
        card.add(this.createServerBox(serverBoxWidth, serverBoxHeight, configLabelWidth));
        card.add(Box.createVerticalStrut(25));
        card.add(this.createSizeTitleLabel());
        card.add(Box.createVerticalStrut(10));
        card.add(this.createFieldsPanel());
        card.add(Box.createVerticalStrut(25));
        card.add(this.createGetMazeButton());
        card.add(Box.createVerticalStrut(15));
        card.add(this.createErrorLabel());

        return card;
    }

    private Border createCardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(243, 213, 220), 1),
                new EmptyBorder(30, 40, 30, 40)
        );
    }

    private JLabel createTitleLabel() {
        JLabel titleLabel = new JLabel("הגדרות ליצירת מבוך");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Main.DEEP_PINK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        return titleLabel;
    }

    // יצירת התיבה שמציגה את נתוני השרת
    private JPanel createServerBox(int serverBoxWidth, int serverBoxHeight, int configLabelWidth) {
        JPanel serverBox = new JPanel();
        serverBox.setLayout(new BoxLayout(serverBox, BoxLayout.Y_AXIS));
        serverBox.setBackground(new Color(255, 247, 248));
        serverBox.setBorder(this.createServerBoxBorder());
        serverBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        serverBox.setPreferredSize(new Dimension(serverBoxWidth, serverBoxHeight));
        serverBox.setMaximumSize(new Dimension(serverBoxWidth, serverBoxHeight));
        serverBox.setMinimumSize(new Dimension(serverBoxWidth, serverBoxHeight));

        this.configLabel = this.createConfigLabel(configLabelWidth);

        JButton refreshButton = new JButton("טען הגדרות שרת");
        Main.styleButton(refreshButton, Main.SOFT_PINK);
        refreshButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        refreshButton.addActionListener(e -> this.loadServerConfig());

        serverBox.add(this.configLabel);
        serverBox.add(Box.createVerticalStrut(10));
        serverBox.add(refreshButton);

        return serverBox;
    }

    private Border createServerBoxBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(248, 222, 228), 1),
                new EmptyBorder(12, 15, 12, 15)
        );
    }

    private JLabel createConfigLabel(int configLabelWidth) {
        JLabel label = new JLabel("הגדרות העיצוב מהשרת יופיעו כאן לאחר טעינה", JLabel.CENTER);
        label.setFont(MAIN_FONT);
        label.setForeground(Main.TEXT_DARK);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setPreferredSize(new Dimension(configLabelWidth, 30));
        label.setMaximumSize(new Dimension(configLabelWidth, 30));
        label.setMinimumSize(new Dimension(configLabelWidth, 30));

        return label;
    }

    private JLabel createSizeTitleLabel() {
        JLabel sizeTitle = new JLabel("קביעת גודל המבוך (בין 5 ל-100):");
        sizeTitle.setFont(Main.LABEL_FONT);
        sizeTitle.setForeground(Main.TEXT_DARK);
        sizeTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        return sizeTitle;
    }

    private JPanel createFieldsPanel() {
        JPanel fieldsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        fieldsPanel.setBackground(CARD_BG);

        this.widthField = new JTextField(String.valueOf(this.mazeWidth), 5);
        this.heightField = new JTextField(String.valueOf(this.mazeHeight), 5);

        this.styleTextField(this.widthField);
        this.styleTextField(this.heightField);

        fieldsPanel.add(this.createInputBox("רוחב", this.widthField));
        fieldsPanel.add(this.createInputBox("גובה", this.heightField));

        return fieldsPanel;
    }

    private JPanel createInputBox(String labelText, JTextField textField) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(CARD_BG);

        JLabel label = new JLabel(labelText);
        label.setFont(Main.LABEL_FONT);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        box.add(label);
        box.add(Box.createVerticalStrut(5));
        box.add(textField);

        return box;
    }

    private JButton createGetMazeButton() {
        this.getMazeButton = new JButton("צור מבוך חדש");
        Main.styleButton(this.getMazeButton, Main.DEEP_PINK);
        this.getMazeButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        this.getMazeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.getMazeButton.setVisible(false);
        this.getMazeButton.setEnabled(false); // יהפוך לפעיל רק לאחר קבלת הגדרות השרת
        this.getMazeButton.addActionListener(e -> this.handleGetMazeClick());

        return this.getMazeButton;
    }

    private JLabel createErrorLabel() {
        this.errorLabel = new JLabel(" ", JLabel.CENTER);
        this.errorLabel.setForeground(Main.ERROR_COLOR);
        this.errorLabel.setFont(Main.LABEL_FONT);
        this.errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        return this.errorLabel;
    }

    // פונקציה המטפלת בלחיצה על יצירת המבוך - מוודאת תקינות קלט לפני הפניה לשרת
    private void handleGetMazeClick() {
        this.errorLabel.setText(" ");

        int checkedWidth = this.validateInput(this.widthField, "הרוחב");
        int checkedHeight = this.validateInput(this.heightField, "הגובה");

        boolean hasError = checkedWidth == -1 || checkedHeight == -1;

        if (checkedWidth == -1) {
            checkedWidth = DEFAULT_MAZE_SIZE;
            this.widthField.setText(String.valueOf(DEFAULT_MAZE_SIZE));
        }

        if (checkedHeight == -1) {
            checkedHeight = DEFAULT_MAZE_SIZE;
            this.heightField.setText(String.valueOf(DEFAULT_MAZE_SIZE));
        }

        this.mazeWidth = checkedWidth;
        this.mazeHeight = checkedHeight;

        // אם הייתה שגיאת קלט, ממתינים שהמשתמש יראה את הודעת השגיאה לפני הטעינה
        if (hasError) {
            this.waitTwoSecondsAndLoadMaze();
        } else {
            this.fetchAndReadMazeImage();
        }
    }

    private void waitTwoSecondsAndLoadMaze() {
        this.getMazeButton.setEnabled(false);

        Timer timer = new Timer(2000, event -> {
            this.getMazeButton.setEnabled(true);
            this.errorLabel.setText(" ");
            this.fetchAndReadMazeImage();
        });

        timer.setRepeats(false);
        timer.start();
    }

    // פניה לשרת לקבלת הגדרות (תרוץ בתהליך נפרד - Thread - כדי לא לתקוע את המסך)
    private void loadServerConfig() {
        this.configLabel.setText("מתחבר לשרת ומביא הגדרות...");
        this.getMazeButton.setVisible(false);
        this.getMazeButton.setEnabled(false);

        new Thread(() -> {
            try {
                JSONObject dataAsJson = this.apiService.fetchRenderConfig();
                this.saveConfigData(dataAsJson);

                // עדכון ה-GUI חייב להתבצע בחזרה בתהליך הראשי (SwingUtilities)
                SwingUtilities.invokeLater(() -> {
                    this.configLabel.setText(this.createConfigText());
                    this.getMazeButton.setVisible(true);
                    this.getMazeButton.setEnabled(true);
                    this.revalidate();
                    this.repaint();
                });

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    this.configLabel.setText("שגיאה בהבאת נתונים מהשרת.");
                    this.getMazeButton.setVisible(false);
                    this.getMazeButton.setEnabled(false);
                });

                ex.printStackTrace();
            }
        }).start();
    }

    private void saveConfigData(JSONObject dataAsJson) {
        this.wallColor = dataAsJson.getString("wallCellColor");
        this.pathColor = dataAsJson.getString("pathColor");
        this.drawGrid = dataAsJson.getBoolean("drawGrid");
        this.gridColor = dataAsJson.getString("gridColor");
        this.animationDelay = dataAsJson.getInt("animationDelayMs");
    }

    private String createConfigText() {
        return String.format(
                "צבע הקירות: %s | צבע הנתיב: %s | האם יהיו קווי רשת? %b | צבע קווי הרשת: %s | זמן השהיה: %d ms",
                this.wallColor,
                this.pathColor,
                this.drawGrid,
                this.gridColor,
                this.animationDelay
        );
    }

    // פניה לשרת לקבלת תמונת המבוך והמרתה למערך
    private void fetchAndReadMazeImage() {
        this.errorLabel.setText("טוען וקורא מבוך מהשרת...");
        this.getMazeButton.setEnabled(false);

        new Thread(() -> {
            try {
                BufferedImage mazeImage = this.apiService.fetchMazeImage(this.mazeWidth, this.mazeHeight);
                this.mazePixel = this.convertImageToBooleanMatrix(mazeImage);

                SwingUtilities.invokeLater(this::showMazePanel);

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    this.errorLabel.setText("שגיאה: לא הצלחנו לקרוא את תמונת המבוך.");
                    this.getMazeButton.setEnabled(true);
                });

                ex.printStackTrace();
            }
        }).start();
    }

    // מעבר למסך המבוך עם כל הנתונים שאספנו
    private void showMazePanel() {
        MazePanel mazePanel = new MazePanel(
                // השינוי לאפשרות 2: מעבירים את ההוראות (Runnable) במקום את ה-JPanel!
                () -> {
                    this.prepareForNewMaze();
                    Main.showPanel(this);
                },
                this.mazePixel,
                this.decodeColor(this.wallColor, Color.BLACK),
                this.decodeColor(this.pathColor, Color.WHITE),
                this.drawGrid,
                this.decodeColor(this.gridColor, Color.GRAY),
                this.animationDelay
        );

        Main.showPanel(mazePanel);
    }

    /**
     * פונקציה חכמה הממירה את התמונה הענקית שהתקבלה מהשרת לתוך מטריצה בגודל הלוגי שהמשתמש ביקש.
     * התמונה מחולקת ל"בלוקים", וכל בלוק הופך לתא אחד במטריצה הסופית.
     */
    private boolean[][] convertImageToBooleanMatrix(BufferedImage mazeImage) {
        int imageWidth = mazeImage.getWidth();
        int imageHeight = mazeImage.getHeight();

        int rows = this.mazeHeight;
        int cols = this.mazeWidth;

        boolean[][] matrix = new boolean[rows][cols];

        // חישוב כמות הפיקסלים בתמונה המקורית שמרכיבה כל תא לוגי אחד במבוך שלנו
        double blockWidth = (double) imageWidth / cols;
        double blockHeight = (double) imageHeight / rows;

        int pathCells = 0;
        int wallCells = 0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                boolean isPath = this.isPathBlock(mazeImage, row, col, blockWidth, blockHeight);
                matrix[row][col] = isPath;

                if (isPath) {
                    pathCells++;
                } else {
                    wallCells++;
                }
            }
        }

        this.printMazeData(imageWidth, imageHeight, matrix, pathCells, wallCells);

        return matrix;
    }

    // פונקציה הסורקת בלוק פיקסלים מסוים ומכריעה האם רובו שביל (פתוח) או קיר
    private boolean isPathBlock(
            BufferedImage mazeImage,
            int row,
            int col,
            double blockWidth,
            double blockHeight
    ) {
        int imageWidth = mazeImage.getWidth();
        int imageHeight = mazeImage.getHeight();

        int startX = (int) Math.floor(col * blockWidth);
        int endX = (int) Math.floor((col + 1) * blockWidth);
        int startY = (int) Math.floor(row * blockHeight);
        int endY = (int) Math.floor((row + 1) * blockHeight);

        // שימוש בפונקציית העזר כדי להבטיח שלא נחרוג מגבולות התמונה הפיזית
        startX = this.limitToRange(startX, 0, imageWidth - 1);
        startY = this.limitToRange(startY, 0, imageHeight - 1);
        endX = this.limitToRange(endX, startX + 1, imageWidth);
        endY = this.limitToRange(endY, startY + 1, imageHeight);

        int whitePixels = 0;
        int wallPixels = 0;

        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                Color pixelColor = new Color(mazeImage.getRGB(x, y), true);

                if (this.isWhite(pixelColor)) {
                    whitePixels++;
                } else {
                    wallPixels++;
                }
            }
        }

        // אם יש יותר פיקסלים לבנים משחורים, זהו מעבר פתוח (Path)
        return whitePixels >= wallPixels;
    }

    // פונקציית עזר המגבילה ערך להיות בתוך טווח מותר (מונעת חריגה ממערכים)
    private int limitToRange(int value, int minValue, int maxValue) {
        if (value < minValue) {
            return minValue;
        }

        if (value > maxValue) {
            return maxValue;
        }

        return value;
    }

    // רק לבדיקה שהגדלים תקינים (מודפס לקונסול)
    private void printMazeData(int imageWidth, int imageHeight, boolean[][] matrix, int pathCells, int wallCells) {
        System.out.println("requested maze width = " + this.mazeWidth);
        System.out.println("requested maze height = " + this.mazeHeight);
        System.out.println("image width = " + imageWidth);
        System.out.println("image height = " + imageHeight);
        System.out.println("matrix rows = " + matrix.length);
        System.out.println("matrix cols = " + matrix[0].length);
        System.out.println("path cells = " + pathCells);
        System.out.println("wall cells = " + wallCells);
    }

    // בודקת האם הצבע הנתון הוא לבן (או קרוב מאוד ללבן)
    private boolean isWhite(Color color) {
        return color.getRed() > 230
                && color.getGreen() > 230
                && color.getBlue() > 230;
    }

    private Color decodeColor(String colorText, Color defaultColor) {
        try {
            return Color.decode(colorText);
        } catch (Exception e) {
            return defaultColor;
        }
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

            return -1;

        } catch (NumberFormatException e) {
            this.errorLabel.setText(
                    "שגיאה: הקלט בתוך " + fieldName +
                            " אינו מספר חוקי. שונה ל-" + DEFAULT_MAZE_SIZE + "."
            );

            return -1;
        }
    }

    // פונקציה הנקראת כשחוזרים אחורה למסך זה - מנקה שגיאות ומאפשרת יצירת מבוך חדש
    public void prepareForNewMaze() {
        this.errorLabel.setText(" ");
        this.getMazeButton.setVisible(true);
        this.getMazeButton.setEnabled(true);
        this.revalidate();
        this.repaint();
    }

    public int getMazeWidth() {
        return this.mazeWidth;
    }

    public int getMazeHeight() {
        return this.mazeHeight;
    }
}