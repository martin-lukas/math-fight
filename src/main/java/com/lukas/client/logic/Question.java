package com.lukas.client.logic;

/**
 * Class for the question.
 *
 * @author Jan Novosad
 */
public class Question {
    private final String text;

    public Question(String question) {
        this.text = question;
    }

    public String getText() {
        return text;
    }
}
