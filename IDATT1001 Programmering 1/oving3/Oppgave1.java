import java.util.*;

class Oppgave1 {
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		System.out.println("Utskrift av multiplikasjonstabellen:");
		System.out.println("---");

		System.out.println("Skriv inn starttall:");
		int startTall = in.nextInt();
		System.out.println("Skriv inn sluttall (må v�re h�yere enn starttall):");
		int sluttTall = in.nextInt();

		if (sluttTall < startTall) {
			System.out.println("Sluttallet m� v�re h�yere enn starttallet!");
			System.exit(0);
		}

		System.out.println("Skriv inn antall stykker som skal vises per tall:");
		int antallStykker = in.nextInt();

		for (int i = startTall; i <= sluttTall; i++) {
			System.out.println(i + "-gangen");
			for (int g = 1; g <= antallStykker; g++) {
				int sum = g * i;
				System.out.println(i + " x " + g + " = " + sum);
			}
		}
	}
}
