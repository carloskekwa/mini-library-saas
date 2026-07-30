-- Flyway Migration: V2__Insert_default_roles_and_seed_data.sql
-- Inserts default roles and sample data for testing

-- Insert default roles
INSERT INTO roles (name, description) VALUES
('ADMIN', 'Administrator with full system access'),
('LIBRARIAN', 'Librarian with book management and borrowing permissions'),
('MEMBER', 'Regular member with browse and borrow permissions');

-- Insert default admin user (password: Admin1234! - bcrypted)
-- Hash generated with htpasswd BCrypt
INSERT INTO users (username, email, password_hash, first_name, last_name, status) VALUES
('admin', 'admin@library.local', '$2a$10$60WDXUw/Eqk4HAV/0X7lc.QhqNkYEO3vwswG3A9vPfmWQYsIDanFG', 'Admin', 'User', 'ACTIVE'),
('librarian', 'librarian@library.local', '$2a$10$60WDXUw/Eqk4HAV/0X7lc.QhqNkYEO3vwswG3A9vPfmWQYsIDanFG', 'Librarian', 'User', 'ACTIVE'),
('member1', 'member1@library.local', '$2a$10$60WDXUw/Eqk4HAV/0X7lc.QhqNkYEO3vwswG3A9vPfmWQYsIDanFG', 'John', 'Doe', 'ACTIVE'),
('member2', 'member2@library.local', '$2a$10$60WDXUw/Eqk4HAV/0X7lc.QhqNkYEO3vwswG3A9vPfmWQYsIDanFG', 'Jane', 'Smith', 'ACTIVE');

-- Assign roles to users
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1), -- admin -> ADMIN
(2, 2), -- librarian -> LIBRARIAN
(3, 3), -- member1 -> MEMBER
(4, 3); -- member2 -> MEMBER

-- Insert default categories
INSERT INTO categories (name, description) VALUES
('Fiction', 'Novels, short stories, and fictional works'),
('Non-Fiction', 'Educational and informational books'),
('Science', 'Science and technology books'),
('History', 'Historical books and biographies'),
('Fantasy', 'Fantasy and science fiction'),
('Mystery', 'Mystery and thriller novels'),
('Romance', 'Romance and contemporary fiction'),
('Business', 'Business and self-help books'),
('Programming', 'Computer science and programming'),
('Art', 'Art, design, and photography');

-- Insert sample books (realistic data)
INSERT INTO books (title, author, isbn, category_id, publisher, publication_year, description, language, shelf_location, total_copies, available_copies, status) VALUES
('Clean Code', 'Robert C. Martin', '978-0132350884', 9, 'Prentice Hall', 2008, 'A practical guide to writing code that is readable, maintainable, and elegant.', 'English', 'A1-001', 3, 3, 'AVAILABLE'),
('The Pragmatic Programmer', 'David Thomas', '978-0201616224', 9, 'Addison-Wesley', 1999, 'Your journey to mastery in software development.', 'English', 'A1-002', 2, 2, 'AVAILABLE'),
('Design Patterns', 'Gang of Four', '978-0201633610', 9, 'Addison-Wesley', 1994, 'Elements of Reusable Object-Oriented Software.', 'English', 'A1-003', 2, 1, 'AVAILABLE'),
('The Lord of the Rings', 'J.R.R. Tolkien', '978-0544003415', 5, 'Houghton Mifflin', 2012, 'Epic fantasy trilogy set in Middle-earth.', 'English', 'B1-001', 4, 3, 'AVAILABLE'),
('Harry Potter and the Philosopher Stone', 'J.K. Rowling', '978-0747532699', 5, 'Bloomsbury', 1997, 'A young wizard discovers his magical heritage.', 'English', 'B1-002', 5, 4, 'AVAILABLE'),
('Sherlock Holmes: The Complete Collection', 'Arthur Conan Doyle', '978-0141040691', 6, 'Penguin Classics', 2009, 'The complete adventures of the famous detective.', 'English', 'B2-001', 2, 1, 'AVAILABLE'),
('A Brief History of Time', 'Stephen Hawking', '978-0553380163', 3, 'Bantam', 1988, 'From the Big Bang to Black Holes.', 'English', 'C1-001', 3, 3, 'AVAILABLE'),
('Sapiens', 'Yuval Noah Harari', '978-0062316097', 2, 'Harper', 2014, 'A brief history of humankind.', 'English', 'C1-002', 2, 2, 'AVAILABLE'),
('The Art of War', 'Sun Tzu', '978-0143039975', 8, 'Penguin Classics', 2002, 'Ancient military strategy and tactics.', 'English', 'C2-001', 2, 2, 'AVAILABLE'),
('To Kill a Mockingbird', 'Harper Lee', '978-0061120084', 1, 'J.B. Lippincott', 2006, 'A gripping tale of racial injustice and childhood innocence.', 'English', 'D1-001', 3, 3, 'AVAILABLE'),
('1984', 'George Orwell', '978-0451524935', 1, 'Signet Classics', 2003, 'A dystopian novel about totalitarianism.', 'English', 'D1-002', 2, 2, 'AVAILABLE'),
('The Great Gatsby', 'F. Scott Fitzgerald', '978-0743273565', 1, 'Scribner', 2004, 'Jazz Age romance and the American Dream.', 'English', 'D1-003', 3, 2, 'AVAILABLE'),
('The Hobbit', 'J.R.R. Tolkien', '978-0547928227', 5, 'Houghton Mifflin', 2012, 'A fantasy adventure in Middle-earth.', 'English', 'B1-003', 4, 4, 'AVAILABLE'),
('Python for Data Science', 'Jake VanderPlas', '978-1491912058', 9, 'O\'Reilly', 2016, 'Data analysis and visualization with Python.', 'English', 'A2-001', 2, 2, 'AVAILABLE'),
('Introduction to Algorithms', 'Cormen', '978-0262033848', 9, 'MIT Press', 2009, 'Comprehensive guide to algorithms and data structures.', 'English', 'A2-002', 1, 1, 'AVAILABLE');
