
package com.lukas.protocol;

import java.io.Serializable;

public class Message implements Serializable {
    private final EMessageType type;
    private final String[] content;

    public Message(EMessageType messageType, String... content) {
        this.type = messageType;
        this.content = content;
    }

    public String[] getContent() {
        return content;
    }

    public EMessageType getType() {
        return type;
    }

}
