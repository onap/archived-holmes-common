package org.onap.holmes.common.utils.transactionid;

import java.util.List;
import java.util.UUID;
import javax.ws.rs.core.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class TransactionIdUtils {
    public static final String REQUEST_ID_HEADER = "X-TransactionID";
    public static final String INVOCATIONIDID_HEADER = "X-InvocationID";

    public static String getUUID() {
        return java.util.UUID.randomUUID().toString();
    }
}
