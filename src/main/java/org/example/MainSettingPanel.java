package org.example;

import javax.swing.*;
import java.util.Scanner;

public class MainSettingPanel extends JPanel {
    private int width;
    private int height;
    private JButton getMazeButton;
    private JButton refreshButton;

    public MainSettingPanel(int x, int y, int windowWidth, int windowHeight) {
        this.setBounds(x, y, windowWidth, windowHeight);
        Scanner scanner = new Scanner(System.in);
        this.width = scanner.nextInt();
        this.height = scanner.nextInt();
    }

    JButton getMaze = new JButton();

}
