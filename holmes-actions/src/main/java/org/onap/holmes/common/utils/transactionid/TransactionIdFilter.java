/**
 * Copyright 2018-2022 ZTE Corporation.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onap.holmes.common.utils.transactionid;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;


@Slf4j
@Component
@WebFilter(urlPatterns = {"/*"})
public class TransactionIdFilter implements Filter {

    public static final Marker INVOKE_SYNCHRONOUS;
    private static final String DEFAULT_REQUEST_ID = UUID.randomUUID().toString();

    static {
        INVOKE_SYNCHRONOUS = MarkerFactory.getMarker("INVOKE");
        INVOKE_SYNCHRONOUS.add(MarkerFactory.getMarker("SYNCHRONOUS"));
    }

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        AddHeadersHttpServletRequestWrapper requestWithTransactionId = new AddHeadersHttpServletRequestWrapper(
                httpServletRequest);

        String requestID = ensureTransactionIdIsPresent(requestWithTransactionId);
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        String validatedRequestID = TransactionIdUtils.validate(requestID);
        if (validatedRequestID != null) {
            httpServletResponse.setHeader(TransactionIdUtils.REQUEST_ID_HEADER, validatedRequestID);
        } else {
            log.warn("A mal-formatted request ID has been detected: {}. It will be replaced by the default ID: {}",
                    requestID, DEFAULT_REQUEST_ID);
            requestID = DEFAULT_REQUEST_ID;
        }

        String invocationID = TransactionIdUtils.getUUID();
        httpServletResponse.setHeader(TransactionIdUtils.INVOCATIONIDID_HEADER, invocationID);

        MDC.put("RequestID", requestID);
        MDC.put("InvocationID", invocationID);

        try {
            filterChain.doFilter(requestWithTransactionId, httpServletResponse);
        } finally {
            MDC.remove("RequestID");
            MDC.remove("InvocationID");
        }
    }

    @Override
    public void destroy() {

    }

    public String ensureTransactionIdIsPresent(
            AddHeadersHttpServletRequestWrapper request) {
        String requestId = request.getHeader(TransactionIdUtils.REQUEST_ID_HEADER);

        if (StringUtils.isBlank(requestId)) {
            requestId = TransactionIdUtils.getUUID();
            log.info("Request ID ({} header) not exist. It was generated: {}",
                    TransactionIdUtils.REQUEST_ID_HEADER, requestId);
            request.addHeader(TransactionIdUtils.REQUEST_ID_HEADER, requestId);
        }
        return requestId;
    }
}
