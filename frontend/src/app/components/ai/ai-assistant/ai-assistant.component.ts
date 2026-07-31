import { Component, OnDestroy, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AiService, AIChatResponse, BookRecommendation } from '../../../services/ai.service';
import { AuthService } from '../../../services/auth.service';

interface ChatMessage {
  role: 'user' | 'assistant';
  text: string;
  books?: BookRecommendation[];
  loading?: boolean;
  error?: boolean;
}

const SUGGESTED_QUESTIONS = [
  'Recommend a book for a beginner programmer',
  'Suggest books similar to Harry Potter',
  'Find books about machine learning',
  'What fantasy books do you have available?',
  'Suggest a book about history',
];

@Component({
  selector: 'app-ai-assistant',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ai-assistant.component.html',
  styleUrls: ['./ai-assistant.component.css']
})
export class AiAssistantComponent implements OnInit, OnDestroy {
  @ViewChild('chatEnd') chatEndRef!: ElementRef;

  messages: ChatMessage[] = [];
  inputText = '';
  isLoading = false;
  suggestedQuestions = SUGGESTED_QUESTIONS;
  private readonly destroy$ = new Subject<void>();
  private currentUserId: number | undefined;

  constructor(
    private aiService: AiService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.pipe(takeUntil(this.destroy$)).subscribe(user => {
      this.currentUserId = user?.id;
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  sendMessage(text?: string): void {
    const message = (text ?? this.inputText).trim();
    if (!message || this.isLoading) return;

    this.inputText = '';
    this.messages.push({ role: 'user', text: message });

    const loadingMsg: ChatMessage = { role: 'assistant', text: '', loading: true };
    this.messages.push(loadingMsg);
    this.isLoading = true;
    this.scrollToBottom();

    this.aiService.recommend(message, this.currentUserId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response: AIChatResponse) => {
          const idx = this.messages.lastIndexOf(loadingMsg);
          this.messages[idx] = {
            role: 'assistant',
            text: response.answer,
            books: response.books
          };
          this.isLoading = false;
          this.scrollToBottom();
        },
        error: () => {
          const idx = this.messages.lastIndexOf(loadingMsg);
          this.messages[idx] = {
            role: 'assistant',
            text: 'Something went wrong. Please ensure the AI service is running.',
            error: true
          };
          this.isLoading = false;
        }
      });
  }

  onKeydown(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.sendMessage();
    }
  }

  clearChat(): void {
    this.messages = [];
  }

  get showSuggestions(): boolean {
    return this.messages.length === 0;
  }

  private scrollToBottom(): void {
    setTimeout(() => {
      this.chatEndRef?.nativeElement?.scrollIntoView({ behavior: 'smooth' });
    }, 50);
  }
}
