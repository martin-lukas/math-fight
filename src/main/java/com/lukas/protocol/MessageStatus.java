package com.lukas.protocol;

import java.util.HashMap;
import java.util.Map;

public class MessageStatus extends Message {
    private final Map<String, Integer> playerList;

    public MessageStatus(String info, Map<String, Integer> playerList) {
        super(EMessageType.STATUS, info);
        this.playerList = new HashMap<>(playerList);
    }

    public Map<String, Integer> getPlayers() {
        return playerList;
    }
}
