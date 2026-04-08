import { useCallback, useEffect, useState } from "react";

const KEY = "filazero.unitId";

function load(): string {
  try {
    const v = localStorage.getItem(KEY);
    return v?.trim() || "1";
  } catch {
    return "1";
  }
}

export function useUnitId(): [string, (u: string) => void] {
  const [unitId, setState] = useState(load);

  useEffect(() => {
    const onStorage = (e: StorageEvent) => {
      if (e.key === KEY || e.key === null) setState(load());
    };
    window.addEventListener("storage", onStorage);
    return () => window.removeEventListener("storage", onStorage);
  }, []);

  const setUnitId = useCallback((u: string) => {
    const next = u.trim() || "1";
    setState(next);
    try {
      localStorage.setItem(KEY, next);
    } catch {
      /* ignore */
    }
  }, []);

  return [unitId, setUnitId];
}
