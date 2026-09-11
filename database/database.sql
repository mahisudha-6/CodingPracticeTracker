-- Create Database if not exists
CREATE DATABASE IF NOT EXISTS coding_tracker_db;
USE coding_tracker_db;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(256) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Topics Table
CREATE TABLE IF NOT EXISTS topics (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- 3. Coding Problems Table
CREATE TABLE IF NOT EXISTS coding_problems (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    platform VARCHAR(100) NOT NULL,
    difficulty VARCHAR(20) NOT NULL, -- 'Easy', 'Medium', 'Hard'
    topic_id INT NOT NULL,
    status VARCHAR(20) NOT NULL,     -- 'Solved', 'Pending', 'Revising'
    date_solved DATE DEFAULT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (topic_id) REFERENCES topics(id) ON DELETE RESTRICT
);

-- Prepopulate Topics (Lookup Table Data)
INSERT IGNORE INTO topics (id, name) VALUES 
(1, 'Arrays'),
(2, 'Strings'),
(3, 'Linked List'),
(4, 'Stack & Queue'),
(5, 'Trees & Graphs'),
(6, 'Sorting & Searching'),
(7, 'Dynamic Programming'),
(8, 'Greedy Algorithms'),
(9, 'Recursion & Backtracking'),
(10, 'Bit Manipulation');
