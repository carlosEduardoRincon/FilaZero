package br.com.filazero.repo;

import br.com.filazero.config.DynamoTableNames;
import br.com.filazero.domain.Triage;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

@Repository
public class TriageRepository {
    private final DynamoDbTable<Triage> table;
    private final DynamoDbIndex<Triage> byPatientIdIndex;

    public TriageRepository(DynamoDbEnhancedClient enhancedClient, DynamoTableNames names) {
        this.table = enhancedClient.table(names.triagesTable(), TableSchema.fromBean(Triage.class));
        this.byPatientIdIndex = table.index("byPatientId");
    }

    public void put(Triage triage) {
        table.putItem(triage);
    }

    public Optional<Triage> getById(String triageId) {
        var key = software.amazon.awssdk.enhanced.dynamodb.Key.builder().partitionValue(triageId).build();
        return Optional.ofNullable(table.getItem(r -> r.key(key)));
    }

    public Optional<Triage> findLatestByPatientId(String patientId) {
        var key = Key.builder().partitionValue(patientId).build();
        var pages =
                byPatientIdIndex.query(
                        r ->
                                r.queryConditional(QueryConditional.keyEqualTo(key))
                                        .scanIndexForward(false)
                                        .limit(1));
        return pages.stream().flatMap(p -> p.items().stream()).findFirst();
    }
}

