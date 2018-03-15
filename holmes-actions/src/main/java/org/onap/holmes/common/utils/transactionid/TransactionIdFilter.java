/**
 * Copyright 2018 ZTE Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onap.holmes.common.utils.transactionid;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;


@Service
@Slf4j
public class TransactionIdFilter implements Filter {

    public static final Marker INVOKE_SYNCHRONOUS;
    public static final Marker ENTRY = MarkerFactory.getMarker("ENTRY");
    public static final Marker EXIT = MarkerFactory.getMarker("EXIT");

    static {
        INVOKE_SYNCHRONOUS = MarkerFactory.getMarker("INVOKE");
        INVOKE_SYNCHRONOUS.add(MarkerFactory.getMarker("SYNCHRONOUS"));
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
            FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        AddHeadersHttpServletRequestWrapper requestWithTransactionId = new AddHeadersHttpServletRequestWrapper(
                httpServletRequest);
        log.warn(ENTRY, "Entering.");

        String requestID = ensureTransactionIdIsPresent(requestWithTransactionId);
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        httpServletResponse.setHeader(TransactionIdUtils.REQUEST_ID_HEADER, requestID);

        String invocationID = TransactionIdUtils.getUUID();
        httpServletResponse.setHeader(TransactionIdUtils.INVOCATIONIDID_HEADER,invocationID);

        MDC.put("RequestID", requestID);
        MDC.put("InvocationID", invocationID);

        log.warn(INVOKE_SYNCHRONOUS, "Invoking synchronously ... ");
        try {
            filterChain.doFilter(requestWithTransactionId, httpServletResponse);
        } finally {
            log.debug(EXIT, "Exiting.");
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
            log.info(INVOKE_SYNCHRONOUS, "This warning has a 'MY_MARKER' annotation.");
            log.info("Request ID ({} header) not exist. It was generated: {}",
                    TransactionIdUtils.REQUEST_ID_HEADER, requestId);
            request.addHeader(TransactionIdUtils.REQUEST_ID_HEADER, requestId);
        }
        return requestId;
    }
}
