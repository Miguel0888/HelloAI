package org.example;

import javax.swing.*;
import java.awt.*;

/**
 * Draws and updates the ball's position.
 */
public class BallPanel extends JPanel {
    private static final int BALL_SIZE = 30;

    private int x = 0;
    private int y = 0;
    private int dx = 2;
    private int dy = 2;

    public void moveBall() {
        x += dx;
        y += dy;

        if (x < 0 || x + BALL_SIZE > getWidth()) {
            dx = -dx;
        }

        if (y < 0 || y + BALL_SIZE > getHeight()) {
            dy = -dy;
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.RED);
        g.fillOval(x, y, BALL_SIZE, BALL_SIZE);
    }
}
