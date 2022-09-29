package Models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Toll passage register, singleton.
 */
public class TollPassageRegister {
	private static TollPassageRegister instance;
	private final ArrayList<TollPassage> passages;

	/**
	 * Instantiates a new Toll passage register.
	 */
	private TollPassageRegister() {
		this.passages = new ArrayList<>();
	}

	/**
	 * Gets instance.
	 *
	 * @return the instance
	 */
	public static TollPassageRegister getInstance() {
		if (instance == null) {
			instance = new TollPassageRegister();
		}
		return instance;
	}

	/**
	 * Gets all passages.
	 *
	 * @return the passages
	 */
	public List<TollPassage> getPassages() {
		return this.passages;
	}

	/**
	 * Gets passages by toll collection point id.
	 *
	 * @param id the toll collection point id
	 * @return the passages by toll collection point id
	 */
	public List<TollPassage> getPassagesByTollCollectionPointId(int id) {
		return this.passages.stream().filter(p -> p.getTollCollectionPointId() == id).collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 * Add passage.
	 *
	 * @param tollCollectionPointId the toll collection point id
	 * @param vehicle               the vehicle
	 * @param time                  the time
	 * @param date                  the date
	 * @return the boolean
	 */
	public boolean addPassage(int tollCollectionPointId, Vehicle vehicle, LocalTime time, LocalDate date) {
		if (vehicle == null || time == null || date == null || tollCollectionPointId < 0) {
			throw new IllegalArgumentException("One of the arguments are wrong");
		}
		boolean withinRushhour = false;
		if ((time.isAfter(LocalTime.of(6,30, 0)) && time.isBefore(LocalTime.of(6,59,0))) || (time.isAfter(LocalTime.of(14,30, 0)) && time.isBefore(LocalTime.of(16,29,0)))) {
			withinRushhour = true;
		}
		passages.add(new TollPassage(tollCollectionPointId, vehicle.getLicensePlateNumber(), vehicle.getCost(withinRushhour), time, date));
		return true;
	}
}
