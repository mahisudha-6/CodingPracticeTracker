package util;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

/**
 * Reusable utility to apply a consistent, professional, and clean modern look
 * to JButton components throughout the application.
 */
public class ButtonStyleUtil {

    // Primary Theme Colors
    public static final Color COLOR_BLUE = Color.decode("#4F46E5");
    public static final Color COLOR_ORANGE = Color.decode("#F57C00");
    public static final Color COLOR_RED = Color.decode("#D32F2F");
    public static final Color COLOR_GRAY = Color.decode("#757575");
    public static final Color COLOR_DARK_GRAY = Color.decode("#424242");

    public static void applyBlueStyle(JButton button) {
        applyStyle(button, COLOR_BLUE);
    }

    public static void applyOrangeStyle(JButton button) {
        applyStyle(button, COLOR_ORANGE);
    }

    public static void applyRedStyle(JButton button) {
        applyStyle(button, COLOR_RED);
    }

    public static void applyGrayStyle(JButton button) {
        applyStyle(button, COLOR_GRAY);
    }

    public static void applyGrayStyle(JButton button, Dimension size) {
        applyStyle(button, COLOR_GRAY, size);
    }

    public static void applyDarkGrayStyle(JButton button) {
        applyStyle(button, COLOR_DARK_GRAY);
    }

    /**
     * Styles a JButton to be rounded, colored, and possess consistent sizes and hover behaviors.
     */
    public static void applyStyle(JButton button, Color backgroundColor) {
        applyStyle(button, backgroundColor, new Dimension(120, 40));
    }

    /**
     * Styles a JButton to be rounded, colored, and possess custom sizes and hover behaviors.
     */
    public static void applyStyle(JButton button, Color backgroundColor, Dimension size) {
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(backgroundColor);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Enforce consistent button size
        button.setPreferredSize(size);
        button.setMinimumSize(size);
        button.setMaximumSize(size);

        // Custom UI painting to draw clean rounded background
        button.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                AbstractButton b = (AbstractButton) c;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color drawColor;
                if (b.getModel().isPressed()) {
                    drawColor = backgroundColor.darker();
                } else if (b.getModel().isRollover()) {
                    drawColor = backgroundColor.brighter();
                } else {
                    drawColor = backgroundColor;
                }

                // Fill rounded rect background
                g2.setColor(drawColor);
                g2.fillRoundRect(0, 0, b.getWidth(), b.getHeight(), 10, 10);
                
                g2.dispose();
                super.paint(g, c); // Draws text and icon on top
            }
        });
    }
}
