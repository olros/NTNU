import java.util.*;

class Oppgave1 {
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		int aar = in.nextInt();
		boolean skuddaar;

		// Test f�rst om det er et hundre�r, s� om delelig p� fire
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
			System.out.println(aar + " er et skudd�r");
		} else {
			System.out.println(aar + " er ikke et skudd�r");
		}
	}
}