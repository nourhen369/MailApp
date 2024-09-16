package org.example.mailapp.spam;

import lombok.Data;

@Data
public class SpamRequest {
    private String text;
    public SpamRequest(String text) {
        this.text = text;
    }
}

