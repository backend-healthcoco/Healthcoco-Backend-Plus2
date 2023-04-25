package com.dpdocter.enums;

import java.util.Random;

public enum ColorCode {
	A("#1ABC9C"), B("#2ECC71"), C("#FF4C83"), D("#9B59B6"), E("#34495E"), F("#F39C12"), G("#D35400"), H("#E74C3C");

	private String color;

	public String getColor() {
		return color;
	}

	private ColorCode(String color) {
		this.color = color;
	}

	public static class RandomEnum<E extends Enum<?>> {

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
