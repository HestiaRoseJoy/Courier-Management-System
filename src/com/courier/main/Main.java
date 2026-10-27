package com.courier.main;

import com.courier.gui.DashboardFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main Class - Application Entry Point
 * 
 * Sets the system Look and Feel for native Windows styling
 * and launches the DashboardFrame on the Swing Event Dispatch Thread (EDT).
 */
public class Main {

    public static void main(String[] args) {
        // Run GUI construction on Event Dispatch Thread for thread safety
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Set native system look and feel for modern window appearance
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    System.out.println("Using default Swing Look and Feel: " + e.getMessage());
                }

                // Initialize and display main Dashboard
                DashboardFrame dashboard = new DashboardFrame();
                dashboard.setVisible(true);
            }
        });
    }
}
