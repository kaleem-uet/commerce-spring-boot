-- Create categories table
CREATE TABLE categories (
    id SMALLSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Create users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255),
    password VARCHAR(255)
);

-- Create profiles table (one-to-one with users, shared primary key)
CREATE TABLE profiles (
    id BIGINT PRIMARY KEY,
    bio TEXT,
    phone_number VARCHAR(50),
    date_of_birth DATE,
    loyalty_points INTEGER,
    CONSTRAINT fk_profile_user FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create addresses table (one-to-many with users)
CREATE TABLE addresses (
    id BIGSERIAL PRIMARY KEY,
    street VARCHAR(255),
    city VARCHAR(255),
    zip VARCHAR(20),
    state VARCHAR(100),
    user_id BIGINT,
    CONSTRAINT fk_address_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create products table (many-to-one with categories)
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    description TEXT,
    price DECIMAL(19, 2),
    category_id SMALLINT,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Create wishlist table (many-to-many join table between users and products)
CREATE TABLE wishlist (
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, product_id),
    CONSTRAINT fk_wishlist_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_wishlist_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Create indexes for foreign keys to improve query performance
CREATE INDEX idx_addresses_user_id ON addresses(user_id);
CREATE INDEX idx_products_category_id ON products(category_id);
CREATE INDEX idx_wishlist_user_id ON wishlist(user_id);
CREATE INDEX idx_wishlist_product_id ON wishlist(product_id);
