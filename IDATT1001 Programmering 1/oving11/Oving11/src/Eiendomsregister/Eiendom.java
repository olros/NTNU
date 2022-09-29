package Eiendomsregister;

/**
 * Klasse som inneholder en eiendom med tilhørende informasjon
 * @author Olaf Rosendahl
 */
public class Eiendom {
    /**
     * Kommunenummer
     */
    private int kNr;
    /**
     * Gårdsnummer
     */
    private int gnr;
    /**
     * Bruksnummer
     */
    private int bnr;
    /**
     * Kommunenavn
     */
    private String kNavn;
    /**
     * Navn på eiendom
     */
    private String navn;
    /**
     * Navn på eier
     */
    private String eier;
    /**
     * Areal i m2
     */
    private double areal;

    /**
     * Lag en ny eiendom
     * @param kNr kommunenummer
     * @param kNavn kommunenavn
     * @param gnr gårdsnummer
     * @param bnr bruksnummer
     * @param navn navn på eiendom
     * @param areal areal i m2
     * @param eier navn på eier
     */
    public Eiendom(int kNr, String kNavn, int gnr, int bnr, String navn, double areal, String eier) {
        if (kNr < 101 || kNr > 5054) {
            throw new IllegalArgumentException("Kommunenummer må være mellom 101 og 5054");
        }
        if (gnr < 0 || bnr < 0 || areal < 0) {
            throw new IllegalArgumentException("Gnr, bnr og areal må være større enn 0");
        }
        this.kNr = kNr;
        this.kNavn = kNavn;
        this.gnr = gnr;
        this.bnr = bnr;
        this.navn = navn;
        this.areal = areal;
        this.eier = eier;
    }

    /**
     * Lag en ny eiendom uten navn
     * @param kNr kommunenummer
     * @param kNavn kommunenavn
     * @param gnr gårdsnummer
     * @param bnr bruksnummer
     * @param areal areal i m2
     * @param eier navn på eier
     */

    public Eiendom(int kNr, String kNavn, int gnr, int bnr, double areal, String eier) {
        if (kNr < 101 || kNr > 5054) {
            throw new IllegalArgumentException("Kommunenummer må være mellom 101 og 5054");
        }
        if (gnr < 0 || bnr < 0 || areal < 0) {
            throw new IllegalArgumentException("Gnr, bnr og areal må være større enn 0");
        }
        this.kNr = kNr;
        this.kNavn = kNavn;
        this.gnr = gnr;
        this.bnr = bnr;
        this.navn = "";
        this.areal = areal;
        this.eier = eier;
    }

    /**
     * Hent kommunenummeret
     * @return kommunenummer
     */
    public int getkNr() {
        return this.kNr;
    }

    /**
     * Hent kommunenavnet
     * @return kommunenavn
     */
    public String getkNavn() {
        return this.kNavn;
    }

    /**
     * Hent gårdsnummeret (gnr)
     * @return gnr
     */
    public int getGnr() {
        return this.gnr;
    }

    /**
     * Hent bruksnummeret (bnr)
     * @return bnr
     */
    public int getBnr() {
        return this.bnr;
    }

    /**
     * Hent navn på eiendommen, tom string om den ikke har et navn
     * @return navn på eiendom
     */
    public String getNavn() {
        return this.navn;
    }

    /**
     * Hent areal i m2
     * @return areal
     */
    public double getAreal() {
        return this.areal;
    }

    /**
     * Hent navn på eier
     * @return navn på eier
     */
    public String getEier() {
        return this.eier;
    }

    /**
     * Hent unik id for eiendommen i formatet "kommunenr-gnr/bnr"
     * @return kNr-gnr/bnr
     */
    public String getId() {
        return this.getkNr() + "-" + this.getGnr() + "/" + this.getBnr();
    }

    /**
     * Endre eier (Fordi eiendommer kan kjøpes og selges, mens andre detaljer endres veldig sjelden)
     * @param nyEier Navn på ny eier
     */
    public void setEier(String nyEier) {
        this.eier = nyEier;
    }

    /**
     * Hent info om eiendommen som en string
     * @return String med kommunenavn, (navn), kommunenummer-gnr/bnr, areal, eier
     */
    public String toString() {
        if (this.getNavn().equals("")) {
            return "Kommune: " + this.getkNavn() + ", id: " + this.getId() + ", areal: " + this.getAreal() + " m2, eier: " + this.getEier();
        } else {
            return "Kommune: " + this.getkNavn() + ", navn: " + this.getNavn() + ", id: " + this.getId() + ", areal: " + this.getAreal() + " m2, eier: " + this.getEier();
        }
    }
}
