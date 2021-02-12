package at.htl.dto;

import java.time.LocalDateTime;

public class ChatMessageDTO {

    private String sender;
    private LocalDateTime timestamp;
    private String content;

    public ChatMessageDTO() {
    }

    public ChatMessageDTO(String sender, LocalDateTime timestamp, String content) {
        this.sender = sender;
        this.timestamp = timestamp;
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
