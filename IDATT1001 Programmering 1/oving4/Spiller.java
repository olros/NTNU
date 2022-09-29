import java.util.Random;

public class Spiller {
  private int sumPoeng;
  private Random terning = new Random();

  public Spiller(int poeng) {
    sumPoeng = poeng;
  }

  public int getPoeng() {
    return sumPoeng;
  }

  public static int kastTerningen(int poeng) {
    int nySum;

    int n = terning.nextInt(6);
    n += 1;
    if (n == 1) {
      nySum = 0;
    } else if (poeng > 100) {
      nySum = poeng - n;
    } else {
      nySum = poeng + n;
    }
    return nySum;
  }

  public static boolean erFerdig(int poeng) {
    if (poeng == 100) {
      return true;
    } else {
      return false;
    }
  }
}
