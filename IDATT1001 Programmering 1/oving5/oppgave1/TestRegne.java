import java.util.*;

class TestRegne {
  public static void main(String[] args) {
    Regne a;
    Regne b;
    try{
      a = new Regne(9, 0);
    } catch(IllegalArgumentException e)
    {
      System.out.println(e);
      a = new Regne(1);
    }
    try{
      b = new Regne(16, 8);
    } catch(IllegalArgumentException e)
    {
      System.out.println(e);
      b = new Regne(1);
    }
    Regne.addere(a, b);
    System.out.println(Regne.getResultat());
    Regne.subtrahere(a, b);
    System.out.println(Regne.getResultat());
    Regne.multiplisere(a, b);
    System.out.println(Regne.getResultat());
    Regne.dividere(a, b);
    System.out.println(Regne.getResultat());
  }
}
