package Oblig5b_design;

import java.time.LocalDate;

/**
 * Represents a male individual.
 */
class MaleIndividual extends Individual {

	/**
	 * Creates an instance of MaleIndividual.
	 *
	 * @param norName     norwegian name of the animal
	 * @param latName     the animal name in latin
	 * @param latFamily   the family in latin
	 * @param arrivalDate date of arrival to the Zoo
	 * @param address     the address of the animal
	 * @param name        the name of the individual
	 * @param dateOfBirth date of birth
	 * @param isDangerous <code>true</code> if dangerous for visitors
	 */
	public MaleIndividual(String norName,
	                      String latName,
	                      String latFamily,
	                      LocalDate arrivalDate,
	                      String name,
	                      LocalDate dateOfBirth,
	                      boolean isDangerous,
	                      String address) {

		super(norName, latName, latFamily, arrivalDate, name, dateOfBirth, isDangerous, address);
	}
}
