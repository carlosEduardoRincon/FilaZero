package br.com.filazero.api;

import br.com.filazero.api.dto.IdentifyPatientRequest;
import br.com.filazero.api.dto.IdentifyPatientResponse;
import br.com.filazero.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/patients")
public class PatientController {
    private final PatientService service;

    public PatientController(PatientService service) {
        this.service = service;
    }

    @PostMapping(value = "/identify-or-create", produces = MediaType.APPLICATION_JSON_VALUE)
    public IdentifyPatientResponse identifyOrCreate(@Valid @RequestBody IdentifyPatientRequest request) {
        var p = service.identifyOrCreate(request.phone(), request.cpf(), request.birthDate());
        return new IdentifyPatientResponse(p.getPatientId());
    }
}

