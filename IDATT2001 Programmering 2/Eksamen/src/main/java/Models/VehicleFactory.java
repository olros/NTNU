package Models;

/**
 * Vehicle factory.
 */
public class VehicleFactory {
	private static VehicleFactory instance;

	// Singleton
	private VehicleFactory() {
	}

	/**
	 * Gets instance.
	 *
	 * @return the instance
	 */
	public static VehicleFactory getInstance() {
		if (instance == null) {
			instance = new VehicleFactory();
		}
		return instance;
	}

	private void checkValidInput(String licensePlateNumber, int maxWeight) {
		if (licensePlateNumber.length() != 7) {
			throw new IllegalArgumentException("The licence plate number must be 7 characters long");
		}
		if (maxWeight < 0) {
			throw new IllegalArgumentException("The max weight cannot be negative");
		}
	}

	/**
	 * New diesel car vehicle.
	 *
	 * @param licensePlateNumber the licence plate number
	 * @param maxWeight          the max weight
	 * @return the vehicle
	 */
	public Vehicle newDieselCar(String licensePlateNumber, int maxWeight) {
		checkValidInput(licensePlateNumber, maxWeight);
		return new DieselCar(licensePlateNumber, maxWeight);
	}

	/**
	 * New electrical car vehicle.
	 *
	 * @param licensePlateNumber the licence plate number
	 * @param maxWeight          the max weight
	 * @return the vehicle
	 */
	public Vehicle newElectricalCar(String licensePlateNumber, int maxWeight) {
		checkValidInput(licensePlateNumber, maxWeight);
		return new ElectricalCar(licensePlateNumber, maxWeight);
	}

	/**
	 * New petrol car vehicle.
	 *
	 * @param licensePlateNumber the licence plate number
	 * @param maxWeight          the max weight
	 * @return the vehicle
	 */
	public Vehicle newPetrolCar(String licensePlateNumber, int maxWeight) {
		checkValidInput(licensePlateNumber, maxWeight);
		return new PetrolCar(licensePlateNumber, maxWeight);
	}

	/**
	 * New truck vehicle.
	 *
	 * @param licensePlateNumber the licence plate number
	 * @param maxWeight          the max weight
	 * @return the vehicle
	 */
	public Vehicle newTruck(String licensePlateNumber, int maxWeight) {
		checkValidInput(licensePlateNumber, maxWeight);
		return new Truck(licensePlateNumber, maxWeight);
	}

	/**
	 * New motorcycle vehicle.
	 *
	 * @param licensePlateNumber the licence plate number
	 * @param maxWeight          the max weight
	 * @return the vehicle
	 */
	public Vehicle newMotorcycle(String licensePlateNumber, int maxWeight) {
		checkValidInput(licensePlateNumber, maxWeight);
		return new Motorcycle(licensePlateNumber, maxWeight);
	}
}
