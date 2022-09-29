import java.util.*;

public class MinRandom {

  private static Random random = new Random();
  private static MinRandom minRandom = new MinRandom();
  private static Scanner in = new Scanner(System.in);

  public MinRandom(){
  }

  public int nesteHeltall(int nedre, int ovre) {
    // Fra og med nedre til øvre:
    int max = ovre - nedre;
    // Fra og med nedre til og med øvre:
    // int max = ovre - nedre + 1;
    int resultat = random.nextInt(max);
    resultat = resultat + nedre;
    return resultat;
  }
  public double nesteDesimaltall(double nedre, double ovre) {
    double differanse = ovre - nedre;
    double tilfeldig = random.nextDouble();
    double resultat = nedre + (differanse * tilfeldig);
    return resultat;
  }

  public static void main(String[] args) {
    visMeny();
    //kjorMange();
  }

  public static void kjorMange() {
    System.out.println("Heltall mellom 10 og 20:");
    for (int i = 0;i < 20;i++) {
      System.out.println(minRandom.nesteHeltall(10,20));
    }
    System.out.println("Desimaltall mellom 10 og 20:");
    for (int i = 0;i < 20;i++) {
      System.out.println(minRandom.nesteDesimaltall(10,20));
    }
  }

  public static void visMeny() {
    System.out.println("Tast inne nederste tall i intervallet:");
    int nedre = in.nextInt();
    System.out.println("Tast inne øverste tall i intervallet:");
    int ovre = in.nextInt();

    System.out.println("Tast 1 for heltall");
    System.out.println("Tast 2 for desimaltall");

    int menyInput = in.nextInt();
    if (menyInput == 1) {
      System.out.println(minRandom.nesteHeltall(nedre,ovre));
      visMeny();
    } else if (menyInput == 2) {
      System.out.println(minRandom.nesteDesimaltall(nedre,ovre));
      visMeny();
    } else {
      System.out.println("Du må taste inn enten 1 eller 2");
      visMeny();
      return;
    }
  }
}
