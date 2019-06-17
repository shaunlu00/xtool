package org.crudboy.toolbar.exception;

public class ToolbarRuntimeException extends RuntimeException {

    public ToolbarRuntimeException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public ToolbarRuntimeException(String msg) {super(msg);}
}
