package com.courier.db;

import com.courier.model.Courier;
import com.courier.model.ExpressCourier;
import com.courier.model.StandardCourier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for Courier Operations.
 * 
 * Demonstrates:
 * 1. JDBC DATABASE OPERATIONS: Using Connection and PreparedStatement.
 * 2. SQL INJECTION PREVENTION: Using parameterized PreparedStatement (?, ?).
 * 3. RUNTIME POLYMORPHISM: Accepts base class 'Courier' reference and accesses
 *    overridden methods dynamically.
 */
public class CourierDAO {

    /**
     * Saves a new courier record into the MySQL database.
     * 
     * @param courier Courier object (can be StandardCourier or ExpressCourier)
     * @return boolean true if inserted successfully, false otherwise
     * @throws SQLException on database errors
     */
    public boolean saveCourier(Courier courier) throws SQLException {
        String sql = "INSERT INTO couriers (" +
                     "tracking_id, sender_name, sender_phone, receiver_name, " +
                     "receiver_phone, destination, parcel_weight, delivery_type, " +
                     "delivery_charge, status" +
                     ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Try-with-resources automatically closes connection and statement
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, courier.getTrackingId());
            pstmt.setString(2, courier.getSenderName());
            pstmt.setString(3, courier.getSenderPhone());
            pstmt.setString(4, courier.getReceiverName());
            pstmt.setString(5, courier.getReceiverPhone());
            pstmt.setString(6, courier.getDestination());
            pstmt.setDouble(7, courier.getParcelWeight());
            
            // Runtime Polymorphism: Calls overridden getDeliveryType() on subclass
            pstmt.setString(8, courier.getDeliveryType());
            
            // Runtime Polymorphism: Calls overridden calculateDeliveryCharge()
            double charge = courier.getDeliveryCharge() > 0 ? courier.getDeliveryCharge() : courier.calculateDeliveryCharge();
            pstmt.setDouble(9, charge);
            
            pstmt.setString(10, courier.getStatus() != null ? courier.getStatus() : "Booked");

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Generates the next sequential tracking ID (e.g. TRK1001, TRK1002...).
     * 
     * @return String new unique tracking ID
     */
    public String generateNextTrackingId() {
        String defaultPrefix = "TRK";
        int defaultNumber = 1001;

        String sql = "SELECT tracking_id FROM couriers WHERE tracking_id LIKE 'TRK%' ORDER BY tracking_id DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString("tracking_id");
                // Extract numeric portion: TRK1005 -> 1005
                String numPart = lastId.replace(defaultPrefix, "").trim();
                try {
                    int lastNum = Integer.parseInt(numPart);
                    return defaultPrefix + (lastNum + 1);
                } catch (NumberFormatException nfe) {
                    // fallback to timestamp-based if format differs
                    return defaultPrefix + (int) (1000 + (System.currentTimeMillis() % 9000));
                }
            }
        } catch (Exception e) {
            // If database is not yet connected or empty, generate timestamp-based ID
            return defaultPrefix + (int) (1000 + (System.currentTimeMillis() % 9000));
        }

        return defaultPrefix + defaultNumber;
    }

    /**
     * Searches and retrieves a Courier record by tracking ID using a PreparedStatement.
     * 
     * Demonstrates:
     * 1. RUNTIME POLYMORPHISM: Depending on 'delivery_type', dynamically instantiates
     *    StandardCourier or ExpressCourier, returning the abstract base reference 'Courier'.
     * 2. SECURE SQL QUERIES: Parameterized PreparedStatement eliminates SQL injection risks.
     * 3. ENCAPSULATION: Object data is populated via setters and retrieved via getters.
     * 
     * @param trackingId Unique parcel tracking ID (e.g., TRK1001, TRK1002)
     * @return Courier object if found, or null if tracking ID does not exist
     * @throws SQLException on database connectivity or query errors
     */
    public Courier getCourierByTrackingId(String trackingId) throws SQLException {
        String sql = "SELECT tracking_id, sender_name, sender_phone, receiver_name, " +
                     "receiver_phone, destination, parcel_weight, delivery_type, " +
                     "delivery_charge, status FROM couriers WHERE tracking_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, trackingId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String id = rs.getString("tracking_id");
                    String senderName = rs.getString("sender_name");
                    String senderPhone = rs.getString("sender_phone");
                    String receiverName = rs.getString("receiver_name");
                    String receiverPhone = rs.getString("receiver_phone");
                    String destination = rs.getString("destination");
                    double parcelWeight = rs.getDouble("parcel_weight");
                    String deliveryType = rs.getString("delivery_type");
                    double deliveryCharge = rs.getDouble("delivery_charge");
                    String status = rs.getString("status");

                    // Runtime Polymorphism: Instantiate child class into base class reference
                    Courier courier;
                    if ("Express".equalsIgnoreCase(deliveryType)) {
                        courier = new ExpressCourier(id, senderName, senderPhone, receiverName, receiverPhone, destination, parcelWeight);
                    } else {
                        courier = new StandardCourier(id, senderName, senderPhone, receiverName, receiverPhone, destination, parcelWeight);
                    }
                    courier.setDeliveryCharge(deliveryCharge);
                    courier.setStatus(status);

                    return courier;
                }
            }
        }
        return null;
    }

    /**
     * Updates only the delivery status of an existing courier record.
     * 
     * Demonstrates:
     * 1. SECURE SQL UPDATE: Uses parameterized PreparedStatement to prevent SQL injection.
     * 2. RETURN VALUE: Returns true if the record was successfully updated, false otherwise.
     * 
     * @param trackingId Unique parcel tracking ID (e.g., TRK1001)
     * @param newStatus The updated delivery status (e.g., 'In Transit', 'Delivered')
     * @return boolean true if status was updated, false if tracking ID does not exist
     * @throws SQLException on database errors
     */
    public boolean updateCourierStatus(String trackingId, String newStatus) throws SQLException {
        String sql = "UPDATE couriers SET status = ? WHERE tracking_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStatus);
            pstmt.setString(2, trackingId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Retrieves all courier shipments from MySQL database.
     * 
     * Demonstrates:
     * 1. RUNTIME POLYMORPHISM: Dynamically creates StandardCourier or ExpressCourier
     *    and stores them in a polymorphic List<Courier>.
     * 2. SAFE SQL EXECUTION: Uses PreparedStatement and try-with-resources.
     * 3. DATA ACCESS OBJECT (DAO): Centralizes query operations for table presentation.
     * 
     * @return List<Courier> list of all shipments sorted by booking_date DESC
     * @throws SQLException on database connectivity or query errors
     */
    public List<Courier> getAllCouriers() throws SQLException {
        List<Courier> list = new ArrayList<>();
        String sql = "SELECT tracking_id, sender_name, sender_phone, receiver_name, " +
                     "receiver_phone, destination, parcel_weight, delivery_type, " +
                     "delivery_charge, status, booking_date FROM couriers ORDER BY booking_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("tracking_id");
                String senderName = rs.getString("sender_name");
                String senderPhone = rs.getString("sender_phone");
                String receiverName = rs.getString("receiver_name");
                String receiverPhone = rs.getString("receiver_phone");
                String destination = rs.getString("destination");
                double parcelWeight = rs.getDouble("parcel_weight");
                String deliveryType = rs.getString("delivery_type");
                double deliveryCharge = rs.getDouble("delivery_charge");
                String status = rs.getString("status");
                String bookingDate = rs.getString("booking_date");

                Courier courier;
                if ("Express".equalsIgnoreCase(deliveryType)) {
                    courier = new ExpressCourier(id, senderName, senderPhone, receiverName, receiverPhone, destination, parcelWeight);
                } else {
                    courier = new StandardCourier(id, senderName, senderPhone, receiverName, receiverPhone, destination, parcelWeight);
                }
                courier.setDeliveryCharge(deliveryCharge);
                courier.setStatus(status);
                courier.setBookingDate(bookingDate);

                list.add(courier);
            }
        }
        return list;
    }
}
