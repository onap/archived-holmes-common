/**
 * Copyright 2020 ZTE Corporation.
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

package org.onap.holmes.common.utils;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.junit.Test;
import org.powermock.reflect.internal.WhiteboxImpl;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class JerseyClientTest {

    private JerseyClient jerseyClient = new JerseyClient();

    @Test
    public void http() {
        jerseyClient.httpClient();
    }

    @Test
    public void https() throws Exception {
        WhiteboxImpl.invokeMethod(jerseyClient, "init");
        jerseyClient.httpsClient();
    }

    @Test
    public void clientHttp() {
        jerseyClient.client(false);
    }

    @Test
    public void clientHttps() throws Exception {
        WhiteboxImpl.invokeMethod(jerseyClient, "init");
        jerseyClient.client(true);
    }

    @Test
    public void test() throws Exception {
        WhiteboxImpl.invokeMethod(jerseyClient, "init");
        Client client = jerseyClient.client(true);
        LoginResponse response = client.target("https://10.89.185.88:28001/api/smtest/v1/login").request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(new Login(), MediaType.APPLICATION_JSON), LoginResponse.class);

        System.out.println(ReflectionToStringBuilder.toString(response));
    }
}


class Login {
    private String username = "admin";
    private String password = "Zenap_123";
    private String isEncypted = "true";
    private String loginToken = "df421cd0874839755a89635e6d08eafb";

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIsEncypted() {
        return isEncypted;
    }

    public void setIsEncypted(String isEncypted) {
        this.isEncypted = isEncypted;
    }

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }
}

class LoginResponse {
    private String code;
    private String result;
    private String loginUser;
    private String loginTenantId;
    private Detail detail;
    private String home;

    public LoginResponse() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }

    public String getLoginTenantId() {
        return loginTenantId;
    }

    public void setLoginTenantId(String loginTenantId) {
        this.loginTenantId = loginTenantId;
    }

    public Detail getDetail() {
        return detail;
    }

    public void setDetail(Detail detail) {
        this.detail = detail;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }
}

class Detail {
    private String code;
    private String message;
    private String bannerStatus;

    public Detail() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getBannerStatus() {
        return bannerStatus;
    }

    public void setBannerStatus(String bannerStatus) {
        this.bannerStatus = bannerStatus;
    }
}