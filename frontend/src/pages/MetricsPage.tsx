import { useCallback, useEffect, useState } from "react";
import * as api from "../api/queueApi";
import type { QueueMetricsResponse } from "../api/types";
import { useUnitId } from "../hooks/useUnitId";
import { formatDurationSeconds, formatPercent } from "../util/format";

function todayInputValue(): string {
  const d = new Date();
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${y}-${m}-${day}`;
}

export function MetricsPage() {
  const [unitId, setUnitId] = useUnitId();
  const [date, setDate] = useState(todayInputValue);
  const [data, setData] = useState<QueueMetricsResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const load = useCallback(async () => {
    if (!unitId) return;
    setLoading(true);
    setError(null);
    try {
      const m = await api.fetchMetrics(unitId, date);
      setData(m);
    } catch (e) {
      setData(null);
      setError(e instanceof Error ? e.message : "Erro ao carregar indicadores");
    } finally {
      setLoading(false);
    }
  }, [unitId, date]);

  useEffect(() => {
    void load();
  }, [load]);

  return (
    <>
      <h1 className="page-title">Indicadores da fila</h1>
      <p className="page-sub">
        Métricas do dia (fuso America/Sao_Paulo no servidor) para apoio à gestão do atendimento.
      </p>

      <div className="unit-bar">
        <div className="field">
          <label htmlFor="unit-m">Unidade</label>
          <input
            id="unit-m"
            value={unitId}
            onChange={(e) => setUnitId(e.target.value)}
            placeholder="ID da unidade"
          />
        </div>
        <div className="field">
          <label htmlFor="date-m">Data</label>
          <input id="date-m" type="date" value={date} onChange={(e) => setDate(e.target.value)} />
        </div>
        <button type="button" className="btn btn-primary" disabled={loading} onClick={() => void load()}>
          {loading ? "Carregando…" : "Recarregar"}
        </button>
      </div>

      {error && (
        <div className="banner banner-error" role="alert">
          {error}
        </div>
      )}

      {data && !error && (
        <div className="metrics-grid">
          <div className="metric-card">
            <h3>Atendimentos concluídos</h3>
            <div className="metric-value">{data.doneCount}</div>
            <div className="metric-hint">Status DONE no dia</div>
          </div>
          <div className="metric-card">
            <h3>Não comparecimento</h3>
            <div className="metric-value">{data.noShowCount}</div>
            <div className="metric-hint">Status NO_SHOW no dia</div>
          </div>
          <div className="metric-card">
            <h3>Taxa de falta</h3>
            <div className="metric-value">{formatPercent(data.noShowRate)}</div>
            <div className="metric-hint">NO_SHOW ÷ (DONE + NO_SHOW)</div>
          </div>
          <div className="metric-card">
            <h3>Tempo médio até a chamada</h3>
            <div className="metric-value">{formatDurationSeconds(data.averageWaitUntilCalledSeconds)}</div>
            <div className="metric-hint">Do check-in até ser chamado (quando houver dados)</div>
          </div>
          <div className="metric-card">
            <h3>Tempo médio de atendimento</h3>
            <div className="metric-value">{formatDurationSeconds(data.averageServiceSeconds)}</div>
            <div className="metric-hint">Chamada até conclusão (DONE)</div>
          </div>
        </div>
      )}

      {!data && !error && loading && <div className="banner banner-muted">Carregando indicadores…</div>}
    </>
  );
}
