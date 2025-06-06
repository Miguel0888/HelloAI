package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controls the ball animation using a Swing Timer.
 */
public class BallAnimator {
    private static final int TIMER_DELAY_MS = 10;

    private final BallPanel ballPanel;
    private final Timer timer;

    public BallAnimator(final BallPanel ballPanel) {
        this.ballPanel = ballPanel;
        this.timer = new Timer(TIMER_DELAY_MS, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                update();
            }
        });
    }

    public void start() {
        timer.start();
    }

    private void update() {
        ballPanel.moveBall();
        ballPanel.repaint();
    }
}
