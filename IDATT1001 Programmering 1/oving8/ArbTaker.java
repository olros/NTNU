import java.util.*;

public class ArbTaker {

  private final Person personalia;
  private final int arbtakernr, ansettelsesaar;
  private static double manedslonn, skatteprosent;
  GregorianCalendar kalender = new GregorianCalendar();
  private static Scanner in = new Scanner(System.in);

  public ArbTaker (Person personalia, int arbtakernr, int ansettelsesaar, double manedslonn, double skatteprosent) {
    this.personalia = personalia;
    this.arbtakernr = arbtakernr;
    this.ansettelsesaar = ansettelsesaar;
    this.manedslonn = manedslonn;
    this.skatteprosent = skatteprosent;
  }
  public Person getPersonalia() {
  	return personalia;
  }
  public int getArbtakernr() {
  	return arbtakernr;
  }
  public int getAnsettelsesaar() {
  	return ansettelsesaar;
  }
  public double getManedslonn() {
  	return manedslonn;
  }
  public double getSkatteprosent() {
  	return skatteprosent;
  }
  private void setManedslonn(double nyLonn) {
    this.manedslonn = nyLonn;
  }
  private void setSkatteprosent(double nySkatteprosent) {
    this.skatteprosent = nySkatteprosent;
  }

  private double getSkattPerManed() {
    double skatt = this.getManedslonn() * (this.getSkatteprosent() / 100);
    return skatt;
  }
  private double getBruttoLonnAar() {
    double bruttoLonn = this.getManedslonn() * 12;
    return bruttoLonn;
  }
  private double getSkattPerAar() {
    double tiManeder = this.getSkattPerManed() * 10;
    double skattetrekk = tiManeder + (this.getSkattPerManed() * 0.5);
    return skattetrekk;
  }
  private String getNavn() {
    String navn = this.getPersonalia().getEtternavn() + ", " + this.getPersonalia().getFornavn();
    return navn;
  }
  private int getAlder() {
    int aarNa = kalender.get(java.util.Calendar.YEAR);
    int alder = aarNa - this.getPersonalia().getFodselsaar();
    return alder;
  }
  private int getAarAnsatt() {
    int aarNa = kalender.get(java.util.Calendar.YEAR);
    int aarAnsatt = aarNa - this.getAnsettelsesaar();
    return aarAnsatt;
  }
  private boolean getAnsattMerEnn(int antallAar) {
    boolean merEnn = false;
    if (this.getAarAnsatt() >= antallAar) {
      merEnn = true;
    }
    return merEnn;
  }

  public static void main(String[] args) {
    System.out.print("Skriv inn fornavn: "); String fornavn = in.nextLine();
    System.out.print("Skriv inn etternavn: "); String etternavn = in.nextLine();
    System.out.print("Skriv inn fødselsår: "); int fodselsaar = in.nextInt();
    Person person = new Person(fornavn, etternavn, fodselsaar);
    System.out.print("Skriv inn arbeidstakernr: "); int arbtakernr = in.nextInt();
    System.out.print("Skriv inn ansettelsesaar: "); int ansettelsesaar = in.nextInt();
    System.out.print("Skriv inn månedslønn: "); double manedslonn = in.nextDouble();
    System.out.print("Skriv inn skatteprosent: "); double skatteprosent = in.nextDouble();
    ArbTaker arbTaker = new ArbTaker(person, arbtakernr, ansettelsesaar, manedslonn, skatteprosent);

    skrivUtAlt(arbTaker);

    while (true) {
      visMeny(arbTaker);
    }
  }

  public static void visMeny(ArbTaker arbTaker) {
    System.out.println("");
    System.out.println("Tast 1 for å endre månedslønnen");
    System.out.println("Tast 2 for å endre skatteprosenten");
    System.out.println("Tast 3 for å skrive ut all data");

    int menyInput = in.nextInt();
    if (menyInput == 1) {
      System.out.print("Skriv inn ny månedslønn: "); double nyLonn = in.nextDouble();
      arbTaker.setManedslonn(nyLonn);
      System.out.println("Ny registrert månedslønn er: " + arbTaker.getManedslonn());
    } else if (menyInput == 2) {
      System.out.print("Skriv inn ny skatteprosent: "); double nySkatteprosent = in.nextDouble();
      arbTaker.setSkatteprosent(nySkatteprosent);
      System.out.println("Ny registrert skatteprosent er: " +  arbTaker.getSkatteprosent());
    } else if (menyInput == 3) {
      skrivUtAlt(arbTaker);
    } else {
      System.out.println("Du må taste inn et tall fra 1-3");
    }
  }

  public static void skrivUtAlt(ArbTaker arbTaker) {
    System.out.println("");
    System.out.println("Data lagt inn:");
    System.out.println("Navn: " + arbTaker.getNavn());
    System.out.println("Alder: " + arbTaker.getAlder());
    System.out.println("Skatt per måned: " + arbTaker.getSkattPerManed());
    System.out.println("Skatt per år: " + arbTaker.getSkattPerAar());
    System.out.println("Bruttolønn per år: " + arbTaker.getBruttoLonnAar());
    System.out.println("År ansatt: " + arbTaker.getAarAnsatt());
    System.out.println("Ansatt mer enn 10 år: " + arbTaker.getAnsattMerEnn(10));
  }
}
