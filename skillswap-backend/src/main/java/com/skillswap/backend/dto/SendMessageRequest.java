package com.skillswap.backend.dto;

import com.skillswap.backend.model.ChatMessage;

public class SendMessageRequest {
    private Long receiverId;
    private Long skillMatchId;
    private String content;
    private ChatMessage.MessageType messageType = ChatMessage.MessageType.TEXT;
    private String attachmentUrl;
    private String attachmentType;
    private String metadata;

    // Constructores
    public SendMessageRequest() {}

    // Getters y Setters
    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Long getSkillMatchId() {
        return skillMatchId;
    }

    public void setSkillMatchId(Long skillMatchId) {
        this.skillMatchId = skillMatchId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ChatMessage.MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(ChatMessage.MessageType messageType) {
        this.messageType = messageType;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public String getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}
