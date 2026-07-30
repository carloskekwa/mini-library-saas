-- Flyway Migration: V4__Populate_book_cover_urls.sql
-- Populates cover_image_url for all books using Open Library ISBN covers.

UPDATE books
SET cover_image_url = CONCAT(
    'https://covers.openlibrary.org/b/isbn/',
    REPLACE(isbn, '-', ''),
    '-L.jpg'
)
WHERE isbn IS NOT NULL
  AND isbn <> ''
  AND (cover_image_url IS NULL OR TRIM(cover_image_url) = '');
