
package com.romif.securityalarm.domain.sms;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "balance",
    "batch_id",
    "cost",
    "num_messages",
    "message",
    "receipt_url",
    "custom",
    "messages"
})
public class MessageResponse {

    @JsonProperty("balance")
    private Integer balance;
    @JsonProperty("batch_id")
    private String batchId;
    @JsonProperty("cost")
    private BigDecimal cost;
    @JsonProperty("num_messages")
    private Integer numMessages;
    @JsonProperty("message")
    private MessageResponse_ message;
    @JsonProperty("receipt_url")
    private String receiptUrl;
    @JsonProperty("custom")
    private String custom;
    @JsonProperty("messages")
    private List<MessageResponse__> messages = null;
    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<>();

    @JsonProperty("balance")
    public Integer getBalance() {
        return balance;
    }

    @JsonProperty("balance")
    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    @JsonProperty("batch_id")
    public String getBatchId() {
        return batchId;
    }

    @JsonProperty("batch_id")
    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    @JsonProperty("cost")
    public BigDecimal getCost() {
        return cost;
    }

    @JsonProperty("cost")
    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    @JsonProperty("num_messages")
    public Integer getNumMessages() {
        return numMessages;
    }

    @JsonProperty("num_messages")
    public void setNumMessages(Integer numMessages) {
        this.numMessages = numMessages;
    }

    @JsonProperty("message")
    public MessageResponse_ getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(MessageResponse_ message) {
        this.message = message;
    }

    @JsonProperty("receipt_url")
    public String getReceiptUrl() {
        return receiptUrl;
    }

    @JsonProperty("receipt_url")
    public void setReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;
    }

    @JsonProperty("custom")
    public String getCustom() {
        return custom;
    }

    @JsonProperty("custom")
    public void setCustom(String custom) {
        this.custom = custom;
    }

    @JsonProperty("messages")
    public List<MessageResponse__> getMessages() {
        return messages;
    }

    @JsonProperty("messages")
    public void setMessages(List<MessageResponse__> messages) {
        this.messages = messages;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
