package org.example.mailapp.spam;

public class SpamRequest {
    private String text;

    public SpamRequest(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

