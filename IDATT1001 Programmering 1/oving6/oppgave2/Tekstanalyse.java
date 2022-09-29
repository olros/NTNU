import java.util.*;

class Tekstanalyse {
  private static int[] antallTegn;
  private static String[] bokstaver = new String[]{"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","æ","ø","å"};

  public Tekstanalyse(String input) {
    antallTegn = new int[30];
    for (int a = 0; a < 30; a++) {
      antallTegn[a] = 0;
    }
    // Gjør om til kun små bokstaver
    input = input.toLowerCase();
    int lengde = input.length();
    for (int i = 0; i < lengde; i++) {
      char indexChar = input.charAt(i);
      String bokstav = Character.toString((char)indexChar);
      int index = Arrays.asList(bokstaver).indexOf(bokstav);
      if (index == -1) {
        index = 29;
      }
      antallTegn[index] += 1;
    }
  }

  public static int getAntallForskjellige() {
    int sum = 0;
    for (int i = 0; i < 29; i++) {
      if (antallTegn[i] > 0) {
        sum++;
      }
    }
    return sum;
  }
  public static int getAntallBokstaver() {
    int sum = 0;
    for (int i = 0; i < 29; i++) {
      sum = antallTegn[i] + sum;
    }
    return sum;
  }
  public static double getProsentIkkeBokstaver() {
    int antallBokstaver = getAntallBokstaver();
    int antallAndre = antallTegn[29];
    double totalt = antallBokstaver + antallAndre;
    double prosent = (antallAndre / totalt) * 100;
    // Rund av
    prosent = Math.round(prosent * 100.0) / 100.0;
    return prosent;
  }
  public static int getAntallSpesifikkBokstav(String bokstav) {
    int index = Arrays.asList(bokstaver).indexOf(bokstav);
    int sum = 0;
    if (index > -1) {
      sum = antallTegn[index];
    }
    return sum;
  }
  public static String getFlestForekomster() {
    int hoyest = -1;
    String hoyestBokstav = "";
    for ( int i = 0; i < 29; i++ ) {
      if (antallTegn[i] > hoyest) {
        hoyest = antallTegn[i];
        hoyestBokstav = bokstaver[i];
      } else if (antallTegn[i] == hoyest) {
        hoyestBokstav = hoyestBokstav + ", " + bokstaver[i];
      }
    }
    return "Bokstaven(e) som forekommer oftest er \"" + hoyestBokstav + "\" (" + hoyest + " ganger)";
  }
}
