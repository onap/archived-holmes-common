package org.onap.holmes.common.utils.transactionid;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class TransactionIdFilterTest {
    TransactionIdFilter filter = new TransactionIdFilter();

    private HttpServletRequest requestMock;
    private HttpServletResponse responseMock;
    private FilterChain chainMock;

    @Before
    public void setUp() throws Exception{
        requestMock = EasyMock.createMock(HttpServletRequest.class);
        responseMock = EasyMock.createMock(HttpServletResponse.class);
        chainMock = EasyMock.createMock(FilterChain.class);
    }
    @Test
    public void callsChainDoFilter() throws Exception {
        filter.doFilter(requestMock, responseMock, chainMock);
        EasyMock.verify();
    }
    @Test
    public void requestIdExistTest() throws Exception{
        String requestID = TransactionIdUtils.getUUID();
        EasyMock.expect(requestMock.getHeader(TransactionIdUtils.REQUEST_ID_HEADER)).andReturn(requestID);

        EasyMock.replay(requestMock);
        filter.doFilter(requestMock, responseMock, chainMock);
        EasyMock.verify();

    }


}