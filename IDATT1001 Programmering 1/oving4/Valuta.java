public class Valuta {

  float valutaKurs;
	String valutaNavn;

  public Valuta(float kurs, String navn) {
    valutaKurs = kurs;
		valutaNavn = navn;
  }

	public static String regnUt(float antall, Valuta valuta) {
		float sum;
		sum = valuta.valutaKurs * antall;
		String svar = antall + " " + valuta.valutaNavn + " er lik: " + sum + " norske kroner";
		return svar;
	}
}
