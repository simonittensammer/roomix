package at.htl.dto;

import java.io.Serializable;

public class SocketMessageDTO implements Serializable {

    private String type;
    private Object message;

    public SocketMessageDTO() {
    }

    public SocketMessageDTO(String type, Object message) {
        this.type = type;
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }
}
