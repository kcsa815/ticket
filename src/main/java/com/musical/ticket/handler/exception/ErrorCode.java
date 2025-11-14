package com.musical.ticket.handler.exception;

//ErrorCode Enum 생성 (모든 에러 메세지와 상태 코드 이 곳에서 관리함.)
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // --- 공통 ---
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON-001", "유효하지 않은 입력 값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON-002", "지원하지 않는 HTTP Method입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON-003", "서버 내부 오류가 발생했습니다."),

    // --- 인증 (Security) ---
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "AUTH-001", "인증에 실패했습니다."), // 401
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH-002", "접근 권한이 없습니다."), // 403
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH-003", "토큰이 만료되었습니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "AUTH-004", "유효하지 않은 토큰입니다."),

    // --- 유저 ---
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "USER-001", "이미 가입된 이메일입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER-002", "존재하지 않는 회원입니다."),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "USER-003", "비밀번호가 일치하지 않습니다."),

    // --- 뮤지컬/공연 ---
    MUSICAL_NOT_FOUND(HttpStatus.NOT_FOUND, "MUSICAL-001", "존재하지 않는 뮤지컬입니다."),
    PERFORMANCE_NOT_FOUND(HttpStatus.NOT_FOUND, "PERF-001", "존재하지 않는 공연 회차입니다."),
    VENUE_NOT_FOUND(HttpStatus.NOT_FOUND, "VENUE-001", "존재하지 않는 공연장입니다."),
    BOOKING_NOT_FOUND(HttpStatus.NOT_FOUND, "BOOKING-001", "존재하지 않는 예매입니다."),

    // --- 예매/좌석 ---
    SEAT_NOT_FOUND(HttpStatus.NOT_FOUND, "SEAT-001", "존재하지 않는 좌석입니다."),
    SEAT_ALREADY_RESERVED(HttpStatus.BAD_REQUEST, "SEAT-002", "이미 예약된 좌석입니다."),

    // --- 파일 업로드 ---
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE-001", "파일 업로드에 실패했습니다.");

    private final HttpStatus status;
    private final String code; // 커스텀 에러 코드
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}