package org.onap.holmes.common.exception;

import org.junit.Test;

public class HttpExceptionTest {

    @Test
    public void exception_without_cause() {
        HttpException e = new HttpException(200, "OK");
    }

    @Test
    public void exception_with_cause() {
        HttpException e = new HttpException(404, "Not Found", new Exception());
    }
}