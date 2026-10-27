package com.courier.model;

/**
 * StandardCourier Class
 * 
 * Demonstrates:
 * 1. INHERITANCE: Inherits properties and methods from parent class 'Courier' using 'extends'.
 * 2. METHOD OVERRIDING: Provides specific implementation of calculateDeliveryCharge() for Standard delivery.
 * 3. SUPER KEYWORD: Invokes the constructor of the parent class.
 */
public class StandardCourier extends Courier {

    // Base delivery charge for standard shipping
    private static final double BASE_CHARGE = 50.0;
    // Rate per kilogram
    private static final double RATE_PER_KG = 30.0;

    // Default Constructor
    public StandardCourier() {
        super();
    }

    // Parameterized Constructor
    public StandardCourier(String trackingId, String senderName, String senderPhone,
                           String receiverName, String receiverPhone, String destination,
                           double parcelWeight) {
        // Calling parent class constructor using 'super'
        super(trackingId, senderName, senderPhone, receiverName, receiverPhone, destination, parcelWeight);
    }

    /**
     * METHOD OVERRIDING:
     * Standard Delivery Formula = Base ₹50 + (Weight in kg * ₹30)
     */
    @Override
    public double calculateDeliveryCharge() {
        return BASE_CHARGE + (getParcelWeight() * RATE_PER_KG);
    }

    /**
     * METHOD OVERRIDING:
     * Returns "Standard" as the delivery type.
     */
    @Override
    public String getDeliveryType() {
        return "Standard";
    }
}
