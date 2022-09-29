import java.util.*;

class NyString {

  private final String tekst;
  private static Scanner in = new Scanner(System.in);

  public NyString (String ord) {
    this.tekst = ord;
  }

  private String forkort() {
    String nyTekst = "";
    String original = this.tekst;
    String[] ord = original.split(" ");
    for (int i = 0; i < ord.length; i++) {
      nyTekst += ord[i].charAt(0);
    }
    return nyTekst;
  }
  private String fjern(char t) {
    String original = this.tekst;
    String nyTekst = original;
    while (nyTekst.indexOf(t) > -1) {
      nyTekst = nyTekst.substring(0, nyTekst.indexOf(t)) +  nyTekst.substring(nyTekst.indexOf(t) + 1);
    }
    return nyTekst;
  }

  public static void main(String[] args) {
    while (true) {
      visMeny();
    }
  }

  public static void visMeny() {
    System.out.println("Skriv inn tekst:");
    String input = in.nextLine();
    NyString a = new NyString(input);
    System.out.println("Tast 1 for å forkorte teksten");
    System.out.println("Tast 2 for å fjerne en bestemt bokstav");

    int menyInput = in.nextInt();
    switch (menyInput) {
      case 1: System.out.println(a.forkort());
              break;
      case 2: System.out.println("Skriv inn bokstaven som skal fjernes:");
              char bokstav = in.next().charAt(0);
              System.out.println(a.fjern(bokstav));
              break;
      default:System.out.println("Du må skrive enten 1 eller 2");
              visMeny();
              break;
    }
  }
}
