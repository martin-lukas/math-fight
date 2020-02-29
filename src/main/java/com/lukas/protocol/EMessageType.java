package com.lukas.protocol;

public enum EMessageType {
    // Client-side message types
    LOGIN,
    ANSWER,
    
    // Server-side message types
    CONNECTED,
    QUESTION,
    STATUS,
    END_OF_ROUND,
    END_OF_GAME,
    WRONG_ANSWER,
    ERROR,
    
    // both
    QUIT
}
