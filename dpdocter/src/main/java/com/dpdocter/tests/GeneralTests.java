package com.dpdocter.tests;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

import common.util.web.DPDoctorUtils;


public class GeneralTests {

		public static void main(String[] args) throws UnsupportedEncodingException{

			int scale = 100;
			BigDecimal num1 = new BigDecimal(9200);
			BigDecimal num2 = new BigDecimal(1000*1000);
			System.out.println(num1.divide(num2).doubleValue());
			
			System.out.println(DPDoctorUtils.getSHA3SecurePassword("neha".toCharArray()));
		}
}
