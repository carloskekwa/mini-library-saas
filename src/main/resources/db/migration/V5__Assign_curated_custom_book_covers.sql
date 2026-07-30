-- Flyway Migration: V5__Assign_curated_custom_book_covers.sql
-- Assigns curated custom cover URLs for known seeded titles.
-- Fallback remains the existing cover_image_url (e.g., ISBN-based from V4) for all other titles.

UPDATE books
SET cover_image_url = 'https://placehold.co/400x600/0d47a1/ffffff?text=Clean+Code'
WHERE title = 'Clean Code';

UPDATE books
SET cover_image_url = 'https://placehold.co/400x600/1565c0/ffffff?text=The+Pragmatic+Programmer'
WHERE title = 'The Pragmatic Programmer';

UPDATE books
SET cover_image_url = 'https://placehold.co/400x600/283593/ffffff?text=Design+Patterns'
WHERE title = 'Design Patterns';

UPDATE books
SET cover_image_url = 'https://placehold.co/400x600/2e7d32/ffffff?text=The+Lord+of+the+Rings'
WHERE title = 'The Lord of the Rings';

UPDATE books
SET cover_image_url = 'https://placehold.co/400x600/388e3c/ffffff?text=Harry+Potter+and+the+Philosopher%27s+Stone'
WHERE title = 'Harry Potter and the Philosopher Stone';

UPDATE books
SET cover_image_url = 'https://placehold.co/400x600/00695c/ffffff?text=Sherlock+Holmes:+The+Complete+Collection'
WHERE title = 'Sherlock Holmes: The Complete Collection';

UPDATE books
SET cover_image_url = 'https://placehold.co/400x600/455a64/ffffff?text=A+Brief+History+of+Time'
WHERE title = 'A Brief History of Time';

UPDATE books
SET cover_image_url = 'https://placehold.co/400x600/5d4037/ffffff?text=Sapiens'
WHERE title = 'Sapiens';

UPDATE books
SET cover_image_url = 'https://placehold.co/400x600/bf360c/ffffff?text=The+Art+of+War'
WHERE title = 'The Art of War';

UPDATE books
SET cover_image_url = 'https://placehold.co/400x600/6a1b9a/ffffff?text=To+Kill+a+Mockingbird'
WHERE title = 'To Kill a Mockingbird';

UPDATE books
SET cover_image_url = 'https://placehold.co/400x600/ad1457/ffffff?text=1984'
WHERE title = '1984';

UPDATE books
SET cover_image_url = 'https://placehold.co/400x600/4e342e/ffffff?text=The+Great+Gatsby'
WHERE title = 'The Great Gatsby';

UPDATE books
SET cover_image_url = 'https://placehold.co/400x600/1b5e20/ffffff?text=The+Hobbit'
WHERE title = 'The Hobbit';

UPDATE books
SET cover_image_url = 'https://placehold.co/400x600/0277bd/ffffff?text=Python+for+Data+Science'
WHERE title = 'Python for Data Science';

UPDATE books
SET cover_image_url = 'https://placehold.co/400x600/37474f/ffffff?text=Introduction+to+Algorithms'
WHERE title = 'Introduction to Algorithms';
