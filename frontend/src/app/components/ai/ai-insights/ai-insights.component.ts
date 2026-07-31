import { Component, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AiService, AIInsightResponse } from '../../../services/ai.service';
import { AuthService } from '../../../services/auth.service';

interface InsightCard {
  icon: string;
  label: string;
  question: string;
}

const PRESET_INSIGHTS: InsightCard[] = [
  {
    icon: 'bi-graph-up-arrow',
    label: 'Most Requested But Unavailable',
    question: 'Which books are most requested but currently unavailable? What should we prioritise restocking?',
  },
  {
    icon: 'bi-cart-plus',
    label: 'Books to Purchase',
    question: 'Based on borrowing trends and reservation queues, which books should the library purchase more copies of?',
  },
  {
    icon: 'bi-archive',
    label: 'Unpopular Books',
    question: 'Which books have never been borrowed or rarely borrowed? Should we consider removing or replacing them?',
  },
];

@Component({
  selector: 'app-ai-insights',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ai-insights.component.html',
  styleUrls: ['./ai-insights.component.css']
})
export class AiInsightsComponent implements OnDestroy {
  presetInsights = PRESET_INSIGHTS;
  customQuestion = '';
  result: AIInsightResponse | null = null;
  activeQuestion = '';
  isLoading = false;
  error = '';
  private readonly destroy$ = new Subject<void>();

  constructor(
    private aiService: AiService,
    private authService: AuthService
  ) {}

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  runPreset(card: InsightCard): void {
    this.askQuestion(card.question);
  }

  runCustom(): void {
    const q = this.customQuestion.trim();
    if (!q) return;
    this.askQuestion(q);
  }

  reindex(): void {
    this.aiService.reindex().pipe(takeUntil(this.destroy$)).subscribe({
      next: () => alert('Re-indexing complete. All books have been re-embedded into the vector store.'),
      error: () => alert('Re-indexing failed. Check the server logs.')
    });
  }

  get isAdmin(): boolean {
    return this.authService.hasRole('ADMIN');
  }

  private askQuestion(question: string): void {
    if (this.isLoading) return;
    this.activeQuestion = question;
    this.result = null;
    this.error = '';
    this.isLoading = true;

    this.aiService.getInsight(question)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.result = response;
          this.isLoading = false;
        },
        error: () => {
          this.error = 'Failed to get insight. Please ensure Ollama is running.';
          this.isLoading = false;
        }
      });
  }
}
