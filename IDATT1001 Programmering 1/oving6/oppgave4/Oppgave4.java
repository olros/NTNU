import java.util.*;

class Oppgave4 {

  private static Scanner in = new Scanner(System.in);
  private static Matrise[] matrise = new Matrise[2];

  public static void main(String[] args) {
    while (true) {
      System.out.println("Lag den første matrisen:");
      matrise[0] = Matrise.fyllMatrise(lagMatrise());
      System.out.println("Matrise nr1:");
      skrivUtMatrise(matrise[0]);
      visMeny();
    }
  }

  public static void visMeny() {
    System.out.println("Tast 1 for å addere en matrise til denne matrisen");
    System.out.println("Tast 2 for å multiplisere denne matrisen med en annen matrise");
    System.out.println("Tast 3 for å transponere denne matrisen");

    int menyInput = in.nextInt();
    switch (menyInput) {
      case 1: matrise[1] = Matrise.fyllMatrise(lagMatrise(matrise[0].getAntallRader(), matrise[0].getAntallKolonner()));
              System.out.println("Matrise nr. 2:");
              skrivUtMatrise(matrise[1]);
              Matrise a = Matrise.addereMatrise(matrise[0], matrise[1]);
              System.out.println("Resultat:");
              skrivUtMatrise(a);
              break;
      case 2: System.out.println("Lag matrise nr. 2 (antall rader må være lik " + matrise[0].getAntallKolonner() + "):");
              matrise[1] = Matrise.fyllMatrise(lagMatrise());
              System.out.println("Matrise nr. 2:");
              skrivUtMatrise(matrise[1]);
              Matrise b = Matrise.multiplisereMatrise(matrise[0], matrise[1]);
              if (b != null) {
                System.out.println("Resultat:");
                skrivUtMatrise(b);
              } else {
                System.out.println("Noe gikk galt, antall kolonner i matrise 1 må være lik antall rader i matrise 2");
              }
              break;
      case 3: Matrise c = Matrise.transponereMatrise(matrise[0]);
              System.out.println("Resultat:");
              skrivUtMatrise(c);
              break;
      default:System.out.println("Du må taste inn et tall mellom 1 og 3");
              visMeny();
              break;
    }
  }

  // Ny matrise
  public static double[][] lagMatrise() {
    System.out.print("Skriv inn antall rader: "); int rader = in.nextInt();
    System.out.print("Skriv inn antall kolonner: "); int kolonner = in.nextInt();
    double[][] m = new double[rader][kolonner];

    System.out.println("Fyll inn matrisen: ");
    for(int i = 0; i < rader; i++) {
      for(int j = 0; j < kolonner; j++) {
        System.out.print("[" + i + "]" + "[" + j + "]: ");
        m[i][j] = in.nextDouble();
      }
    }
    return m;
  }
  // Ny matrise med bestemt antall rader og kolonner
  public static double[][] lagMatrise(int rader, int kolonner) {
    double[][] m = new double[rader][kolonner];

    System.out.println("Fyll inn matrisen: ");
    for(int i = 0; i < rader; i++) {
      for(int j = 0; j < kolonner; j++) {
        System.out.print("[" + i + "]" + "[" + j + "]: ");
        m[i][j] = in.nextDouble();
      }
    }
    return m;
  }

  // Skriv ut matrise
  public static void skrivUtMatrise(Matrise m) {
    int rader = m.getAntallRader();
    int kolonner = m.getAntallKolonner();
    double[][] t = m.getMatrise();
    for (int i = 0; i < rader; i++) {
      String rad = "";
      for (int j = 0; j < kolonner; j++) {
        rad += t[i][j] + " ";
      }
      System.out.println(rad);
    }
  }

}
