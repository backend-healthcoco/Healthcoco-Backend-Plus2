package com.dpdocter.test;


import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import common.util.web.DPDoctorUtils;

public class DrugTypeTests {

	public static void main(String[] args) throws IOException {Calendar cal = Calendar.getInstance();
	 String shortUrl = DPDoctorUtils.urlShortner("https://d1ayyk8caoqrgh.cloudfront.net/rtpcr/finalcopy1617795732224.png");
	System.out.println(shortUrl);
	}
}
