package org.onap.holmes.common.utils.transactionid;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TransactionIdUtilsTest {

    @Test
    public void validate_is_uuid() {
        assertThat(TransactionIdUtils.validate(TransactionIdUtils.getUUID()), is(true));
    }

    @Test
    public void validate_not_uuid() {
        assertThat(TransactionIdUtils.validate("a-random-string"), is(false));
    }
}