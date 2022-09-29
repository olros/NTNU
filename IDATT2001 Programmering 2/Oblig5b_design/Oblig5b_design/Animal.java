package Oblig5b_design;

import java.time.LocalDate;

/**
 * Represents an animal described by a norwegian name, a latin name, and the family
 * in latin.
 */
abstract class Animal {
	private final String norName;
	private final String latName;
	private final String latFamily;
	private final LocalDate arrivalDate;
	private String address;

	/**
	 * Creates an instance of the Animal.
	 *
	 * @param norName     norwegian name of the animal
	 * @param latName     the animal name in latin
	 * @param latFamily   the family in latin
	 * @param arrivalDate date of arrival to the Zoo
	 * @param address     the address of the animal
	 */
	public Animal(String norName,
	              String latName,
	              String latFamily,
	              LocalDate arrivalDate,
	              String address) {

		this.norName = norName;
		this.latName = latName;
		this.latFamily = latFamily;
		this.arrivalDate = arrivalDate;
		this.address = address;
	}


	/**
	 * Returns the address of the animal.
	 *
	 * @return the address of the animal.
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Sets the address to the animal.
	 *
	 * @param address the address to be set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * Returns the Norwegian name of the animal.
	 *
	 * @return the Norwegian name of the animal.
	 */
	public String getNorName() {
		return norName;
	}

	/**
	 * Returns the latin name of the animal.
	 *
	 * @return the latin name of the animal.
	 */
	public String getLatName() {
		return latName;
	}

	/**
	 * Returns the family of the animal in Latin.
	 *
	 * @return the family of the animal in Latin.
	 */
	public String getLatFamily() {
		return latFamily;
	}

	/**
	 * Returns the date when the animal arrived at the Zoo.
	 *
	 * @return the date when the animal arrived at the Zoo
	 */
	public LocalDate getArrivalDate() {
		return arrivalDate;
	}

	/**
	 * Returns a string representation of the animal.
	 *
	 * @return a string representation of the animal.
	 */
	public String toString() {
		return "Norsk navn: " + this.norName
				+ "\nLatinsk navn og familie: "
				+ this.latName + ", " + this.latFamily
				+ "\nAdresse: " + this.address
				+ "\nAnkomstdato: " + this.arrivalDate;
	}
}