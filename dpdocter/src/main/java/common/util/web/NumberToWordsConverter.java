package common.util.web;

import java.text.DecimalFormat;

public class NumberToWordsConverter {
	   private static final String[] tensNames = {
	            "", " Ten", " Twenty", " Thirty", " Forty", " Fifty", " Sixty", " Seventy", " Eighty", " Ninety"
	    };

	    private static final String[] numNames = {
	            "", " One", " Two", " Three", " Four", " Five", " Six", " Seven", " Eight", " Nine", " Ten",
	            " Eleven", " Twelve", " Thirteen", " Fourteen", " Fifteen", " Sixteen", " Seventeen", " Eighteen", " Nineteen"
	    };

	    private static String convertLessThanOneThousand(int number) {
	        String current;

	        if (number % 100 < 20) {
	            current = numNames[number % 100];
	            number /= 100;
	        } else {
	            current = numNames[number % 10];
	            number /= 10;

	            current = tensNames[number % 10] + current;
	            number /= 10;
	        }
	        if (number == 0) return current.trim();
	        return numNames[number] + " Hundred" + current;
	    }

	    public static String convert(double number) {
	        if (number == 0) {
	            return "Zero";
	        }

	        // Split the number into integer and fractional parts
	        long dollars = (long) number; // Integer part (e.g., dollars)
	        long cents = Math.round((number - dollars) * 100); // Fractional part (e.g., cents)

	        String result = convertWholeNumber(dollars);

	        if (cents > 0) {
	            result += " and " + convertWholeNumber(cents) + " Cents";
	        }

	        return result + " Only";
	    }

	    private static String convertWholeNumber(long number) {
	        if (number == 0) {
	            return "Zero";
	        }

	        String snumber = Long.toString(number);

	        // Pad with "0"
	        String mask = "000000000000";
	        DecimalFormat df = new DecimalFormat(mask);
	        snumber = df.format(number);

	        // XXXnnnnnnnnn
	        int billions = Integer.parseInt(snumber.substring(0, 3));
	        // nnnXXXnnnnnn
	        int millions = Integer.parseInt(snumber.substring(3, 6));
	        // nnnnnnXXXnnn
	        int hundredThousands = Integer.parseInt(snumber.substring(6, 9));
	        // nnnnnnnnnXXX
	        int thousands = Integer.parseInt(snumber.substring(9, 12));

	        String tradBillions;
	        switch (billions) {
	            case 0:
	                tradBillions = "";
	                break;
	            case 1:
	                tradBillions = "One Billion ";
	                break;
	            default:
	                tradBillions = convertLessThanOneThousand(billions) + " Billion ";
	        }
	        String result = tradBillions;

	        String tradMillions;
	        switch (millions) {
	            case 0:
	                tradMillions = "";
	                break;
	            case 1:
	                tradMillions = "One Million ";
	                break;
	            default:
	                tradMillions = convertLessThanOneThousand(millions) + " Million ";
	        }
	        result = result + tradMillions;

	        String tradHundredThousands;
	        switch (hundredThousands) {
	            case 0:
	                tradHundredThousands = "";
	                break;
	            case 1:
	                tradHundredThousands = "One Thousand ";
	                break;
	            default:
	                tradHundredThousands = convertLessThanOneThousand(hundredThousands) + " Thousand ";
	        }
	        result = result + tradHundredThousands;

	        String tradThousand;
	        tradThousand = convertLessThanOneThousand(thousands);
	        result = result + tradThousand;

	        return result.replaceAll("^\\s+", "").replaceAll("\\b\\s{2,}\\b", " ").trim();
	    }
}
