/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.httpclient.ssl;

import org.ccs.apimaster.apifactory.httpclient.BasicHttpClient;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/*
@Purpose: This class manages timeout config for http clients
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 01/12/2021
*/
public class MasterSslTrustHttpClient extends BasicHttpClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(MasterSslTrustHttpClient.class);

    public static final String HTTP_MAX_TIMEOUT_MILLISECONDS = "http.max.timeout.milliseconds";

    @Inject(optional = true)
    @Named(HTTP_MAX_TIMEOUT_MILLISECONDS)
    private Integer implicitWait;

    public MasterSslTrustHttpClient() {
        super();
    }

    public MasterSslTrustHttpClient(CloseableHttpClient httpclient) {
        super(httpclient);
    }

    /*
    @Method: createHttpClient
    @Purpose: To create custom configured http client
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 01/12/2021
    */
    @Override
    public CloseableHttpClient createHttpClient() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
        LOGGER.info("###Used SSL Enabled Http Client for http/https/TLS connections");

        SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(null, (certificate, authType) -> true).build();

        CookieStore cookieStore = new BasicCookieStore();

        RequestConfig timeOutConfig = createMaxTimeOutConfig();

        return HttpClients.custom()
                .setSSLContext(sslContext)
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .setDefaultCookieStore(cookieStore)
                .setDefaultRequestConfig(timeOutConfig)
                .build();
    }

    /*
    @Method: createMaxTimeOutConfig
    @Purpose: To config http client with timeout
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 01/12/2021
    */
    private RequestConfig createMaxTimeOutConfig() {
        RequestConfig timeOutConfig;
        if (implicitWait == null) {
            timeOutConfig = RequestConfig.DEFAULT;
            LOGGER.debug("\n*Implicit-Wait/Connection-Timeout not configured.*" +
                            "\nE.g. to configure it for 10sec, use: '{}={}' in the host-config properties. " +
                            "\n**You can safely ignore this warning to retain the default httpClient behavior**\n",
                    HTTP_MAX_TIMEOUT_MILLISECONDS, 10000);
        } else {
            int timeout = implicitWait.intValue();
            timeOutConfig = RequestConfig.custom()
                    .setConnectTimeout(timeout)
                    .setSocketTimeout(timeout)
                    .setConnectionRequestTimeout(timeout)
                    .build();
            LOGGER.info("\n----------------------------------------------------------------\n" +
                    "Implicit-Wait/Connection-Timeout config = " + implicitWait +
                    " milli-second." +
                    "\n----------------------------------------------------------------\n");
        }

        return timeOutConfig;
    }

    // Unit testing
    void setImplicitWait(Integer implicitWait) {
        this.implicitWait = implicitWait;
    }
}

