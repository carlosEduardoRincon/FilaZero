# FilaZero (MVP) — SUS (UBS/UPA)

Backend inicial em **Java/Spring Boot** preparado para rodar em **AWS Lambda** e provisionado via **AWS SAM**.

## Requisitos

- Java 21
- Maven 3.9+
- AWS SAM CLI

## Como rodar local (API via SAM)

1. Build:

```bash
mvn -q -DskipTests package
```

2. Subir API local:

```bash
sam build
sam local start-api
```

3. Testar:

- `GET http://127.0.0.1:3000/health`

## Deploy (AWS)

```bash
sam build
sam deploy --guided
```

