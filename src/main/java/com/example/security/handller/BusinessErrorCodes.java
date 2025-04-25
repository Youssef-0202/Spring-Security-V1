package com.example.security.handller;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;


public enum BusinessErrorCodes {
    NO_CODE(0, NOT_IMPLEMENTED,"No code"),
    INCORRECT_CURRENT_PASSWORD(300,BAD_REQUEST,"current password is in correct"),
    NEW_PASSWORD_NOT_MATCH(301,BAD_REQUEST,"the new password does not match"),
    ACCOUNT_DISABLED(303,FORBIDDEN,"User account is disabled"),
    BAD_CREDENTIAL(304,FORBIDDEN,"Login and / or password is incorrect  "),
    Account_LOCKED(302,FORBIDDEN,"User account is locked")
    ;
    @Getter
    private final  int code;
    @Getter
    private final  String description;
    @Getter
    private final  HttpStatus httpStatus;

    BusinessErrorCodes(int code, HttpStatus httpStatus, String description) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }
}
