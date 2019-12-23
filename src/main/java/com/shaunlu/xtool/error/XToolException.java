package com.shaunlu.xtool.error;

public class XToolException extends RuntimeException {

    private String errorCode;

    public XToolException(String errorCode, String errorDetail) {
        super(ErrorCode.getErrorMsg(errorCode, errorDetail));
        this.errorCode = errorCode;
    }

    public XToolException(String errorCode, String errorDetail, Throwable e) {
        super(ErrorCode.getErrorMsg(errorCode, errorDetail), e);
        this.errorCode = errorCode;
    }

    public XToolException(String errorCode, Throwable e) {
        super(ErrorCode.getErrorMsg(errorCode, e.getMessage()), e);
        this.errorCode = errorCode;
    }

    public XToolException(String errorCode) {
        super();
        this.errorCode = errorCode;
    }
}
