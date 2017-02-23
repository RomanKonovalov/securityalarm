
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
    "test",
    "sender",
    "receiptUrl"
})
public class Request {

    @JsonProperty("messages")
    private List<MessageRequest> messages = null;
    @JsonProperty("test")
    private Boolean test;
    @JsonProperty("sender")
    private String sender;
    @JsonProperty("receiptUrl")
    private String receiptUrl;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("messages")
    public List<MessageRequest> getMessages() {
        return messages;
    }

    @JsonProperty("messages")
    public void setMessages(List<MessageRequest> messages) {
        this.messages = messages;
    }

    @JsonProperty("test")
    public Boolean getTest() {
        return test;
    }

    @JsonProperty("test")
    public void setTest(Boolean test) {
        this.test = test;
    }

    @JsonProperty("sender")
    public String getSender() {
        return sender;
    }

    @JsonProperty("sender")
    public void setSender(String sender) {
        this.sender = sender;
    }

    @JsonProperty("receiptUrl")
    public String getReceiptUrl() {
        return receiptUrl;
    }

    @JsonProperty("receiptUrl")
    public void setReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;
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
