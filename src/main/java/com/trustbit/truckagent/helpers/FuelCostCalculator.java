package com.trustbit.truckagent.helpers;

public class FuelCostCalculator {
	private static final double FUEL_PRICE = 2.023;
	private static final double NO_CARGO_COEFFICIENT = 0.75;
	
	public static double calculateFuelCost(double km, boolean hasCargoLoaded) {
		return km * 0.214 * FUEL_PRICE * (hasCargoLoaded ? 1 :NO_CARGO_COEFFICIENT);
	}
}
