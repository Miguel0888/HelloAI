package org.example;

import javax.swing.*;

/**
 * Entry point of the bouncing ball application.
 */
public class BouncingBallApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGui();
            }
        });
    }

    private static void createAndShowGui() {
        JFrame frame = new JFrame("Springender Ball");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null); // zentriere das Fenster

        BallPanel ballPanel = getBallPanel(frame);

        BallAnimator animator = new BallAnimator(ballPanel);
        animator.start();
    }

    private static BallPanel getBallPanel(JFrame frame) {
        BallPanel ballPanel = new BallPanel();
        frame.add(ballPanel);
        frame.setVisible(true);
        return ballPanel;
    }
}
