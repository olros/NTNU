package Oblig5b_design;

import java.time.LocalDate;

public class WildAnimalFactory {
	public static ScandinavianWildAnimal newMaleBear(String name, LocalDate birthDate, LocalDate arrivalDate, String address) {
		return new MaleIndividual("Brunbjørn", "Ursus arctos", "Ursidae", arrivalDate, name, birthDate, true, address);
	}

	public static ScandinavianWildAnimal newFemaleWolf(String name, LocalDate birthDate, LocalDate arrivalDate, String address, int noLitters) {
		return new FemaleIndividual("Ulv", "Canis lupus", "Canidae", arrivalDate, name, birthDate, true, address, noLitters);
	}

	public static ScandinavianWildAnimal newMaleWolf(String name, LocalDate birthDate, LocalDate arrivalDate, String address) {
		return new MaleIndividual("Ulv", "Canis lupus", "Canidae", arrivalDate, name, birthDate, true, address);
	}
}
