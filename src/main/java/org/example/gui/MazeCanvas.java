package org.example.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class MazeCanvas extends JPanel {

    private boolean[][] mazeMatrix;
    private Color wallColor;
    private Color pathColor;
    private boolean drawGrid;
    private Color gridColor;

    public MazeCanvas(
            boolean[][] mazeMatrix,
            Color wallColor,
            Color pathColor,
            boolean drawGrid,
            Color gridColor
    ) {
        this.mazeMatrix = mazeMatrix;
        this.wallColor = wallColor;
        this.pathColor = pathColor;
        this.drawGrid = drawGrid;
        this.gridColor = gridColor;

        setBackground(new Color(253, 242, 244));
        setOpaque(true);    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (mazeMatrix == null || mazeMatrix.length == 0 || mazeMatrix[0].length == 0) {
            return;
        }

        int rows = mazeMatrix.length;
        int cols = mazeMatrix[0].length;

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        double cellSize = Math.min(
                (double) panelWidth / cols,
                (double) panelHeight / rows
        );

        if (cellSize <= 0) {
            return;
        }

        double mazeWidth = cols * cellSize;
        double mazeHeight = rows * cellSize;

        double startX = (panelWidth - mazeWidth) / 2.0;
        double startY = (panelHeight - mazeHeight) / 2.0;

        Graphics2D g2 = (Graphics2D) g;

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {

                if (mazeMatrix[y][x]) {
                    g2.setColor(Color.WHITE);
                } else {
                    g2.setColor(wallColor);
                }

                double pixelX = startX + x * cellSize;
                double pixelY = startY + y * cellSize;

                g2.fill(new Rectangle2D.Double(
                        pixelX,
                        pixelY,
                        cellSize,
                        cellSize
                ));
            }
        }

        if (drawGrid && cellSize >= 4) {
            g2.setColor(gridColor);

            for (int x = 0; x <= cols; x++) {
                int lineX = (int) Math.round(startX + x * cellSize);
                g2.drawLine(
                        lineX,
                        (int) Math.round(startY),
                        lineX,
                        (int) Math.round(startY + mazeHeight)
                );
            }

            for (int y = 0; y <= rows; y++) {
                int lineY = (int) Math.round(startY + y * cellSize);
                g2.drawLine(
                        (int) Math.round(startX),
                        lineY,
                        (int) Math.round(startX + mazeWidth),
                        lineY
                );
            }
        }
    }
}