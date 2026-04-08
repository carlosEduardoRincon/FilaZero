package br.com.filazero.domain;

import java.time.Instant;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;

@DynamoDbBean
public class QueueTicket {
    private String ticketId;
    private String triageId;
    private String unitId;
    private String status; // WAITING|CHECKED_IN|CALLED|DONE|NO_SHOW
    private Integer priority; // 1 highest
    private Instant windowStart;
    private Instant windowEnd;
    private Instant createdAt;
    private Instant checkinAt;
    private Instant calledAt;
    private Instant finishedAt;
    private String unitQueueSk;

    @DynamoDbPartitionKey
    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    @DynamoDbAttribute("triageId")
    public String getTriageId() {
        return triageId;
    }

    public void setTriageId(String triageId) {
        this.triageId = triageId;
    }

    @DynamoDbAttribute("unitId")
    @DynamoDbSecondaryPartitionKey(indexNames = "byUnitQueue")
    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    @DynamoDbAttribute("status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @DynamoDbAttribute("priority")
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @DynamoDbAttribute("windowStart")
    public Instant getWindowStart() {
        return windowStart;
    }

    public void setWindowStart(Instant windowStart) {
        this.windowStart = windowStart;
    }

    @DynamoDbAttribute("windowEnd")
    public Instant getWindowEnd() {
        return windowEnd;
    }

    public void setWindowEnd(Instant windowEnd) {
        this.windowEnd = windowEnd;
    }

    @DynamoDbAttribute("createdAt")
    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @DynamoDbAttribute("checkinAt")
    public Instant getCheckinAt() {
        return checkinAt;
    }

    public void setCheckinAt(Instant checkinAt) {
        this.checkinAt = checkinAt;
    }

    @DynamoDbAttribute("calledAt")
    public Instant getCalledAt() {
        return calledAt;
    }

    public void setCalledAt(Instant calledAt) {
        this.calledAt = calledAt;
    }

    @DynamoDbAttribute("finishedAt")
    public Instant getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Instant finishedAt) {
        this.finishedAt = finishedAt;
    }

    @DynamoDbAttribute("unitQueueSk")
    @DynamoDbSecondarySortKey(indexNames = "byUnitQueue")
    public String getUnitQueueSk() {
        return unitQueueSk;
    }

    public void setUnitQueueSk(String unitQueueSk) {
        this.unitQueueSk = unitQueueSk;
    }
}

