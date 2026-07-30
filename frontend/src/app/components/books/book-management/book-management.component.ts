import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { Book, Category, CreateBookRequest, UpdateBookRequest } from '../../../models/index';
import { BookService } from '../../../services/book.service';
import { CategoryService } from '../../../services/category.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-book-management',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './book-management.component.html',
  styleUrls: ['./book-management.component.css']
})
export class BookManagementComponent implements OnInit, OnDestroy {
  books: Book[] = [];
  categories: Category[] = [];
  loading = false;
  saving = false;
  deletingId: number | null = null;
  error = '';
  success = '';
  showForm = false;
  editingBook: Book | null = null;
  form: FormGroup = new FormGroup({});

  private readonly destroy$ = new Subject<void>();

  constructor(
    private readonly bookService: BookService,
    private readonly categoryService: CategoryService,
    private readonly formBuilder: FormBuilder
  ) {}

  ngOnInit(): void {
    this.form = this.formBuilder.group({
      title: ['', [Validators.required, Validators.maxLength(255)]],
      author: ['', [Validators.required, Validators.maxLength(255)]],
      categoryId: [null, [Validators.required]],
      totalCopies: [1, [Validators.required, Validators.min(1)]],
      availableCopies: [1, [Validators.required, Validators.min(0)]],
      isbn: ['', [Validators.maxLength(20)]],
      publisher: ['', [Validators.maxLength(255)]],
      publicationYear: [null, [Validators.min(1000), Validators.max(2100)]],
      description: ['', [Validators.maxLength(2000)]],
      language: ['English', [Validators.maxLength(50)]],
      shelfLocation: ['', [Validators.maxLength(100)]],
      coverImageUrl: ['', [Validators.maxLength(500)]]
    }, { validators: [this.copiesValidator()] });

    this.loadCategories();
    this.loadBooks();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadBooks(): void {
    this.loading = true;
    this.error = '';
    this.bookService.getBooks(0, 50)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.books = response.content || [];
          this.loading = false;
        },
        error: () => {
          this.error = 'Failed to load books';
          this.loading = false;
        }
      });
  }

  loadCategories(): void {
    this.categoryService.getCategories()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (categories) => {
          this.categories = categories;
        },
        error: () => {
          this.error = 'Failed to load categories';
        }
      });
  }

  openCreateForm(): void {
    this.editingBook = null;
    this.showForm = true;
    this.success = '';
    this.error = '';
    this.form.reset({
      title: '',
      author: '',
      categoryId: null,
      totalCopies: 1,
      availableCopies: 1,
      isbn: '',
      publisher: '',
      publicationYear: null,
      description: '',
      language: 'English',
      shelfLocation: '',
      coverImageUrl: ''
    });
  }

  openEditForm(book: Book): void {
    this.editingBook = book;
    this.showForm = true;
    this.success = '';
    this.error = '';
    this.form.patchValue({
      title: book.title,
      author: book.author,
      categoryId: book.category?.id || null,
      totalCopies: book.totalCopies,
      availableCopies: book.availableCopies,
      isbn: book.isbn || '',
      publisher: book.publisher || '',
      publicationYear: book.publicationYear || null,
      description: book.description || '',
      language: book.language || 'English',
      shelfLocation: book.shelfLocation || '',
      coverImageUrl: book.coverImageUrl || ''
    });
  }

  closeForm(): void {
    this.showForm = false;
    this.editingBook = null;
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving = true;
    this.error = '';

    if (this.editingBook) {
      const updateRequest: UpdateBookRequest = {
        title: this.form.value.title,
        author: this.form.value.author,
        categoryId: Number(this.form.value.categoryId),
        totalCopies: Number(this.form.value.totalCopies),
        availableCopies: Number(this.form.value.availableCopies),
        isbn: this.form.value.isbn || undefined,
        publisher: this.form.value.publisher || undefined,
        publicationYear: this.form.value.publicationYear || undefined,
        description: this.form.value.description || undefined,
        language: this.form.value.language || undefined,
        shelfLocation: this.form.value.shelfLocation || undefined,
        coverImageUrl: this.form.value.coverImageUrl || undefined
      };

      this.bookService.updateBook(this.editingBook.id, updateRequest)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            this.saving = false;
            this.success = 'Book updated successfully';
            this.closeForm();
            this.loadBooks();
          },
          error: (err) => {
            this.saving = false;
            this.error = err.error?.message || 'Failed to update book';
          }
        });
      return;
    }

    const createRequest: CreateBookRequest = {
      title: this.form.value.title,
      author: this.form.value.author,
      categoryId: Number(this.form.value.categoryId),
      totalCopies: Number(this.form.value.totalCopies),
      availableCopies: Number(this.form.value.availableCopies),
      isbn: this.form.value.isbn || undefined,
      publisher: this.form.value.publisher || undefined,
      publicationYear: this.form.value.publicationYear || undefined,
      description: this.form.value.description || undefined,
      language: this.form.value.language || undefined,
      shelfLocation: this.form.value.shelfLocation || undefined,
      coverImageUrl: this.form.value.coverImageUrl || undefined
    };

    this.bookService.createBook(createRequest)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.saving = false;
          this.success = 'Book created successfully';
          this.closeForm();
          this.loadBooks();
        },
        error: (err) => {
          this.saving = false;
          this.error = err.error?.message || 'Failed to create book';
        }
      });
  }

  removeBook(book: Book): void {
    if (!confirm(`Delete "${book.title}"?`)) {
      return;
    }

    this.deletingId = book.id;
    this.error = '';

    this.bookService.deleteBook(book.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.deletingId = null;
          this.success = 'Book deleted successfully';
          this.loadBooks();
        },
        error: (err) => {
          this.deletingId = null;
          this.error = err.error?.message || 'Failed to delete book';
        }
      });
  }

  get f() {
    return this.form.controls;
  }

  private copiesValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const totalCopies = Number(control.get('totalCopies')?.value);
      const availableCopies = Number(control.get('availableCopies')?.value);

      if (Number.isNaN(totalCopies) || Number.isNaN(availableCopies)) {
        return null;
      }

      return availableCopies > totalCopies ? { availableExceedsTotal: true } : null;
    };
  }
}
