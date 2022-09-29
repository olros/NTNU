public class Antall {

  private int totalt;

  public Antall(int sum) {
    totalt = sum;
  }

  public void okAntall(int sum) {
    totalt = totalt + sum;
  }

  public int getAntall() {
    return totalt;
  }
}
