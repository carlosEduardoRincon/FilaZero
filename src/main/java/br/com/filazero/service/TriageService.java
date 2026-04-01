package br.com.filazero.service;

import br.com.filazero.api.dto.EvaluateTriageRequest;
import br.com.filazero.domain.Triage;
import br.com.filazero.repo.TriageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class TriageService {
    private final TriageRepository repo;
    private final ObjectMapper objectMapper;
    private final SymptomAlertAnalyzer symptomAlertAnalyzer;

    public TriageService(
            TriageRepository repo, ObjectMapper objectMapper, SymptomAlertAnalyzer symptomAlertAnalyzer) {
        this.repo = repo;
        this.objectMapper = objectMapper;
        this.symptomAlertAnalyzer = symptomAlertAnalyzer;
    }

    public Triage evaluate(EvaluateTriageRequest evaluateTriageRequest) {
        boolean explicitAlarm = Boolean.TRUE.equals(evaluateTriageRequest.isUrgent());
        boolean symptomTextAlarm = symptomAlertAnalyzer.hasAlertSignal(evaluateTriageRequest.symptomDescription());
        boolean isUrgent = explicitAlarm || symptomTextAlarm;

        String risk = isUrgent ? "RED" : "GREEN";
        String unitTypeSuggested = isUrgent ? "UPA" : "UBS";
        String recommendation =
                isUrgent
                        ? "Procure uma UPA imediatamente ou ligue 192 se necessário."
                        : "Procure uma UBS para avaliação e acompanhamento.";

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

