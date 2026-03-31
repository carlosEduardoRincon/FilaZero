package br.com.filazero.service;

import br.com.filazero.domain.Patient;
import br.com.filazero.repo.PatientRepository;
import br.com.filazero.util.Hashing;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class PatientService {
    private final PatientRepository repo;

    public PatientService(PatientRepository repo) {
        this.repo = repo;
    }

    public Patient identifyOrCreate(String phone, String cpf, String birthDate) {
        String cpfHash = Hashing.sha256Hex(cpf.trim());

        var existing = repo.getByCpfHash(cpfHash);
        if (existing.isPresent()) {
            return existing.get();
        }

        Patient p = new Patient();
        p.setPatientId(UUID.randomUUID().toString());
        p.setPhone(phone.trim());
        p.setCpfHash(cpfHash);
        p.setBirthDate(birthDate.trim());
        p.setCreatedAt(Instant.now());
        repo.put(p);
        return p;
    }
}

