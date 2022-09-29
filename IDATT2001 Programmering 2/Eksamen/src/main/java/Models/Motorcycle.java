package Models;

/**
 * The type Motorcycle.
 */
public class Motorcycle extends Vehicle {
	private static final double rushhourCost = 0.0;
	private static final double nonRushhourCost = 0.0;

	/**
	 * Instantiates a new Motorcycle.
	 *
	 * @param licensePlateNumber the licence plate number
	 * @param maxWeight          the max weight
	 */
	public Motorcycle(String licensePlateNumber, int maxWeight) {
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