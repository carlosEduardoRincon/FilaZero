function apiRoot(): string {
  const raw = import.meta.env.VITE_API_URL?.trim() ?? "";
  return raw.replace(/\/$/, "");
}

export function apiUrl(path: string): string {
  const p = path.startsWith("/") ? path : `/${path}`;
  const root = apiRoot();
  return root ? `${root}${p}` : p;
}

export async function readErrorMessage(res: Response): Promise<string> {
  const text = await res.text();
  if (!text) return res.statusText || `Erro HTTP ${res.status}`;
  try {
    const j = JSON.parse(text) as { message?: string; error?: string };
    if (typeof j.message === "string") return j.message;
    if (typeof j.error === "string") return j.error;
  } catch {
    /* not JSON */
  }
  return text.slice(0, 200);
}
