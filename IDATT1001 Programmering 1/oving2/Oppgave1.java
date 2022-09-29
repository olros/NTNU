import java.util.*;

class Oppgave1 {
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		int aar = in.nextInt();
		boolean skuddaar;

		// Test først om det er et hundreår, så om delelig på fire
		if ((aar % 100) == 0) {
			if ((aar % 400) == 0) {
				skuddaar = true;
			} else {
				skuddaar = false;
			}
		} else if ((aar % 4) == 0) {
			skuddaar = true;
		} else {
			skuddaar = false;
		}

		// Skriv ut resultat
		if (skuddaar == true) {
			System.out.println(aar + " er et skuddår");
		} else {
			System.out.println(aar + " er ikke et skuddår");
		}
	}
}