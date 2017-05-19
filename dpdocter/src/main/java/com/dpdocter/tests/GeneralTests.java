package com.dpdocter.tests;

public class GeneralTests {

	public static void main(String args[]) {
		int[] a = { 1, 5, 4, 3, 2 };
		double i = 80;
		

		if (i > 1139) {
			i = i - 1440;
		}
		

		if (i >= 0 && i < 720) {
			System.out.println(String.format("%.2f", i / 60).replace('.', ':') + " AM");
		}
		if (i >= 720 && i < 1440) {
			System.out.println(String.format("%.2f", i / 60).replace('.', ':') + " PM");
		}

	}
}
