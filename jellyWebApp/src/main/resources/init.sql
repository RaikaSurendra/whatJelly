-- Database initialization script
-- Creates tables and populates sample data

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(50) DEFAULT 'user',
    active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Categories table
CREATE TABLE IF NOT EXISTS categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Products table
CREATE TABLE IF NOT EXISTS products (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    category_id INT,
    stock INT DEFAULT 0,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Orders table
CREATE TABLE IF NOT EXISTS orders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Order items table
CREATE TABLE IF NOT EXISTS order_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Insert sample users
INSERT INTO users (name, email, password, role, active) VALUES
('John Doe', 'john@example.com', 'password123', 'admin', TRUE),
('Jane Smith', 'jane@example.com', 'password123', 'user', TRUE),
('Bob Wilson', 'bob@example.com', 'password123', 'user', TRUE),
('Alice Johnson', 'alice@example.com', 'password123', 'user', FALSE),
('Charlie Brown', 'charlie@example.com', 'password123', 'user', TRUE);

-- Insert sample categories
INSERT INTO categories (name, description) VALUES
('Electronics', 'Electronic devices and gadgets'),
('Books', 'Physical and digital books'),
('Clothing', 'Apparel and accessories'),
('Home & Garden', 'Home improvement and garden supplies'),
('Sports', 'Sports equipment and gear');

-- Insert sample products
INSERT INTO products (name, description, price, category_id, stock, active) VALUES
('Laptop Pro', 'High-performance laptop for professionals', 1299.99, 1, 15, TRUE),
('Wireless Mouse', 'Ergonomic wireless mouse with USB receiver', 29.99, 1, 50, TRUE),
('USB-C Cable', '6ft USB-C charging cable', 12.99, 1, 100, TRUE),
('Programming Book', 'Learn advanced programming concepts', 49.99, 2, 30, TRUE),
('Fiction Novel', 'Bestselling fiction novel', 14.99, 2, 45, TRUE),
('T-Shirt', 'Cotton t-shirt in various colors', 19.99, 3, 75, TRUE),
('Jeans', 'Classic denim jeans', 59.99, 3, 40, TRUE),
('Garden Tools Set', 'Complete set of garden tools', 89.99, 4, 20, TRUE),
('LED Light Bulbs', 'Energy efficient LED bulbs (4-pack)', 24.99, 4, 60, TRUE),
('Tennis Racket', 'Professional tennis racket', 129.99, 5, 12, TRUE),
('Yoga Mat', 'Non-slip yoga mat with carrying strap', 34.99, 5, 25, TRUE),
('Bluetooth Speaker', 'Portable waterproof speaker', 79.99, 1, 35, TRUE);

-- Insert sample orders
INSERT INTO orders (user_id, total, status) VALUES
(2, 1329.98, 'completed'),
(3, 89.97, 'completed'),
(2, 164.97, 'pending'),
(5, 79.99, 'shipped');

-- Insert sample order items
INSERT INTO order_items (order_id, product_id, quantity, price) VALUES
(1, 1, 1, 1299.99),
(1, 2, 1, 29.99),
(2, 6, 3, 19.99),
(2, 7, 1, 59.99),
(3, 4, 1, 49.99),
(3, 5, 2, 14.99),
(3, 12, 1, 79.99),
(4, 12, 1, 79.99);
