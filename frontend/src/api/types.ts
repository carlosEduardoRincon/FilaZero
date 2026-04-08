export interface QueueTicketView {
  ticketId: string;
  priority: number;
  status: string;
  windowStart: string;
  windowEnd: string;
  createdAt: string | null;
  checkinAt: string | null;
  calledAt: string | null;
  finishedAt: string | null;
}

export interface QueueUnitPanelResponse {
  waiting: QueueTicketView[];
  checkedIn: QueueTicketView[];
  called: QueueTicketView[];
}

export interface QueueMetricsResponse {
  doneCount: number;
  noShowCount: number;
  averageWaitUntilCalledSeconds: number | null;
  averageServiceSeconds: number | null;
  noShowRate: number | null;
}

export interface CreateQueueTicketResponse {
  ticketId: string;
  priority: number;
  status: string;
  windowStart: string;
  windowEnd: string;
}
