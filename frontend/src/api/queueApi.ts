import { apiUrl, readErrorMessage } from "./client";
import type {
  CreateQueueTicketResponse,
  QueueMetricsResponse,
  QueueUnitPanelResponse,
  QueueTicketView,
} from "./types";

async function json<T>(res: Response): Promise<T> {
  if (!res.ok) throw new Error(await readErrorMessage(res));
  return res.json() as Promise<T>;
}

export async function fetchPanel(unitId: string): Promise<QueueUnitPanelResponse> {
  const res = await fetch(apiUrl(`/api/queue/units/${encodeURIComponent(unitId)}/panel`));
  return json<QueueUnitPanelResponse>(res);
}

export async function checkin(ticketId: string): Promise<CreateQueueTicketResponse> {
  const res = await fetch(apiUrl(`/api/queue/tickets/${encodeURIComponent(ticketId)}/checkin`), {
    method: "POST",
  });
  return json<CreateQueueTicketResponse>(res);
}

export async function callTicket(ticketId: string): Promise<CreateQueueTicketResponse> {
  const res = await fetch(apiUrl(`/api/queue/tickets/${encodeURIComponent(ticketId)}/call`), {
    method: "POST",
  });
  return json<CreateQueueTicketResponse>(res);
}

export async function finishTicket(
  ticketId: string,
  outcome: "DONE" | "NO_SHOW",
): Promise<CreateQueueTicketResponse> {
  const res = await fetch(apiUrl(`/api/queue/tickets/${encodeURIComponent(ticketId)}/finish`), {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ outcome }),
  });
  return json<CreateQueueTicketResponse>(res);
}

export async function createTicket(triageId: string, unitId: number): Promise<CreateQueueTicketResponse> {
  const res = await fetch(apiUrl("/api/queue/tickets"), {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ triageId, unitId }),
  });
  return json<CreateQueueTicketResponse>(res);
}

export async function fetchMetrics(unitId: string, date?: string): Promise<QueueMetricsResponse> {
  const q = date ? `?date=${encodeURIComponent(date)}` : "";
  const res = await fetch(apiUrl(`/api/queue/units/${encodeURIComponent(unitId)}/metrics${q}`));
  return json<QueueMetricsResponse>(res);
}

export async function fetchTicket(ticketId: string): Promise<QueueTicketView> {
  const res = await fetch(apiUrl(`/api/queue/tickets/${encodeURIComponent(ticketId)}`));
  return json<QueueTicketView>(res);
}
