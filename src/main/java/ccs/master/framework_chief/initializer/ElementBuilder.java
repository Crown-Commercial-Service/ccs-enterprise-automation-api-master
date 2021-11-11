/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor:
 * Authors:
 * Contributors:
 */
package ccs.master.framework_chief.initializer;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.By;

/*
@Class: ElementBuilder
@Purpose: This class handles the configuration of web elements locators
@Author: Mibin Boban, CCS Test Analyst
@Creation: 01/11/2021
*/
public class ElementBuilder
{
    private String elementName;
    private String elementValue;
    private String elementType;
    private String errorMessage;

    public static final String XPATH = "XPATH";
    public static final String CSS = "CSS";
    public static final String ID = "ID";
    public static final String CLASS = "CLASS";
    public static final String NAME = "NAME";
    public static final String LINKTEXT = "LINKTEXT";
    public static final String PARTIALLINKTEXT = "PARTIALLINKTEXT";
    public static final String TAGNAME = "TAGNAME";

    public ElementBuilder(final String elementName, final String elementValue, final String elementType) {
        super();
        this.errorMessage = "";
        this.elementName = elementName;
        this.elementValue = elementValue;
        this.elementType = elementType;
    }

    public String getelementName() {
        return this.elementName;
    }

    public String getelementValue() {
        return this.elementValue;
    }

    public String getelementType() {
        return this.elementType;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    /*
    @Method: format
    @Purpose: To format the object locators
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 01/11/2021
    */
    public ElementBuilder format(final Object... substitutions) {
        String elementValue = this.elementValue;
        final Pattern typePattern = Pattern.compile("[=,]''[^\\]]*''");
        final Matcher typeMatcher = typePattern.matcher(elementValue);
        if (typeMatcher.find()) {
            elementValue = MessageFormat.format(elementValue, substitutions);
        }
        else {
            Pattern pattern = Pattern.compile("([{][0-9]+[}])");
            Matcher matcher = pattern.matcher(elementValue);
            int count = 0;
            while (matcher.find()) {
                ++count;
            }
            for (int i = 0; i < count; ++i) {
                pattern = Pattern.compile("([{]" + i + "[}])");
                matcher = pattern.matcher(elementValue);
                elementValue = matcher.replaceAll(substitutions[i].toString());
            }
        }
        return this.fixXpath(elementValue);
    }

    /*
    @Method: format
    @Purpose: To format the string locators
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 01/11/2021
    */
    public ElementBuilder format(final String substitution) {

        String elementValue = this.elementValue;
        final Pattern typePattern = Pattern.compile("[=,]''[^\\]]*''");
        final Matcher typeMatcher = typePattern.matcher(elementValue);
        if (typeMatcher.find()) {
            elementValue = MessageFormat.format(elementValue, substitution);
        }
        else {

            Pattern pattern = Pattern.compile("([{][0-9]+[}])");
            Matcher matcher = pattern.matcher(elementValue);
            if (matcher.find()) {
                pattern = Pattern.compile("([{]0[}])");
                matcher = pattern.matcher(elementValue);
                elementValue = matcher.replaceAll(substitution.toString());

            }
        }
        return this.fixXpath(elementValue);
    }

    /*
    @Method: fixXpath
    @Purpose: To format the xpath for web elements
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 01/11/2021
    */
    private ElementBuilder fixXpath(String elementValue) {
        Pattern replacePattern = Pattern.compile("[=,]'[^']*(['][\\w\\s!@#$%^&*-;:.\342\u201E\242/]*)+'");
        final Matcher replaceMatcher = replacePattern.matcher(elementValue);
        while (replaceMatcher.find()) {
            String matchValue = replaceMatcher.group();
            matchValue = matchValue.replace("='", "=\"");
            matchValue = matchValue.replace(",'", ",\"");
            matchValue = matchValue.substring(0, matchValue.length() - 1) + "\"";
            elementValue = elementValue.replace(replaceMatcher.group(), matchValue);
        }
        return new ElementBuilder(this.elementName, elementValue, this.elementType);
    }

    /*
    @Method: replace
    @Purpose: To replace the strings in locator
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 01/11/2021
    */
    public ElementBuilder replace(final String findString, final String replaceString) {
        return new ElementBuilder(this.elementName, this.elementValue.replaceFirst(findString, replaceString), this.elementType);
    }

    /*
    @Method: getByObject
    @Purpose: To locate the element based on identifiers
    @Author: Mibin Boban, CCS Test Analyst
    @Creation: 01/11/2021
    */
    public static By getByObject(ElementBuilder elementToReturn){
        String elementType= elementToReturn.getelementType();
        String locatorValue = elementToReturn.getelementValue();
        By by=null;
        switch(elementType.toLowerCase()){
            case "xpath"		:		by=By.xpath(locatorValue);
                break;
            case "id"				:		by=By.id(locatorValue);
                break;
            case "class"			:		by=By.className(locatorValue);
                break;
            case "css"			:		by = By.cssSelector(locatorValue);
                break;

            case "name"		:		by=By.name(locatorValue);
                break;

            case "linktext"		:		by=By.linkText(locatorValue);
                break;

            case "partiallink":		by=By.partialLinkText(locatorValue);
                break;

            case "tagname"	:		by=By.tagName(locatorValue);
                break;
        }
        return by;
    }

}
