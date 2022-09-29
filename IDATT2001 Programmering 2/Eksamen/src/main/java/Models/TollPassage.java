package Models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Toll passage.
 */
@Entity
public class TollPassage {
	@Id @GeneratedValue(strategy= GenerationType.IDENTITY)
	private int id;
	private int tollCollectionPointId;
	private String licensePlateNumber;
	private double cost;
	private LocalTime time;
	private LocalDate date;

	/**
	 * Instantiates a new Toll passage.
	 */
	public TollPassage() {}

	/**
	 * Instantiates a new Toll passage.
	 *
	 * @param tollCollectionPointId the toll collection point id
	 * @param licensePlateNumber    the license plate number
	 * @param cost                  the cost
	 * @param time                  the time
	 * @param date                  the date
	 */
	public TollPassage(int tollCollectionPointId, String licensePlateNumber, double cost, LocalTime time, LocalDate date) {
		this.tollCollectionPointId = tollCollectionPointId;
		this.licensePlateNumber = licensePlateNumber;
		this.cost = cost;
		this.time = time;
		this.date = date;
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
	 * Gets toll collection point id.
	 *
	 * @return the toll collection point id
	 */
	public int getTollCollectionPointId() {
		return this.tollCollectionPointId;
	}

	/**
	 * Gets license plate number.
	 *
	 * @return the license plate number
	 */
	public String getLicensePlateNumber() {
		return this.licensePlateNumber;
	}

	/**
	 * Gets cost.
	 *
	 * @return the cost
	 */
	public double getCost() {
		return this.cost;
	}

	/**
	 * Gets time.
	 *
	 * @return the time
	 */
	public LocalTime getTime() {
		return this.time;
	}

	/**
	 * Gets date.
	 *
	 * @return the date
	 */
	public LocalDate getDate() {
		return this.date;
	}

	/**
	 * Sets id.
	 *
	 * @param id the id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Sets toll collection point id.
	 *
	 * @param tollCollectionPointId the toll collection point id
	 */
	public void setTollCollectionPointId(int tollCollectionPointId) {
		this.tollCollectionPointId = tollCollectionPointId;
	}

	/**
	 * Sets license plate number.
	 *
	 * @param licensePlateNumber the license plate number
	 */
	public void setLicensePlateNumber(String licensePlateNumber) {
		this.licensePlateNumber = licensePlateNumber;
	}

	/**
	 * Sets cost.
	 *
	 * @param cost the cost
	 */
	public void setCost(double cost) {
		this.cost = cost;
	}

	/**
	 * Sets time.
	 *
	 * @param time the time
	 */
	public void setTime(LocalTime time) {
		this.time = time;
	}

	/**
	 * Sets date.
	 *
	 * @param date the date
	 */
	public void setDate(LocalDate date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "TollPassage: " +
				"tollCollectionPointId=" + tollCollectionPointId +
				", licensePlateNumber='" + licensePlateNumber + '\'' +
				", cost=" + cost +
				", time=" + time +
				", date=" + date;
	}
}
