package com.saladbar.houseoftoss;

import java.util.ArrayList;

public class Salad{
	public static final String ITEM_SEP = System.getProperty("line.separator");
	private ArrayList<String> toppings;
	
	public Salad(){
		this.toppings = new ArrayList<String>();
	}
	
	public Salad(ArrayList<String> toppings){
		this.toppings = toppings;
	}
	
	public ArrayList<String> getToppings(){
		return this.toppings;
	}
	
	public void toggleSaladTopping(String topping){
		if(this.toppings.contains(topping)){
			toppings.remove(topping);
		}else{
			toppings.add(topping);
		}
	}
	
	public boolean hasTopping(String topping){
		return this.toppings.contains(topping);
	}
	
	public String toString() {
        return "Salad";
	}
	
	public String toLog() {
		    return "order:1" + ITEM_SEP; 
	}
}
