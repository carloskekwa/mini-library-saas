-- Docker initialization script for MySQL
-- This script is run when the MySQL container starts

-- Create the database if it doesn't exist
CREATE DATABASE IF NOT EXISTS library_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Use the database
USE library_db;

-- Create the library user
CREATE USER IF NOT EXISTS 'library_user'@'%' IDENTIFIED BY 'library_password';

-- Grant privileges to the library user
GRANT ALL PRIVILEGES ON library_db.* TO 'library_user'@'%';
FLUSH PRIVILEGES;
