-- Flyway Migration: V6__Add_default_cover_placeholder_triggers.sql
-- Applies a curated default placeholder cover URL when cover_image_url is empty
-- on insert or update of books.

DROP TRIGGER IF EXISTS trg_books_default_cover_before_insert;
DROP TRIGGER IF EXISTS trg_books_default_cover_before_update;

CREATE TRIGGER trg_books_default_cover_before_insert
BEFORE INSERT ON books
FOR EACH ROW
SET NEW.cover_image_url = IF(
    NEW.cover_image_url IS NULL OR TRIM(NEW.cover_image_url) = '',
    CONCAT(
        'https://placehold.co/400x600/263238/ffffff?text=',
        REPLACE(COALESCE(NULLIF(TRIM(NEW.title), ''), 'Library Book'), ' ', '+')
    ),
    NEW.cover_image_url
);

CREATE TRIGGER trg_books_default_cover_before_update
BEFORE UPDATE ON books
FOR EACH ROW
SET NEW.cover_image_url = IF(
    NEW.cover_image_url IS NULL OR TRIM(NEW.cover_image_url) = '',
    CONCAT(
        'https://placehold.co/400x600/263238/ffffff?text=',
        REPLACE(COALESCE(NULLIF(TRIM(NEW.title), ''), 'Library Book'), ' ', '+')
    ),
    NEW.cover_image_url
);
