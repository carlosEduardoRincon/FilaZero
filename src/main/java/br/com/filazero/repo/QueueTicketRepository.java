package br.com.filazero.repo;

import br.com.filazero.config.DynamoTableNames;
import br.com.filazero.domain.QueueTicket;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

@Repository
public class QueueTicketRepository {
    static final String BY_UNIT_QUEUE_INDEX = "byUnitQueue";

    private final DynamoDbTable<QueueTicket> table;

    public QueueTicketRepository(DynamoDbEnhancedClient enhancedClient, DynamoTableNames names) {
        this.table =
                enhancedClient.table(names.queueTicketsTable(), TableSchema.fromBean(QueueTicket.class));
    }

    public void put(QueueTicket ticket) {
        table.putItem(ticket);
    }

    public Optional<QueueTicket> getById(String ticketId) {
        var key = Key.builder().partitionValue(ticketId).build();
        return Optional.ofNullable(table.getItem(r -> r.key(key)));
    }

    public List<QueueTicket> listByUnitIdAndSortKeyPrefix(String unitId, String sortKeyPrefix, int maxItems) {
        DynamoDbIndex<QueueTicket> index = table.index(BY_UNIT_QUEUE_INDEX);
        Key key = Key.builder().partitionValue(unitId).sortValue(sortKeyPrefix).build();
        QueryConditional conditional = QueryConditional.sortBeginsWith(key);

        List<QueueTicket> out = new ArrayList<>();
        for (Page<QueueTicket> page :
                index.query(q -> q.queryConditional(conditional).limit(Math.min(100, maxItems)))) {
            for (QueueTicket t : page.items()) {
                out.add(t);
                if (out.size() >= maxItems) {
                    return out;
                }
            }
        }
        return out;
    }
}

