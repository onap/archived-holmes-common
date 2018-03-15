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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        AdditionalHeadersHttpServletRequestWrapper requestWithTransactionId = new AdditionalHeadersHttpServletRequestWrapper(
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
        long startTime = System.currentTimeMillis();
        boolean success = false;
        try {
            filterChain.doFilter(requestWithTransactionId, httpServletResponse);
            success = true;
        } finally {
            long endTime = System.currentTimeMillis();
            long timeTakenMillis = (endTime - startTime);
            log.info("[REQUEST HANDLED] uri={} time_ms={} status={} exception_was_thrown={}",
                    httpServletRequest.getPathInfo(), timeTakenMillis,
                    httpServletResponse.getStatus(), !success);
            log.debug(EXIT, "Exiting.");
            MDC.remove("RequestID");
            MDC.remove("InvocationID");
        }
    }

    @Override
    public void destroy() {

    }

    private String ensureTransactionIdIsPresent(
            AdditionalHeadersHttpServletRequestWrapper request) {
        String requestId = request.getHeader(TransactionIdUtils.REQUEST_ID_HEADER);

        if (isRequestIdNotExist(requestId)) {
            requestId = TransactionIdUtils.getUUID();
            log.info(INVOKE_SYNCHRONOUS, "This warning has a 'MY_MARKER' annotation.");
            log.info("Request ID ({} header) not exist. It was generated: {}",
                    TransactionIdUtils.REQUEST_ID_HEADER, requestId);
            request.addHeader(TransactionIdUtils.REQUEST_ID_HEADER, requestId);
        }
        return requestId;
    }

    private boolean isRequestIdNotExist(String transactionId) {
        return StringUtils.isEmpty(transactionId) || transactionId.trim().isEmpty();
    }
}
