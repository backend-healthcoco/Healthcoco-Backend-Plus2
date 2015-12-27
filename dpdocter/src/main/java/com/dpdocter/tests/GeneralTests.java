package com.dpdocter.tests;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

public class GeneralTests {
    private static final double MIN_REQ = 0.80;

    public static void main(String[] args) throws IOException {
	String term = "isank";
	String query = "i";
	if (StringUtils.getJaroWinklerDistance(term, query) > MIN_REQ) {
	    System.out.println(StringUtils.getJaroWinklerDistance(term, query));
	    System.out.println("Good Match");
	} else {
	    System.out.println(StringUtils.getJaroWinklerDistance(term, query));
	    System.out.println("Bad Match");
	}

    }
}
