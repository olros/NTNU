import java.util.*;

class Oppgave2 {
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);

		System.out.println("Skriv inn antall timer:");
		double timer = in.nextDouble();

		System.out.println("Skriv inn antall minutter:");
		double minutter = in.nextDouble();

		System.out.println("Skriv inn antall sekunder:");
		double sekunder = in.nextDouble();

		double totSekunder = (timer * 3600) + (minutter * 60) + sekunder;

		System.out.println(timer + " timer, " + minutter + " minutter og " + sekunder + " sekunder er totalt " + totSekunder + " sekunder");
	}
}
