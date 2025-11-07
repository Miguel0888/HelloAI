package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

/**
 * Draws and updates the ball's position.
 */
public class BallPanel extends JPanel {
    private static final int BALL_SIZE = 30;
    private static final int PADDLE_WIDTH = 80;
    private static final int PADDLE_HEIGHT = 12;
    // Bewegungs-Charakteristik des Paddles für flüssigere Steuerung
    private static final double PADDLE_ACC = 1.4;      // Beschleunigung pro Tick
    private static final double PADDLE_FRICTION = 0.88; // Trägheit/Abbremsen pro Tick
    private static final double MAX_PADDLE_SPEED = 12.0;
    private static final int PADDING_BOTTOM = 20;

    private final Random rnd = new Random();

    private int x = 0;
    private int y = 0;
    private int dx = 2;
    private int dy = 2;

    private double paddleX = 0.0;
    private int paddleY = 0; // will be positioned near bottom lazily when size is known
    private double paddleVx = 0.0;

    private boolean leftPressed = false;
    private boolean rightPressed = false;

    public BallPanel() {
        setFocusable(true);
        setBackground(Color.WHITE);
        setupKeyBindings();
    }

    private void setupKeyBindings() {
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        // Key-Pressed/-Released für kontinuierliche, verzögerungsfreie Eingabe
        im.put(KeyStroke.getKeyStroke("pressed LEFT"), "leftPressed");
        im.put(KeyStroke.getKeyStroke("released LEFT"), "leftReleased");
        im.put(KeyStroke.getKeyStroke("pressed RIGHT"), "rightPressed");
        im.put(KeyStroke.getKeyStroke("released RIGHT"), "rightReleased");

        am.put("leftPressed", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) { leftPressed = true; }
        });
        am.put("leftReleased", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) { leftPressed = false; }
        });
        am.put("rightPressed", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) { rightPressed = true; }
        });
        am.put("rightReleased", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) { rightPressed = false; }
        });
    }

    private void updatePaddle() {
        // Beschleunigen basierend auf Eingabe
        if (leftPressed && !rightPressed) {
            paddleVx -= PADDLE_ACC;
        } else if (rightPressed && !leftPressed) {
            paddleVx += PADDLE_ACC;
        } else {
            // Abbremseffekt für smoothes Auslaufen
            paddleVx *= PADDLE_FRICTION;
            if (Math.abs(paddleVx) < 0.05) paddleVx = 0;
        }

        // Maximalgeschwindigkeit begrenzen
        if (paddleVx > MAX_PADDLE_SPEED) paddleVx = MAX_PADDLE_SPEED;
        if (paddleVx < -MAX_PADDLE_SPEED) paddleVx = -MAX_PADDLE_SPEED;

        paddleX += paddleVx;

        // Grenzen beachten
        double minX = 0.0;
        double maxX = Math.max(0, getWidth() - PADDLE_WIDTH);
        if (paddleX < minX) { paddleX = minX; paddleVx = 0; }
        if (paddleX > maxX) { paddleX = maxX; paddleVx = 0; }
    }

    public void moveBall() {
        // Ensure paddle is positioned when size is known
        if (paddleY == 0 && getHeight() > 0) {
            paddleY = getHeight() - PADDING_BOTTOM - PADDLE_HEIGHT;
            // center paddle initially
            paddleX = Math.max(0, (getWidth() - PADDLE_WIDTH) / 2.0);
        }

        // Paddle-Update pro Tick (kontinuierliche Steuerung ohne Key-Repeat-Latenz)
        updatePaddle();

        x += dx;
        y += dy;

        // Bounce on left/right walls
        if (x < 0) {
            x = 0;
            dx = -dx;
        } else if (x + BALL_SIZE > getWidth()) {
            x = Math.max(0, getWidth() - BALL_SIZE);
            dx = -dx;
        }

        // Bounce on ceiling
        if (y < 0) {
            y = 0;
            dy = -dy;
        }

        // Paddle collision: only bounce if hitting the paddle while moving down
        boolean movingDown = dy > 0;
        int paddleXi = (int)Math.round(paddleX);
        boolean withinPaddleX = (x + BALL_SIZE) >= paddleXi && x <= (paddleXi + PADDLE_WIDTH);
        boolean atPaddleY = (y + BALL_SIZE) >= paddleY && (y + BALL_SIZE) <= (paddleY + PADDLE_HEIGHT);
        if (movingDown && withinPaddleX && atPaddleY) {
            y = paddleY - BALL_SIZE;
            dy = -dy;
            // Leichtes "Angle" je nach Trefferpunkt für mehr Spielgefühl
            int ballCenter = x + BALL_SIZE / 2;
            int paddleCenter = paddleXi + PADDLE_WIDTH / 2;
            int offset = ballCenter - paddleCenter; // negativ links, positiv rechts
            if (offset != 0) {
                dx += (int)Math.signum(offset) * Math.max(1, Math.abs(offset) / 12);
                // dx begrenzen, damit es nicht zu schnell wird
                if (dx > 10) dx = 10;
                if (dx < -10) dx = -10;
            }
        }

        // If ball passes below the bottom (missed paddle), let it fall through
        if (y > getHeight()) {
            // Simple reset to keep the game going
            resetBall();
        }
    }

    private void resetBall() {
        // spawn near top with random horizontal direction
        y = 0;
        x = rnd.nextInt(Math.max(1, Math.max(1, getWidth() - BALL_SIZE)));
        dy = Math.max(2, Math.abs(dy)); // ensure downward start
        dx = (rnd.nextBoolean() ? 1 : -1) * Math.max(2, Math.abs(dx));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            // Ball
            g2.setColor(Color.RED);
            g2.fillOval(x, y, BALL_SIZE, BALL_SIZE);

            // Paddle
            if (paddleY == 0 && getHeight() > 0) {
                paddleY = getHeight() - PADDING_BOTTOM - PADDLE_HEIGHT;
            }
            g2.setColor(new Color(30, 144, 255));
            g2.fillRect((int)Math.round(paddleX), paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);

            // Ground line (visual reference)
            g2.setColor(new Color(220, 220, 220));
            int groundY = getHeight() - PADDING_BOTTOM;
            g2.drawLine(0, groundY, getWidth(), groundY);
        } finally {
            g2.dispose();
        }
    }
}
