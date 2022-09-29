import java.util.*;

class Oppgave3 {
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		System.out.println("Skriv inn antall sekunder:");
		//int inpSek = 3700;
		int inpSek = in.nextInt();

		int timer = inpSek / 3600;
		int minutter = (inpSek - (timer * 3600)) / 60;
		int sekunder = inpSek - (timer * 3600) - (minutter * 60);

		System.out.println(inpSek + " sekunder er lik " + timer + " timer, " + minutter + " minutter og " + sekunder + " sekunder");
	}
}