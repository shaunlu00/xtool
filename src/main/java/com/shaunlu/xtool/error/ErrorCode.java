package com.shaunlu.xtool.error;

public class ErrorCode {

    public static String EXCEL_OPEN_ERROR = "EXCEL_OPEN_ERROR";
    public static String EXCEL_CLOSE_ERROR = "EXCEL_CLOSE_ERROR";
    public static String EXCEL_READ_ERROR = "EXCEL_READ_ERROR";
    public static String EXCEL_WRITE_ERROR = "EXCEL_WRITE_ERROR";

    public static String EMAIL_SEND_ERROR = "EMAIL_SEND_ERROR";

    public static String JSON_SERIALIZE_ERROR = "JSON_SERIALIZE_ERROR";
    public static String JSON_DESERIALIZE_ERROR = "JSON_DESERIALIZE_ERROR";

    public static String BAD_PARAMETER = "BAD_PARAMETER";

    public static String getErrorMsg(String errorCode, String errorDetail) {
        return "[ErrorCode=" + errorCode + "], [ErrorDetail=" + errorDetail + "]";
    }
}
