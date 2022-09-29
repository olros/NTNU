public class Rett {

  private String navn, type, oppskrift;
  private double pris;

  public Rett (String navn, String type, double pris, String oppskrift) {
    this.navn = navn;
    this.type = type;
    this.pris = pris;
    this.oppskrift = oppskrift;
  }

  public String getNavn() {
  	return navn;
  }
  public String getType() {
  	return type;
  }
  public double getPris() {
  	return pris;
  }
  public String getOppskrift() {
  	return oppskrift;
  }

  public String toString() {
    return "Navn: " + this.getNavn() + ", type: " + this.getType() + ", pris: " + this.getPris() + ", oppskrift: " + this.getOppskrift();
  }
  public String toSmallString() {
    return this.getNavn() + " (" + this.getPris() + " kr)";
  }
}
