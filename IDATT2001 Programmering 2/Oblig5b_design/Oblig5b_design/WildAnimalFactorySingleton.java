package Oblig5b_design;

import java.time.LocalDate;

public class WildAnimalFactorySingleton {
	public static WildAnimalFactorySingleton instance = null;

	private WildAnimalFactorySingleton() {
	}

	// static method to create instance of Singleton class
	public static WildAnimalFactorySingleton getInstance() {
		if (instance == null) {
			instance = new WildAnimalFactorySingleton();
		}

		return instance;
	}

	public ScandinavianWildAnimal newMaleBear(String name, LocalDate birthDate, LocalDate arrivalDate, String address) {
		if (instance != null) {
			return new MaleIndividual("Brunbjørn", "Ursus arctos",
					"Ursidae", arrivalDate, name, birthDate, true, address);
		}
		return null;
	}

	public ScandinavianWildAnimal newFemaleWolf(String name, LocalDate birthDate, LocalDate arrivalDate, String address, int noLitters) {
		if (instance != null) {
			return new FemaleIndividual("Ulv", "Canis lupus", "Canidae", arrivalDate,
					name, birthDate, true, address, noLitters);
		}
		return null;
	}

	public ScandinavianWildAnimal newMaleWolf(String name, LocalDate birthDate, LocalDate arrivalDate, String address) {
		if (instance != null) {
			return new MaleIndividual("Ulv", "Canis lupus", "Canidae", arrivalDate,
					name, birthDate, true, address);
		}
		return null;
	}
}
