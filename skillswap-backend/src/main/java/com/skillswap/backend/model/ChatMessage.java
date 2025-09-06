package com.skillswap.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_message")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    @NotNull(message = "Sender is required")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    @NotNull(message = "Receiver is required")
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_match_id")
    private SkillMatch skillMatch;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Message content cannot be empty")
    @Size(max = 2000, message = "Message content cannot exceed 2000 characters")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType = MessageType.TEXT;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "edited_at")
    private LocalDateTime editedAt;

    @Column(name = "is_edited", nullable = false)
    private Boolean isEdited = false;

    @Column(name = "attachment_url")
    @Size(max = 500, message = "Attachment URL cannot exceed 500 characters")
    private String attachmentUrl;

    @Column(name = "attachment_type")
    @Size(max = 50, message = "Attachment type cannot exceed 50 characters")
    private String attachmentType;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON para información adicional

    // Constructores
    public ChatMessage() {
        this.sentAt = LocalDateTime.now();
    }

    public ChatMessage(User sender, User receiver, String content, MessageType messageType) {
        this();
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.messageType = messageType;
    }

    // Enums
    public enum MessageType {
        TEXT, IMAGE, FILE, AUDIO, VIDEO, SYSTEM, MATCH_REQUEST, MATCH_ACCEPTED, MATCH_COMPLETED
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public SkillMatch getSkillMatch() {
        return skillMatch;
    }

    public void setSkillMatch(SkillMatch skillMatch) {
        this.skillMatch = skillMatch;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
        if (isRead && this.readAt == null) {
            this.readAt = LocalDateTime.now();
        }
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

    // Métodos de utilidad
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    public void markAsEdited() {
        this.isEdited = true;
        this.editedAt = LocalDateTime.now();
    }

    public boolean isFromUser(Long userId) {
        return this.sender != null && this.sender.getId().equals(userId);
    }

    public boolean isToUser(Long userId) {
        return this.receiver != null && this.receiver.getId().equals(userId);
    }

    public boolean involvesUser(Long userId) {
        return isFromUser(userId) || isToUser(userId);
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id=" + id +
                ", sender=" + (sender != null ? sender.getId() : null) +
                ", receiver=" + (receiver != null ? receiver.getId() : null) +
                ", content='" + content + '\'' +
                ", messageType=" + messageType +
                ", sentAt=" + sentAt +
                ", isRead=" + isRead +
                '}';
    }
}
