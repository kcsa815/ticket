package com.musical.ticket.handler;
//클라이언트에게 반환할 JSON 응답 형식을 정의함.


import com.musical.ticket.handler.exception.ErrorCode;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private final int status;
    private final String code;
    private final String message;

    public ErrorResponse(ErrorCode errorCode) {
        this.status = errorCode.getStatus().value();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }
    
    // @Valid 실패 시 사용
    public ErrorResponse(ErrorCode errorCode, String message) {
        this.status = errorCode.getStatus().value();
        this.code = errorCode.getCode();
        this.message = message;
    }
}
