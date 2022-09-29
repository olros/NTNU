package Oblig5b_design;

import java.time.LocalDate;

public interface ScandinavianWildAnimal {
	String getName();

	LocalDate getDateOfBirth();

	int getAge();

	String getAddress();

	void move(String newAddress);

	String printInfo();
}