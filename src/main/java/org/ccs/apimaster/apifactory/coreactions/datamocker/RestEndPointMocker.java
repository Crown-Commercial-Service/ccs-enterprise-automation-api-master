/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.coreactions.datamocker;

import org.ccs.apimaster.apifactory.supportcenter.actionee.TestMapper;
import org.ccs.apimaster.apifactory.supportcenter.actionee.PackageMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.jknack.handlebars.Helper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

/*
@Purpose: This class manages mocking for Rest Endpoints
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 29/11/2021
*/
public class RestEndPointMocker {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestEndPointMocker.class);

    public static WireMockServer wireMockServer;

    public static Boolean strictUrlMatcherForAllUrls = false;

    /*
   @Method: hasMoreThanOneStubForSameUrlPath
   @Purpose: To handle more than one stub
   @Author: Mibin Boban, CCS Senior QAT Analyst
   @Creation: 29/10/2021
   */
    private static boolean hasMoreThanOneStubForSameUrlPath(List<String> urls) {
        Set<String> urlPathsSet = urls.stream()
                .map(u -> (u.contains("?")) ? u.substring(0, u.indexOf("?")) : u) // remove query params for comparison
                .collect(Collectors.toSet());
        return urlPathsSet.size() != urls.size();
    }

    /*
   @Method: createWithWireMock
   @Purpose: To create exec connection with wire mock and hanlde different operations
   @Author: Mibin Boban, CCS Senior QAT Analyst
   @Creation: 29/10/2021
   */
    public static void createWithWireMock(PackageMapper packageMapper, int mockPort) {

        restartWireMock(mockPort);

        List<String> urls = packageMapper.getMocks()
                .stream()
                .map(TestMapper::getUrl)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (urls.size() != 0 && hasMoreThanOneStubForSameUrlPath(urls)) {
            strictUrlMatcherForAllUrls = true;
        }
        LOGGER.info("Going to build strict url matcher - {}", strictUrlMatcherForAllUrls);
        packageMapper.getMocks().forEach(testMapper -> {
            JsonNode jsonNodeResponse = testMapper.getResponse();
            JsonNode jsonNodeBody = jsonNodeResponse.get("body");
            String jsonBodyRequest = (jsonNodeBody != null) ? jsonNodeBody.toString() : jsonNodeResponse.get("xmlBody").asText();

            if ("GET".equals(testMapper.getOperation())) {
                LOGGER.info("*****WireMock- Mocking the GET endpoint");
                givenThat(createGetRequestBuilder(testMapper)
                        .willReturn(responseBuilder(testMapper, jsonBodyRequest)));
                LOGGER.info("WireMock- Mocking the GET endpoint -done- *****");
            } else if ("POST".equals(testMapper.getOperation())) {
                LOGGER.info("*****WireMock- Mocking the POST endpoint");
                givenThat(createPostRequestBuilder(testMapper)
                        .willReturn(responseBuilder(testMapper, jsonBodyRequest)));
                LOGGER.info("WireMock- Mocking the POST endpoint -done-*****");
            } else if ("PUT".equals(testMapper.getOperation())) {
                LOGGER.info("*****WireMock- Mocking the PUT endpoint");
                givenThat(createPutRequestBuilder(testMapper)
                        .willReturn(responseBuilder(testMapper, jsonBodyRequest)));
                LOGGER.info("WireMock- Mocking the PUT endpoint -done-*****");
            } else if ("PATCH".equals(testMapper.getOperation())) {
                LOGGER.info("*****WireMock- Mocking the PATCH endpoint");
                givenThat(createPatchRequestBuilder(testMapper)
                        .willReturn(responseBuilder(testMapper, jsonBodyRequest)));
                LOGGER.info("WireMock- Mocking the PATCH endpoint -done-*****");
            } else if ("DELETE".equals(testMapper.getOperation())) {
                LOGGER.info("*****WireMock- Mocking the DELETE endpoint");
                givenThat(createDeleteRequestBuilder(testMapper)
                        .willReturn(responseBuilder(testMapper, jsonBodyRequest)));
                LOGGER.info("WireMock- Mocking the DELETE endpoint -done-*****");
            }

        });
    }

    /*
   @Method: restartWireMock
   @Purpose: To restart wire mocker instance
   @Author: Mibin Boban, CCS Senior QAT Analyst
   @Creation: 29/10/2021
   */
    public static void restartWireMock(int dynamicPort) {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
        wireMockServer = new WireMockServer(
                wireMockConfig()
                        .extensions(new ResponseTemplateTransformer(true, getWiremockHelpers()))
                        .port(dynamicPort)); // <-- Strange
        wireMockServer.start();
        WireMock.configureFor("localhost", dynamicPort); // <-- Repetition of PORT was needed, this is a wireMock bug
    }

    /*
   @Method: stopWireMockServer
   @Purpose: To stop wire mocker instance
   @Author: Mibin Boban, CCS Senior QAT Analyst
   @Creation: 29/10/2021
   */
    public static void stopWireMockServer() {
        if (null != wireMockServer) {
            wireMockServer.stop();
            wireMockServer = null;
            LOGGER.info("Scenario: All mockings done via WireMock server. Dependant end points executed. Stopped WireMock.");
        }
    }

    /*
      @Method: createDeleteRequestBuilder
      @Purpose: To build the mapper for delete requests
      @Author: Mibin Boban, CCS Senior QAT Analyst
      @Creation: 29/10/2021
    */
    private static MappingBuilder createDeleteRequestBuilder(TestMapper testMapper) {
        final MappingBuilder requestBuilder = delete(buildUrlPattern(testMapper.getUrl()));
        return createRequestBuilderWithHeaders(testMapper, requestBuilder);
    }

    /*
      @Method: createPatchRequestBuilder
      @Purpose: To build the mapper for patch requests
      @Author: Mibin Boban, CCS Senior QAT Analyst
      @Creation: 29/10/2021
    */
    private static MappingBuilder createPatchRequestBuilder(TestMapper testMapper) {
        final MappingBuilder requestBuilder = patch(buildUrlPattern(testMapper.getUrl()));
        return createRequestBuilderWithHeaders(testMapper, requestBuilder);
    }

    /*
      @Method: createPutRequestBuilder
      @Purpose: To build the mapper for Put requests
      @Author: Mibin Boban, CCS Senior QAT Analyst
      @Creation: 29/10/2021
    */
    private static MappingBuilder createPutRequestBuilder(TestMapper testMapper) {
        final MappingBuilder requestBuilder = put(buildUrlPattern(testMapper.getUrl()));
        return createRequestBuilderWithHeaders(testMapper, requestBuilder);
    }

    /*
      @Method: createPostRequestBuilder
      @Purpose: To build the mapper for Post requests
      @Author: Mibin Boban, CCS Senior QAT Analyst
      @Creation: 29/10/2021
    */
    private static MappingBuilder createPostRequestBuilder(TestMapper testMapper) {
        final MappingBuilder requestBuilder = post(buildUrlPattern(testMapper.getUrl()));
        return createRequestBuilderWithHeaders(testMapper, requestBuilder);
    }

    /*
      @Method: createGetRequestBuilder
      @Purpose: To build the mapper for Get requests
      @Author: Mibin Boban, CCS Senior QAT Analyst
      @Creation: 29/10/2021
    */
    private static MappingBuilder createGetRequestBuilder(TestMapper testMapper) {
        final MappingBuilder requestBuilder = get(buildUrlPattern(testMapper.getUrl()));
        return createRequestBuilderWithHeaders(testMapper, requestBuilder);
    }

    /*
      @Method: buildUrlPattern
      @Purpose: To build url pattern for requests
      @Author: Mibin Boban, CCS Senior QAT Analyst
      @Creation: 29/10/2021
    */
    private static UrlPattern buildUrlPattern(String url) {
        // if url pattern doesn't have query params and shouldBuildStrictUrlMatcher is true, then match url regardless query parameters
        if (url != null && !url.contains("?") && !strictUrlMatcherForAllUrls) {
            LOGGER.info("Going to build lenient matcher for url={}",url);
            return urlPathEqualTo(url);
        } else { // if url pattern has query params then match url strictly including query params
            LOGGER.info("Going to build strict matcher for url={}",url);
            return urlEqualTo(url);
        }
    }

    /*
      @Method: createRequestBuilderWithHeaders
      @Purpose: To read header and body and set to request
      @Author: Mibin Boban, CCS Senior QAT Analyst
      @Creation: 29/10/2021
    */
    private static MappingBuilder createRequestBuilderWithHeaders(TestMapper testMapper, MappingBuilder requestBuilder) {

        final String bodyJson = testMapper.getBody();

        if (StringUtils.isNotEmpty(bodyJson)) {
            requestBuilder.withRequestBody(equalToJson(bodyJson));
        }

        final Map<String, Object> headersMap = testMapper.getHeadersMap();

        if (headersMap.size() > 0) {
            for (Object key : headersMap.keySet()) {
                requestBuilder.withHeader((String) key, equalTo((String) headersMap.get(key)));
            }
        }
        return requestBuilder;
    }

    /*
      @Method: responseBuilder
      @Purpose: To read and build response
      @Author: Mibin Boban, CCS Senior QAT Analyst
      @Creation: 29/10/2021
    */
    private static ResponseDefinitionBuilder responseBuilder(TestMapper testMapper, String jsonBodyRequest) {
        ResponseDefinitionBuilder responseBuilder = aResponse()
                .withStatus(testMapper.getResponse().get("status").asInt());
        JsonNode headers = testMapper.getResponse().get("headers");
        JsonNode contentType = headers != null ? headers.get("Content-Type") : null;
        responseBuilder = contentType != null ?
                responseBuilder.withHeader("Content-Type", contentType.textValue()).withBody(jsonBodyRequest) :
                responseBuilder.withBody(jsonBodyRequest);

        return responseBuilder;
    }

    public static int createWithLocalMock(String endPointJsonApi) {
        if (StringUtils.isNotEmpty(endPointJsonApi)) {
        }

        return 200;
    }

    public static WireMockServer getWireMockServer() {
        return wireMockServer;
    }

    public static int createWithVirtuosoMock(String endPointJsonApi) {
        return 200;
    }

    private static Map<String, Helper> getWiremockHelpers() {
        Map<String, Helper> helperMap = new HashedMap();
        helperMap.put("localdatetime", new HandlebarsLocalDateHelper());
        return helperMap;
    }

}
