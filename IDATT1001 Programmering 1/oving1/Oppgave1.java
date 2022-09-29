import java.util.*;

class Oppgave1 {
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		System.out.println("Skriv inn antall tommer:");
		double tommer = in.nextDouble();
		double cm = tommer * 2.54;

		System.out.println(tommer + " tommer er lik " + cm + " cm");
	}
}