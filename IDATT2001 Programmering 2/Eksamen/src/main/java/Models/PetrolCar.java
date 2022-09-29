package Models;

/**
 * The type Petrol car.
 */
public class PetrolCar extends Vehicle {
	private static final double rushhourCost = 21.0;
	private static final double nonRushhourCost = 17.0;

	/**
	 * Instantiates a new Petrol car.
	 *
	 * @param licensePlateNumber the licence plate number
	 * @param maxWeight          the max weight
	 */
	public PetrolCar(String licensePlateNumber, int maxWeight) {
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