package com.payment.exceptions;

public class WebhookAlreadyExistsException extends Exception{

    public WebhookAlreadyExistsException(String message){
        super(message);
    }
}
