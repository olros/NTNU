import java.util.*;

public class Regne {
  private double teller, nevner, resTeller, resNevner;
  private String resOutput, resTotal;

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

  public void addere(Regne b) {
    double resTeller = (this.teller * b.nevner) + (b.teller * this.nevner);
    double resNevner = this.nevner * b.nevner;
    String resTotal = resTeller + "/" + resNevner;
    this.resTeller = resTeller;
    this.resNevner = resNevner;
    this.resTotal = resTotal;
    this.resOutput = "(" + this.teller + "/" + this.nevner + ") + (" + b.teller + "/" + b.nevner + ")";
  }
  public void subtrahere(Regne b) {
    double resTeller = (this.teller * b.nevner) - (b.teller * this.nevner);
    double resNevner = this.nevner * b.nevner;
    String resTotal = resTeller + "/" + resNevner;
    this.resTeller = resTeller;
    this.resNevner = resNevner;
    this.resTotal = resTotal;
    this.resOutput = "(" + this.teller + "/" + this.nevner + ") - (" + b.teller + "/" + b.nevner + ")";
  }
  public void multiplisere(Regne b) {
    double resTeller = this.teller * b.teller;
    double resNevner = this.nevner * b.nevner;
    String resTotal = resTeller + "/" + resNevner;
    this.resTeller = resTeller;
    this.resNevner = resNevner;
    this.resTotal = resTotal;
    this.resOutput = "(" + this.teller + "/" + this.nevner + ") * (" + b.teller + "/" + b.nevner + ")";
  }
  public void dividere(Regne b) {
    double resTeller = this.teller * b.nevner;
    double resNevner = this.nevner * b.teller;
    String resTotal = resTeller + "/" + resNevner;
    this.resTeller = resTeller;
    this.resNevner = resNevner;
    this.resTotal = resTotal;
    this.resOutput = "(" + this.teller + "/" + this.nevner + ") / (" + b.teller + "/" + b.nevner + ")";
  }

  public String getResultat() {
    String resultat = this.resOutput + " = " + this.resTotal;
    return resultat;
  }

  public static void main(String[] args) {

    Regne a, b;
    try {
      a = new Regne(9.9, 2.7);
    } catch(IllegalArgumentException e) {
      System.out.println(e);
      a = new Regne(1);
    }
    try {
      b = new Regne(9, 4);
    } catch(IllegalArgumentException e)   {
      System.out.println(e);
      b = new Regne(1);
    }
    a.addere(b);
    System.out.println(a.getResultat());
    a.subtrahere(b);
    System.out.println(a.getResultat());
    a.multiplisere(b);
    System.out.println(a.getResultat());
    a.dividere(b);
    System.out.println(a.getResultat());
  }
}
