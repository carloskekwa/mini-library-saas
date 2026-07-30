import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ScheduledTask } from '../models/index';

@Injectable({
  providedIn: 'root'
})
export class ScheduledTaskService {
  private apiUrl = `${environment.apiUrl}/scheduled-tasks`;

  constructor(private http: HttpClient) {}

  getActiveTasks(): Observable<ScheduledTask[]> {
    return this.http.get<ScheduledTask[]>(this.apiUrl);
  }

  createTask(payload: { taskName: string; description: string; cronExpression: string }): Observable<ScheduledTask> {
    const params = new HttpParams()
      .set('taskName', payload.taskName)
      .set('description', payload.description)
      .set('cronExpression', payload.cronExpression);
    return this.http.post<ScheduledTask>(this.apiUrl, {}, { params });
  }

  disableTask(taskId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${taskId}/disable`, {});
  }
}
