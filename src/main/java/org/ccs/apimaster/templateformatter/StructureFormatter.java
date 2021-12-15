/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.templateformatter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import org.json.XML;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static org.slf4j.LoggerFactory.getLogger;

/*
@Purpose: This class manages formatting of test case structure
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
public class StructureFormatter implements Formatter {

    private static final org.slf4j.Logger LOGGER = getLogger(StructureFormatter.class);

    private final ObjectMapper mapper;

    @Inject
    public StructureFormatter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Object xmlToJson(String xmlContent) {

        prettyXml(xmlContent);

        String jsonNotPretty = XML.toJSONObject(xmlContent).toString();

        try {
            return mapper.readTree(jsonNotPretty);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("XmlToJson conversion problem-  " + e.getMessage());
        }

    }

    @Override
    public Object stringToJson(String jsonString) throws IOException {
        return mapper.readValue(jsonString, JsonNode.class);
    }

    @Override
    public Object jsonToJson(String jsonString) throws IOException {
        return stringToJson(jsonString);
    }

    public Object jsonBlockToJson(JsonNode jsonNode) {
        return jsonNode;
    }

    public static String prettyXml(String input) {

        final String formattedXml = prettyXmlWithIndentType(input, 2);

        LOGGER.info("\n--------------------- Pretty XML -------------------------\n"
                + formattedXml +
                "\n------------------------- * -----------------------------\n");

        return formattedXml;
    }

    public static String prettyXmlWithIndentType(String originalXml, int indentType) {
        try {
            Source xmlInput = new StreamSource(new StringReader(originalXml));
            StringWriter stringWriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", indentType);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString();
        } catch (Throwable e) {
            e.printStackTrace();
            try {
                Source xmlInput = new StreamSource(new StringReader(originalXml));
                StringWriter stringWriter = new StringWriter();
                StreamResult xmlOutput = new StreamResult(stringWriter);
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(indentType));
                transformer.transform(xmlInput, xmlOutput);
                return xmlOutput.getWriter().toString();
            } catch (Throwable t) {
                e.printStackTrace();
                return originalXml;
            }
        }
    }


}
