# Role Verification Matrix

This checklist validates role-based behavior for the seeded personas:
- admin (ADMIN)
- librarian (LIBRARIAN)
- member1 (MEMBER)
- member2 (MEMBER)

## Preconditions
- Backend and frontend are running.
- Login credentials are valid.
- Seed data exists from migrations.

## Route Access Matrix

| Route | ADMIN | LIBRARIAN | MEMBER |
|---|---|---|---|
| /dashboard/admin | Allow | Deny | Deny |
| /dashboard/librarian | Allow | Allow | Deny |
| /dashboard/member | Allow | Allow | Allow |
| /manage/books | Allow | Allow | Deny |
| /manage/categories | Allow | Allow | Deny |
| /reports | Allow | Allow | Deny |
| /operations/batch-import | Allow | Allow | Deny |
| /admin/penalties | Allow | Allow | Deny |
| /admin/audit-logs | Allow | Deny | Deny |
| /admin/users | Allow | Deny | Deny |
| /admin/config | Allow | Deny | Deny |
| /admin/email-templates | Allow | Deny | Deny |
| /admin/scheduled-tasks | Allow | Deny | Deny |
| /books, /borrows, /reservations, /wishlist, /book-requests, /notifications | Allow | Allow | Allow |

## Scenario Checklist: admin

1. Login as admin and verify redirect to /dashboard/admin.
2. Open /manage/books and create, edit, and delete one test book.
3. Open /manage/categories and create, edit, and delete one test category.
4. Open /reports and generate:
- popular books report
- circulation report with a start/end range
5. Delete a generated report.
6. Open /admin/penalties and create a penalty for userId 3.
7. From /admin/penalties, load active penalties for userId 3 and lift one.
8. Open /admin/audit-logs and test:
- search by userId
- search by date range
9. Open /admin/users and verify:
- list/search users by username/email
- filter by status
- update user status (ACTIVE, INACTIVE, SUSPENDED)
- select a user and open penalties/audit shortcuts with prefilled query params
10. Open /admin/config and upsert one config property, then delete it.
11. Open /admin/email-templates, load a template type, update and save.
12. Open /admin/scheduled-tasks, create one task and disable it.
13. Open /operations/batch-import and create a job; start and complete it.

Expected:
- All actions succeed or return business-validation errors with readable messages.
- Admin-only routes stay accessible.
- Admin menu shows all staff and admin entries.

## Scenario Checklist: librarian

1. Login as librarian and verify redirect to /dashboard/librarian.
2. Confirm /dashboard/admin and /admin/* routes are blocked.
3. Open /manage/books and create/edit a book; delete only if backend allows librarian delete (should be denied for ADMIN-only delete).
4. Open /manage/categories and create/edit categories.
5. Open /reservations, open a queue by book, fulfill a pending reservation.
6. Open /reports and generate reports (delete should be denied).
7. Open /operations/batch-import and create/list jobs (start/complete should be denied if ADMIN-only).
8. Open /admin/penalties and create a penalty.

Expected:
- Librarian can use operational modules.
- Admin-only actions return 403 or are hidden.
- Librarian menu excludes admin-only entries (/admin/users, /admin/audit-logs, /admin/config, /admin/email-templates, /admin/scheduled-tasks).

## Scenario Checklist: member1/member2

1. Login as member and verify redirect to /dashboard/member.
2. Confirm access denied for /manage/*, /reports, /operations/batch-import, /admin/*.
3. In /books:
- search books
- borrow available book
- reserve a book
- add to wishlist
4. In /books/:id:
- submit review
- delete own review only (ADMIN can delete any; MEMBER only own review)
5. In /wishlist:
- verify item appears
- remove item
6. In /book-requests:
- submit request
- verify appears in My Requests
7. In /borrows:
- renew borrow
- return borrow with condition
- pay fine if an unpaid fine exists in return records
8. In /reservations:
- cancel one reservation
9. In /notifications:
- mark read and verify unread badge changes

Expected:
- Member features work end-to-end.
- Staff/admin routes are blocked by role guard.
- Member menu excludes staff/admin navigation entries.

## URL State Persistence Checklist

1. /books
- set q and move page
- reload and verify q/page preserved
2. /reports
- set start/end and page
- reload and verify state restored
3. /operations/batch-import
- change page and reload
- verify page persists
4. /admin/penalties
- set userId lookup and load
- reload and verify lookup preserved
5. /admin/audit-logs
- run user or range search
- reload and verify mode + inputs restored

## User Management API Checks

1. GET /api/users?page=0&pageSize=20&search=adm&status=ACTIVE returns paged UserDTO content.
2. GET /api/users/{id} returns a single UserDTO.
3. PUT /api/users/{id}/status?status=SUSPENDED updates user status and returns UserDTO.

Expected:
- All endpoints require ADMIN role.
- Frontend /admin/users is fully wired to these endpoints.
