package com.courier.model;

/**
 * ExpressCourier Class
 * 
 * Demonstrates:
 * 1. INHERITANCE: Inherits from 'Courier'.
 * 2. METHOD OVERRIDING: Implements faster express delivery charge calculation.
 * 3. SUPER KEYWORD: Invokes the constructor of the parent class.
 */
public class ExpressCourier extends Courier {

    // Higher base charge for fast-track processing
    private static final double BASE_CHARGE = 100.0;
    // Higher rate per kilogram for express handling
    private static final double RATE_PER_KG = 60.0;

    // Default Constructor
    public ExpressCourier() {
        super();
    }

    // Parameterized Constructor
    public ExpressCourier(String trackingId, String senderName, String senderPhone,
                         String receiverName, String receiverPhone, String destination,
                         double parcelWeight) {
        // Calling parent class constructor using 'super'
        super(trackingId, senderName, senderPhone, receiverName, receiverPhone, destination, parcelWeight);
    }

    /**
     * METHOD OVERRIDING:
     * Express Delivery Formula = Base ₹100 + (Weight in kg * ₹60)
     */
    @Override
    public double calculateDeliveryCharge() {
        return BASE_CHARGE + (getParcelWeight() * RATE_PER_KG);
    }

    /**
     * METHOD OVERRIDING:
     * Returns "Express" as the delivery type.
     */
    @Override
    public String getDeliveryType() {
        return "Express";
    }
}
