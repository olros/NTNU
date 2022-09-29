package Models;

/**
 * The type Diesel car.
 */
public class DieselCar extends Vehicle {
	private static final double rushhourCost = 23.0;
	private static final double nonRushhourCost = 23.0;

	/**
	 * Instantiates a new Diesel car.
	 *
	 * @param licensePlateNumber the licence plate number
	 * @param maxWeight          the max weight
	 */
	public DieselCar(String licensePlateNumber, int maxWeight) {
		super(licensePlateNumber, maxWeight);
	}

	public double getCost(boolean rushhour) {
		if (rushhour) {
			return rushhourCost;
		} else {
			return nonRushhourCost;
		}
	}
}