import java.util.*;

class Oppgave2 {
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		System.out.println("Prissammenligning:");
		System.out.println("---");

		System.out.println("Skriv inn vekt til kjøttdeig A i gram:");
		float aVekt = in.nextFloat();
		System.out.println("Skriv inn pris til kjøttdeig A i kroner:");
		float aPris = in.nextFloat();

		float aKilopris = (aPris / aVekt) * 1000;

		System.out.println("Skriv inn vekt til kjøttdeig B i gram:");
		float bVekt = in.nextFloat();
		System.out.println("Skriv inn pris til kjøttdeig B i kroner:");
		float bPris = in.nextFloat();

		float bKilopris = (bPris / bVekt) * 1000;

		if (aKilopris < bKilopris) {
			System.out.println("Kjøttdeig A er billigst");
		} else {
			System.out.println("Kjøttdeig B er billigst");
		}
		System.out.println("Kiloprisen til kjøttdeig A er " + aKilopris + "kr/kg");
		System.out.println("Kiloprisen til kjøttdeig B er " + bKilopris + "kr/kg");
	}
}
