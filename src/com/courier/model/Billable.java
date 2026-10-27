package com.courier.model;

/**
 * Interface Billable
 * 
 * Demonstrates: ABSTRACTION & INTERFACE
 * An interface defines a contract specifying what methods a class must implement,
 * without providing the implementation details.
 */
public interface Billable {

    /**
     * Calculates the delivery charge based on weight and type.
     * Each implementing class will provide its own calculation formula.
     * 
     * @return double delivery charge amount in rupees
     */
    double calculateDeliveryCharge();

    /**
     * Returns the delivery category (e.g., Standard, Express).
     * 
     * @return String delivery type
     */
    String getDeliveryType();
}
