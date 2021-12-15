/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.utils;

import org.ccs.apimaster.apifactory.coreactions.tokens.MasterValueTokens;
import org.apache.commons.lang.text.StrSubstitutor;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang.StringEscapeUtils.escapeJava;

/*
@Purpose: This class manages token utils from project level
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
public class TokenUtils {

    public static String resolveKnownTokens(String requestJsonOrAnyString) {
        Map<String, Object> paramMap = new HashMap<>();

        final List<String> testCaseTokens = getTestCaseTokens(requestJsonOrAnyString);
        testCaseTokens.stream().distinct().forEach(runTimeToken -> {
            populateParamMap(paramMap, runTimeToken);
        });

        StrSubstitutor sub = new StrSubstitutor(paramMap);

        return sub.replace(requestJsonOrAnyString);
    }

    public static void populateParamMap(Map<String, Object> paramaMap, String runTimeToken) {
        MasterValueTokens.getKnownTokens().forEach(inStoreToken -> {
                    if (runTimeToken.startsWith(inStoreToken)) {
                        if (runTimeToken.startsWith(MasterValueTokens.RANDOM_NUMBER)) {
                            String[] slices = runTimeToken.split(":");
                            if (slices.length == 2) {
                                if(runTimeToken.startsWith(MasterValueTokens.RANDOM_NUMBER_FIXED)){
                                    paramaMap.put(runTimeToken,  FixedLengthRandomGenerator.getGenerator(Integer.parseInt(slices[1])).toString());
                                }else{
                                    paramaMap.put(runTimeToken, FixedLengthRandomGenerator.getGenerator(Integer.parseInt(slices[1])));
                                }
                            } else {
                                if(runTimeToken.equals(MasterValueTokens.RANDOM_NUMBER_FIXED)){
                                    paramaMap.put(runTimeToken, new RandomNumberGenerator().toString());
                                }else {
                                    paramaMap.put(runTimeToken, new RandomNumberGenerator());
                                }
                            }

                        } else if (runTimeToken.startsWith(MasterValueTokens.RANDOM_STRING_ALPHA)) {
                            int length = Integer.parseInt(runTimeToken.substring(MasterValueTokens.RANDOM_STRING_ALPHA.length()));
                            paramaMap.put(runTimeToken, createRandomAlphaString(length));

                        } else if (runTimeToken.startsWith(MasterValueTokens.RANDOM_STRING_ALPHA_NUMERIC)) {
                            int length = Integer.parseInt(runTimeToken.substring(MasterValueTokens.RANDOM_STRING_ALPHA_NUMERIC.length()));
                            paramaMap.put(runTimeToken, createRandomAlphaNumericString(length));

                        }
                        else if (runTimeToken.startsWith(MasterValueTokens.RANDOM_STRING_EMAIL)) {
                            String domain = runTimeToken.split(":")[1];
                            paramaMap.put(runTimeToken, createRandomEmail(domain));

                        } else if (runTimeToken.startsWith(MasterValueTokens.STATIC_ALPHABET)) {
                            int length = Integer.parseInt(runTimeToken.substring(MasterValueTokens.STATIC_ALPHABET.length()));
                            paramaMap.put(runTimeToken, createStaticAlphaString(length));

                        } else if (runTimeToken.startsWith(MasterValueTokens.LOCALDATE_TODAY)) {
                            String formatPattern = runTimeToken.substring(MasterValueTokens.LOCALDATE_TODAY.length());
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern);
                            paramaMap.put(runTimeToken, LocalDate.now().format(formatter));

                        } else if (runTimeToken.startsWith(MasterValueTokens.LOCALDATETIME_NOW)) {
                            String formatPattern = runTimeToken.substring(MasterValueTokens.LOCALDATETIME_NOW.length());
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern);
                            paramaMap.put(runTimeToken, LocalDateTime.now().format(formatter));

                        } else if (runTimeToken.startsWith(MasterValueTokens.SYSTEM_PROPERTY)) {
                            String propertyName = runTimeToken.substring(MasterValueTokens.SYSTEM_PROPERTY.length());
                            paramaMap.put(runTimeToken, System.getProperty(propertyName));

                        } else if (runTimeToken.startsWith(MasterValueTokens.SYSTEM_ENV)) {
                            String propertyName = runTimeToken.substring(MasterValueTokens.SYSTEM_ENV.length());
                            paramaMap.put(runTimeToken, System.getenv(propertyName));

                        } else if (runTimeToken.startsWith(MasterValueTokens.XML_FILE)) {
                            String xmlFileResource = runTimeToken.substring(MasterValueTokens.XML_FILE.length());
                            final String xmlString = getXmlContent(xmlFileResource);
                            paramaMap.put(runTimeToken, escapeJava(xmlString));
                        } else if (runTimeToken.startsWith(MasterValueTokens.GQL_FILE)) {
                            String gqlFileResource = runTimeToken.substring(MasterValueTokens.GQL_FILE.length());
                            final String gqlString = getXmlContent(gqlFileResource);
                            paramaMap.put(runTimeToken, escapeJava(gqlString));

                        } else if (runTimeToken.startsWith(MasterValueTokens.RANDOM_UU_ID)) {
                            if(runTimeToken.equals(MasterValueTokens.RANDOM_UU_ID_FIXED)){
                                paramaMap.put(runTimeToken, UUID.randomUUID().toString());
                            }else{
                                paramaMap.put(runTimeToken, new UUIDGenerator());
                            }

                        } else if (runTimeToken.startsWith(MasterValueTokens.ABS_PATH)) {
                            String propertyName = runTimeToken.substring(MasterValueTokens.ABS_PATH.length());
                            paramaMap.put(runTimeToken, absolutePathOf(propertyName));
                        }
                    }
                }
        );

    }

    public static List<String> getTestCaseTokens(String aString) {

        Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
        Matcher matcher = pattern.matcher(aString);

        List<String> keyTokens = new ArrayList<>();

        while (matcher.find()) {
            keyTokens.add(matcher.group(1));
        }

        return keyTokens;
    }

    public static String createRandomAlphaString(int length) {
        return randomAlphabetic(length);
    }

    public static String createRandomAlphaNumericString(int length) {
        return randomAlphanumeric(length);
    }

    public static String createRandomEmail(String domain) {
        return randomAlphanumeric(10)+domain;
    }
    public static String createStaticAlphaString(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append((char) ('a' + i));

            /*
             * This will repeat after A to Z
             */
            i = i >= 26 ? 0 : i;
        }

        return builder.toString();
    }


    public static String getXmlContent(String xmlFileResource) {
        try {
            return SmartUtils.readJsonAsString(xmlFileResource);
        } catch (RuntimeException e) {
            throw new RuntimeException("Oops! Problem occurred while reading the XML file '" + xmlFileResource
                    + "', details:" + e);
        }
    }

    public static String absolutePathOf(String resourceFilePath) {
        URL res = TokenUtils.class.getClassLoader().getResource(resourceFilePath);
        if(res == null){
            throw new RuntimeException("Wrong file name or path found '" + resourceFilePath + "', Please fix it and rerun.");
        }

        File file = null;
        try {
            file = Paths.get(res.toURI()).toFile();
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong while fetching abs path of '" + resourceFilePath + "', " +
                    "Please recheck the file/path. Full exception is : " + e);
        }

        return file.getAbsolutePath();
    }
}
