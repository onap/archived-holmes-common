/**
 * Copyright 2018 - 2021 ZTE Corporation.
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

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    @Test
    public void requestIdInvalidRerquestId() throws Exception{
        String requestID = "TransactionIdUtils.getUUID()";
        EasyMock.expect(requestMock.getHeader(TransactionIdUtils.REQUEST_ID_HEADER)).andReturn(requestID);

        EasyMock.replay(requestMock);
        filter.doFilter(requestMock, responseMock, chainMock);
        EasyMock.verify();
    }
}