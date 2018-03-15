package org.onap.holmes.common.utils.transactionid;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.lang3.StringUtils;

public class AdditionalHeadersHttpServletRequestWrapper extends HttpServletRequestWrapper {
    final private Map<String, String> additionalHeaders = new HashMap<>();

    public AdditionalHeadersHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    public void addHeader(String name, String value) {
        additionalHeaders.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        String header = additionalHeaders.get(name);
        return (header != null) ? header : super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> names = new HashSet<>(Collections.list(super.getHeaderNames()));
        names.addAll(additionalHeaders.keySet());
        return Collections.enumeration(names);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        String header = additionalHeaders.get(name);
        if (StringUtils.isEmpty(header) || header.trim().isEmpty()) {
            return super.getHeaders(name);
        } else {
            return Collections.enumeration(Collections.singleton(header));
        }
    }
}
