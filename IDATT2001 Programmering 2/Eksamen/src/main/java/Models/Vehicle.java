package Models;

/**
 * Vehicle.
 */
public abstract class Vehicle implements Payable {
	private final String licensePlateNumber;
	private final int maxWeight;

	/**
	 * Instantiates a new Vehicle.
	 *
	 * @param licensePlateNumber the licence plate number
	 * @param maxWeight          the max weight
	 */
	public Vehicle(String licensePlateNumber, int maxWeight) {
		this.licensePlateNumber = licensePlateNumber;
		this.maxWeight = maxWeight;
	}

	/**
	 * Gets licence plate number.
	 *
	 * @return the licence plate number
	 */
	@Override
	public String getLicensePlateNumber() {
		return this.licensePlateNumber;
	}

	/**
	 * Gets max weight.
	 *
	 * @return the max weight
	 */
	public int getMaxWeight() {
		return this.maxWeight;
	}

	/**
	 * Gets toll passage cost.
	 *
	 * @param rushhour if it is rushhour
	 * @return the toll passage cost
	 */
	@Override
	public abstract double getCost(boolean rushhour);
}