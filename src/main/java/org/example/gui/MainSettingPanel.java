package org.example.gui;

import org.example.Main;
import org.example.api.MazeApiService;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class MainSettingPanel extends JPanel {

    private static final int DEFAULT_MAZE_SIZE = 30;
    private static final int MIN_MAZE_SIZE = 5;
    private static final int MAX_MAZE_SIZE = 100;

    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    private static final Color WINDOW_BG = new Color(253, 242, 244);
    private static final Color CARD_BG = Color.WHITE;

    private JTextField widthField;
    private JTextField heightField;
    private JLabel configLabel;
    private JLabel errorLabel;

    // הפכנו את הכפתור לשדה מחלקה כדי שנוכל להסתיר/להציג אותו
    private JButton getMazeButton;

    private String wallColor;
    private String pathColor;
    private boolean drawGrid;
    private String gridColor;
    private int animationDelay;
    private int mazeWidth;
    private int mazeHeight;
    private boolean[][] mazePixel;

    private final MazeApiService apiService;

    public MainSettingPanel(int x, int y, int windowWidth, int windowHeight) {
        this.apiService = new MazeApiService();

        this.mazeWidth = DEFAULT_MAZE_SIZE;
        this.mazeHeight = DEFAULT_MAZE_SIZE;

        this.setBounds(x, y, windowWidth, windowHeight);
        this.setBackground(WINDOW_BG);
        this.setLayout(new GridBagLayout());

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
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(243, 213, 220), 1),
                new EmptyBorder(30, 40, 30, 40)
        ));

        JLabel titleLabel = new JLabel("הגדרות ליצירת מבוך");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Main.DEEP_PINK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(20));

        JPanel serverBox = new JPanel();
        serverBox.setLayout(new BoxLayout(serverBox, BoxLayout.Y_AXIS));
        serverBox.setBackground(new Color(255, 247, 248));
        serverBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(248, 222, 228), 1),
                new EmptyBorder(12, 15, 12, 15)
        ));
        serverBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        serverBox.setPreferredSize(new Dimension(serverBoxWidth, serverBoxHeight));
        serverBox.setMaximumSize(new Dimension(serverBoxWidth, serverBoxHeight));
        serverBox.setMinimumSize(new Dimension(serverBoxWidth, serverBoxHeight));

        this.configLabel = new JLabel("הגדרות העיצוב מהשרת יופיעו כאן לאחר טעינה", JLabel.CENTER);
        this.configLabel.setFont(MAIN_FONT);
        this.configLabel.setForeground(Main.TEXT_DARK);
        this.configLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.configLabel.setPreferredSize(new Dimension(configLabelWidth, 30));
        this.configLabel.setMaximumSize(new Dimension(configLabelWidth, 30));
        this.configLabel.setMinimumSize(new Dimension(configLabelWidth, 30));

        JButton refreshButton = new JButton("טען הגדרות שרת");
        Main.styleButton(refreshButton, Main.SOFT_PINK);
        refreshButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        refreshButton.addActionListener(e -> loadServerConfig());

        serverBox.add(configLabel);
        serverBox.add(Box.createVerticalStrut(10));
        serverBox.add(refreshButton);

        card.add(serverBox);
        card.add(Box.createVerticalStrut(25));

        JLabel sizeTitle = new JLabel("קביעת גודל המבוך (בין 5 ל-100):");
        sizeTitle.setFont(Main.LABEL_FONT);
        sizeTitle.setForeground(Main.TEXT_DARK);
        sizeTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(sizeTitle);
        card.add(Box.createVerticalStrut(10));

        JPanel fieldsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        fieldsPanel.setBackground(CARD_BG);

        JPanel widthBox = new JPanel();
        widthBox.setLayout(new BoxLayout(widthBox, BoxLayout.Y_AXIS));
        widthBox.setBackground(CARD_BG);

        JLabel widthLabel = new JLabel("רוחב");
        widthLabel.setFont(Main.LABEL_FONT);
        widthLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.widthField = new JTextField(String.valueOf(mazeWidth), 5);
        styleTextField(widthField);

        widthBox.add(widthLabel);
        widthBox.add(Box.createVerticalStrut(5));
        widthBox.add(widthField);

        JPanel heightBox = new JPanel();
        heightBox.setLayout(new BoxLayout(heightBox, BoxLayout.Y_AXIS));
        heightBox.setBackground(CARD_BG);

        JLabel heightLabel = new JLabel("גובה");
        heightLabel.setFont(Main.LABEL_FONT);
        heightLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.heightField = new JTextField(String.valueOf(mazeHeight), 5);
        styleTextField(heightField);

        heightBox.add(heightLabel);
        heightBox.add(Box.createVerticalStrut(5));
        heightBox.add(heightField);

        fieldsPanel.add(widthBox);
        fieldsPanel.add(heightBox);

        card.add(fieldsPanel);
        card.add(Box.createVerticalStrut(25));

        // כפתור יצירת המבוך משתמש עכשיו בשדה המחלקה
        this.getMazeButton = new JButton("צור מבוך חדש");
        Main.styleButton(this.getMazeButton, Main.DEEP_PINK);
        this.getMazeButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        this.getMazeButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // מוסתר כברירת מחדל כדי למנוע קריסה (עד שהצבעים יגיעו מהשרת)
        this.getMazeButton.setVisible(false);

        this.getMazeButton.addActionListener(e -> {
            this.errorLabel.setText(" ");

            this.mazeWidth = validateInput(widthField, "הרוחב");
            this.mazeHeight = validateInput(heightField, "הגובה");

            widthField.setText(String.valueOf(this.mazeWidth));
            heightField.setText(String.valueOf(this.mazeHeight));

            fetchAndReadMazeImage();
        });

        card.add(this.getMazeButton);
        card.add(Box.createVerticalStrut(15));

        this.errorLabel = new JLabel(" ", JLabel.CENTER);
        this.errorLabel.setForeground(Main.ERROR_COLOR);
        this.errorLabel.setFont(Main.LABEL_FONT);
        this.errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(errorLabel);
        this.add(card);
    }

    private void loadServerConfig() {
        this.configLabel.setText("מתחבר לשרת ומביא הגדרות...");

        new Thread(() -> {
            try {
                JSONObject dataAsJson = apiService.fetchRenderConfig();

                this.wallColor = dataAsJson.getString("wallCellColor");
                this.pathColor = dataAsJson.getString("pathColor");
                this.drawGrid = dataAsJson.getBoolean("drawGrid");
                this.gridColor = dataAsJson.getString("gridColor");
                this.animationDelay = dataAsJson.getInt("animationDelayMs");

                String configText = String.format(
                        "צבע הקירות: %s | צבע הנתיב: %s | האם יהיו קווי רשת? %b | צבע קווי הרשת: %s | זמן השהיה: %d ms",
                        this.wallColor,
                        this.pathColor,
                        this.drawGrid,
                        this.gridColor,
                        this.animationDelay
                );

                SwingUtilities.invokeLater(() -> {
                    this.configLabel.setText(configText);
                    // הנתונים הגיעו בהצלחה - עכשיו אפשר להציג את הכפתור בבטחה!
                    this.getMazeButton.setVisible(true);
                    this.revalidate();
                    this.repaint();
                });

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> this.configLabel.setText("שגיאה בהבאת נתונים מהשרת."));
                ex.printStackTrace();
            }
        }).start();
    }

    private void fetchAndReadMazeImage() {
        this.errorLabel.setText("טוען וקורא מבוך מהשרת...");

        new Thread(() -> {
            try {
                BufferedImage mazeImage = this.apiService.fetchMazeImage(this.mazeWidth, this.mazeHeight);

                // שינוי קריטי: אנחנו מעבירים גם את הגודל הלוגי כדי ליצור מערך בגודל 30x30!
                this.mazePixel = this.convertImageToBooleanMatrix(mazeImage, this.mazeWidth, this.mazeHeight);

                SwingUtilities.invokeLater(() -> {
                    this.errorLabel.setText("המבוך נקרא בהצלחה מהשרת.");

                    MazePanel mazePanel = new MazePanel(
                            MainSettingPanel.this,
                            this.mazePixel,
                            Color.decode(this.wallColor),
                            Color.decode(this.pathColor),
                            this.drawGrid,
                            Color.decode(this.gridColor),
                            this.animationDelay
                    );

                    JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(MainSettingPanel.this);

                    if (mainFrame != null) {
                        mainFrame.setContentPane(mazePanel);
                        mainFrame.revalidate();
                        mainFrame.repaint();
                    }
                });

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() ->
                        this.errorLabel.setText("שגיאה: לא הצלחנו לקרוא את תמונת המבוך.")
                );
                ex.printStackTrace();
            }
        }).start();
    }

    // פונקציה משודרגת - מכווצת את התמונה המקורית למערך לוגי!
    private boolean[][] convertImageToBooleanMatrix(BufferedImage mazeImage, int logicalWidth, int logicalHeight) {
        int imgW = mazeImage.getWidth();
        int imgH = mazeImage.getHeight();

        BufferedImage normalizedImg = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = normalizedImg.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, imgW, imgH);
        g2d.drawImage(mazeImage, 0, 0, null);
        g2d.dispose();

        // יצירת מערך לפי הגודל של המבוך (למשל 30x30)
        boolean[][] matrix = new boolean[logicalHeight][logicalWidth];

        // חישוב הגודל של כל "בלוק" בתמונה הענקית
        double blockW = (double) imgW / logicalWidth;
        double blockH = (double) imgH / logicalHeight;

        for (int y = 0; y < logicalHeight; y++) {
            for (int x = 0; x < logicalWidth; x++) {
                // לוקחים פיקסל מייצג ממרכז הבלוק
                int pixelX = (int) (x * blockW + blockW / 2);
                int pixelY = (int) (y * blockH + blockH / 2);

                // הגנה מחריגה מגבולות התמונה
                pixelX = Math.min(pixelX, imgW - 1);
                pixelY = Math.min(pixelY, imgH - 1);

                int rgb = normalizedImg.getRGB(pixelX, pixelY);
                Color pixelColor = new Color(rgb);

                if (pixelColor.getRed() > 240 && pixelColor.getGreen() > 240 && pixelColor.getBlue() > 240) {
                    matrix[y][x] = true;
                } else {
                    matrix[y][x] = false;
                }
            }
        }
        return matrix;
    }

    private void openMazeImageInBrowser(BufferedImage mazeImage) throws Exception {
        File mazeFile = File.createTempFile("maze-", ".png");
        ImageIO.write(mazeImage, "png", mazeFile);
        Desktop.getDesktop().browse(mazeFile.toURI());
    }

    // הפונקציה החסינה שמתקנת שקיפויות ומונעת באגים!
    private boolean[][] convertImageToBooleanMatrix(BufferedImage mazeImage) {
        int w = mazeImage.getWidth();
        int h = mazeImage.getHeight();

        BufferedImage normalizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = normalizedImg.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, w, h);
        g2d.drawImage(mazeImage, 0, 0, null);
        g2d.dispose();

        boolean[][] matrix = new boolean[h][w];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = normalizedImg.getRGB(x, y);
                Color pixelColor = new Color(rgb);

                if (pixelColor.getRed() > 240 && pixelColor.getGreen() > 240 && pixelColor.getBlue() > 240) {
                    matrix[y][x] = true;
                } else {
                    matrix[y][x] = false;
                }
            }
        }
        return matrix;
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