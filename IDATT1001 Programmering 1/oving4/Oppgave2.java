import java.util.*;

class Oppgave2 {
	public static void main(String[] args) {
    System.out.println("---Terningspillet 100---");
    System.out.println("");

    starter();
	}
  public static void starter() {
    System.out.println("Tast 1 for å starte");
    Scanner in = new Scanner(System.in);
    int input = in.nextInt();

    if (input == 1) {
      startSpill();
    } else {
      starter();
    }
  }

  public static void startSpill() {
    Spiller[] spiller = new Spiller[2];
    spiller[0] = new Spiller(0);
    spiller[1] = new Spiller(0);

    int rundenummer = 0;

    while (!spiller[0].erFerdig(spiller[0].sumPoeng) && spiller[1].erFerdig(spiller[1].sumPoeng) == false) {
      spiller[0].sumPoeng = spiller[0].kastTerningen(spiller[0].sumPoeng);
      spiller[1].sumPoeng = spiller[1].kastTerningen(spiller[1].sumPoeng);
      System.out.println("Rundenr: " + rundenummer + ", spiller 1: " + spiller[0].sumPoeng + ", spiller 2: " + spiller[1].sumPoeng);
      rundenummer += 1;
    }

    System.out.println("---------------");
    System.out.println("-_-_-_-_-_-_-_-");
    if (spiller[0].erFerdig(spiller[0].sumPoeng) == true) {
      System.out.println("Spiller 1 vant!");
    } else {
      System.out.println("Spiller 2 vant!");
    }
    System.out.println("-_-_-_-_-_-_-_-");
    System.out.println("---------------");

    System.out.println("Tast 1 for å spille igjen");
    Scanner in = new Scanner(System.in);
    int input = in.nextInt();

    if (input == 1) {
      startSpill();
    } else {
      starter();
    }

  }
}
