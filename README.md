# 📦 Warehouse Orders Management System

A Java-based desktop application designed to manage clients, products, and orders for a warehouse. The system uses a relational database and is built with a strict layered architecture.

## 🛠️ Tech Stack
* **Language:** Java
* **Database:** MySQL (JDBC)
* **GUI:** Java Swing (JTable)
* **Key Concepts:** Object-Oriented Programming, Java Reflection, Java Records, Lambda Expressions, Streams

## 🏗️ Architecture
The application is structured using a **Layered Architecture** pattern, separated into 4 main packages:
1. **Presentation Layer:** Contains the Graphical User Interface (GUI) classes.
2. **Business Logic Layer:** Handles the core application logic and validations.
3. **Data Access Layer:** Manages database connectivity and CRUD operations.
4. **Model Layer:** Contains the data models mapping to the database tables.

## ✨ Features
* **Client Management:** Add, edit, delete, and view all clients in a dynamically generated JTable.
* **Product Management:** Manage warehouse inventory (add, edit, delete, view products).
* **Order Processing:** Create orders by selecting a client and a product. Includes under-stock validation and automatic stock decrement.
* **Dynamic Table Generation:** Uses **Java Reflection** to dynamically extract object properties and generate JTable headers and rows for any list of objects.
* **Generic Data Access:** A generic class uses Reflection to perform database CRUD operations (create, edit, delete, find) for any model.
* **Immutable Billing:** Generates an immutable `Bill` (using **Java Records**) for each valid order and stores it in a read-only Log table.
