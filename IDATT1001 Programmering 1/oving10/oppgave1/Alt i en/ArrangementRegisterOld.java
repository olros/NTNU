import java.util.*;

public class ArrangementRegister {

  private ArrayList<Arrangement> arrangementer;
  private Scanner in = new Scanner(System.in);

  public ArrangementRegister() {
    arrangementer = new ArrayList<Arrangement>();
  }

  public Arrangement nyttArrangement(String navn, String sted, String arrangor, String type, double tidspunkt) {
    int id = arrangementer.size();
    Arrangement arrangement = new Arrangement(id, navn, sted, arrangor, type, tidspunkt);
    return arrangement;
  }

  public ArrayList<Arrangement> listeStedArrangementer(String sted) {
    ArrayList<Arrangement> stedArr = new ArrayList<Arrangement>();
    for (int i = 0; i < arrangementer.size(); i++) {
      if (arrangementer.get(i).getSted().equals(sted)) {
        stedArr.add(arrangementer.get(i));
      }
    }
    return stedArr;
  }
  public ArrayList<Arrangement> listeDatoArrangementer(double dato) {
    ArrayList<Arrangement> datoArr = new ArrayList<Arrangement>();
    for (int i = 0; i < arrangementer.size(); i++) {
      // double arrangementDato = Double.parseDouble(Double.toString(arrangementer.get(i).getTidspunkt()).substring(0, 8));
      double arrangementDato = arrangementer.get(i).getTidspunkt();
      if (arrangementDato == dato * 10000) {
        datoArr.add(arrangementer.get(i));
      }
    }
    return datoArr;
  }
  public ArrayList<Arrangement> listeMellomDatoerArrangementer(double dato1, double dato2) {
    ArrayList<Arrangement> datoArr = new ArrayList<Arrangement>();
    for (int i = 0; i < arrangementer.size(); i++) {
      double arrangementDato = Double.parseDouble(Double.toString(arrangementer.get(i).getTidspunkt()).substring(0, 8));
      if (arrangementDato >= dato1 && arrangementDato <= dato2) {
        datoArr.add(arrangementer.get(i));
      }
    }
    // Comparator<Arrangement> compareByTime = (Arrangement a1, Arrangement a2) -> a1.getNavn().compareTo( a2.getNavn() );
    Comparator<Arrangement> sorterEtterTid = (Arrangement a1, Arrangement a2) -> (int)a1.getTidspunkt() - (int)a2.getTidspunkt();
    Collections.sort(datoArr, sorterEtterTid);
    return datoArr;
  }

  public static void main(String[] args) {
    ArrangementRegister o = new ArrangementRegister();
    while (true) {
      o.visMeny();
    }
  }
  public void skrivUtArrangementer(ArrayList<Arrangement> liste) {
    System.out.println("");
    if (liste.size() > 0) {
      for (int i = 0; i < liste.size(); i++) {
        System.out.println(liste.get(i).toString());
      }
    } else {
      System.out.println("Fant ingen arrangementer");
    }
  }
  public void visMeny() {
    // Scanner in = new Scanner(System.in);
    System.out.println("");
    System.out.println("Tast 1 for å registrere nytt arrangement");
    System.out.println("Tast 2 for å finne alle arrangementer på et sted");
    System.out.println("Tast 3 for å finne alle arrangementer på en dato");
    System.out.println("Tast 4 for å finne alle arrangementer innenfor et tidsintervall");
    System.out.println("Tast 5 for å se alle arrangementer sortert");

    int menyInput = in.nextInt();
    switch (menyInput) {
      case 1:
        lagArrangement();
        break;
      case 2:
        stedArrangementer();
        break;
      case 3:
        datoArrangementer();
        break;
      case 4:
        mellomDatoArrangementer();
        break;
      case 5:
        alleArrangementer();
        break;
      default:
        System.out.println("Du må skrive et tall fra 1-5");
        break;
    }
  }

  private void lagArrangement() {
    in.nextLine();
    System.out.print("Navn: "); String navn = in.nextLine();
    System.out.print("Sted: "); String sted = in.nextLine();
    System.out.print("Arrangør: "); String arrangor = in.nextLine();
    System.out.print("Type: "); String type = in.nextLine();
    System.out.print("Tidspunkt (YYYYMMDDTTMM): "); double tidspunkt = in.nextDouble();
    Arrangement a = nyttArrangement(navn, sted, arrangor, type, tidspunkt);
    arrangementer.add(a);
  }
  private void stedArrangementer() {
    in.nextLine();
    System.out.print("Sted: "); String sted = in.nextLine();
    skrivUtArrangementer(listeStedArrangementer(sted));
  }
  private void datoArrangementer() {
    System.out.print("Dato (YYYYMMDD): "); double dato = in.nextDouble();
    skrivUtArrangementer(listeDatoArrangementer(dato));
  }
  private void mellomDatoArrangementer() {
    System.out.print("Startdato (YYYYMMDD): "); double dato1 = in.nextDouble();
    System.out.print("Sluttdato (YYYYMMDD): "); double dato2 = in.nextDouble();
    if (dato1 < dato2) {
      skrivUtArrangementer(listeMellomDatoerArrangementer(dato1, dato2));
    } else {
      System.out.println("Sluttdatoen må komme etter startdatoen!");
    }
  }
  private void alleArrangementer() {
    System.out.println("Tast 1 for å sortere etter sted");
    System.out.println("Tast 2 for å sortere etter type");
    System.out.println("Tast 3 for å sortere etter tidspunkt");
    int aInput = in.nextInt();
    switch (aInput) {
      case 1:
        Comparator<Arrangement> sorterEtterSted = (Arrangement a1, Arrangement a2) -> a1.getSted().compareTo(a2.getSted());
        ArrayList<Arrangement> ar1 = arrangementer;
        Collections.sort(ar1, sorterEtterSted);
        skrivUtArrangementer(ar1);
        break;
      case 2:
        Comparator<Arrangement> sorterEtterType = (Arrangement a1, Arrangement a2) -> a1.getType().compareTo(a2.getType());
        ArrayList<Arrangement> ar2 = arrangementer;
        Collections.sort(ar2, sorterEtterType);
        skrivUtArrangementer(ar2);
        break;
      case 3:
        Comparator<Arrangement> sorterEtterTid = (Arrangement a1, Arrangement a2) -> (int)a1.getTidspunkt() - (int)a2.getTidspunkt();
        ArrayList<Arrangement> ar3 = arrangementer;
        Collections.sort(ar3, sorterEtterTid);
        skrivUtArrangementer(ar3);
        break;
      default:
        System.out.println("Du må taste et tall fra 1-3");
        alleArrangementer();
        break;
    }
  }
}
