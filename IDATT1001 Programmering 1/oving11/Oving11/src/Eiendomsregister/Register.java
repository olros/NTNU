package Eiendomsregister;

import java.util.ArrayList;

/**
 * Klasse som inneholder et register med eiendommer og metoder for å finne/fjerne/legg til eiendom
 * @author Olaf Rosendahl
 */
public class Register {
    /**
     * Liste av eiendommer. Bruker ArrayList fordi den er superenkel å utvide med flere eiendommer i forhold til en fixed size array
     */
    private ArrayList<Eiendom> eiendommer;

    /**
     * Opprett en ny tom eiendomsliste
     */
    public Register() {
        eiendommer = new ArrayList<Eiendom>();
    }

    /**
     * Legg til en ny eiendom og putt den i registeret
     * @param kNr Kommunenummer
     * @param kNavn Kommunenavn
     * @param gnr Gårdsnummer
     * @param bnr Bruksnummer
     * @param navn Navn på eiendom
     * @param areal Areal i m2
     * @param eier Navn på eier
     * @return Info om eiendommen som ble lagt til
     */
    public String nyEiendom(int kNr, String kNavn, int gnr, int bnr, String navn, double areal, String eier) {
        Eiendom eiendom = new Eiendom(kNr, kNavn, gnr, bnr, navn, areal, eier);
        eiendommer.add(eiendom);
        return eiendom.toString();
    }

    /**
     * Legg til en ny eiendom og putt den i registeret
     * @param kNr Kommunenummer
     * @param kNavn Kommunenavn
     * @param gnr Gårdsnummer
     * @param bnr Bruksnummer
     * @param areal Areal i m2
     * @param eier Navn på eier
     * @return Info om eiendommen som ble lagt til
     */
    public String nyEiendom(int kNr, String kNavn, int gnr, int bnr, double areal, String eier) {
        Eiendom eiendom = new Eiendom(kNr, kNavn, gnr, bnr, areal, eier);
        eiendommer.add(eiendom);
        return eiendom.toString();
    }

    /**
     * Hent data om eiendom
     * @param eiendom Eiendommen du vil ha info om
     * @return Info om eiendommen
     */
    public String getEiendom(Eiendom eiendom) {
        return eiendom.toString();
    }

    /**
     * Fjern en spesifikk eiendom fra listen
     * @param eiendom Eiendommen som skal fjernes
     * @return Info om eiendommen som ble fjernet
     */
    public String fjernEiendom(Eiendom eiendom) {
        for (int i = 0; i < eiendommer.size(); i++) {
            if (eiendommer.get(i) == eiendom) {
                String info = eiendommer.get(i).toString();
                eiendommer.remove(i);
                return info;
            }
        }
        return null;
    }

    /**
     * Finn en spesifikk eiendom i listen
     * @param kNr Kommunenummeret til eiendommen
     * @param gnr Gårdsnummeret til eiendommen
     * @param bnr Bruksnummeret til eiendommen
     * @return Eiendom om funnet, null hvis ikke
     */
    public String finnEiendom(int kNr, int gnr, int bnr) {
        for (int i = 0; i < eiendommer.size(); i++) {
            if (eiendommer.get(i).getkNr() == kNr && eiendommer.get(i).getGnr() == gnr && eiendommer.get(i).getBnr() == bnr) {
                return eiendommer.get(i).toString();
            }
        }
        return "Fant ikke eiendommen";
    }

    /**
     * Finn antall eiendommer i listen
     * @return Antall eiendommer
     */
    public int getAntall() {
        return eiendommer.size();
    }

    /**
     * Finn snittarealet til alle eiendommene i listen, returnerer som double slik at man kan regne videre med arealet om ønskelig
     * @return Snittareal i m2
     */
    public double getSnittareal() {
        double totalAreal = 0;
        for (int i = 0; i < eiendommer.size(); i++) {
            totalAreal += eiendommer.get(i).getAreal();
        }
        double snittAreal = totalAreal / getAntall();
        return snittAreal;
    }

    /**
     * Hent alle eiendommene med et spesifikt gårdsnummer. Returnerer liste med eiendomsobjekter slik at en selv kan velge hvordan eiendommene skal skrives ut, istedenfor at dette er forhåndsdefinert.
     * @param gnr Gårdsnummer
     * @return Liste med eiendommer
     */
    public String getAlleGnr(int gnr) {
        String gnrListe = "";
        for (int i = 0; i < eiendommer.size(); i++) {
            if (eiendommer.get(i).getGnr() == gnr) {
                gnrListe += eiendommer.get(i).toString() + "\n";
            }
        }
        return gnrListe;
    }

    /**
     * Hent alle eiendommene i listen, string fordi det er praktisk
     * @return Liste med alle eiendommene som string
     */
    public String getAlle() {
        String alleEiendommer = "";
        for (int i = 0; i < eiendommer.size(); i++) {
            alleEiendommer += eiendommer.get(i).toString() + "\n";
        }
        return alleEiendommer;
    }
}
