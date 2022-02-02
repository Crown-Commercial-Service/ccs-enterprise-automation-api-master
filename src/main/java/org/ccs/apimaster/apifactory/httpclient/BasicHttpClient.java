/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.httpclient;

import org.ccs.apimaster.apifactory.utils.HelperJsonUtils;
import org.ccs.apimaster.apifactory.httpclient.utils.UrlQueryParamsManager;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.ccs.apimaster.apifactory.httpclient.utils.MasterFileUploader.*;
import static org.ccs.apimaster.apifactory.httpclient.utils.HeaderManager.processFrameworkDefault;
import static org.ccs.apimaster.apifactory.utils.HelperJsonUtils.getContentAsItIsJson;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

/*
@Purpose: This class manages basic http/https client
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 02/12/2021
*/
public class BasicHttpClient {
    Logger LOGGER = LoggerFactory.getLogger(BasicHttpClient.class);

    public static final String FILES_FIELD = "files";
    public static final String BOUNDARY_FIELD = "boundary";
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public static final String APPLICATION_FORM_URL_ENCODED = "application/x-www-form-urlencoded";
    public static final String CONTENT_TYPE = "Content-Type";

    private Object COOKIE_JSESSIONID_VALUE;
    private CloseableHttpClient httpclient;

    public BasicHttpClient() {
    }

    public BasicHttpClient(CloseableHttpClient httpclient) {
        this.httpclient = httpclient;
    }

    public CloseableHttpClient createHttpClient() throws Exception {

        LOGGER.info("###Creating SSL Enabled Http Client for both http/https/TLS connections");

        SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(null, (certificate, authType) -> true).build();

        CookieStore cookieStore = new BasicCookieStore();

        return HttpClients.custom()
                .setSSLContext(sslContext)
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .setDefaultCookieStore(cookieStore)
                .build();

    }


    public Response execute(String httpUrl,
                            String methodName,
                            Map<String, Object> headers,
                            Map<String, Object> queryParams,
                            Object body) throws Exception {

        httpclient = createHttpClient();

        String reqBodyAsString = handleRequestBody(body);

        httpUrl = handleUrlAndQueryParams(httpUrl, queryParams);

        RequestBuilder requestBuilder = createRequestBuilder(httpUrl, methodName, headers, reqBodyAsString);

        handleHeaders(headers, requestBuilder);

        addCookieToHeader(requestBuilder);

        CloseableHttpResponse httpResponse = httpclient.execute(requestBuilder.build());

        return handleResponse(httpResponse);
    }

    public Response handleResponse(CloseableHttpResponse httpResponse) throws IOException {
        Response serverResponse = createCharsetResponse(httpResponse);

        Header[] allHeaders = httpResponse.getAllHeaders();
        Response.ResponseBuilder responseBuilder = Response.fromResponse(serverResponse);
        for (Header thisHeader : allHeaders) {
            String headerKey = thisHeader.getName();
            responseBuilder = responseBuilder.header(headerKey, thisHeader.getValue());

            handleHttpSession(serverResponse, headerKey);
        }

        return responseBuilder.build();
    }

    public Response createCharsetResponse(CloseableHttpResponse httpResponse) throws IOException {
        HttpEntity entity = httpResponse.getEntity();
        Charset charset = ContentType.getOrDefault(httpResponse.getEntity()).getCharset();
        charset = (charset == null) ? Charset.defaultCharset() : charset;
        return Response
                .status(httpResponse.getStatusLine().getStatusCode())
                .entity(entity != null ? IOUtils.toString(entity.getContent(), charset) : null)
                .build();
    }

    public String handleUrlAndQueryParams(String httpUrl, Map<String, Object> queryParams) throws URISyntaxException {
        if ((queryParams != null) && (!queryParams.isEmpty())) {
            httpUrl = UrlQueryParamsManager.setQueryParams(httpUrl, queryParams);
        }
        return httpUrl;
    }

    public RequestBuilder handleHeaders(Map<String, Object> headers, RequestBuilder requestBuilder) {
        Map<String, Object> amendedHeaders = amendRequestHeaders(headers);
        processFrameworkDefault(amendedHeaders, requestBuilder);
        return requestBuilder;
    }

    public Map<String, Object> amendRequestHeaders(Map<String, Object> headers) {
        return headers;
    }

    public String handleRequestBody(Object body) {
        return getContentAsItIsJson(body);
    }

    public RequestBuilder createDefaultRequestBuilder(String httpUrl, String methodName, String reqBodyAsString) {
        RequestBuilder requestBuilder = RequestBuilder
                .create(methodName)
                .setUri(httpUrl);

        if (reqBodyAsString != null) {
            HttpEntity httpEntity = EntityBuilder.create()
                    .setContentType(APPLICATION_JSON)
                    .setText(reqBodyAsString)
                    .build();
            requestBuilder.setEntity(httpEntity);
        }
        return requestBuilder;
    }

    public RequestBuilder createFormUrlEncodedRequestBuilder(String httpUrl, String methodName, String reqBodyAsString) throws IOException {
        RequestBuilder requestBuilder = RequestBuilder
                .create(methodName)
                .setUri(httpUrl);
        if (reqBodyAsString != null) {
            Map<String, Object> reqBodyMap = HelperJsonUtils.readObjectAsMap(reqBodyAsString);
            List<NameValuePair> reqBody = new ArrayList<>();
             for(String key : reqBodyMap.keySet()) {
                 reqBody.add(new BasicNameValuePair(key, reqBodyMap.get(key).toString()));
             }
             HttpEntity httpEntity = new UrlEncodedFormEntity(reqBody);
             requestBuilder.setEntity(httpEntity);
            requestBuilder.setHeader(CONTENT_TYPE, APPLICATION_FORM_URL_ENCODED);
        }
        return requestBuilder;
    }

    public RequestBuilder createFileUploadRequestBuilder(String httpUrl, String methodName, String reqBodyAsString) throws IOException {
        Map<String, Object> fileFieldNameValueMap = getFileFieldNameValue(reqBodyAsString);

        List<String> fileFieldsList = (List<String>) fileFieldNameValueMap.get(FILES_FIELD);

        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();

        if(fileFieldsList != null) {
        	buildAllFilesToUpload(fileFieldsList, multipartEntityBuilder);
	}

        buildOtherRequestParams(fileFieldNameValueMap, multipartEntityBuilder);

        buildMultiPartBoundary(fileFieldNameValueMap, multipartEntityBuilder);

        return createUploadRequestBuilder(httpUrl, methodName, multipartEntityBuilder);
    }

    public void handleHttpSession(Response serverResponse, String headerKey) {

        if ("Set-Cookie".equals(headerKey)) {
            COOKIE_JSESSIONID_VALUE = serverResponse.getMetadata().get(headerKey);
        }
    }

    private void addCookieToHeader(RequestBuilder uploadRequestBuilder) {

        if (COOKIE_JSESSIONID_VALUE != null) {
            uploadRequestBuilder.addHeader("Cookie", (String) COOKIE_JSESSIONID_VALUE);
        }
    }

    public RequestBuilder createRequestBuilder(String httpUrl, String methodName, Map<String, Object> headers, String reqBodyAsString) throws IOException {

        String contentType = headers != null? (String) headers.get(CONTENT_TYPE) :null;

        if(contentType!=null){

            if(contentType.equals(MULTIPART_FORM_DATA)){

                return createFileUploadRequestBuilder(httpUrl, methodName, reqBodyAsString);

            } else if(contentType.equals(APPLICATION_FORM_URL_ENCODED)) {

                return createFormUrlEncodedRequestBuilder(httpUrl, methodName, reqBodyAsString);
            }

        }
        return createDefaultRequestBuilder(httpUrl, methodName, reqBodyAsString);
    }
}
