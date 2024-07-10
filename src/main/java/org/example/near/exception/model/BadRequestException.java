package org.example.near.exception.model;

import org.example.near.exception.ErrorCode;

public class BadRequestException extends CustomException{
    public BadRequestException(ErrorCode errorCode, String message){
        super(errorCode, message);
    }
}
