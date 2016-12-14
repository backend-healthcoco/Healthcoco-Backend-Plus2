package com.dpdocter.tests;

import java.math.BigDecimal;

public class GeneralTests {

		public static void show(){System.out.println("Hello");}

		public static void main(String[] args){

			int scale = 100;
			BigDecimal num1 = new BigDecimal(9200);
			BigDecimal num2 = new BigDecimal(1000*1000);
			System.out.println(num1.divide(num2).doubleValue());
		}
}  	
