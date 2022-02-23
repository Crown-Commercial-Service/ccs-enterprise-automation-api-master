/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.httpclient.utils;

import org.ccs.apimaster.apifactory.mainmodule.support.ObjectMapperProvider;
import org.ccs.apimaster.apifactory.httpclient.BasicHttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.currentTimeMillis;
import static java.time.LocalDateTime.now;
import static org.apache.http.entity.ContentType.TEXT_PLAIN;

/*
@Purpose: This class manages functions for file upload
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 02/12/2021
*/
public class CCSMasterFileUploadUtil {

    /*
    @Method: buildUploadRequest
    @Purpose: To build file upload request
    @Author: Mibin Boban, CCS Senior QAT Analyst
    @Creation: 23/02/2022
    */
    public static RequestBuilder buildUploadRequest(String httpUrl, String methodName, MultipartEntityBuilder multipartEntityBuilder) {

        RequestBuilder uploadRequestBuilder = RequestBuilder
                .create(methodName)
                .setUri(httpUrl);

        HttpEntity reqEntity = multipartEntityBuilder.build();

        uploadRequestBuilder.setEntity(reqEntity);

        return uploadRequestBuilder;
    }

    /*
      @Method: buildDelimiterForMultiPart
      @Purpose: To create boundary or delimiter to separate data blocks
      @Author: Mibin Boban, CCS Senior QAT Analyst
      @Creation: 23/02/2022
    */
    public static void buildDelimiterForMultiPart(Map<String, Object> fileFieldNameValueMap, MultipartEntityBuilder multipartEntityBuilder) {
        String delimiter = (String) fileFieldNameValueMap.get(BasicHttpClient.BOUNDARY_FIELD);
        multipartEntityBuilder.setBoundary(delimiter != null ? delimiter : currentTimeMillis() + now().toString());
    }

    /*
   @Method: buildAllFilesForUpload
   @Purpose: To fetch and build file bodies for upload as part of request
   @Author: Mibin Boban, CCS Senior QAT Analyst
   @Creation: 23/02/2022
   */
    public static void buildAllFilesForUpload(List<String> fileFiledsList, MultipartEntityBuilder multipartEntityBuilder) {
        fileFiledsList.forEach(fileField -> {
            String[] fieldNameValue = fileField.split(":");
            String fieldName = fieldNameValue[0];
            String fileNameWithPath = fieldNameValue[1].trim();

            FileBody fileBody = new FileBody(new File(getAbsPath(fileNameWithPath)));
            multipartEntityBuilder.addPart(fieldName, fileBody);
        });
    }

    /*
   @Method: buildRequestTextParams
   @Purpose: To build request params in text plain format for file upload
   @Author: Mibin Boban, CCS Senior QAT Analyst
   @Creation: 23/02/2022
   */
    public static void buildRequestTextParams(Map<String, Object> fileFieldNameValueMap, MultipartEntityBuilder multipartEntityBuilder) {
        for (Map.Entry<String, Object> entry : fileFieldNameValueMap.entrySet()) {
            if (entry.getKey().equals(BasicHttpClient.FILES_FIELD) || entry.getKey().equals(BasicHttpClient.BOUNDARY_FIELD)) {
                continue;
            }
            multipartEntityBuilder.addPart(entry.getKey(), new StringBody((String) entry.getValue(), TEXT_PLAIN));
        }
    }

    public static Map<String, Object> getFileFieldNameValue(String reqBodyAsString) throws IOException {
        return new ObjectMapperProvider().get().readValue(reqBodyAsString, HashMap.class);
    }

    public static String getAbsPath(String filePath) {

        if (new File(filePath).exists()) {
            return filePath;
        }

        ClassLoader classLoader = CCSMasterFileUploadUtil.class.getClassLoader();
        URL resource = classLoader.getResource(filePath);
        if (resource == null) {
            throw new RuntimeException("Could not get details of file or folder - `" + filePath + "`, does this exist?");
        }
        return resource.getPath();
    }

}
