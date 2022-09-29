import java.util.*;

public class Oppgave1Ferdig {

  private static Random random = new Random();
  // Antall runder som skal kjøres
  private static int runder = 1000;
  // Antall tall som er med
  private static int antallTall = 10;
  private static int[] antall = new int[antallTall];

  public static void main(String[] args) {
    // Kjører løkke og legger til tilfeldige tall
    for (int i = 0; i < runder; i++) {
      int tall = random.nextInt(antallTall);
      antall[tall]++;
    }

    // Skriver ut resultat
    for (int i = 0; i < antallTall; i++) {
      int sum = antall[i];
      long stjerner = Math.round(sum / (runder / 100.0));
      String stjerne = "";
      for (long k = 0; k < stjerner; k++) {
        stjerne = stjerne + "*";
      }
      System.out.println("Tall " + i + ": " + sum + " " + stjerne);
    }
  }
}
