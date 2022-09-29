import java.util.*;

class Oppgave3 {

  private static Scanner in = new Scanner(System.in);

  public static void main(String[] args) {
    visMeny();
  }

  public static void visMeny() {
    System.out.println("Tast 1 for å finne middeltemperaturen for hver dag i måneden");
    System.out.println("Tast 2 for å finne middeltemperaturen for hver time i døgnet i måneden");
    System.out.println("Tast 3 for å finne middeltemperaturen for hele måneden");
    System.out.println("Tast 4 for å finne antall døgn med middeltemperatur innen visse grupper");

    int menyInput = in.nextInt();
    switch (menyInput) {
      case 1: tempDag();
              break;
      case 2: tempTime();
              break;
      case 3: tempMonth();
              break;
      case 4: tempAntall();
              break;
      default:System.out.println("Du må taste inn et tall mellom 1 og 4");
              visMeny();
              break;
    }
  }

  public static void tempDag() {
    int antallDager = Temperaturer.getAlt().length;
    System.out.println("Velg dag du vil se middeltemperatur for. Tast et tall mellom 1 og " + antallDager + ":");
    int input = in.nextInt();
    if (0 < input && input <= antallDager) {
      double[] middelTempDag = Temperaturer.getMiddelTempDag();
      System.out.println("Middeltemperaturen dag " + input + " er lik: " + middelTempDag[(input - 1)]);
      System.out.println("-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-");
      visMeny();
    } else {
      System.out.println("Du må skrive inn en gyldig dag, prøv på nytt:");
      tempDag();
    }
  }
  public static void tempTime() {
    int antallTimer = 24;
    System.out.println("Velg time du vil se middeltemperatur for. Tast et tall mellom 0 og 23:");
    int input = in.nextInt();
    if (0 <= input && input <= 23) {
      double[] middelTempTime = Temperaturer.getMiddelTempTime();
      System.out.println("Middeltemperaturen time " + input + " er lik: " + middelTempTime[input]);
      System.out.println("-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-");
      visMeny();
    } else {
      System.out.println("Du må skrive inn en gyldig dag, prøv på nytt:");
      tempTime();
    }
  }
  public static void tempMonth() {
    double middelTempMonth = Temperaturer.getMiddelTempMonth();
    System.out.println("Middeltemperaturen for hele måneden er lik: " + middelTempMonth);
    System.out.println("-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-");
    visMeny();
  }
  public static void tempAntall() {
    int[] antallDagTemp = Temperaturer.getAntallDagTemp();
    System.out.println("Antall døgn med middeltemperatur mindre enn -5: " + antallDagTemp[0]);
    System.out.println("Antall døgn med middeltemperatur mellom -5 og 0: " + antallDagTemp[1]);
    System.out.println("Antall døgn med middeltemperatur mellom 0 og 5: " + antallDagTemp[2]);
    System.out.println("Antall døgn med middeltemperatur mellom 5 og 10: " + antallDagTemp[3]);
    System.out.println("Antall døgn med middeltemperatur over 10: " + antallDagTemp[4]);
    System.out.println("-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-");
    visMeny();
  }
}
