/**
 * Copyright (C) 2021 Crown Commercial Service. All rights reserved This Test Automation Solution is the confidential
 * and proprietary information of Crown Commercial Service. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Crown Commercial Service.
 * Mentor: Anne Vaudrey-McVey, CCS Enterprise Test Manager
 * Author: Mibin Boban, CCS Senior QAT Analyst
 * Development period: Nov-Dec, 2021
 */
package org.ccs.apimaster.apifactory.utils;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

/*
@Purpose: This class generates random fixed length number (up to 19 digits)
@Author: Mibin Boban, CCS Senior QAT Analyst
@Creation: 15/12/2021
*/
public class FixedLengthRandomGenerator {

    private static final HashMap<Integer, FixedLengthRandomGenerator> GENERATOR_MAP = new HashMap<>();

    private long lowerBound;
    private long upperBound;


    private FixedLengthRandomGenerator(int length) {
        this.lowerBound = (long) Math.pow(10, (length - 1));
        this.upperBound = (long) Math.pow(10, length);
    }

    public String generateRandomNumber() {
        return String.valueOf(ThreadLocalRandom.current().nextLong(this.lowerBound, this.upperBound));
    }

    @Override
    public String toString() {
        return this.generateRandomNumber();
    }

    public static FixedLengthRandomGenerator getGenerator(int length) {
        if (length < 1 || length > 19) {
            throw new RuntimeException("length of the random number should be between (1-19)");
        }
        FixedLengthRandomGenerator buff = GENERATOR_MAP.get(length);
        if (buff == null) {
            buff = new FixedLengthRandomGenerator(length);
            GENERATOR_MAP.put(length, buff);
            return buff;
        } else {
            return buff;
        }
    }
}
