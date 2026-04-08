import { apiUrl, readErrorMessage } from "./client";

export interface IdentifyPatientResponse {
  patientId: string;
}

export async function identifyOrCreate(
  phone: string,
  cpf: string,
  birthDate: string,
): Promise<IdentifyPatientResponse> {
  const res = await fetch(apiUrl("/api/patients/identify-or-create"), {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ phone: phone.trim(), cpf: cpf.trim(), birthDate: birthDate.trim() }),
  });
  if (!res.ok) throw new Error(await readErrorMessage(res));
  return res.json() as Promise<IdentifyPatientResponse>;
}
