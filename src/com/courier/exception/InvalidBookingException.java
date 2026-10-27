package com.courier.exception;

/**
 * Custom Exception: InvalidBookingException
 * 
 * Demonstrates: EXCEPTION HANDLING (User-defined Checked Exception)
 * In Java, custom exceptions extend the Exception class to handle domain-specific errors.
 */
public class InvalidBookingException extends Exception {

    // Default Constructor
    public InvalidBookingException() {
        super("Invalid courier booking details entered.");
    }

    // Parameterized Constructor accepting custom error message
    public InvalidBookingException(String message) {
        super(message);
    }
}
