import java.util.*;

public final class Matrise {

  private final double[][] m;
  private static Scanner in = new Scanner(System.in);

  private Matrise(double[][] n) {
    m = n;
  }

  public static Matrise fyllMatrise(double[][] n) {
    return new Matrise(n);
  }
  public double[][] getMatrise() {
    return m;
  }


  public static Matrise addereMatrise(Matrise matrise1, Matrise matrise2) {
    int rader = matrise1.getAntallRader();
    int kolonner = matrise1.getAntallKolonner();
    double[][] n = new double[rader][kolonner];
    for (int i = 0; i < rader; i++) {
      for (int j = 0; j < kolonner; j++) {
        n[i][j] = matrise1.m[i][j] + matrise2.m[i][j];
      }
    }
    return new Matrise(n);
  }
  public static Matrise multiplisereMatrise(Matrise a, Matrise b) {
    int aRader = a.getMatrise().length;
    int aKolonner = a.getMatrise()[0].length;
    int bRader = b.getMatrise().length;
    int bKolonner = b.getMatrise()[0].length;
    if (aKolonner == bRader) {
      double[][] n = new double[aRader][bKolonner];
      for (int i = 0; i < bKolonner; i++) {
        for (int j = 0; j < aRader; j++) {
          for (int k = 0; k < aKolonner; k++) {
            n[j][i] = n[j][i] + a.getMatrise()[j][k] * b.getMatrise()[k][i];
          }
        }
      }
      return new Matrise(n);
    } else {
      return null;
    }
  }
  public static Matrise transponereMatrise(Matrise a) {
    int rader = a.getMatrise().length;
    int kolonner = a.getMatrise()[0].length;
    double[][] n = new double[kolonner][rader];
    for (int i = 0; i < rader; i++) {
      for (int j = 0; j < kolonner; j++) {
        n[j][i] = a.getMatrise()[i][j];
      }
    }
    return new Matrise(n);
  }

  public int getAntallRader() {
    int rader = m.length;
    return rader;
  }
  public int getAntallKolonner() {
    int kolonner = m[0].length;
    return kolonner;
  }
}
