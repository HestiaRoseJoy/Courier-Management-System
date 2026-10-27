package com.courier.gui;

import com.courier.db.CourierDAO;
import com.courier.model.Courier;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 * ViewShipmentsFrame Class
 * 
 * Part of Phase 2: View Shipments Feature
 * 
 * Demonstrates KTU S3 Java OOP Concepts:
 * 1. SWING GUI & JTABLE: Displays tabular shipment records using JTable, DefaultTableModel, and JScrollPane.
 * 2. RUNTIME POLYMORPHISM & COLLECTIONS: Loads a polymorphic List<Courier> containing
 *    instances of StandardCourier and ExpressCourier.
 * 3. ENCAPSULATION: Accesses parcel attributes via standard getters.
 * 4. DAO DESIGN PATTERN & JDBC: Interacts with MySQL via CourierDAO using safe PreparedStatement queries.
 * 5. EXCEPTION HANDLING: Catches SQL and general exceptions gracefully without crashing.
 */
public class ViewShipmentsFrame extends JFrame implements ActionListener {

    private final DashboardFrame parentDashboard;
    private final CourierDAO courierDAO;

    // GUI Components
    private JTable shipmentsTable;
    private DefaultTableModel tableModel;
    private JLabel lblRecordCount;
    private JButton btnRefresh;
    private JButton btnBack;

    // Table Column Definitions
    private static final String[] COLUMN_NAMES = {
        "Tracking ID", "Sender Name", "Sender Phone", "Receiver Name",
        "Receiver Phone", "Destination", "Parcel Weight", "Delivery Type",
        "Delivery Charge", "Status", "Booking Date"
    };

    public ViewShipmentsFrame(DashboardFrame parentDashboard) {
        this.parentDashboard = parentDashboard;
        this.courierDAO = new CourierDAO();

        setTitle("All Courier Shipments");
        setSize(1020, 600);
        setMinimumSize(new Dimension(850, 450));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        initComponents();
        loadShipmentsData();

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
        headerPanel.setBackground(new Color(110, 57, 160)); // Purple matching Dashboard
        headerPanel.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JLabel lblTitle = new JLabel("ALL COURIER SHIPMENTS", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Live parcel logistics manifest and shipment records from MySQL database", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(244, 236, 247));

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSub, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // -------------------------------------------------------------
        // 2. Center Panel (Status Bar + JTable in JScrollPane)
        // -------------------------------------------------------------
        JPanel centerPanel = new JPanel(new BorderLayout(8, 8));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 10, 20));

        // Sub-panel 2A: Record summary status bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createEmptyBorder(0, 5, 8, 5));

        lblRecordCount = new JLabel("Loading shipments from database...");
        lblRecordCount.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblRecordCount.setForeground(new Color(60, 70, 80));
        topBar.add(lblRecordCount, BorderLayout.WEST);

        centerPanel.add(topBar, BorderLayout.NORTH);

        // Sub-panel 2B: JTable with non-editable DefaultTableModel
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Read-only table cells for safe presentation
                return false;
            }
        };

        shipmentsTable = new JTable(tableModel);
        shipmentsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        shipmentsTable.setRowHeight(28);
        shipmentsTable.setGridColor(new Color(230, 235, 242));
        shipmentsTable.setSelectionBackground(new Color(225, 235, 250));
        shipmentsTable.setSelectionForeground(Color.BLACK);
        shipmentsTable.setShowGrid(true);
        shipmentsTable.setAutoCreateRowSorter(true); // Allows sorting by clicking headers

        // Table Header Styling
        JTableHeader header = shipmentsTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(24, 43, 73)); // Dark Navy
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 34));
        header.setReorderingAllowed(false);

        // Cell Alignment: Center for specific columns (ID, Phone, Type, Status, Date)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        shipmentsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Tracking ID
        shipmentsTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Sender Phone
        shipmentsTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Receiver Phone
        shipmentsTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer); // Weight
        shipmentsTable.getColumnModel().getColumn(7).setCellRenderer(centerRenderer); // Type
        shipmentsTable.getColumnModel().getColumn(8).setCellRenderer(centerRenderer); // Charge
        shipmentsTable.getColumnModel().getColumn(9).setCellRenderer(centerRenderer); // Status
        shipmentsTable.getColumnModel().getColumn(10).setCellRenderer(centerRenderer); // Booking Date

        // Preferred Column Widths
        shipmentsTable.getColumnModel().getColumn(0).setPreferredWidth(90);  // Tracking ID
        shipmentsTable.getColumnModel().getColumn(1).setPreferredWidth(110); // Sender Name
        shipmentsTable.getColumnModel().getColumn(2).setPreferredWidth(95);  // Sender Phone
        shipmentsTable.getColumnModel().getColumn(3).setPreferredWidth(110); // Receiver Name
        shipmentsTable.getColumnModel().getColumn(4).setPreferredWidth(95);  // Receiver Phone
        shipmentsTable.getColumnModel().getColumn(5).setPreferredWidth(90);  // Destination
        shipmentsTable.getColumnModel().getColumn(6).setPreferredWidth(85);  // Weight
        shipmentsTable.getColumnModel().getColumn(7).setPreferredWidth(85);  // Type
        shipmentsTable.getColumnModel().getColumn(8).setPreferredWidth(95);  // Charge
        shipmentsTable.getColumnModel().getColumn(9).setPreferredWidth(95);  // Status
        shipmentsTable.getColumnModel().getColumn(10).setPreferredWidth(130); // Date

        JScrollPane scrollPane = new JScrollPane(shipmentsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(210, 218, 228), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // -------------------------------------------------------------
        // 3. Footer Action Panel
        // -------------------------------------------------------------
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 14));
        footerPanel.setBackground(new Color(245, 247, 250));
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 224, 230)));

        btnRefresh = createStyledButton("Refresh", new Color(110, 57, 160), new Dimension(140, 36));
        btnRefresh.setActionCommand("Refresh");
        btnRefresh.setToolTipText("Reload latest shipment data from MySQL database");

        btnBack = createStyledButton("Back to Dashboard", new Color(74, 85, 104), new Dimension(170, 36));

        footerPanel.add(btnRefresh);
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
     * Loads courier shipment records from MySQL via CourierDAO into the JTable.
     * Invoked both on initial window display and whenever the user clicks Refresh.
     */
    public void loadShipmentsData() {
        try {
            // 1. Fetch latest records from MySQL database via CourierDAO
            List<Courier> couriers = courierDAO.getAllCouriers();

            // 2. Clear existing rows before adding fresh records to prevent duplicates
            tableModel.setRowCount(0);

            // 3. Populate fresh records or display empty notice without crashing
            if (couriers.isEmpty()) {
                lblRecordCount.setText("No shipments found in database. Use 'Book Courier' to add new bookings.");
                lblRecordCount.setForeground(new Color(192, 57, 43));
            } else {
                for (Courier c : couriers) {
                    tableModel.addRow(new Object[] {
                        c.getTrackingId(),
                        c.getSenderName(),
                        c.getSenderPhone(),
                        c.getReceiverName(),
                        c.getReceiverPhone(),
                        c.getDestination(),
                        String.format("%.1f kg", c.getParcelWeight()),
                        c.getDeliveryType(),
                        String.format("₹ %.2f", c.getDeliveryCharge()),
                        c.getStatus() != null ? c.getStatus() : "Booked",
                        c.getBookingDate() != null ? c.getBookingDate() : "-"
                    });
                }

                lblRecordCount.setText("Total Registered Shipments: " + couriers.size() + " parcel(s)");
                lblRecordCount.setForeground(new Color(33, 115, 70)); // Forest green
            }

            // 4. Notify table model and trigger component re-render
            tableModel.fireTableDataChanged();
            if (shipmentsTable.getRowSorter() != null) {
                shipmentsTable.getRowSorter().allRowsChanged();
            }
            shipmentsTable.revalidate();
            shipmentsTable.repaint();

        } catch (SQLException sqle) {
            tableModel.setRowCount(0);
            tableModel.fireTableDataChanged();
            lblRecordCount.setText("Database error: Could not load shipments.");
            lblRecordCount.setForeground(new Color(192, 57, 43));

            String err = "Database operation failed:\n" + sqle.getMessage() +
                         "\n\nPlease ensure MySQL is running and database 'courier_db' exists.";
            JOptionPane.showMessageDialog(this, err, "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            tableModel.setRowCount(0);
            tableModel.fireTableDataChanged();
            lblRecordCount.setText("Unexpected error loading shipments.");
            lblRecordCount.setForeground(new Color(192, 57, 43));
            JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
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
        if (e.getSource() == btnRefresh || "Refresh".equals(e.getActionCommand())) {
            loadShipmentsData();
        } else if (e.getSource() == btnBack) {
            returnToDashboard();
        }
    }
}
