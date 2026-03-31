package br.com.filazero.service;

import java.util.Locale;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class HeuristicSymptomAlertAnalyzer implements SymptomAlertAnalyzer {
  private static final Set<String> ALERT_TERMS =
      Set.of(
          "dor no peito",
          "falta de ar",
          "desmaio",
          "convuls",
          "sangramento intenso",
          "perda de consciencia",
          "pressao muito baixa",
          "paralisia",
          "face torta");

  @Override
  public boolean hasAlertSignal(String symptomDescription) {
    if (symptomDescription == null || symptomDescription.isBlank()) {
      return false;
    }
    String normalized = symptomDescription.toLowerCase(Locale.ROOT);
    return ALERT_TERMS.stream().anyMatch(normalized::contains);
  }
}

