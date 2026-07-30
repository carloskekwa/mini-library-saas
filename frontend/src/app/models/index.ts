export interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  status: string;
  roles: string[];
  createdAt?: string;
  updatedAt?: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  passwordConfirm: string;
  firstName: string;
  lastName: string;
}

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  userId: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  roles: string[];
  expiresAt: string;
  user?: User;
}

export interface Category {
  id: number;
  name: string;
  description: string;
}

export interface Book {
  id: number;
  title: string;
  author: string;
  isbn: string;
  category?: Category;
  publisher: string;
  publicationYear: number;
  description: string;
  language: string;
  shelfLocation: string;
  totalCopies: number;
  availableCopies: number;
  coverImageUrl: string;
  status: string;
}

export interface CreateBookRequest {
  title: string;
  author: string;
  categoryId: number;
  totalCopies: number;
  availableCopies?: number;
  isbn?: string;
  publisher?: string;
  publicationYear?: number;
  description?: string;
  language?: string;
  shelfLocation?: string;
  coverImageUrl?: string;
}

export interface UpdateBookRequest {
  title: string;
  author: string;
  totalCopies: number;
  availableCopies?: number;
  publisher?: string;
  publicationYear?: number;
  description?: string;
  language?: string;
  shelfLocation?: string;
  coverImageUrl?: string;
}

export interface PagedResponse<T> {
  content: T[];
  totalElements: number;
}

export interface BorrowRecord {
  id: number;
  userId: number;
  username?: string;
  bookId: number;
  bookTitle?: string;
  bookAuthor?: string;
  totalCopies?: number;
  availableCopies?: number;
  book: Book;
  borrowDate: string;
  dueDate: string;
  returnDate: string;
  status: string;
  renewalCount: number;
  isOverdue: boolean;
}

export interface ReturnRecord {
  id: number;
  borrowRecordId: number;
  userId: number;
  username: string;
  bookId: number;
  bookTitle: string;
  returnDate: string;
  bookCondition: string;
  damageNotes: string;
  daysLate: number;
  fineAmount: number;
  finePaid: boolean;
  finePaidDate: string;
}

export interface Reservation {
  id: number;
  userId: number;
  username?: string;
  bookId: number;
  bookTitle?: string;
  book: Book;
  reservationDate: string;
  expiryDate: string;
  status: string;
  positionInQueue: number;
}

export interface Review {
  id: number;
  bookId: number;
  userId: number;
  rating: number;
  reviewText: string;
  helpfulCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface Notification {
  id: number;
  userId: number;
  type: string;
  title: string;
  message: string;
  isRead: boolean;
  createdAt: string;
  readAt: string;
}

export interface Penalty {
  id: number;
  userId: number;
  type: string;
  reason: string;
  status: string;
  suspendedUntil: string;
  createdAt: string;
}

export interface WishlistItem {
  id: number;
  userId: number;
  bookId: number;
  bookTitle: string;
  addedAt: string;
}

export interface BookRequest {
  id: number;
  userId: number;
  bookTitle: string;
  author: string;
  isbn: string;
  status: string;
  requestedAt: string;
  processedAt: string;
}

export interface Report {
  id: number;
  type: string;
  content: string;
  generatedDate: string;
  startDate: string;
  endDate: string;
}

export interface AuditLog {
  id: number;
  userId: number;
  action: string;
  entityType: string;
  entityId: number;
  details: string;
  ipAddress: string;
  timestamp: string;
}

export interface ConfigProperty {
  id: number;
  key: string;
  value: string;
  description: string;
  type: string;
  isEditable: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface EmailTemplate {
  id: number;
  type: string;
  name: string;
  subject: string;
  body: string;
  isActive: boolean;
  createdAt: string;
}

export interface ScheduledTask {
  id: number;
  taskName: string;
  description: string;
  status: string;
  cronExpression: string;
  lastExecution: string;
  nextExecution: string;
  isActive: boolean;
}

export interface BatchImportJob {
  id: number;
  userId: number;
  status: string;
  filePath: string;
  totalRecords: number;
  processedRecords: number;
  failedRecords: number;
  startedAt: string;
  completedAt: string;
}
