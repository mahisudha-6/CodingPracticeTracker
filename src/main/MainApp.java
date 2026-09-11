package main;

import ui.MainFrame;

import javax.swing.*;

/**
 * Entry point launcher for the Coding Practice Tracker desktop application.
 */
public class MainApp {

    public static void main(String[] args) {
        // Set native OS Look and Feel for clean look
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("System Look and Feel could not be applied. Falling back to default.");
        }

        // Run Swing application on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    MainFrame frame = new MainFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, 
                            "A fatal error occurred while starting the application:\n" + e.getMessage(), 
                            "Startup Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        });
    }
}
