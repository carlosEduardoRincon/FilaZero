package br.com.filazero.repo;

import br.com.filazero.config.DynamoTableNames;
import br.com.filazero.domain.Patient;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;

@Repository
public class PatientRepository {
    private final DynamoDbTable<Patient> table;
    private final DynamoDbIndex<Patient> byCpfHashIndex;

    public PatientRepository(DynamoDbEnhancedClient enhancedClient, DynamoTableNames names) {
        this.table = enhancedClient.table(names.patientsTable(), TableSchema.fromBean(Patient.class));
        this.byCpfHashIndex = table.index("byCpfHash");
    }

    public void put(Patient patient) {
        table.putItem(patient);
    }

    public Optional<Patient> getById(String patientId) {
        var key = software.amazon.awssdk.enhanced.dynamodb.Key.builder().partitionValue(patientId).build();
        return Optional.ofNullable(table.getItem((GetItemEnhancedRequest.Builder b) -> b.key(key)));
    }

    public Optional<Patient> getByCpfHash(String cpfHash) {
        var key = software.amazon.awssdk.enhanced.dynamodb.Key.builder().partitionValue(cpfHash).build();
        var result =
                byCpfHashIndex.query(
                        r ->
                                r.queryConditional(
                                                software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional.keyEqualTo(key))
                                        .limit(1));
        return result.stream().flatMap(p -> p.items().stream()).findFirst();
    }
}

