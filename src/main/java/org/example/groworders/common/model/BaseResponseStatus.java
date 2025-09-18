package org.example.groworders.common.model;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 20000 : 요청 성공
     */
    SUCCESS(true, 20000, "요청에 성공하였습니다."),


    /**
     * 30000 : Request 오류, Validation 오류
     */
    // Common
    REQUEST_ERROR(false, 30001, "입력값을 확인해주세요."),
    EXPIRED_JWT(false, 20001, "JWT 토큰이 만료되었습니다."),
    INVALID_JWT(false, 20002, "유효하지 않은 JWT입니다."),
    INVALID_USER_ROLE(false,20003,"권한이 없는 유저의 접근입니다."),
    INVALID_USER_INFO(false,20004,"이메일 또는 비밀번호를 확인해주세요."),
    INVALID_USER_DISABLED(false,20005,"이메일 인증이 필요합니다. 이메일을 확인해주세요."),
    DUPLICATE_USER_EMAIL(false,20006,"중복된 이메일입니다. 다른 이메일을 사용해주세요."),
    LECTURE_NOT_IN_COURSE(false,20007,"해당 강의는 해당 코스의 강의가 아닙니다."),
    ALREADY_LECTURE_COMPLETE(false,20008,"해당 강의를 이미 완료하였습니다."),
    INVALID_USER_PASSWORD(false,20009,"비밀번호가 일치하지 않습니다."),
    INVALID_USER_EMAIL(false,20010,"이메일 정보가 잘못되었습니다."),
    INVALID_EMAIL_RESET_TIMEOUT(false,20011,"이메일 변경 링크가 만료되었습니다. 다시 시도해주세요."),
    INVALID_CROP_INFO(false, 20012, "작물을 확인 해주세요."),
    INVALID_FARM_INFO(false,20013, "농장을 확인 해주세요."),
    FILE_UPLOAD_ERROR(false, 20012, "파일 업로드 실패"),
    PROFILE_IMAGE_UPLOAD_ERROR(false, 20013, "프로필 이미지 업로드 실패"),
    EMAIL_VERIFY_FAIL(false, 20014, "이메일 검증 실패"),
    METHOD_ARGUMENT_NOT_VALID(false, 20015, "MethodArgumentNotValid 오류"),



    /**
     * 40000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 40001, "값을 불러오는데 실패하였습니다."),
    RESPONSE_NULL_ERROR(false,40002,"[NULL]입력된 IDX 값로 접근한 DB의 유효한 ROW가 존재하지 않습니다."),

    ORDERS_VALIDATION_FAIL(false, 40003, "결제 정보가 잘못되었습니다."),
    IAMPORT_ERROR(false, 40004, "결제 금액이 잘못되었습니다."),
    ORDERS_NOT_ORDERED(false, 40005, "결제 정보가 없습니다. 구매 후 이용해주세요."),

    /**
     * 50000 : Database 오류
     */
    DATABASE_ERROR(false, 50001, "데이터베이스 연결에 실패하였습니다."),

    /**
     * 60000 : Server 오류
     */
    SERVER_ERROR(false, 60001, "서버와의 연결에 실패하였습니다.");



    /**
     * 70000 : 커스텀
     */

    private final boolean isSuccess;
    private final int code;
    private final String message;

    BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
