package Models;

/**
 * The type Electrical car.
 */
public class ElectricalCar extends Vehicle {
	private static final double rushhourCost = 8.0;
	private static final double nonRushhourCost = 4.0;

	/**
	 * Instantiates a new Electrical car.
	 *
	 * @param licensePlateNumber the licence plate number
	 * @param maxWeight          the max weight
	 */
	public ElectricalCar(String licensePlateNumber, int maxWeight) {
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