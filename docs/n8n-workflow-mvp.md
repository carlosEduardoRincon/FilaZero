# Fluxo n8n do MVP (WhatsApp -> APIs FilaZero)

Arquivo importavel: `docs/n8n-workflow-mvp.json`

## O que esse fluxo faz

1. Recebe evento no webhook (`filazero-whatsapp-webhook`).
2. Normaliza campos da entrada (`phone`, `text`, `sessionState`, `cpf`, `birthDate`, `alarm`, `unitId`).
3. Roteia por `sessionState`:
   - `START`: pede dados iniciais do paciente.
   - `PATIENT_IDENTIFIED`: chama identificacao do paciente e depois triagem.
   - `TRIAGED`: se resposta contem "sim", cria ticket.
   - `TICKET_CREATED`: se resposta contem "cheguei", faz check-in.
4. Responde ao webhook com:
   - `message`: mensagem para enviar no WhatsApp.
   - `sessionState`: proximo estado.
   - IDs (`patientId`, `triageId`, `ticketId`) para persistir na sua sessao.

## Variavel de ambiente obrigatoria no n8n

- `FILAZERO_API_BASE_URL` (exemplo: `https://xxxxx.execute-api.us-east-1.amazonaws.com/prod`)

## Como usar no seu n8n

1. Importar JSON:
   - Workflows -> Import from file -> `docs/n8n-workflow-mvp.json`.
2. Ajustar o provedor WhatsApp:
   - Seu fluxo de entrada (360dialog/Twilio/Zenvia) deve chamar este webhook.
3. Persistir sessao por telefone:
   - Guarde `sessionState`, `patientId`, `triageId`, `ticketId` (Redis, Postgres, Data Store do n8n, etc.).
   - Na proxima mensagem do mesmo telefone, injete esses campos no webhook payload.
4. Ativar workflow.
5. O risco na triagem segue apenas o indicador de urgencia enviado pelo fluxo (ex.: campo `alarm` / `isUrgent` na chamada a `/api/triages/evaluate`). O texto dos sintomas pode ser enviado para registro, sem inferencia automatica na API.

## Payload de entrada esperado (exemplo)

```json
{
  "phone": "5511999999999",
  "text": "sim",
  "sessionState": "TRIAGED",
  "cpf": "12345678900",
  "birthDate": "1990-01-01",
  "alarm": false,
  "symptomDescription": "dor no peito ha 20 minutos e falta de ar",
  "unitId": "upa-centro",
  "patientId": "uuid-opcional",
  "triageId": "uuid-opcional",
  "ticketId": "uuid-opcional"
}
```

## Resposta do workflow (exemplo)

```json
{
  "message": "Ticket criado: ...",
  "sessionState": "TICKET_CREATED",
  "patientId": "...",
  "triageId": "...",
  "ticketId": "..."
}
```

## Observacoes importantes

- Este fluxo foi feito para entendimento e MVP rapido.
- Em producao, adicione:
  - validacao de payload
  - retry/backoff nos HTTP nodes
  - tratamento de erro por tipo (4xx/5xx)
  - logs estruturados por `phone` e `ticketId`
  - idempotencia para mensagens duplicadas do WhatsApp

