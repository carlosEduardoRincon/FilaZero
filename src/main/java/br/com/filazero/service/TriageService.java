package br.com.filazero.service;

import br.com.filazero.api.dto.EvaluateTriageRequest;
import br.com.filazero.domain.Triage;
import br.com.filazero.repo.PatientRepository;
import br.com.filazero.repo.TriageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TriageService {
    private final TriageRepository repo;
    private final PatientRepository patients;
    private final ObjectMapper objectMapper;

    public TriageService(
            TriageRepository repo,
            PatientRepository patients,
            ObjectMapper objectMapper) {
        this.repo = repo;
        this.patients = patients;
        this.objectMapper = objectMapper;
    }

    public Triage findLatestByPhone(String phone) {
        var patient =
                patients
                        .getByPhone(phone)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "patient not found"));
        return repo.findLatestByPatientId(patient.getPatientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "triage not found"));
    }

    public Triage evaluate(EvaluateTriageRequest evaluateTriageRequest) {
        boolean isUrgent = Boolean.TRUE.equals(evaluateTriageRequest.isUrgent());

        // Vermelho (imediato), Amarelo (urgente), Verde (pouco urgente) e Azul (não urgente).
        String risk = isUrgent ? "Vermelho (imediato) ⚠\uFE0F" : "Verde (pouco urgente) ✅";
        String unitTypeSuggested = isUrgent ? "UPA" : "UBS";
        String recommendation =
                isUrgent
                        ? "Procure uma UPA imediatamente ou ligue 192"
                        : "Procure uma UBS para avaliação e acompanhamento";

        Triage triage = new Triage();
        triage.setTriageId(UUID.randomUUID().toString());
        triage.setPatientId(evaluateTriageRequest.patientId());
        triage.setRisk(risk);
        triage.setUnitTypeSuggested(unitTypeSuggested);
        triage.setRecommendation(recommendation);
        triage.setCreatedAt(Instant.now());
        triage.setRawAnswersJson(toJson(evaluateTriageRequest));

        repo.put(triage);
        return triage;
    }

    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}

