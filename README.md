# Courier Management System
**KTU S3 Object-Oriented Programming (Java) Academic Project**

---

## 1. Project Overview
A desktop GUI application developed in Java Swing with MySQL database connectivity using JDBC. This project demonstrates core Object-Oriented Programming concepts in a clean, beginner-friendly architecture suitable for college practical exams and viva discussions.

---

## 2. OOP Concepts Demonstrated
| OOP Concept | Class / Interface | Viva Explanation |
|---|---|---|
| **Encapsulation** | `Courier.java` | Private attributes (`trackingId`, `senderName`, `parcelWeight`, etc.) accessed strictly through public getters and setters with validation logic. |
| **Inheritance** | `StandardCourier.java`, `ExpressCourier.java` | Both subclasses inherit common fields and methods from the abstract `Courier` parent class using the `extends` keyword. |
| **Method Overriding** | Subclasses of `Courier` | Subclasses override `calculateDeliveryCharge()` and `getDeliveryType()` to provide specialized pricing and handling behavior. |
| **Method Overloading** | `DeliveryCalculator.java` | Overloaded `calculateCharge()` methods with different parameter counts (`(weight)`, `(weight, type)`, `(weight, type, discount)`). |
| **Runtime Polymorphism** | `BookingFrame.java`, `CourierDAO.java` | A parent `Courier` reference dynamically resolves and executes overridden subclass methods at runtime based on the actual object instantiated. |
| **Abstraction & Interfaces**| `Billable.java` & `Courier.java` | `Billable` defines the contract for charge calculation; `Courier` provides an abstract base template. |
| **Constructors** | All Model classes | Default and parameterized constructors utilizing the `super(...)` keyword for parent initialization. |
| **Exception Handling** | `InvalidBookingException.java` | Custom user-defined exception class handling form validation (phone format, positive weight, required fields) with Swing dialogs. |

---

## 3. Database Setup (MySQL)
1. Open MySQL Command Line, MySQL Workbench, or phpMyAdmin (XAMPP/WAMP).
2. Execute the script located in [database.sql](file:///c:/Java project/database.sql):
   ```sql
   CREATE DATABASE IF NOT EXISTS courier_db;
   USE courier_db;
   ```
3. Check and update your database credentials in [db.properties](file:///c:/Java project/db.properties):
   ```properties
   db.url=jdbc:mysql://localhost:3306/courier_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   db.user=root
   db.password=your_mysql_password
   ```
   *(If using XAMPP, the default user is `root` with a blank password).*

---

## 4. How to Compile & Run

### Option A: Using Batch Files (Single Click)
- Double-click `compile.bat` to compile all source files into the `bin` folder.
- Double-click `run.bat` to launch the application.

### Option B: Using Terminal Commands
```bash
# 1. Compile:
javac -cp "lib/mysql-connector-j-8.4.0.jar" -d bin src/com/courier/model/*.java src/com/courier/exception/*.java src/com/courier/util/*.java src/com/courier/db/*.java src/com/courier/gui/*.java src/com/courier/main/*.java

# 2. Run:
java -cp "bin;lib/mysql-connector-j-8.4.0.jar" com.courier.main.Main
```

---

## 5. Current Phase Scope (Phase 1)
- **Main Dashboard**: Navigation interface with connection status indicator.
- **Courier Booking Form**: Complete booking workflow with live charge calculation, automatic tracking ID generation, input validation, and MySQL persistence using `PreparedStatement`.
- **Planned for Next Phases**: Track Courier screen, Status Update screen, and View All Shipments `JTable`.
