import java.util.*;

public class MenyRegister {

  private ArrayList<Meny> menyer;
  private ArrayList<Rett> retter;

  public MenyRegister() {
    menyer = new ArrayList<Meny>();
    retter = new ArrayList<Rett>();
  }

  public ArrayList<Meny> getMenyer() {
  	return menyer;
  }
  public ArrayList<Rett> getRetter() {
  	return retter;
  }

  // Registrer en ny meny
  public Rett registrerNyRett(String navn, String type, double pris, String oppskrift) {
    Rett nyRett = new Rett(navn, type, pris, oppskrift);
    retter.add(nyRett);
    return nyRett;
  }

  // Finn en rett, gitt navn
  public ArrayList<Rett> getRettMedNavn(String navn) {
    ArrayList<Rett> navnArr = new ArrayList<Rett>();
    for (int i = 0; i < getRetter().size(); i++) {
      if (getRetter().get(i).getNavn().toLowerCase().equals(navn.toLowerCase())) {
        navnArr.add(getRetter().get(i));
      }
    }
    return navnArr;
  }

  // Finn alle retter av en gitt type
  public ArrayList<Rett> getRettMedType(String type) {
    ArrayList<Rett> typeArr = new ArrayList<Rett>();
    for (int i = 0; i < getRetter().size(); i++) {
      if (getRetter().get(i).getType().toLowerCase().equals(type.toLowerCase())) {
        typeArr.add(getRetter().get(i));
      }
    }
    return typeArr;
  }

  // Registrer en ny meny med gitte retter
  public Meny registrerNyMeny(ArrayList<Rett> menyRetter) {
    Meny nyMeny = new Meny(menyRetter);
    getMenyer().add(nyMeny);
    return nyMeny;
  }

  // Finn alle menyer med totalpris innenfor gitt intervall
  public ArrayList<Meny> getMenyerMedPrisintervall(double min, double max) {
    ArrayList<Meny> menyArr = new ArrayList<Meny>();
    for (int i = 0; i < getMenyer().size(); i++) {
      if (getMenyer().get(i).getTotalpris() >= min && getMenyer().get(i).getTotalpris() <= max) {
        menyArr.add(getMenyer().get(i));
      }
    }
    return menyArr;
  }
}
