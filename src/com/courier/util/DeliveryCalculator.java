package com.courier.util;

/**
 * Utility Class: DeliveryCalculator
 * 
 * Demonstrates: METHOD OVERLOADING (Compile-time Polymorphism)
 * Method overloading occurs when multiple methods in the same class share the same
 * name but have different parameter lists (different number, types, or order of parameters).
 */
public class DeliveryCalculator {

    // Overloaded Method 1: Takes only weight (defaults to Standard delivery)
    public static double calculateCharge(double weight) {
        double base = 50.0;
        double perKg = 30.0;
        return base + (weight * perKg);
    }

    // Overloaded Method 2: Takes weight AND delivery type
    public static double calculateCharge(double weight, String deliveryType) {
        if ("Express".equalsIgnoreCase(deliveryType)) {
            double expressBase = 100.0;
            double expressPerKg = 60.0;
            return expressBase + (weight * expressPerKg);
        } else {
            // Standard
            return calculateCharge(weight); // Calls Overloaded Method 1
        }
    }

    // Overloaded Method 3: Takes weight, delivery type, AND student/promotional discount percentage
    public static double calculateCharge(double weight, String deliveryType, double discountPercent) {
        double total = calculateCharge(weight, deliveryType); // Calls Overloaded Method 2
        if (discountPercent > 0 && discountPercent <= 100) {
            double discountAmount = (total * discountPercent) / 100.0;
            total = total - discountAmount;
        }
        return total;
    }
}
