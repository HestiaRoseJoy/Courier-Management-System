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
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * UpdateStatusFrame Class
 * 
 * Part of Phase 2: Update Status Feature
 * 
 * Demonstrates KTU S3 Java OOP Concepts:
 * 1. SWING GUI: Form layout using GridBagLayout and consistent, accessible styling.
 * 2. JDBC & DAO PATTERN: Secure database status updates via CourierDAO and PreparedStatement.
 * 3. RUNTIME POLYMORPHISM & ENCAPSULATION: Base class 'Courier' reference holds records,
 *    and values are accessed via standard getters/setters.
 * 4. EXCEPTION HANDLING: Catching SQL errors and validating user input.
 */
public class UpdateStatusFrame extends JFrame implements ActionListener {

    private final DashboardFrame parentDashboard;
    private final CourierDAO courierDAO;

    // GUI Form Controls
    private JTextField txtTrackingId;
    private JButton btnCheck;
    private JTextField txtCurrentStatus;
    private JTextField txtParcelDetails;
    private JComboBox<String> cmbNewStatus;
    private JLabel lblMessage;

    // Action Buttons
    private JButton btnUpdate;
    private JButton btnReset;
    private JButton btnBack;

    public UpdateStatusFrame(DashboardFrame parentDashboard) {
        this.parentDashboard = parentDashboard;
        this.courierDAO = new CourierDAO();

        setTitle("Update Shipment Status");
        setSize(720, 560);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));

        initComponents();

        // Restore dashboard on window close
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
        headerPanel.setBackground(new Color(195, 75, 0)); // Deep Amber/Orange matching Dashboard
        headerPanel.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JLabel lblTitle = new JLabel("UPDATE SHIPMENT STATUS", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Update parcel transit milestones and delivery progress in MySQL", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(254, 237, 222));

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSub, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // -------------------------------------------------------------
        // 2. Form Panel (GridBagLayout inside TitledBorder)
        // -------------------------------------------------------------
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 35, 10, 35));

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(215, 220, 230), 1),
                " Status Update Form ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13),
                new Color(195, 75, 0)
            ),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 13);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 13);

        // Row 0: Tracking ID Input + "Check" button
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.25;
        JLabel lblTracking = new JLabel("Tracking ID *:");
        lblTracking.setFont(labelFont);
        formCard.add(lblTracking, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.55;
        txtTrackingId = new JTextField();
        txtTrackingId.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtTrackingId.addActionListener(this); // Allows pressing Enter to fetch
        formCard.add(txtTrackingId, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.20;
        btnCheck = createStyledButton("Check", new Color(31, 110, 165), new Dimension(90, 32));
        formCard.add(btnCheck, gbc);

        // Row 1: Current Status (Read-only)
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.25;
        JLabel lblCurrent = new JLabel("Current Status:");
        lblCurrent.setFont(labelFont);
        formCard.add(lblCurrent, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2; gbc.weightx = 0.75;
        txtCurrentStatus = new JTextField("Enter Tracking ID above");
        txtCurrentStatus.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtCurrentStatus.setEditable(false);
        txtCurrentStatus.setBackground(new Color(248, 250, 252));
        txtCurrentStatus.setForeground(new Color(80, 90, 100));
        txtCurrentStatus.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(225, 230, 238), 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        formCard.add(txtCurrentStatus, gbc);
        gbc.gridwidth = 1; // reset gridwidth

        // Row 2: Route / Details Summary (Read-only)
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.25;
        JLabel lblDetails = new JLabel("Parcel Details:");
        lblDetails.setFont(labelFont);
        formCard.add(lblDetails, gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 2; gbc.weightx = 0.75;
        txtParcelDetails = new JTextField("-");
        txtParcelDetails.setFont(fieldFont);
        txtParcelDetails.setEditable(false);
        txtParcelDetails.setBackground(new Color(248, 250, 252));
        txtParcelDetails.setForeground(Color.DARK_GRAY);
        txtParcelDetails.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(225, 230, 238), 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        formCard.add(txtParcelDetails, gbc);
        gbc.gridwidth = 1; // reset gridwidth

        // Row 3: New Status Dropdown
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.25;
        JLabel lblNewStatus = new JLabel("New Status *:");
        lblNewStatus.setFont(labelFont);
        formCard.add(lblNewStatus, gbc);

        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 2; gbc.weightx = 0.75;
        String[] statuses = {"Booked", "In Transit", "Out for Delivery", "Delivered"};
        cmbNewStatus = new JComboBox<>(statuses);
        cmbNewStatus.setFont(fieldFont);
        cmbNewStatus.setPreferredSize(new Dimension(200, 34));
        formCard.add(cmbNewStatus, gbc);
        gbc.gridwidth = 1; // reset gridwidth

        // Row 4: Status / Feedback message
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3;
        lblMessage = new JLabel("Enter a Tracking ID (e.g. TRK1001), choose the new status, and click Update Status.", SwingConstants.CENTER);
        lblMessage.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMessage.setForeground(new Color(100, 110, 125));
        lblMessage.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        formCard.add(lblMessage, gbc);

        centerPanel.add(formCard, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // -------------------------------------------------------------
        // 3. Action Buttons Panel
        // -------------------------------------------------------------
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(new Color(245, 247, 250));
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 224, 230)));

        btnUpdate = createStyledButton("Update Status", new Color(195, 75, 0), new Dimension(160, 36));
        btnReset = createStyledButton("Reset Form", new Color(85, 95, 105), new Dimension(130, 36));
        btnBack = createStyledButton("Back to Dashboard", new Color(74, 85, 104), new Dimension(170, 36));

        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnReset);
        buttonPanel.add(btnBack);

        add(buttonPanel, BorderLayout.SOUTH);
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
     * Fetches and displays existing parcel details to verify Tracking ID.
     */
    private Courier checkTrackingId(boolean showAlerts) {
        String trackingId = txtTrackingId.getText().trim();

        if (trackingId.isEmpty()) {
            if (showAlerts) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a Tracking ID to check.",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
            }
            txtTrackingId.requestFocus();
            return null;
        }

        try {
            Courier courier = courierDAO.getCourierByTrackingId(trackingId);

            if (courier != null) {
                txtCurrentStatus.setText(courier.getStatus() != null ? courier.getStatus() : "Booked");
                txtCurrentStatus.setForeground(new Color(33, 115, 70)); // Forest Green
                txtParcelDetails.setText(courier.getSenderName() + "  ➔  " + courier.getReceiverName() + " (" + courier.getDestination() + ")");
                // Note: Do NOT reset cmbNewStatus here to avoid overwriting user dropdown selection

                lblMessage.setText("✓ Shipment verified for " + courier.getTrackingId() + ". Select new status and click Update.");
                lblMessage.setForeground(new Color(33, 115, 70));
                return courier;
            } else {
                txtCurrentStatus.setText("Not found");
                txtCurrentStatus.setForeground(new Color(192, 57, 43));
                txtParcelDetails.setText("-");

                lblMessage.setText("Tracking ID not found.");
                lblMessage.setForeground(new Color(192, 57, 43));

                if (showAlerts) {
                    JOptionPane.showMessageDialog(this,
                        "Tracking ID not found.",
                        "Check Result",
                        JOptionPane.WARNING_MESSAGE);
                }
                txtTrackingId.selectAll();
                txtTrackingId.requestFocus();
                return null;
            }
        } catch (SQLException sqle) {
            String err = "Database operation failed:\n" + sqle.getMessage() +
                         "\n\nPlease ensure MySQL is running and database 'courier_db' exists.";
            JOptionPane.showMessageDialog(this, err, "Database Error", JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /**
     * Updates the status of the courier in MySQL using CourierDAO.
     */
    private void processUpdateStatus() {
        String trackingId = txtTrackingId.getText().trim();

        if (trackingId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a Tracking ID to update.",
                "Input Required",
                JOptionPane.WARNING_MESSAGE);
            txtTrackingId.requestFocus();
            return;
        }

        // 1. Read the user's selected new status from the dropdown
        String newStatus = (String) cmbNewStatus.getSelectedItem();
        if (newStatus == null || newStatus.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please select a new status from the dropdown.",
                "Selection Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // 2. Verify that Tracking ID exists in database (without touching dropdown selection)
            Courier courier = courierDAO.getCourierByTrackingId(trackingId);

            if (courier == null) {
                txtCurrentStatus.setText("Not found");
                txtCurrentStatus.setForeground(new Color(192, 57, 43));
                txtParcelDetails.setText("-");

                lblMessage.setText("Tracking ID not found.");
                lblMessage.setForeground(new Color(192, 57, 43));

                JOptionPane.showMessageDialog(this,
                    "Tracking ID not found.",
                    "Update Failed",
                    JOptionPane.WARNING_MESSAGE);

                txtTrackingId.selectAll();
                txtTrackingId.requestFocus();
                return;
            }

            // 3. Perform the database update using PreparedStatement
            boolean updated = courierDAO.updateCourierStatus(trackingId, newStatus);

            if (updated) {
                // Update GUI display to reflect the new status
                txtCurrentStatus.setText(newStatus);
                txtCurrentStatus.setForeground(new Color(33, 115, 70));
                txtParcelDetails.setText(courier.getSenderName() + "  ➔  " + courier.getReceiverName() + " (" + courier.getDestination() + ")");

                lblMessage.setText("✓ Status updated to '" + newStatus + "' successfully!");
                lblMessage.setForeground(new Color(33, 115, 70));

                String successMsg = "<html><font size='4'><b>Status Updated Successfully!</b></font><br><br>" +
                                    "<b>Tracking ID:</b> " + trackingId + "<br>" +
                                    "<b>Previous Status:</b> " + courier.getStatus() + "<br>" +
                                    "<b>New Status:</b> " + newStatus + "<br>" +
                                    "<b>Receiver:</b> " + courier.getReceiverName() + " (" + courier.getDestination() + ")</html>";

                JOptionPane.showMessageDialog(this, successMsg, "Status Updated", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to update status. Please try again.",
                    "Update Error",
                    JOptionPane.ERROR_MESSAGE);
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
     * Resets form to initial state.
     */
    private void resetForm() {
        txtTrackingId.setText("");
        txtCurrentStatus.setText("Enter Tracking ID above");
        txtCurrentStatus.setForeground(new Color(80, 90, 100));
        txtParcelDetails.setText("-");
        txtParcelDetails.setForeground(Color.DARK_GRAY);
        cmbNewStatus.setSelectedIndex(0);
        lblMessage.setText("Enter a Tracking ID (e.g. TRK1001), choose the new status, and click Update Status.");
        lblMessage.setForeground(new Color(100, 110, 125));
        txtTrackingId.requestFocus();
    }

    /**
     * Disposes frame and restores Dashboard visibility.
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
        if (e.getSource() == btnCheck || e.getSource() == txtTrackingId) {
            checkTrackingId(true);
        } else if (e.getSource() == btnUpdate) {
            processUpdateStatus();
        } else if (e.getSource() == btnReset) {
            resetForm();
        } else if (e.getSource() == btnBack) {
            returnToDashboard();
        }
    }
}
