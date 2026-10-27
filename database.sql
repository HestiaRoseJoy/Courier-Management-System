-- ====================================================================
-- Database Creation Script for Courier Management System (KTU S3 OOP)
-- ====================================================================

-- 1. Create database if it does not already exist
CREATE DATABASE IF NOT EXISTS courier_db;

-- 2. Switch to courier_db
USE courier_db;

-- 3. Create couriers table
CREATE TABLE IF NOT EXISTS couriers (
    tracking_id VARCHAR(20) PRIMARY KEY,
    sender_name VARCHAR(100) NOT NULL,
    sender_phone VARCHAR(15) NOT NULL,
    receiver_name VARCHAR(100) NOT NULL,
    receiver_phone VARCHAR(15) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    parcel_weight DOUBLE NOT NULL,
    delivery_type VARCHAR(20) NOT NULL,
    delivery_charge DOUBLE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'Booked',
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. Sample record for quick testing (Optional)
INSERT INTO couriers (
    tracking_id, sender_name, sender_phone, receiver_name, receiver_phone, 
    destination, parcel_weight, delivery_type, delivery_charge, status
) VALUES (
    'TRK1001', 'Rahul Sharma', '9876543210', 'Anjali Nair', '9876501234', 
    'Kochi', 2.5, 'Standard', 125.0, 'Booked'
) ON DUPLICATE KEY UPDATE tracking_id=tracking_id;
