public class Arrangement {

  private final int id;
  private final double tidspunkt;
  private final String navn, sted, arrangor, type;

  private int AntId = 1;

  public Arrangement(int id, String navn, String sted, String arrangor, String type, double tidspunkt) {
    this.id = id;
    this.navn = navn;
    this.sted = sted;
    this.arrangor = arrangor;
    this.type = type;
    this.tidspunkt =tidspunkt;
  }

  public int getId() {
  	return id;
  }
  public String getNavn() {
  	return navn;
  }
  public String getSted() {
  	return sted;
  }
  public String getArrangor() {
  	return arrangor;
  }
  public String getType() {
  	return type;
  }
  public double getTidspunkt() {
  	return tidspunkt;
  }

  public String toString() {
    String t = String.format("%.1f", this.getTidspunkt());
    String tid = t.substring(6, 8) + "." + t.substring(4, 6) + "." + t.substring(0, 4) + " kl. " + t.substring(8, 10) + ":" + t.substring(10, 12);
    return "Id: " + this.getId() + ", navn: " + this.getNavn() + ", sted: " + this.getSted() + ", arrangør: " + this.getArrangor() + ", type: " + this.getType() + ", tidspunkt: " + tid;
  }
}
