import java.util.*;

public class ArrangementRegister {

  public ArrayList<Arrangement> arrangementer;

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
      int arrangementDato = (int)Math.floor(arrangementer.get(i).getTidspunkt() / 10000);
      if (arrangementDato == (int)dato) {
        datoArr.add(arrangementer.get(i));
      }
    }
    return datoArr;
  }
  public ArrayList<Arrangement> listeMellomDatoerArrangementer(double dato1, double dato2) {
    ArrayList<Arrangement> datoArr = new ArrayList<Arrangement>();
    for (int i = 0; i < arrangementer.size(); i++) {
      int arrangementDato = (int)Math.floor(arrangementer.get(i).getTidspunkt() / 10000);
      if (arrangementDato >= (int)dato1 && arrangementDato <= (int)dato2) {
        datoArr.add(arrangementer.get(i));
      }
    }
    Comparator<Arrangement> sorterEtterTid = (Arrangement a1, Arrangement a2) -> (int)a1.getTidspunkt() - (int)a2.getTidspunkt();
    Collections.sort(datoArr, sorterEtterTid);
    return datoArr;
  }
}
