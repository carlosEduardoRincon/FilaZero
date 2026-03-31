package br.com.filazero.repo;

import br.com.filazero.config.DynamoTableNames;
import br.com.filazero.domain.Triage;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class TriageRepository {
    private final DynamoDbTable<Triage> table;

    public TriageRepository(DynamoDbEnhancedClient enhancedClient, DynamoTableNames names) {
        this.table = enhancedClient.table(names.triagesTable(), TableSchema.fromBean(Triage.class));
    }

    public void put(Triage triage) {
        table.putItem(triage);
    }

    public Optional<Triage> getById(String triageId) {
        var key = software.amazon.awssdk.enhanced.dynamodb.Key.builder().partitionValue(triageId).build();
        return Optional.ofNullable(table.getItem(r -> r.key(key)));
    }
}

