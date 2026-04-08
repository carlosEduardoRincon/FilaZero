export function formatDurationSeconds(seconds: number | null | undefined): string {
  if (seconds == null || Number.isNaN(seconds)) return "—";
  if (seconds < 60) return `${Math.round(seconds)} s`;
  const m = Math.floor(seconds / 60);
  const s = Math.round(seconds % 60);
  return `${m} min ${s}s`;
}

export function formatPercent(rate: number | null | undefined): string {
  if (rate == null || Number.isNaN(rate)) return "—";
  return `${(rate * 100).toFixed(1)}%`;
}

export function priorityClass(priority: number): string {
  if (priority <= 1) return "prio-1";
  if (priority === 2) return "prio-2";
  return "prio-3";
}

export function priorityLabel(priority: number): string {
  if (priority <= 1) return "Prioridade 1";
  if (priority === 2) return "Prioridade 2";
  return "Prioridade 3";
}
