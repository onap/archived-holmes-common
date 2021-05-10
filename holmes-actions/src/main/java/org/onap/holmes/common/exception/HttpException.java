package org.onap.holmes.common.exception;

public class HttpException extends RuntimeException {
    public HttpException(int statusCode, String msg) {
        super(String.format("Status code: <%d>. Message: %s", statusCode, msg));
    }

    public HttpException(int statusCode, String msg, Throwable t) {
        super(String.format("Status code: <%d>. Message: %s", statusCode, msg), t);
    }
}
