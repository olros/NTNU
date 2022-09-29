import java.util.*;

class Oppgave2 {
	public static void main(String[] args) {
		check();
	}

	public static void check() {
		Scanner in = new Scanner(System.in);
		System.out.println("Skriv inn tall for å sjekke om det er et primtall:");
		int tall = in.nextInt();

		boolean primtall = true;

		for(int i = 2; i <= tall / 2; i++) {
			int temp = tall % i;
			if(temp == 0) {
				primtall = false;
				break;
			}
		}
		if (tall == 0 || tall == 1) {
			primtall = false;
		}

		if(primtall) {
			System.out.println(tall + " er et primtall");
		} else {
	   	System.out.println(tall + " er ikke et primtall");
		}

		System.out.println("---");
		check();
	}
}
