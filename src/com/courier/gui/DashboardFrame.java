package com.courier.gui;

import com.courier.db.DatabaseConnection;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * DashboardFrame Class
 * 
 * Main administrative dashboard for the Courier Management System.
 * Provides intuitive navigation to Book Courier, Track Courier,
 * Update Status, View Shipments, and Exit.
 */
public class DashboardFrame extends JFrame implements ActionListener {

    private JButton btnBook;
    private JButton btnTrack;
    private JButton btnUpdate;
    private JButton btnViewAll;
    private JButton btnExit;
    private JLabel lblDbStatus;

    public DashboardFrame() {
        setTitle("Courier Management System - Dashboard");
        setSize(780, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setResizable(false);
        setLayout(new BorderLayout(15, 15));

        initComponents();
        checkDatabaseStatus();
    }

    private void initComponents() {
        // -------------------------------------------------------------
        // 1. Header Panel
        // -------------------------------------------------------------
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(24, 43, 73)); // Dark Navy
        headerPanel.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));

        JLabel lblTitle = new JLabel("COURIER MANAGEMENT SYSTEM", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel("Fast, Reliable & Secure Parcel Logistics | KTU S3 Java OOP Project", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(180, 205, 237));

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // -------------------------------------------------------------
        // 2. Center Grid of Action Buttons
        // -------------------------------------------------------------
        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 20, 40));
        centerPanel.setBackground(new Color(245, 247, 250));

        btnBook = createStyledButton("Book Courier", "Register a new parcel shipment", new Color(33, 115, 70));
        btnTrack = createStyledButton("Track Courier", "Track parcel location by Tracking ID", new Color(31, 110, 165));
        btnUpdate = createStyledButton("Update Status", "Update parcel delivery progress", new Color(195, 75, 0));
        btnViewAll = createStyledButton("View Shipments", "Display all parcels in data table", new Color(110, 57, 160));

        centerPanel.add(btnBook);
        centerPanel.add(btnTrack);
        centerPanel.add(btnUpdate);
        centerPanel.add(btnViewAll);

        add(centerPanel, BorderLayout.CENTER);

        // -------------------------------------------------------------
        // 3. Footer Panel with Database Status & Exit
        // -------------------------------------------------------------
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 25, 15, 25));
        footerPanel.setBackground(new Color(245, 247, 250));

        lblDbStatus = new JLabel("Checking MySQL Connection...");
        lblDbStatus.setFont(new Font("Segoe UI", Font.ITALIC, 12));

        btnExit = new JButton("Exit Application");
        btnExit.setUI(new BasicButtonUI());
        btnExit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnExit.setForeground(Color.WHITE);
        btnExit.setBackground(new Color(192, 57, 43));
        btnExit.setOpaque(true);
        btnExit.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnExit.setFocusPainted(false);
        btnExit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExit.addActionListener(this);

        footerPanel.add(lblDbStatus, BorderLayout.WEST);
        footerPanel.add(btnExit, BorderLayout.EAST);

        add(footerPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String title, String desc, Color baseColor) {
        JButton button = new JButton("<html><center><font size='5'><b>" + title + "</b></font><br><font size='3' color='#F0F4F8'>" + desc + "</font></center></html>");
        button.setUI(new BasicButtonUI());
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(baseColor);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        button.addActionListener(this);
        return button;
    }

    public void checkDatabaseStatus() {
        boolean connected = DatabaseConnection.testConnection();
        if (connected) {
            lblDbStatus.setText("● MySQL Database: Connected (courier_db)");
            lblDbStatus.setForeground(new Color(39, 174, 96));
        } else {
            lblDbStatus.setText("● MySQL Database: Not connected (Check db.properties)");
            lblDbStatus.setForeground(new Color(211, 84, 0));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBook) {
            // Open the Courier Booking Form
            BookingFrame bookingFrame = new BookingFrame(this);
            bookingFrame.setVisible(true);
            this.setVisible(false); // Hide dashboard while booking form is active
        } else if (e.getSource() == btnTrack) {
            // Open the Courier Tracking Form
            TrackingFrame trackingFrame = new TrackingFrame(this);
            trackingFrame.setVisible(true);
            this.setVisible(false); // Hide dashboard while tracking form is active
        } else if (e.getSource() == btnUpdate) {
            // Open the Update Status Form
            UpdateStatusFrame updateFrame = new UpdateStatusFrame(this);
            updateFrame.setVisible(true);
            this.setVisible(false); // Hide dashboard while update form is active
        } else if (e.getSource() == btnViewAll) {
            // Open the View Shipments Table
            ViewShipmentsFrame viewFrame = new ViewShipmentsFrame(this);
            viewFrame.setVisible(true);
            this.setVisible(false); // Hide dashboard while shipments table is active
        } else if (e.getSource() == btnExit) {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to exit the application?",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
    }
}
