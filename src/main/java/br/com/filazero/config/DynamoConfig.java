package br.com.filazero.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class DynamoConfig {
    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.create();
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient client) {
        return DynamoDbEnhancedClient.builder().dynamoDbClient(client).build();
    }

    @Bean
    public DynamoTableNames dynamoTableNames(
            @Value("${app.dynamo.patientsTable:${PATIENTS_TABLE:filazero-patients}}") String patientsTable,
            @Value("${app.dynamo.triagesTable:${TRIAGES_TABLE:filazero-triages}}") String triagesTable,
            @Value("${app.dynamo.queueTicketsTable:${QUEUE_TICKETS_TABLE:filazero-queue-tickets}}")
            String queueTicketsTable) {
        return new DynamoTableNames(patientsTable, triagesTable, queueTicketsTable);
    }
}

