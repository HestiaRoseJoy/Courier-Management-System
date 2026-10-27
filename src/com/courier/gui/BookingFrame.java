package com.courier.gui;

import com.courier.db.CourierDAO;
import com.courier.exception.InvalidBookingException;
import com.courier.model.Courier;
import com.courier.model.ExpressCourier;
import com.courier.model.StandardCourier;
import com.courier.util.DeliveryCalculator;

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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * BookingFrame Class
 * 
 * Demonstrates:
 * 1. SWING GUI: Form layout using GridBagLayout and custom component styling.
 * 2. RUNTIME POLYMORPHISM: Instantiating StandardCourier or ExpressCourier polymorphically.
 * 3. EXCEPTION HANDLING: Catching custom InvalidBookingException and SQL exceptions.
 * 4. METHOD OVERLOADING: Demonstrating DeliveryCalculator usage.
 */
public class BookingFrame extends JFrame implements ActionListener {

    private final DashboardFrame parentDashboard;
    private final CourierDAO courierDAO;

    // GUI Form Controls
    private JTextField txtTrackingId;
    private JTextField txtSenderName;
    private JTextField txtSenderPhone;
    private JTextField txtReceiverName;
    private JTextField txtReceiverPhone;
    private JComboBox<String> cmbDestination;
    private JTextField txtWeight;
    private JComboBox<String> cmbDeliveryType;
    private JTextField txtDeliveryCharge;

    private JButton btnCalculate;
    private JButton btnBook;
    private JButton btnReset;
    private JButton btnBack;

    public BookingFrame(DashboardFrame parentDashboard) {
        this.parentDashboard = parentDashboard;
        this.courierDAO = new CourierDAO();

        setTitle("Courier Booking Form");
        setSize(780, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));

        initComponents();
        loadNextTrackingId();

        // Handle window closing by restoring dashboard
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                returnToDashboard();
            }
        });
    }

    private void initComponents() {
        // -------------------------------------------------------------
        // 1. Header Banner
        // -------------------------------------------------------------
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(33, 115, 70)); // Forest Green
        headerPanel.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JLabel lblTitle = new JLabel("BOOK NEW COURIER SHIPMENT", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Fill sender, receiver, and parcel details below", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(210, 245, 225));

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSub, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // -------------------------------------------------------------
        // 2. Form Input Panel (GridBagLayout for alignment)
        // -------------------------------------------------------------
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 13);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 13);

        // Row 0: Tracking ID (Auto-generated & Read-only)
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel lblTracking = new JLabel("Tracking ID (Auto):");
        lblTracking.setFont(labelFont);
        formPanel.add(lblTracking, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        txtTrackingId = new JTextField();
        txtTrackingId.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtTrackingId.setEditable(false);
        txtTrackingId.setBackground(new Color(238, 242, 245));
        txtTrackingId.setForeground(new Color(41, 128, 185));
        formPanel.add(txtTrackingId, gbc);

        // Row 1: Sender Name
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel lblSenderName = new JLabel("Sender Name *:");
        lblSenderName.setFont(labelFont);
        formPanel.add(lblSenderName, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.7;
        txtSenderName = new JTextField();
        txtSenderName.setFont(fieldFont);
        formPanel.add(txtSenderName, gbc);

        // Row 2: Sender Phone
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblSenderPhone = new JLabel("Sender Phone (10 digits) *:");
        lblSenderPhone.setFont(labelFont);
        formPanel.add(lblSenderPhone, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        txtSenderPhone = new JTextField();
        txtSenderPhone.setFont(fieldFont);
        formPanel.add(txtSenderPhone, gbc);

        // Row 3: Receiver Name
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblReceiverName = new JLabel("Receiver Name *:");
        lblReceiverName.setFont(labelFont);
        formPanel.add(lblReceiverName, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        txtReceiverName = new JTextField();
        txtReceiverName.setFont(fieldFont);
        formPanel.add(txtReceiverName, gbc);

        // Row 4: Receiver Phone
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel lblReceiverPhone = new JLabel("Receiver Phone (10 digits) *:");
        lblReceiverPhone.setFont(labelFont);
        formPanel.add(lblReceiverPhone, gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        txtReceiverPhone = new JTextField();
        txtReceiverPhone.setFont(fieldFont);
        formPanel.add(txtReceiverPhone, gbc);

        // Row 5: Destination
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel lblDest = new JLabel("Destination City *:");
        lblDest.setFont(labelFont);
        formPanel.add(lblDest, gbc);

        gbc.gridx = 1; gbc.gridy = 5;
        String[] destinations = {"Kochi", "Thiruvananthapuram", "Kozhikode", "Thrissur", "Bengaluru", "Chennai", "Mumbai", "Delhi"};
        cmbDestination = new JComboBox<>(destinations);
        cmbDestination.setEditable(true); // Allow custom destinations too
        cmbDestination.setFont(fieldFont);
        formPanel.add(cmbDestination, gbc);

        // Row 6: Parcel Weight
        gbc.gridx = 0; gbc.gridy = 6;
        JLabel lblWeight = new JLabel("Parcel Weight (in kg) *:");
        lblWeight.setFont(labelFont);
        formPanel.add(lblWeight, gbc);

        gbc.gridx = 1; gbc.gridy = 6;
        txtWeight = new JTextField();
        txtWeight.setFont(fieldFont);
        txtWeight.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                autoCalculateCharge();
            }
        });
        formPanel.add(txtWeight, gbc);

        // Row 7: Delivery Type
        gbc.gridx = 0; gbc.gridy = 7;
        JLabel lblType = new JLabel("Delivery Type:");
        lblType.setFont(labelFont);
        formPanel.add(lblType, gbc);

        gbc.gridx = 1; gbc.gridy = 7;
        String[] types = {"Standard", "Express"};
        cmbDeliveryType = new JComboBox<>(types);
        cmbDeliveryType.setFont(fieldFont);
        cmbDeliveryType.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    autoCalculateCharge();
                }
            }
        });
        formPanel.add(cmbDeliveryType, gbc);

        // Row 8: Delivery Charge (Calculated)
        gbc.gridx = 0; gbc.gridy = 8;
        JLabel lblCharge = new JLabel("Total Delivery Charge (₹):");
        lblCharge.setFont(labelFont);
        formPanel.add(lblCharge, gbc);

        gbc.gridx = 1; gbc.gridy = 8;
        txtDeliveryCharge = new JTextField();
        txtDeliveryCharge.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtDeliveryCharge.setEditable(false);
        txtDeliveryCharge.setBackground(new Color(245, 252, 247));
        txtDeliveryCharge.setForeground(new Color(33, 115, 70));
        formPanel.add(txtDeliveryCharge, gbc);

        add(formPanel, BorderLayout.CENTER);

        // -------------------------------------------------------------
        // 3. Action Buttons Panel
        // -------------------------------------------------------------
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 14));
        buttonPanel.setBackground(new Color(245, 247, 250));
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 224, 230)));

        btnCalculate = createButton("Calculate Charge", new Color(31, 110, 165));
        btnBook = createButton("Confirm & Book Courier", new Color(33, 115, 70));
        btnReset = createButton("Reset Form", new Color(85, 95, 105));
        btnBack = createButton("Back to Dashboard", new Color(74, 85, 104));

        buttonPanel.add(btnCalculate);
        buttonPanel.add(btnBook);
        buttonPanel.add(btnReset);
        buttonPanel.add(btnBack);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setUI(new BasicButtonUI());
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 36));
        btn.addActionListener(this);
        return btn;
    }

    /**
     * Loads the next unique tracking ID from database or counter.
     */
    private void loadNextTrackingId() {
        String nextId = courierDAO.generateNextTrackingId();
        txtTrackingId.setText(nextId);
    }

    /**
     * Helper to automatically update delivery charge field when weight/type changes.
     */
    private void autoCalculateCharge() {
        String weightText = txtWeight.getText().trim();
        if (weightText.isEmpty()) {
            txtDeliveryCharge.setText("");
            return;
        }
        try {
            double weight = Double.parseDouble(weightText);
            if (weight > 0) {
                String type = (String) cmbDeliveryType.getSelectedItem();
                // Demonstrates Method Overloading via DeliveryCalculator
                double charge = DeliveryCalculator.calculateCharge(weight, type);
                txtDeliveryCharge.setText(String.format("%.2f", charge));
            } else {
                txtDeliveryCharge.setText("");
            }
        } catch (NumberFormatException ignored) {
            txtDeliveryCharge.setText("");
        }
    }

    /**
     * Validates all form inputs.
     * Throws custom user-defined InvalidBookingException on any invalid input.
     */
    private void validateInputs() throws InvalidBookingException {
        String senderName = txtSenderName.getText().trim();
        String senderPhone = txtSenderPhone.getText().trim();
        String receiverName = txtReceiverName.getText().trim();
        String receiverPhone = txtReceiverPhone.getText().trim();
        String destination = (cmbDestination.getSelectedItem() != null) ? 
                             cmbDestination.getSelectedItem().toString().trim() : "";
        String weightText = txtWeight.getText().trim();

        if (senderName.isEmpty()) {
            throw new InvalidBookingException("Sender Name cannot be blank.");
        }
        if (!senderPhone.matches("\\d{10}")) {
            throw new InvalidBookingException("Sender Phone number must be exactly 10 digits.");
        }
        if (receiverName.isEmpty()) {
            throw new InvalidBookingException("Receiver Name cannot be blank.");
        }
        if (!receiverPhone.matches("\\d{10}")) {
            throw new InvalidBookingException("Receiver Phone number must be exactly 10 digits.");
        }
        if (destination.isEmpty()) {
            throw new InvalidBookingException("Destination city must be selected or entered.");
        }
        if (weightText.isEmpty()) {
            throw new InvalidBookingException("Parcel Weight cannot be empty.");
        }

        try {
            double weight = Double.parseDouble(weightText);
            if (weight <= 0) {
                throw new InvalidBookingException("Parcel Weight must be greater than 0 kg.");
            }
            if (weight > 100) {
                throw new InvalidBookingException("Parcel Weight exceeds the maximum limit of 100 kg.");
            }
        } catch (NumberFormatException nfe) {
            throw new InvalidBookingException("Please enter a valid numeric value for Parcel Weight.");
        }
    }

    /**
     * Books courier by:
     * 1. Validating inputs
     * 2. Demonstrating RUNTIME POLYMORPHISM (Courier base ref -> Standard/Express instance)
     * 3. Saving to MySQL database using CourierDAO
     */
    private void processBooking() {
        try {
            // 1. Validation using custom Exception Handling
            validateInputs();

            String trackingId = txtTrackingId.getText().trim();
            String senderName = txtSenderName.getText().trim();
            String senderPhone = txtSenderPhone.getText().trim();
            String receiverName = txtReceiverName.getText().trim();
            String receiverPhone = txtReceiverPhone.getText().trim();
            String destination = cmbDestination.getSelectedItem().toString().trim();
            double weight = Double.parseDouble(txtWeight.getText().trim());
            String deliveryType = (String) cmbDeliveryType.getSelectedItem();

            // 2. RUNTIME POLYMORPHISM:
            // Base class reference 'courier' dynamically references subclass objects.
            Courier courier;
            if ("Express".equalsIgnoreCase(deliveryType)) {
                courier = new ExpressCourier(trackingId, senderName, senderPhone, receiverName, receiverPhone, destination, weight);
            } else {
                courier = new StandardCourier(trackingId, senderName, senderPhone, receiverName, receiverPhone, destination, weight);
            }

            // Polymorphic method call: JVM determines which calculateDeliveryCharge() to invoke at runtime
            double totalCharge = courier.calculateDeliveryCharge();
            courier.setDeliveryCharge(totalCharge);

            // 3. Save to MySQL database via DAO with PreparedStatement
            boolean isSaved = courierDAO.saveCourier(courier);

            if (isSaved) {
                // Success Dialog
                String successMsg = "<html><font size='4'><b>Courier Booked Successfully!</b></font><br><br>" +
                                    "<b>Tracking ID:</b> " + courier.getTrackingId() + "<br>" +
                                    "<b>Sender:</b> " + courier.getSenderName() + "<br>" +
                                    "<b>Receiver:</b> " + courier.getReceiverName() + " (" + courier.getDestination() + ")<br>" +
                                    "<b>Type:</b> " + courier.getDeliveryType() + "<br>" +
                                    "<b>Weight:</b> " + courier.getParcelWeight() + " kg<br>" +
                                    "<b>Total Charge:</b> ₹" + String.format("%.2f", totalCharge) + "<br>" +
                                    "<b>Status:</b> Booked</html>";

                JOptionPane.showMessageDialog(this, successMsg, "Booking Confirmed", JOptionPane.INFORMATION_MESSAGE);
                resetForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save booking. Please check database connection.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (InvalidBookingException ibe) {
            // Handled custom exception
            JOptionPane.showMessageDialog(this, ibe.getMessage(), "Input Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException sqle) {
            // Handled SQL exception
            String err = "Database operation failed:\n" + sqle.getMessage() +
                         "\n\nPlease ensure MySQL is running and database 'courier_db' exists (run database.sql).";
            JOptionPane.showMessageDialog(this, err, "Database Connection Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetForm() {
        txtSenderName.setText("");
        txtSenderPhone.setText("");
        txtReceiverName.setText("");
        txtReceiverPhone.setText("");
        cmbDestination.setSelectedIndex(0);
        txtWeight.setText("");
        cmbDeliveryType.setSelectedIndex(0);
        txtDeliveryCharge.setText("");
        loadNextTrackingId();
    }

    private void returnToDashboard() {
        this.dispose();
        if (parentDashboard != null) {
            parentDashboard.checkDatabaseStatus();
            parentDashboard.setVisible(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnCalculate) {
            autoCalculateCharge();
            if (txtDeliveryCharge.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a valid weight first.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            }
        } else if (e.getSource() == btnBook) {
            processBooking();
        } else if (e.getSource() == btnReset) {
            resetForm();
        } else if (e.getSource() == btnBack) {
            returnToDashboard();
        }
    }
}
