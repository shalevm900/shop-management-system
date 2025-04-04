-- DATABASE NAME - store

-- Create the GENDER enum type
CREATE TYPE GENDER AS ENUM ('Male', 'Female', 'No-Gender');

-- Create the Products table
CREATE TABLE Products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    available INT NOT NULL CHECK (available >= 0),
    quantity INT NOT NULL CHECK (quantity >= 0),
    price NUMERIC(10,2) NOT NULL CHECK (price >= 0),
    last_update DATE NOT NULL DEFAULT CURRENT_DATE,
    product_type VARCHAR(20) NOT NULL  -- Column to distinguish product types
);

-- Create attribute tables for each product type
CREATE TABLE Books (
    product_id INT PRIMARY KEY REFERENCES Products(id) ON DELETE CASCADE,
    author VARCHAR(40) NOT NULL,
    pages INT NOT NULL CHECK (pages > 0)
);

CREATE TABLE Electronics (
    product_id INT PRIMARY KEY REFERENCES Products(id) ON DELETE CASCADE,
    brand VARCHAR(40) NOT NULL,
    model VARCHAR(70) NOT NULL
);

CREATE TABLE Clothing (
    product_id INT PRIMARY KEY REFERENCES Products(id) ON DELETE CASCADE,
    size VARCHAR(20) NOT NULL,
    color VARCHAR(20) NOT NULL,
    gender GENDER NOT NULL
);

-- Create the Customers table
CREATE TABLE Customers (
    cid SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL
);

-- Create the Orders table
CREATE TABLE Orders (
    order_id SERIAL PRIMARY KEY,
    cid INT NOT NULL REFERENCES Customers(cid) ON DELETE CASCADE,
    order_date DATE NOT NULL DEFAULT CURRENT_DATE,
    total_price NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (total_price >= 0)
);

-- Create the OrderProducts table
CREATE TABLE OrderProducts (
    order_product_id SERIAL PRIMARY KEY,
    order_id INT NOT NULL REFERENCES Orders(order_id) ON DELETE CASCADE,
    product_id INT NOT NULL REFERENCES Products(id),
    quantity INT NOT NULL CHECK (quantity > 0),
    price_per_unit NUMERIC(10,2) NOT NULL CHECK (price_per_unit >= 0)
);
-- Create the CustomerOrderSummary table

CREATE TABLE CustomerOrderSummary (
    cid INT PRIMARY KEY,
    total_orders INT NOT NULL DEFAULT 0,
    total_spent NUMERIC(10,2) NOT NULL DEFAULT 0,
    FOREIGN KEY (cid) REFERENCES Customers(cid)
);


-- Function to update total_price in Orders after inserting or updating OrderProducts
CREATE OR REPLACE FUNCTION update_order_total_price()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE Orders
    SET total_price = (
        SELECT COALESCE(SUM(quantity * price_per_unit), 0)
        FROM OrderProducts
        WHERE order_id = NEW.order_id
    )
    WHERE order_id = NEW.order_id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger to invoke the update_order_total_price function after insert or update on OrderProducts
CREATE TRIGGER trg_update_order_total_price
AFTER INSERT OR UPDATE OR DELETE ON OrderProducts
FOR EACH ROW
EXECUTE FUNCTION update_order_total_price();

-- Function to reduce product quantity after an order is placed
CREATE OR REPLACE FUNCTION reduce_product_quantity()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE Products
    SET available = available - NEW.quantity
    WHERE id = NEW.product_id AND quantity >= NEW.quantity;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger to invoke the reduce_product_quantity function after inserting into OrderProducts
CREATE TRIGGER trg_reduce_product_quantity
AFTER INSERT ON OrderProducts
FOR EACH ROW
EXECUTE FUNCTION reduce_product_quantity();

CREATE OR REPLACE FUNCTION update_customer_order_summary()
RETURNS TRIGGER AS $$
BEGIN
    -- Handle INSERT or UPDATE
    IF TG_OP = 'INSERT' OR TG_OP = 'UPDATE' THEN
        -- Update or insert summary
        INSERT INTO CustomerOrderSummary (cid, total_orders, total_spent)
        VALUES (
            NEW.cid,
            (SELECT COUNT(*) FROM Orders WHERE cid = NEW.cid),
            (SELECT COALESCE(SUM(total_price), 0) FROM Orders WHERE cid = NEW.cid)
        )
        ON CONFLICT (cid) 
        DO UPDATE SET 
            total_orders = EXCLUDED.total_orders, 
            total_spent = EXCLUDED.total_spent;

    -- Handle DELETE
    ELSIF TG_OP = 'DELETE' THEN
        -- Update summary for the customer, removing the deleted order
        UPDATE CustomerOrderSummary
        SET total_orders = (
            SELECT COUNT(*)
            FROM Orders
            WHERE cid = OLD.cid
        ),
        total_spent = (
            SELECT COALESCE(SUM(total_price), 0)
            FROM Orders
            WHERE cid = OLD.cid
        )
        WHERE cid = OLD.cid;

        -- If the customer no longer has any orders, remove their summary
        IF NOT EXISTS (SELECT 1 FROM Orders WHERE cid = OLD.cid) THEN
            DELETE FROM CustomerOrderSummary
            WHERE cid = OLD.cid;
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_customer_order_summary
AFTER INSERT OR UPDATE OR DELETE ON Orders
FOR EACH ROW
EXECUTE FUNCTION update_customer_order_summary();
	
-- Insert into Products table for Books
INSERT INTO Products (name, available, quantity, last_update, price, product_type) VALUES
('To Kill a Mockingbird', 15, 15, CURRENT_DATE, 12.99, 'Book'),
('1984', 25, 25, CURRENT_DATE, 9.99, 'Book'),
('Harry Potter and the Sorcerer''s Stone', 30, 30, CURRENT_DATE, 14.99, 'Book'),
('Brave New World', 17, 17, CURRENT_DATE, 10.99, 'Book'),
('The Lord of the Rings', 28, 28, CURRENT_DATE, 25.99, 'Book'),
('To the Lighthouse', 14, 14, CURRENT_DATE, 11.99, 'Book'),
('The Catcher in the Rye', 22, 22, CURRENT_DATE, 8.99, 'Book'),
('The Great Gatsby', 20, 20, CURRENT_DATE, 10.99, 'Book'),
('The Alchemist', 12, 12, CURRENT_DATE, 9.99, 'Book'),
('Pride and Prejudice', 18, 18, CURRENT_DATE, 8.99, 'Book');

-- Insert into Books table
INSERT INTO Books (product_id, author, pages) VALUES
((SELECT id FROM Products WHERE name = 'To Kill a Mockingbird'), 'Harper Lee', 281),
((SELECT id FROM Products WHERE name = '1984'), 'George Orwell', 328),
((SELECT id FROM Products WHERE name = 'Harry Potter and the Sorcerer''s Stone'), 'J.K. Rowling', 320),
((SELECT id FROM Products WHERE name = 'Brave New World'), 'Aldous Huxley', 325),
((SELECT id FROM Products WHERE name = 'The Lord of the Rings'), 'J.R.R. Tolkien', 1178),
((SELECT id FROM Products WHERE name = 'To the Lighthouse'), 'Virginia Woolf', 209),
((SELECT id FROM Products WHERE name = 'The Catcher in the Rye'), 'J.D. Salinger', 234),
((SELECT id FROM Products WHERE name = 'The Great Gatsby'), 'F. Scott Fitzgerald', 180),
((SELECT id FROM Products WHERE name = 'The Alchemist'), 'Paulo Coelho', 197),
((SELECT id FROM Products WHERE name = 'Pride and Prejudice'), 'Jane Austen', 279);

-- Insert into Products table for Clothing
INSERT INTO Products (name, available, quantity, last_update, price, product_type) VALUES
('The North Face Resolve 2 Jacket', 17, 17, CURRENT_DATE, 89.99, 'Clothing'),
('Calvin Klein Underwear (Pack of 3)', 30, 30, CURRENT_DATE, 29.99, 'Clothing'),
('Vans Old Skool Sneakers', 28, 28, CURRENT_DATE, 59.99, 'Clothing'),
('Puma Men''s Essential Sweatpants', 22, 22, CURRENT_DATE, 39.99, 'Clothing'),
('Nike Air Max 90 Sneakers', 15, 15, CURRENT_DATE, 120.00, 'Clothing'),
('Levi''s 501 Original Fit Jeans', 20, 20, CURRENT_DATE, 69.99, 'Clothing'),
('Adidas Originals Trefoil Hoodie', 18, 18, CURRENT_DATE, 64.99, 'Clothing'),
('Lululemon Align Leggings', 12, 12, CURRENT_DATE, 98.00, 'Clothing'),
('Tommy Hilfiger Classic T-Shirt', 25, 25, CURRENT_DATE, 24.99, 'Clothing'),
('Champion Reverse Weave Hoodie', 14, 14, CURRENT_DATE, 59.99, 'Clothing');

-- Insert into Clothing table
INSERT INTO Clothing (product_id, size, color, gender) VALUES
((SELECT id FROM Products WHERE name = 'The North Face Resolve 2 Jacket'), 'Medium', 'Blue', 'Female'),
((SELECT id FROM Products WHERE name = 'Calvin Klein Underwear (Pack of 3)'), 'Medium', 'Assorted Colors', 'Male'),
((SELECT id FROM Products WHERE name = 'Vans Old Skool Sneakers'), 'US 9', 'White', 'Male'),
((SELECT id FROM Products WHERE name = 'Puma Men''s Essential Sweatpants'), 'Large', 'Black', 'Male'),
((SELECT id FROM Products WHERE name = 'Nike Air Max 90 Sneakers'), 'US 10', 'Black', 'Female'),
((SELECT id FROM Products WHERE name = 'Levi''s 501 Original Fit Jeans'), '32x32', 'Blue', 'Male'),
((SELECT id FROM Products WHERE name = 'Adidas Originals Trefoil Hoodie'), 'Large', 'Grey', 'Male'),
((SELECT id FROM Products WHERE name = 'Lululemon Align Leggings'), 'Medium', 'Black', 'Female'),
((SELECT id FROM Products WHERE name = 'Tommy Hilfiger Classic T-Shirt'), 'Small', 'Navy', 'Female'),
((SELECT id FROM Products WHERE name = 'Champion Reverse Weave Hoodie'), 'Small', 'Red', 'Female');

-- Insert into Products table for Electronics
INSERT INTO Products (name, available, quantity, last_update, price, product_type) VALUES
('Lenovo ThinkPad X1 Carbon', 8, 8, CURRENT_DATE, 1299.99, 'Electronics'),
('Sony Alpha a7 III Camera', 7, 7, CURRENT_DATE, 1999.99, 'Electronics'),
('iPhone 13 Pro', 10, 10, CURRENT_DATE, 999.99, 'Electronics'),
('Samsung QLED 65-inch TV', 5, 5, CURRENT_DATE, 1799.99, 'Electronics'),
('Bose QuietComfort Earbuds', 12, 12, CURRENT_DATE, 279.99, 'Electronics'),
('Amazon Echo Dot', 30, 30, CURRENT_DATE, 49.99, 'Electronics'),
('Xiaomi Mi Smart Band 6', 25, 25, CURRENT_DATE, 44.99, 'Electronics'),
('Sony WH-1000XM4 Wireless Headphones', 18, 18, CURRENT_DATE, 349.99, 'Electronics'),
('Microsoft Surface Laptop 4', 20, 20, CURRENT_DATE, 1199.99, 'Electronics'),
('Canon EOS R6 Mirrorless Camera', 15, 15, CURRENT_DATE, 2499.99, 'Electronics');

-- Insert into Electronics table
INSERT INTO Electronics (product_id, brand, model) VALUES
((SELECT id FROM Products WHERE name = 'Lenovo ThinkPad X1 Carbon'), 'Lenovo', 'ThinkPad X1 Carbon'),
((SELECT id FROM Products WHERE name = 'Sony Alpha a7 III Camera'), 'Sony', 'Alpha a7 III'),
((SELECT id FROM Products WHERE name = 'iPhone 13 Pro'), 'Apple', 'iPhone 13 Pro'),
((SELECT id FROM Products WHERE name = 'Samsung QLED 65-inch TV'), 'Samsung', 'QLED 65-inch'),
((SELECT id FROM Products WHERE name = 'Bose QuietComfort Earbuds'), 'Bose', 'QuietComfort Earbuds'),
((SELECT id FROM Products WHERE name = 'Amazon Echo Dot'), 'Amazon', 'Echo Dot'),
((SELECT id FROM Products WHERE name = 'Xiaomi Mi Smart Band 6'), 'Xiaomi', 'Mi Smart Band 6'),
((SELECT id FROM Products WHERE name = 'Sony WH-1000XM4 Wireless Headphones'), 'Sony', 'WH-1000XM4'),
((SELECT id FROM Products WHERE name = 'Microsoft Surface Laptop 4'), 'Microsoft', 'Surface Laptop 4'),
((SELECT id FROM Products WHERE name = 'Canon EOS R6 Mirrorless Camera'), 'Canon', 'EOS R6');

INSERT INTO Customers (first_name, last_name) VALUES
('Alice', 'Johnson'),
('Bob', 'Smith'),
('Carol', 'Williams'),
('David', 'Brown'),
('Emma', 'Davis');

ALTER TABLE orderproducts
ALTER COLUMN price_per_unit TYPE float;

ALTER TABLE products
ALTER COLUMN price TYPE float;

ALTER TABLE customerordersummary
ALTER COLUMN total_spent TYPE float;

ALTER TABLE orders
ALTER COLUMN total_price TYPE float;

CREATE VIEW ClothingProducts AS
SELECT 
    p.id AS product_id,
    p.name AS name,
	c.size,
    c.color,
	c.gender,
    p.available,
    p.quantity,
    p.price,
    p.last_update
FROM 
    Products p
JOIN 
    Clothing c ON p.id = c.product_id;

CREATE VIEW BookProducts AS
SELECT 
    p.id AS product_id,
    p.name AS name,
	b.author,
    b.pages,
    p.available,
    p.quantity,
    p.price,
    p.last_update
FROM 
    Products p
JOIN 
    Books b ON p.id = b.product_id;
CREATE VIEW ElectronicsProducts AS
SELECT 
    p.id AS product_id,
    p.name AS name,
	e.brand,
    e.model,
    p.available,
    p.quantity,
    p.price,
    p.last_update
FROM 
    Products p
JOIN 
    Electronics e ON p.id = e.product_id;
	
insert into Orders (cid) Values (1) returning order_id;

insert into orderProducts (order_id, product_id, quantity, price_per_unit) VALUES
(1,1,2,(select price from products where id = 1));

insert into Orders (cid) Values (1) returning order_id;

insert into orderProducts (order_id, product_id, quantity, price_per_unit) VALUES
(2,3,1,(select price from products where id = 3)),
(2,5,1,(select price from products where id = 5));

insert into Orders (cid) Values (2) returning order_id;
insert into orderProducts (order_id, product_id, quantity, price_per_unit) VALUES
(3,1,1,(select price from products where id = 1)),
(3,2,1,(select price from products where id = 2));

