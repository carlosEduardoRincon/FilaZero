package br.com.filazero.service;

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

    public Triage evaluate(String patientId, Map<String, Object> answers) {
        boolean explicitAlarm =
                Boolean.TRUE.equals(answers.get("alarm"))
                        || Boolean.TRUE.equals(answers.get("sinais_alarme"))
                        || Boolean.TRUE.equals(answers.get("red_flags"));
        String symptomDescription = extractSymptomDescription(answers);
        boolean symptomTextAlarm = symptomAlertAnalyzer.hasAlertSignal(symptomDescription);
        boolean alarm = explicitAlarm || symptomTextAlarm;

        String risk = alarm ? "RED" : "GREEN";
        String unitTypeSuggested = alarm ? "UPA" : "UBS";
        String recommendation =
                alarm
                        ? "Procure uma UPA imediatamente ou ligue 192 se necessário."
                        : "Procure uma UBS para avaliação e acompanhamento.";

        Triage triage = new Triage();
        triage.setTriageId(UUID.randomUUID().toString());
        triage.setPatientId(patientId);
        triage.setRisk(risk);
        triage.setUnitTypeSuggested(unitTypeSuggested);
        triage.setRecommendation(recommendation);
        triage.setCreatedAt(Instant.now());
        triage.setRawAnswersJson(toJson(answers));

        repo.put(triage);
        return triage;
    }

    private String extractSymptomDescription(Map<String, Object> answers) {
        Object value = answers.get("symptomDescription");
        if (value == null) {
            value = answers.get("descricao_sintomas");
        }
        if (value == null) {
            value = answers.get("queixa");
        }
        return value == null ? "" : String.valueOf(value);
    }

    private String toJson(Map<String, Object> answers) {
        try {
            return objectMapper.writeValueAsString(answers);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}

