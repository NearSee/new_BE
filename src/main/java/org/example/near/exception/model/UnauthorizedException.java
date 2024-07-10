package org.example.near.exception.model;

import org.example.near.exception.ErrorCode;
public class UnauthorizedException extends CustomException {
    public UnauthorizedException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}