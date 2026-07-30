-- Flyway Migration: V3__Fix_borrow_records_status_constraint.sql
-- Fixes the borrow_records status check constraint to include 'BORROWED' status

-- Drop the old check constraint and add the corrected one
ALTER TABLE borrow_records DROP CHECK borrow_records_chk_1;

-- Add the corrected check constraint with proper status values
ALTER TABLE borrow_records ADD CONSTRAINT borrow_records_chk_1 CHECK (status IN ('BORROWED', 'RETURNED', 'OVERDUE', 'LOST'));
