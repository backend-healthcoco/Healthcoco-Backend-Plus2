package com.dpdocter.beans;

public class FoodPref {
	
	private boolean veg = false;
	private boolean grains = false;
	private boolean 	milk = false;
	private boolean 	egg = false;
	private boolean 	fish = false;
	private boolean 	meat = false;
	private boolean 	seaFood = false;
	private boolean 	honey = false;
	
	public boolean isVeg() {
		return veg;
	}

	public void setVeg(boolean veg) {
		this.veg = veg;
	}

	public boolean isGrains() {
		return grains;
	}

	public void setGrains(boolean grains) {
		this.grains = grains;
	}

	public boolean isMilk() {
		return milk;
	}

	public void setMilk(boolean milk) {
		this.milk = milk;
	}

	public boolean isEgg() {
		return egg;
	}

	public void setEgg(boolean egg) {
		this.egg = egg;
	}


	public boolean isFish() {
		return fish;
	}

	public void setFish(boolean fish) {
		this.fish = fish;
	}

	public boolean isMeat() {
		return meat;
	}

	public void setMeat(boolean meat) {
		this.meat = meat;
	}

	public boolean isSeaFood() {
		return seaFood;
	}

	public void setSeaFood(boolean seaFood) {
		this.seaFood = seaFood;
	}

	public boolean isHoney() {
		return honey;
	}

	public void setHoney(boolean honey) {
		this.honey = honey;
	}

	@Override
	public String toString() {
		return "FoodPreferences [veg=" + veg + ", grains=" + grains + ", milk=" + milk + ", egg=" + egg + ", fish="
				+ fish + ", meat=" + meat + ", seaFood=" + seaFood + ", honey=" + honey + "]";
	}

}
