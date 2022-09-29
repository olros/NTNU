package Oblig3_arv;

/**
 * Klassen Ticket med subklasser
 * Denne blir delt ut sammen med øvingen
 */
abstract class Ticket {
    private final String tribuneName;
    private final int price;
    /**
     * Konstruktør:
     * Tribunenavn må oppgis. Ingen krav til pris.
     */
    public Ticket(String tribuneName, int price) {
        if (tribuneName == null || tribuneName.trim().equals("")) {
            throw new IllegalArgumentException("Tribunenavn må oppgis.");
        }
        this.tribuneName = tribuneName.trim();
        this.price = price;
    }
    public String getTribune() {
        return tribuneName;
    }
    public int getPris() {
        return price;
    }
    public String toString() {
        return "Billett: Tribune: " + tribuneName + ", Pris: " + price;
    }
}
