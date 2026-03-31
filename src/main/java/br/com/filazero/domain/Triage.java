package br.com.filazero.domain;

import java.time.Instant;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class Triage {
    private String triageId;
    private String patientId;
    private String risk;
    private String recommendation;
    private String unitTypeSuggested; // UBS|UPA
    private String rawAnswersJson;
    private Instant createdAt;

    @DynamoDbPartitionKey
    public String getTriageId() {
        return triageId;
    }

    public void setTriageId(String triageId) {
        this.triageId = triageId;
    }

    @DynamoDbAttribute("patientId")
    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    @DynamoDbAttribute("risk")
    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    @DynamoDbAttribute("recommendation")
    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    @DynamoDbAttribute("unitTypeSuggested")
    public String getUnitTypeSuggested() {
        return unitTypeSuggested;
    }

    public void setUnitTypeSuggested(String unitTypeSuggested) {
        this.unitTypeSuggested = unitTypeSuggested;
    }

    @DynamoDbAttribute("rawAnswersJson")
    public String getRawAnswersJson() {
        return rawAnswersJson;
    }

    public void setRawAnswersJson(String rawAnswersJson) {
        this.rawAnswersJson = rawAnswersJson;
    }

    @DynamoDbAttribute("createdAt")
    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}

