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
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
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
@Purpose: This class manages functions for handling corporate proxy with SSL Trust
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 01/12/2021
*/
public class MasterSslTrustCorporateProxyClient extends BasicHttpClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(MasterSslTrustCorporateProxyClient.class);

    @Inject
    @Named("corporate.proxy.host")
    private String proxyHost;

    @Inject
    @Named("corporate.proxy.port")
    private int proxyPort;

    @Inject
    @Named("corporate.proxy.username")
    private String proxyUserName;

    @Inject
    @Named("corporate.proxy.password")
    private String proxyPassword;

    /*
    @Method: createHttpClient
    @Purpose: To create http/https client with ssl enabled proxy setup
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 01/12/2021
    */
    @Override
    public CloseableHttpClient createHttpClient() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
        LOGGER.info("###Used SSL Enabled Http Client with Corporate Proxy, for both Http and Https connections");

        SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(null, (certificate, authType) -> true).build();

        CookieStore cookieStore = new BasicCookieStore();

        CredentialsProvider credsProvider = createProxyCredentialsProvider(proxyHost, proxyPort, proxyUserName, proxyPassword);

        HttpHost proxy = new HttpHost(proxyHost, proxyPort);

        return HttpClients.custom()
                .setSSLContext(sslContext)
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .setDefaultCookieStore(cookieStore)
                .setDefaultCredentialsProvider(credsProvider)
                .setProxy(proxy)
                .build();
    }

    /*
    @Method: createProxyCredentialsProvider
    @Purpose: To create proxy credential provider for with ssl enabled client
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 01/12/2021
    */
    private CredentialsProvider createProxyCredentialsProvider(String proxyHost, int proxyPort, String proxyUserName, String proxyPassword) {

        CredentialsProvider credsProvider = new BasicCredentialsProvider();

        credsProvider.setCredentials(
                new AuthScope(proxyHost, proxyPort),
                new UsernamePasswordCredentials(proxyUserName, proxyPassword));

        return credsProvider;

    }

}

