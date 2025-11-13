package com.musical.ticket.handler.exception;
// ErrorCode를 담을 수 있는 예외 클래스

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode, String string) {
        super(errorCode.getMessage()); // RuntimeException의 message 필드에 저장
        this.errorCode = errorCode;
    }

    public CustomException(ErrorCode userNotFound) {
        this.errorCode = null;
    }
}