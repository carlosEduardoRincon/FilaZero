import { useEffect, useId, useState, type FormEvent } from "react";
import * as patientApi from "../api/patientApi";
import * as queueApi from "../api/queueApi";
import * as triageApi from "../api/triageApi";

interface Props {
  open: boolean;
  onClose: () => void;
  unitId: string;
  onCompleted: (message: string) => void;
}

export function PatientTriageModal({ open, onClose, unitId, onCompleted }: Props) {
  const titleId = useId();
  const [phone, setPhone] = useState("");
  const [cpf, setCpf] = useState("");
  const [birthDate, setBirthDate] = useState("");
  const [isUrgent, setIsUrgent] = useState(false);
  const [symptomDescription, setSymptomDescription] = useState("");
  const [localError, setLocalError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (!open) return;
    const onKey = (e: KeyboardEvent) => {
      if (e.key === "Escape") onClose();
    };
    document.addEventListener("keydown", onKey);
    const prev = document.body.style.overflow;
    document.body.style.overflow = "hidden";
    return () => {
      document.removeEventListener("keydown", onKey);
      document.body.style.overflow = prev;
    };
  }, [open, onClose]);

  useEffect(() => {
    if (open) {
      setLocalError(null);
    }
  }, [open]);

  if (!open) return null;

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setLocalError(null);
    const uid = Number(unitId);
    if (Number.isNaN(uid)) {
      setLocalError("Defina uma unidade válida (número) no painel antes de cadastrar.");
      return;
    }
    if (!phone.trim() || !cpf.trim() || !birthDate.trim()) {
      setLocalError("Preencha telefone, CPF e data de nascimento.");
      return;
    }
    if (!symptomDescription.trim()) {
      setLocalError("Descreva os sintomas ou motivo do atendimento.");
      return;
    }

    setSubmitting(true);
    try {
      const { patientId } = await patientApi.identifyOrCreate(phone, cpf, birthDate);
      const triage = await triageApi.evaluateTriage({
        patientId,
        isUrgent,
        symptomDescription,
      });
      const ticket = await queueApi.createTicket(triage.triageId, uid, { alreadyAtReception: true });
      onCompleted(
        `Paciente cadastrado. Triagem: ${triage.triageId}. Ticket: ${ticket.ticketId}. Classificação: ${triage.risk}`,
      );
      setPhone("");
      setCpf("");
      setBirthDate("");
      setIsUrgent(false);
      setSymptomDescription("");
      onClose();
    } catch (err) {
      setLocalError(err instanceof Error ? err.message : "Falha ao salvar.");
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="modal-backdrop" role="presentation" onMouseDown={(e) => e.target === e.currentTarget && onClose()}>
      <div
        className="modal-sheet"
        role="dialog"
        aria-modal="true"
        aria-labelledby={titleId}
        onMouseDown={(e) => e.stopPropagation()}
      >
        <div className="modal-head">
          <h2 id={titleId} className="modal-title">
            Paciente e triagem
          </h2>
          <button type="button" className="modal-close" onClick={onClose} aria-label="Fechar">
            ×
          </button>
        </div>
        <p className="modal-lead">
          Cadastro manual na recepção: identifica o paciente, registra a triagem e emite o ticket na fila desta unidade.
        </p>

        <form onSubmit={onSubmit} className="modal-form">
          <fieldset className="modal-fieldset" disabled={submitting}>
            <legend>Dados do paciente</legend>
            <div className="modal-grid">
              <div className="field">
                <label htmlFor="pt-phone">Telefone</label>
                <input
                  id="pt-phone"
                  type="tel"
                  autoComplete="tel"
                  value={phone}
                  onChange={(e) => setPhone(e.target.value)}
                  placeholder="11999999999"
                  required
                />
              </div>
              <div className="field">
                <label htmlFor="pt-cpf">CPF</label>
                <input
                  id="pt-cpf"
                  inputMode="numeric"
                  autoComplete="off"
                  value={cpf}
                  onChange={(e) => setCpf(e.target.value)}
                  placeholder="Somente números ou com pontuação"
                  required
                />
              </div>
              <div className="field modal-span-2">
                <label htmlFor="pt-birth">Data de nascimento</label>
                <input
                  id="pt-birth"
                  type="date"
                  value={birthDate}
                  onChange={(e) => setBirthDate(e.target.value)}
                  required
                />
              </div>
            </div>
          </fieldset>

          <fieldset className="modal-fieldset" disabled={submitting}>
            <legend>Triagem</legend>
            <label className="modal-check">
              <input
                type="checkbox"
                checked={isUrgent}
                onChange={(e) => setIsUrgent(e.target.checked)}
              />
              Atendimento urgente (classificação de maior prioridade)
            </label>
            <div className="field" style={{ marginTop: "0.75rem" }}>
              <label htmlFor="pt-symptoms">Sintomas / motivo do atendimento</label>
              <textarea
                id="pt-symptoms"
                className="modal-textarea"
                rows={4}
                value={symptomDescription}
                onChange={(e) => setSymptomDescription(e.target.value)}
                placeholder="Descreva o que o paciente relata…"
                required
              />
            </div>
          </fieldset>

          {localError && (
            <div className="banner banner-error" role="alert" style={{ marginTop: "0.5rem" }}>
              {localError}
            </div>
          )}

          <div className="modal-actions">
            <button type="button" className="btn btn-secondary" disabled={submitting} onClick={onClose}>
              Cancelar
            </button>
            <button type="submit" className="btn btn-primary" disabled={submitting}>
              {submitting ? "Salvando…" : "Salvar e emitir ticket"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
