package com.skillswap.backend.dto;

import com.skillswap.backend.model.ChatMessage;

import java.time.LocalDateTime;

public class ChatMessageDTO {
    private Long id;
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private Long receiverId;
    private String receiverName;
    private Long skillMatchId;
    private String content;
    private ChatMessage.MessageType messageType;
    private Boolean isRead;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
    private LocalDateTime editedAt;
    private Boolean isEdited;
    private String attachmentUrl;
    private String attachmentType;
    private String metadata;

    // Constructores
    public ChatMessageDTO() {}

    public ChatMessageDTO(ChatMessage message) {
        this.id = message.getId();
        this.senderId = message.getSender().getId();
        this.senderName = message.getSender().getDisplayNameOrFullName();
        this.senderAvatar = message.getSender().getProfilePictureUrl();
        this.receiverId = message.getReceiver().getId();
        this.receiverName = message.getReceiver().getDisplayNameOrFullName();
        this.skillMatchId = message.getSkillMatch() != null ? message.getSkillMatch().getId() : null;
        this.content = message.getContent();
        this.messageType = message.getMessageType();
        this.isRead = message.getIsRead();
        this.sentAt = message.getSentAt();
        this.readAt = message.getReadAt();
        this.editedAt = message.getEditedAt();
        this.isEdited = message.getIsEdited();
        this.attachmentUrl = message.getAttachmentUrl();
        this.attachmentType = message.getAttachmentType();
        this.metadata = message.getMetadata();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderAvatar() {
        return senderAvatar;
    }

    public void setSenderAvatar(String senderAvatar) {
        this.senderAvatar = senderAvatar;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
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

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public LocalDateTime getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(LocalDateTime editedAt) {
        this.editedAt = editedAt;
    }

    public Boolean getIsEdited() {
        return isEdited;
    }

    public void setIsEdited(Boolean isEdited) {
        this.isEdited = isEdited;
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

    @Override
    public String toString() {
        return "ChatMessageDTO{" +
                "id=" + id +
                ", senderId=" + senderId +
                ", senderName='" + senderName + '\'' +
                ", receiverId=" + receiverId +
                ", content='" + content + '\'' +
                ", messageType=" + messageType +
                ", sentAt=" + sentAt +
                ", isRead=" + isRead +
                '}';
    }
}

    // SendMessageRequest se ha movido a su propio archivo: com.skillswap.backend.dto.SendMessageRequest
