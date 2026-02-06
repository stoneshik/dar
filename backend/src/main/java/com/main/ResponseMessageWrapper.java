package com.main;


public record ResponseMessageWrapper(String responseMessage) {
    public String responseMessage() {
        return responseMessage;
    }
}
