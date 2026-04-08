import { useCallback, useEffect, useMemo, useState, type FormEvent } from "react";
import * as api from "../api/queueApi";
import type { QueueUnitPanelResponse } from "../api/types";
import { PatientTriageModal } from "../components/PatientTriageModal";
import { TicketCard } from "../components/TicketCard";
import { useUnitId } from "../hooks/useUnitId";
import { sortTicketsByPriorityThenTime } from "../util/sortQueueTickets";

export function AttendancePage() {
  const [unitId, setUnitId] = useUnitId();
  const [panel, setPanel] = useState<QueueUnitPanelResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [updated, setUpdated] = useState<Date | null>(null);
  const [busy, setBusy] = useState(false);
  const [checkinId, setCheckinId] = useState("");
  const [triageForNew, setTriageForNew] = useState("");
  const [newTicketHint, setNewTicketHint] = useState<string | null>(null);
  const [patientModalOpen, setPatientModalOpen] = useState(false);
  const [successNotice, setSuccessNotice] = useState<string | null>(null);

  const refresh = useCallback(async () => {
    if (!unitId) return;
    try {
      const p = await api.fetchPanel(unitId);
      setPanel(p);
      setError(null);
      setUpdated(new Date());
    } catch (e) {
      setError(e instanceof Error ? e.message : "Erro ao carregar painel");
    }
  }, [unitId]);

  const sortedWaiting = useMemo(
    () => (panel ? sortTicketsByPriorityThenTime(panel.waiting) : []),
    [panel],
  );
  const sortedCheckedIn = useMemo(
    () => (panel ? sortTicketsByPriorityThenTime(panel.checkedIn) : []),
    [panel],
  );
  const sortedCalled = useMemo(
    () => (panel ? sortTicketsByPriorityThenTime(panel.called) : []),
    [panel],
  );

  useEffect(() => {
    refresh();
    const t = setInterval(refresh, 4000);
    return () => clearInterval(t);
  }, [refresh]);

  async function run<T>(fn: () => Promise<T>): Promise<void> {
    setBusy(true);
    setError(null);
    try {
      await fn();
      await refresh();
    } catch (e) {
      setError(e instanceof Error ? e.message : "Operação falhou");
    } finally {
      setBusy(false);
    }
  }

  function onCheckinSubmit(e: FormEvent) {
    e.preventDefault();
    const id = checkinId.trim();
    if (!id) return;
    void run(() => api.checkin(id).then(() => setCheckinId("")));
  }

  function onNewTicket(e: FormEvent) {
    e.preventDefault();
    const tri = triageForNew.trim();
    if (!tri) return;
    const uid = Number(unitId);
    if (Number.isNaN(uid)) {
      setNewTicketHint("Unidade inválida para número.");
      return;
    }
    void run(async () => {
      const r = await api.createTicket(tri, uid, { alreadyAtReception: true });
      setTriageForNew("");
      setNewTicketHint(`Ticket criado: ${r.ticketId}`);
    });
  }

  return (
    <>
      <h1 className="page-title">Painel de atendimento</h1>
      <p className="page-sub">
        Acompanhe a fila: marque não comparecimento ainda em aguardando chegada, registre chegada, chame na recepção e
        conclua o atendimento quando já foi chamado. Atualização automática a cada 4 segundos.
      </p>

      <div className="unit-bar">
        <div className="field">
          <label htmlFor="unit">Unidade</label>
          <input
            id="unit"
            value={unitId}
            onChange={(e) => setUnitId(e.target.value)}
            placeholder="ID da unidade"
            autoComplete="off"
          />
        </div>
        <button type="button" className="btn btn-primary" disabled={busy} onClick={() => setPatientModalOpen(true)}>
          Paciente e triagem
        </button>
        <button type="button" className="btn btn-secondary" disabled={busy} onClick={() => void refresh()}>
          Atualizar agora
        </button>
        {updated && (
          <span className="ticket-meta" style={{ alignSelf: "center" }}>
            <span className="status-dot live" aria-hidden />
            Última atualização: {updated.toLocaleTimeString("pt-BR")}
          </span>
        )}
      </div>

      {error && (
        <div className="banner banner-error" role="alert">
          {error}
        </div>
      )}
      {successNotice && (
        <div className="banner banner-success" role="status">
          {successNotice}
          <button type="button" className="banner-dismiss" onClick={() => setSuccessNotice(null)} aria-label="Fechar aviso">
            ×
          </button>
        </div>
      )}

      <PatientTriageModal
        open={patientModalOpen}
        onClose={() => setPatientModalOpen(false)}
        unitId={unitId}
        onCompleted={(msg) => {
          setSuccessNotice(msg);
          void refresh();
        }}
      />

      <section className="checkin-box">
        <h2>Chegada na unidade</h2>
        <form className="checkin-row" onSubmit={onCheckinSubmit}>
          <input
            type="text"
            placeholder="ID do ticket (UUID)"
            value={checkinId}
            onChange={(e) => setCheckinId(e.target.value)}
            disabled={busy}
            aria-label="ID do ticket para check-in"
          />
          <button type="submit" className="btn btn-primary" disabled={busy || !checkinId.trim()}>
            Registrar check-in
          </button>
        </form>
        <div className="optional-block">
          <h3>Novo ticket na fila</h3>
          <p className="ticket-meta" style={{ marginBottom: "0.65rem" }}>
            O ticket é criado já na coluna <strong>Na recepção</strong> (check-in automático na unidade).
          </p>
          <form className="checkin-row" onSubmit={onNewTicket}>
            <input
              type="text"
              placeholder="ID da triagem (UUID)"
              value={triageForNew}
              onChange={(e) => setTriageForNew(e.target.value)}
              disabled={busy}
              aria-label="ID da triagem"
            />
            <button type="submit" className="btn btn-secondary" disabled={busy || !triageForNew.trim()}>
              Emitir ticket
            </button>
          </form>
          {newTicketHint && <p className="ticket-meta" style={{ marginTop: "0.5rem" }}>{newTicketHint}</p>}
        </div>
      </section>

      <div className="panel-grid">
        <section className="column">
          <div className="column-header">
            Aguardando chegada
            <span className="column-count">({panel?.waiting.length ?? "…"})</span>
          </div>
          <div className="column-body">
            {sortedWaiting.map((t) => (
              <TicketCard key={t.ticketId} ticket={t}>
                <button
                  type="button"
                  className="btn btn-danger btn-sm"
                  disabled={busy}
                  onClick={() => void run(() => api.noShowFromWaiting(t.ticketId))}
                >
                  Não compareceu
                </button>
              </TicketCard>
            ))}
            {panel && sortedWaiting.length === 0 && (
              <p className="ticket-meta" style={{ margin: "0.5rem" }}>
                Nenhum paciente aguardando check-in.
              </p>
            )}
          </div>
        </section>

        <section className="column">
          <div className="column-header">
            Na recepção
            <span className="column-count">({panel?.checkedIn.length ?? "…"})</span>
          </div>
          <div className="column-body">
            {sortedCheckedIn.map((t) => (
              <TicketCard key={t.ticketId} ticket={t}>
                <button
                  type="button"
                  className="btn btn-primary btn-sm"
                  disabled={busy}
                  onClick={() => void run(() => api.callTicket(t.ticketId))}
                >
                  Chamar paciente
                </button>
              </TicketCard>
            ))}
            {panel && sortedCheckedIn.length === 0 && (
              <p className="ticket-meta" style={{ margin: "0.5rem" }}>
                Fila vazia para chamada.
              </p>
            )}
          </div>
        </section>

        <section className="column">
          <div className="column-header">
            Em atendimento / chamados
            <span className="column-count">({panel?.called.length ?? "…"})</span>
          </div>
          <div className="column-body">
            {sortedCalled.map((t) => (
              <TicketCard key={t.ticketId} ticket={t}>
                <button
                  type="button"
                  className="btn btn-primary btn-sm"
                  disabled={busy}
                  onClick={() => void run(() => api.finishTicket(t.ticketId, "DONE"))}
                >
                  Concluir
                </button>
              </TicketCard>
            ))}
            {panel && sortedCalled.length === 0 && (
              <p className="ticket-meta" style={{ margin: "0.5rem" }}>
                Nenhum paciente chamado no momento.
              </p>
            )}
          </div>
        </section>
      </div>
    </>
  );
}
