import java.util.*;

class Oppgave1 {
	private static Valuta[] valuta = new Valuta[3];


	public static void main(String[] args) {
		valuta[0] = new Valuta(8.97f, "dollar");
		valuta[1] = new Valuta(9.95f, "euro");
		valuta[2] = new Valuta(0.92f, "svenske kroner");
    System.out.println("---Valutakalkulator---");

    visMeny();
	}

  public static void visMeny() {
    Scanner in = new Scanner(System.in);
    System.out.println("Tast 1 for dollar");
    System.out.println("Tast 2 for euro");
    System.out.println("Tast 3 for svenske kroner");
    System.out.println("Tast 4 for å avslutte");

    int menyInput = in.nextInt();
    if (menyInput == 4) {
      System.out.println("Avslutter...");
      System.exit(0);
    } else if (menyInput != 1 && menyInput != 2 && menyInput != 3) {
      System.out.println("Du må taste inn et tall mellom 1 og 4");
      visMeny();
      return;
    } else {
	    kalkuler(menyInput);
		}
  }

  public static void kalkuler(int valutaId) {

    Scanner in = new Scanner(System.in);

    System.out.println("Hvor mye?");
    float antall = in.nextFloat();

    System.out.println(Valuta.regnUt(antall, valuta[(valutaId - 1)]));

		System.out.println("");
		System.out.println("");
		System.out.println("---Valutakalkulator---");
		visMeny();
  }
}
