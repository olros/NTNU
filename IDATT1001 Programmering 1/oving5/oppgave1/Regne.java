import java.util.*;

public class Regne {
  private double teller, nevner;
  private static double resultat;

  public Regne(double teller, double nevner) {
    if (nevner == 0) {
      throw new IllegalArgumentException("Nevneren kan ikke være lik 0");
    } else {
      this.teller = teller;
      this.nevner = nevner;
    }
  }
  public Regne(double teller) {
    this.teller = teller;
    this.nevner = 1;
  }

  public static void addere(Regne a, Regne b) {
    resultat = (a.teller / a.nevner) + (b.teller / b.nevner);
  }
  public static void subtrahere(Regne a, Regne b) {
    resultat = (a.teller / a.nevner) - (b.teller / b.nevner);
  }
  public static void multiplisere(Regne a, Regne b) {
    resultat = (a.teller / a.nevner) * (b.teller / b.nevner);
  }
  public static void dividere(Regne a, Regne b) {
    resultat = (a.teller / a.nevner) / (b.teller / b.nevner);
  }

  public static double getResultat() {
    return resultat;
  }
}
