package Models;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The type Vehicle register.
 */
public class VehicleRegister {
	// Static instance to keep it.
	private static VehicleRegister instance;
	public ArrayList<Vehicle> register;

	/**
	 * Instantiates a new Vehicle register. Private to hinder other classes to create an instance of this class.
	 */
	private VehicleRegister() {
		this.register = new ArrayList<>();
	}

	/**
	 * By having to use a "getInstance", the users will always get the same instance and this makes sure there will be max one instance of this class.
	 *
	 * @return the vehicle register
	 */
	public static VehicleRegister getInstance() {
		if (instance == null) {
			instance = new VehicleRegister();
		}
		return instance;
	}

	/**
	 * Register new vehicle boolean.
	 *
	 * @param vehicle the vehicle
	 * @return the boolean
	 */
	public boolean registerNewVehicle(Vehicle vehicle) {
		if (vehicle == null) {
			throw new IllegalArgumentException("Veichle cannot be null");
		}
		register.add(vehicle);
		return true;
	}

	/**
	 * Search by licence plate number.
	 *
	 * @param licensePlate the licence plate
	 * @return the vehicle
	 */
	public Vehicle searchBylicensePlate(String licensePlate) {
		Optional<Vehicle> vehicle = register.stream().filter(c -> c.getLicensePlateNumber().equals(licensePlate)).findFirst();;
		if (vehicle.isPresent()) {
			return vehicle.get();
		}
		return null;
	}

	/**
	 * Search by weight more then given.
	 *
	 * @param weight the weight
	 * @return the array list
	 */
	public ArrayList<Vehicle> searchByWeightMoreThen(int weight) {
		return register.stream().filter(c -> c.getMaxWeight() > weight).collect(Collectors.toCollection(ArrayList::new));
	}
}
