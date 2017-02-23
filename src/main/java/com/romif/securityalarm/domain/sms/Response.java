
package com.romif.securityalarm.domain.sms;

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
    "messages",
    "messages_not_sent",
    "balance_pre_send",
    "total_cost",
    "balance_post_send",
    "status"
})
public class Response {

    @JsonProperty("messages")
    private List<MessageResponse> messages = null;
    @JsonProperty("messages_not_sent")
    private List<MessagesNotSent> messagesNotSent = null;
    @JsonProperty("balance_pre_send")
    private Integer balancePreSend;
    @JsonProperty("total_cost")
    private Integer totalCost;
    @JsonProperty("balance_post_send")
    private Integer balancePostSend;
    @JsonProperty("errors")
    private List<Error> errors = null;
    @JsonProperty("status")
    private String status;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("messages")
    public List<MessageResponse> getMessages() {
        return messages;
    }

    @JsonProperty("messages_not_sent")
    public List<MessagesNotSent> getMessagesNotSent() {
        return messagesNotSent;
    }

    @JsonProperty("messages_not_sent")
    public void setMessagesNotSent(List<MessagesNotSent> messagesNotSent) {
        this.messagesNotSent = messagesNotSent;
    }

    @JsonProperty("messages")
    public void setMessages(List<MessageResponse> messages) {
        this.messages = messages;
    }

    @JsonProperty("balance_pre_send")
    public Integer getBalancePreSend() {
        return balancePreSend;
    }

    @JsonProperty("balance_pre_send")
    public void setBalancePreSend(Integer balancePreSend) {
        this.balancePreSend = balancePreSend;
    }

    @JsonProperty("total_cost")
    public Integer getTotalCost() {
        return totalCost;
    }

    @JsonProperty("total_cost")
    public void setTotalCost(Integer totalCost) {
        this.totalCost = totalCost;
    }

    @JsonProperty("balance_post_send")
    public Integer getBalancePostSend() {
        return balancePostSend;
    }

    @JsonProperty("balance_post_send")
    public void setBalancePostSend(Integer balancePostSend) {
        this.balancePostSend = balancePostSend;
    }

    @JsonProperty("errors")
    public List<Error> getErrors() {
        return errors;
    }

    @JsonProperty("errors")
    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
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
