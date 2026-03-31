package br.com.filazero.repo;

import br.com.filazero.config.DynamoTableNames;
import br.com.filazero.domain.QueueTicket;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class QueueTicketRepository {
    private final DynamoDbTable<QueueTicket> table;

    public QueueTicketRepository(DynamoDbEnhancedClient enhancedClient, DynamoTableNames names) {
        this.table =
                enhancedClient.table(names.queueTicketsTable(), TableSchema.fromBean(QueueTicket.class));
    }

    public void put(QueueTicket ticket) {
        table.putItem(ticket);
    }

    public Optional<QueueTicket> getById(String ticketId) {
        var key = software.amazon.awssdk.enhanced.dynamodb.Key.builder().partitionValue(ticketId).build();
        return Optional.ofNullable(table.getItem(r -> r.key(key)));
    }
}

