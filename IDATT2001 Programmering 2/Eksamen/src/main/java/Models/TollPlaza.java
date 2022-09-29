package Models;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Toll plaza.
 */
public class TollPlaza {
	private final int id;
	private final String name;

	/**
	 * Instantiates a new Toll plaza.
	 *
	 * @param id   the id
	 * @param name the name
	 */
	public TollPlaza(int id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * Gets id.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Register toll passage boolean.
	 *
	 * @param licencePlateNumber the licence plate number
	 * @param date               the date
	 * @param time               the time
	 * @return if registration was successful
	 */
	public boolean registerTollPassage(String licencePlateNumber, LocalDate date, LocalTime time) {
		Vehicle vehicle = VehicleRegister.getInstance().searchBylicensePlate(licencePlateNumber);
		if (vehicle != null) {
			TollPassageRegister.getInstance().addPassage(this.id, vehicle, time, date);
			return true;
		}
		return false;
	}
}
