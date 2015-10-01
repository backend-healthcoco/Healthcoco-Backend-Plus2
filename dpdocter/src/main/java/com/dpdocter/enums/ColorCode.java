package com.dpdocter.enums;

import java.util.Random;

public enum ColorCode {
	A("#099688"),B("#3F51B5"),C("#2196F3"),D("#4CAF50"),E("#9E9E9E"),F("#9C27B0");
	
	private String color;
	
    public String getColor() {return color;}

    private ColorCode(String color) {this.color = color;}

    public static class RandomEnum<E extends Enum> {

        private static final Random RND = new Random();
        private final E[] values;

        public RandomEnum(Class<E> token) {
            values = token.getEnumConstants();
        }

        public E random() {
            return values[RND.nextInt(values.length)];
        }
    }
}
