import type { QueueTicketView } from "../api/types";

export function sortTicketsByPriorityThenTime(tickets: QueueTicketView[]): QueueTicketView[] {
  return [...tickets].sort((a, b) => {
    const pa = a.priority ?? 999;
    const pb = b.priority ?? 999;
    if (pa !== pb) return pa - pb;
    const tKey = (t: QueueTicketView) => t.calledAt ?? t.checkinAt ?? t.createdAt ?? "";
    return tKey(a).localeCompare(tKey(b));
  });
}
