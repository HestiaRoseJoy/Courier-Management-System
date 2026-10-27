package com.courier.model;

/**
 * Abstract Class Courier
 * 
 * Demonstrates:
 * 1. ABSTRACTION: An abstract class acts as a blueprint for specialized courier types.
 * 2. ENCAPSULATION: All member variables are private and accessed through public getters and setters.
 * 3. CONSTRUCTORS: Default and parameterized constructors for object initialization.
 */
public abstract class Courier implements Billable {

    // Encapsulation: Private member variables
    private String trackingId;
    private String senderName;
    private String senderPhone;
    private String receiverName;
    private String receiverPhone;
    private String destination;
    private double parcelWeight;
    private double deliveryCharge;
    private String status;
    private String bookingDate;

    // 1. Default Constructor
    public Courier() {
        this.status = "Booked";
    }

    // 2. Parameterized Constructor
    public Courier(String trackingId, String senderName, String senderPhone,
                   String receiverName, String receiverPhone, String destination,
                   double parcelWeight) {
        this.trackingId = trackingId;
        this.senderName = senderName;
        this.senderPhone = senderPhone;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.destination = destination;
        this.parcelWeight = parcelWeight;
        this.status = "Booked";
    }

    // Abstract method from Billable interface (can be implemented in child classes)
    @Override
    public abstract double calculateDeliveryCharge();

    @Override
    public abstract String getDeliveryType();

    // ---------------------------------------------------------
    // Getters and Setters (Demonstrating Encapsulation)
    // ---------------------------------------------------------

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public double getParcelWeight() {
        return parcelWeight;
    }

    public void setParcelWeight(double parcelWeight) {
        if (parcelWeight > 0) {
            this.parcelWeight = parcelWeight;
        }
    }

    public double getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(double deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    // Method to return concise summary of courier details
    public String getCourierSummary() {
        return "ID: " + trackingId + " | From: " + senderName + " | To: " + receiverName + 
               " (" + destination + ") | Type: " + getDeliveryType() + " | Status: " + status;
    }
}
