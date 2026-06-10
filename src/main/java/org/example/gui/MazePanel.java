package org.example.gui;

import org.example.Main;

import javax.swing.*;
import java.awt.*;

public class MazePanel extends JPanel {

    private final JPanel previousPanel;
    private int animationDelay;

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

        setLayout(new BorderLayout());
        setBackground(Main.LIGHT_PINK);
        setOpaque(true);

        MazeCanvas mazeCanvas = new MazeCanvas(
                mazeMatrix,
                wallColor,
                pathColor,
                drawGrid,
                gridColor
        );

        add(mazeCanvas, BorderLayout.CENTER);

        JButton backButton = new JButton("חזור");
        Main.styleButton(backButton, Main.SOFT_PINK);
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 15));

        backButton.addActionListener(e -> {
            if (previousPanel instanceof MainSettingPanel) {
                MainSettingPanel settingPanel = (MainSettingPanel) previousPanel;
                settingPanel.prepareForNewMaze();
            }

            Main.showPanel(previousPanel);
        });

        JButton checkSolution = new JButton("בדוק פתרון");
        Main.styleButton(checkSolution, Main.DEEP_PINK);
        checkSolution.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Main.LIGHT_PINK);
        buttonPanel.add(backButton);
        buttonPanel.add(checkSolution);

        add(buttonPanel, BorderLayout.SOUTH);
    }
}