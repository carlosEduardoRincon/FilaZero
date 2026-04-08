import { apiUrl, readErrorMessage } from "./client";

export interface EvaluateTriageResponse {
  triageId: string;
  risk: string;
  recommendation: string;
  unitTypeSuggested: string;
}

export async function evaluateTriage(params: {
  patientId: string;
  isUrgent: boolean;
  symptomDescription: string;
}): Promise<EvaluateTriageResponse> {
  const res = await fetch(apiUrl("/api/triages/evaluate"), {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      patientId: params.patientId,
      isUrgent: params.isUrgent,
      symptomDescription: params.symptomDescription.trim(),
    }),
  });
  if (!res.ok) throw new Error(await readErrorMessage(res));
  return res.json() as Promise<EvaluateTriageResponse>;
}
