package Models;

/**
 * The type Truck.
 */
public class Truck extends Vehicle {
	private static final double rushhourCost = 101.0;
	private static final double nonRushhourCost = 86.0;

	/**
	 * Instantiates a new Truck.
	 *
	 * @param licensePlateNumber the licence plate number
	 * @param maxWeight          the max weight
	 */
	public Truck(String licensePlateNumber, int maxWeight) {
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