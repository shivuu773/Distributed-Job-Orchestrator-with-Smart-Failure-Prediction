export type JobState = 'PENDING' | 'RUNNING' | 'COMPLETED' | 'FAILED';

export interface Job {
  id: string;
  jobType: string;
  state: JobState;
  payload: string;
  priority: number;
  currentRetries: number;
  maxRetries: number;
  predictedRiskScore: number;
  predictedDurationMs: number;
  assignedWorkerId: string | null;
  scheduledAt: string;
  createdAt: string;
  updatedAt: string;
}
