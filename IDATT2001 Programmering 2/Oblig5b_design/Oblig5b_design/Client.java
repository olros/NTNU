package Oblig5b_design;

import java.time.LocalDate;
import java.util.logging.Level;

public class Client {
	public static void main(String[] args) {
		try {
			ScandinavianWildAnimal ulla = WildAnimalFactorySingleton.getInstance().newFemaleWolf("Ulla", LocalDate.of(2015, 2, 26), LocalDate.of(2015, 4, 29), "Innhegning 2, Skandinaviske rovdyr", 0);
			System.out.println(ulla.printInfo());
			ScandinavianWildAnimal erik = WildAnimalFactorySingleton.getInstance().newMaleBear("Erik", LocalDate.of(2015, 2, 26), LocalDate.of(2015, 4, 29), "Innhegning 2, Skandinaviske rovdyr");
			System.out.println(erik.printInfo());
		} catch (Exception e) {
			ZooLogger.getLogger().log(Level.FINE, e.getMessage());
		}
	}
}

