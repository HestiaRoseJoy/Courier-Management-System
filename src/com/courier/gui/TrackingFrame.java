package com.courier.gui;

import com.courier.db.CourierDAO;
import com.courier.model.Courier;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * TrackingFrame Class
 * 
 * Part of Phase 2: Track Courier Feature
 * 
 * Demonstrates KTU S3 Java OOP Concepts:
 * 1. SWING GUI: Clean layout with GridBagLayout, TitledBorder, and consistent color styling.
 * 2. RUNTIME POLYMORPHISM: Base class 'Courier' reference holds StandardCourier or ExpressCourier.
 * 3. EXCEPTION HANDLING: Catching SQL errors and validating user input.
 * 4. JDBC INTEGRATION: Queries database using CourierDAO and PreparedStatement.
 * 5. ENCAPSULATION: Retrieves parcel state through standard getters.
 */
public class TrackingFrame extends JFrame implements ActionListener {

    private final DashboardFrame parentDashboard;
    private final CourierDAO courierDAO;

    // Search Controls
    private JTextField txtSearchId;
    private JButton btnSearch;
    private JButton btnClear;
    private JLabel lblStatusMessage;

    // Result Display Fields (Read-only)
    private JTextField txtResultTrackingId;
    private JTextField txtResultStatus;
    private JTextField txtResultSender;
    private JTextField txtResultReceiver;
    private JTextField txtResultDestination;
    private JTextField txtResultType;
    private JTextField txtResultWeight;
    private JTextField txtResultCharge;

    private JButton btnBack;

    public TrackingFrame(DashboardFrame parentDashboard) {
        this.parentDashboard = parentDashboard;
        this.courierDAO = new CourierDAO();

        setTitle("Track Courier Shipment");
        setSize(750, 640);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));

        initComponents();

        // Restore dashboard on window closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                returnToDashboard();
            }
        });
    }

    private void initComponents() {
        // -------------------------------------------------------------
        // 1. Header Banner
        // -------------------------------------------------------------
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(31, 110, 165)); // Track Blue
        headerPanel.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JLabel lblTitle = new JLabel("TRACK COURIER SHIPMENT", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Enter your Tracking ID to view parcel transit status and details", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(220, 238, 252));

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSub, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // -------------------------------------------------------------
        // 2. Center Content Panel (Search Bar + Results Card)
        // -------------------------------------------------------------
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 10, 25));

        // Sub-panel 2A: Search Bar
        JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        searchBarPanel.setBackground(new Color(245, 248, 252));
        searchBarPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(205, 218, 235), 1),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));

        JLabel lblPrompt = new JLabel("Enter Tracking ID:");
        lblPrompt.setFont(new Font("Segoe UI", Font.BOLD, 13));

        txtSearchId = new JTextField(12);
        txtSearchId.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtSearchId.setPreferredSize(new Dimension(150, 34));
        txtSearchId.addActionListener(this); // Allows pressing Enter to search

        btnSearch = createStyledButton("Track Parcel", new Color(31, 110, 165), new Dimension(130, 34));
        btnClear = createStyledButton("Clear", new Color(85, 95, 105), new Dimension(90, 34));

        searchBarPanel.add(lblPrompt);
        searchBarPanel.add(txtSearchId);
        searchBarPanel.add(btnSearch);
        searchBarPanel.add(btnClear);

        // Sub-panel 2B: Status Message Label
        lblStatusMessage = new JLabel("Enter a Tracking ID (e.g. TRK1001, TRK1002) and click Track Parcel", SwingConstants.CENTER);
        lblStatusMessage.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatusMessage.setForeground(new Color(100, 110, 125));
        lblStatusMessage.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JPanel topContainer = new JPanel(new BorderLayout(5, 5));
        topContainer.setBackground(Color.WHITE);
        topContainer.add(searchBarPanel, BorderLayout.NORTH);
        topContainer.add(lblStatusMessage, BorderLayout.SOUTH);
        centerPanel.add(topContainer, BorderLayout.NORTH);

        // Sub-panel 2C: Result Details Panel (GridBagLayout inside TitledBorder)
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(205, 215, 225), 1),
                " Shipment Information ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13),
                new Color(31, 110, 165)
            ),
            BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 12, 6, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 12);

        // Row 0: Tracking ID & Current Status
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.18;
        JLabel lblTracking = new JLabel("Tracking ID:");
        lblTracking.setFont(labelFont);
        detailsPanel.add(lblTracking, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.32;
        txtResultTrackingId = createReadOnlyField(new Font("Segoe UI", Font.BOLD, 13), new Color(31, 110, 165));
        detailsPanel.add(txtResultTrackingId, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.18;
        JLabel lblStatus = new JLabel("Current Status:");
        lblStatus.setFont(labelFont);
        detailsPanel.add(lblStatus, gbc);

        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0.32;
        txtResultStatus = createReadOnlyField(new Font("Segoe UI", Font.BOLD, 13), new Color(33, 115, 70));
        detailsPanel.add(txtResultStatus, gbc);

        // Row 1: Sender Name & Receiver Name
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblSender = new JLabel("Sender Name:");
        lblSender.setFont(labelFont);
        detailsPanel.add(lblSender, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        txtResultSender = createReadOnlyField(new Font("Segoe UI", Font.PLAIN, 12), Color.DARK_GRAY);
        detailsPanel.add(txtResultSender, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        JLabel lblReceiver = new JLabel("Receiver Name:");
        lblReceiver.setFont(labelFont);
        detailsPanel.add(lblReceiver, gbc);

        gbc.gridx = 3; gbc.gridy = 1;
        txtResultReceiver = createReadOnlyField(new Font("Segoe UI", Font.PLAIN, 12), Color.DARK_GRAY);
        detailsPanel.add(txtResultReceiver, gbc);

        // Row 2: Destination City & Delivery Type
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblDest = new JLabel("Destination:");
        lblDest.setFont(labelFont);
        detailsPanel.add(lblDest, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        txtResultDestination = createReadOnlyField(new Font("Segoe UI", Font.PLAIN, 12), Color.DARK_GRAY);
        detailsPanel.add(txtResultDestination, gbc);

        gbc.gridx = 2; gbc.gridy = 2;
        JLabel lblType = new JLabel("Delivery Type:");
        lblType.setFont(labelFont);
        detailsPanel.add(lblType, gbc);

        gbc.gridx = 3; gbc.gridy = 2;
        txtResultType = createReadOnlyField(new Font("Segoe UI", Font.PLAIN, 12), Color.DARK_GRAY);
        detailsPanel.add(txtResultType, gbc);

        // Row 3: Parcel Weight & Delivery Charge
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblWeight = new JLabel("Parcel Weight:");
        lblWeight.setFont(labelFont);
        detailsPanel.add(lblWeight, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        txtResultWeight = createReadOnlyField(new Font("Segoe UI", Font.PLAIN, 12), Color.DARK_GRAY);
        detailsPanel.add(txtResultWeight, gbc);

        gbc.gridx = 2; gbc.gridy = 3;
        JLabel lblCharge = new JLabel("Delivery Charge:");
        lblCharge.setFont(labelFont);
        detailsPanel.add(lblCharge, gbc);

        gbc.gridx = 3; gbc.gridy = 3;
        txtResultCharge = createReadOnlyField(new Font("Segoe UI", Font.BOLD, 13), new Color(33, 115, 70));
        detailsPanel.add(txtResultCharge, gbc);

        centerPanel.add(detailsPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // -------------------------------------------------------------
        // 3. Footer Action Panel
        // -------------------------------------------------------------
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 14));
        footerPanel.setBackground(new Color(245, 247, 250));
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 224, 230)));

        btnBack = createStyledButton("Back to Dashboard", new Color(74, 85, 104), new Dimension(180, 36));
        footerPanel.add(btnBack);

        add(footerPanel, BorderLayout.SOUTH);
    }

    /**
     * Helper to create styled buttons with BasicButtonUI for guaranteed cross-platform rendering.
     */
    private JButton createStyledButton(String text, Color bg, Dimension dim) {
        JButton btn = new JButton(text);
        btn.setUI(new BasicButtonUI());
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(dim);
        btn.addActionListener(this);
        return btn;
    }

    /**
     * Helper to create read-only text fields for clean, selectable parcel output.
     */
    private JTextField createReadOnlyField(Font font, Color fg) {
        JTextField field = new JTextField();
        field.setFont(font);
        field.setForeground(fg);
        field.setEditable(false);
        field.setBackground(new Color(248, 250, 252));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(225, 230, 238), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return field;
    }

    /**
     * Executes tracking search using CourierDAO and PreparedStatement.
     */
    private void performTracking() {
        String searchId = txtSearchId.getText().trim();

        if (searchId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a Tracking ID to search.",
                "Input Required",
                JOptionPane.WARNING_MESSAGE);
            txtSearchId.requestFocus();
            return;
        }

        try {
            // Polymorphic retrieval from MySQL via CourierDAO
            Courier courier = courierDAO.getCourierByTrackingId(searchId);

            if (courier != null) {
                // Populate result fields using Encapsulation (getters)
                txtResultTrackingId.setText(courier.getTrackingId());
                txtResultStatus.setText(courier.getStatus() != null ? courier.getStatus() : "Booked");
                txtResultSender.setText(courier.getSenderName() + " (" + courier.getSenderPhone() + ")");
                txtResultReceiver.setText(courier.getReceiverName() + " (" + courier.getReceiverPhone() + ")");
                txtResultDestination.setText(courier.getDestination());
                txtResultType.setText(courier.getDeliveryType());
                txtResultWeight.setText(courier.getParcelWeight() + " kg");
                txtResultCharge.setText(String.format("₹ %.2f", courier.getDeliveryCharge()));

                lblStatusMessage.setText("✓ Shipment details retrieved successfully for " + courier.getTrackingId());
                lblStatusMessage.setForeground(new Color(33, 115, 70));
            } else {
                // Tracking ID not found in database
                clearDetails();
                lblStatusMessage.setText("Tracking ID not found.");
                lblStatusMessage.setForeground(new Color(192, 57, 43));

                JOptionPane.showMessageDialog(this,
                    "Tracking ID not found.",
                    "Tracking Result",
                    JOptionPane.INFORMATION_MESSAGE);

                txtSearchId.selectAll();
                txtSearchId.requestFocus();
            }
        } catch (SQLException sqle) {
            String err = "Database operation failed:\n" + sqle.getMessage() +
                         "\n\nPlease ensure MySQL is running and database 'courier_db' exists.";
            JOptionPane.showMessageDialog(this, err, "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Clears all shipment details fields.
     */
    private void clearDetails() {
        txtResultTrackingId.setText("");
        txtResultStatus.setText("");
        txtResultSender.setText("");
        txtResultReceiver.setText("");
        txtResultDestination.setText("");
        txtResultType.setText("");
        txtResultWeight.setText("");
        txtResultCharge.setText("");
    }

    /**
     * Resets both search input and details fields.
     */
    private void resetAll() {
        txtSearchId.setText("");
        clearDetails();
        lblStatusMessage.setText("Enter a Tracking ID (e.g. TRK1001, TRK1002) and click Track Parcel");
        lblStatusMessage.setForeground(new Color(100, 110, 125));
        txtSearchId.requestFocus();
    }

    /**
     * Disposes this frame and restores dashboard visibility.
     */
    private void returnToDashboard() {
        this.dispose();
        if (parentDashboard != null) {
            parentDashboard.checkDatabaseStatus();
            parentDashboard.setVisible(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearch || e.getSource() == txtSearchId) {
            performTracking();
        } else if (e.getSource() == btnClear) {
            resetAll();
        } else if (e.getSource() == btnBack) {
            returnToDashboard();
        }
    }
}
