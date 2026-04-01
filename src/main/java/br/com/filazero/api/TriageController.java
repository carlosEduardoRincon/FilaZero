package br.com.filazero.api;

import br.com.filazero.api.dto.EvaluateTriageRequest;
import br.com.filazero.api.dto.EvaluateTriageResponse;
import br.com.filazero.service.TriageService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/triages")
public class TriageController {
    private final TriageService service;

    public TriageController(TriageService service) {
        this.service = service;
    }

    @PostMapping(value = "/evaluate", produces = MediaType.APPLICATION_JSON_VALUE)
    public EvaluateTriageResponse evaluate(@Valid @RequestBody EvaluateTriageRequest request) {
        var triage = service.evaluate(request);
        return new EvaluateTriageResponse(
                triage.getTriageId(), triage.getRisk(), triage.getRecommendation(), triage.getUnitTypeSuggested());
    }
}

