package Eiendomsregister;

import java.util.Scanner;

/**
 * Klientklasse for eiendomsregisteret
 */
public class Klient {
    /**
     * Scanner
     */
    public Scanner in = new Scanner(System.in);
    /**
     * Eiendomsregister
     */
    public Register register = new Register();

    /**
     * Main-metode
     * @param args Args
     */
    public static void main(String[] args) {
        Klient klient = new Klient();
        klient.testData();
        while(true) {
            klient.meny();
        }
    }

    /**
     * Legg til testdata
     */
    public void testData() {
        register.nyEiendom(1445, "Gloppen", 77, 631, 1017.6, "Jens Olsen");
        register.nyEiendom(1445, "Gloppen", 77, 131, "Syningom", 661.3, "Nicolay Madsen");
        register.nyEiendom(1445, "Gloppen", 75, 19, "Fugletun", 650.6, "Evilyn Jensen");
        register.nyEiendom(1445, "Gloppen", 74, 188, 1457.2, "Karl Ove Bråten");
        register.nyEiendom(1445, "Gloppen", 69, 47, "Høiberg", 1339.4, "Elsa Indregård");
    }

    /**
     * Vis meny og ta input
     */
    public void meny() {
        System.out.println("\n***** Eiendomsregister *****");
        System.out.println("1. Legg til eiendom");
        System.out.println("2. Skriv ut alle eiendommer");
        System.out.println("3. Finn eiendom");
        System.out.println("4. Se gjennomsnittsareal for eiendommer");
        System.out.println("5. Avslutt");
        System.out.println("\nSkriv inn et tall mellom 1 and 5:");

        int menyInput = in.nextInt();
        switch (menyInput) {
            case 1:
                System.out.println(nyEiendom());
                break;
            case 2:
                printAlle();
                break;
            case 3:
                System.out.println(finnEiendom());
                break;
            case 4:
                System.out.print("Gjennomsnittsarealet er: " + register.getSnittareal() + " m2");
                break;
            case 5:
                System.out.println("Avslutter...");
                System.exit(0);
                break;
            default:
                System.out.println("Du må skrive et tall mellom 1 og 5!");
                break;
        }
    }

    /**
     * Legg til ny eiendom i registeret
     * @return Info om eiendommen som ble lagt til
     */
    public String nyEiendom() {
        String returInfo = "";

        System.out.println("Registrer data om ny eiendom:");
        in.nextLine();
        System.out.print("Kommunenavn: "); String kNavn = in.nextLine();
        System.out.print("Kommunenummer (101 - 5054): "); int kNr = in.nextInt();
        System.out.print("Gårdsnummer: "); int gnr = in.nextInt();
        System.out.print("Bruksnummer: "); int bnr = in.nextInt();
        System.out.print("Areal i m2: "); double areal = in.nextDouble();
        in.nextLine();
        System.out.print("Eier: "); String eier = in.nextLine();
        System.out.println("Tast 1 for å legge inn navn på eiendom. "); int input = in.nextInt();
        if (input == 1) {
            in.nextLine();
            System.out.print("Navn på eiendom: "); String navn = in.nextLine();
            try {
                returInfo = register.nyEiendom(kNr, kNavn, gnr, bnr, navn, areal, eier);
            } catch (Exception err) {
                err.printStackTrace();
                returInfo = "Noe gikk galt, prøv på nytt";
            }
        } else {
            try {
                returInfo = register.nyEiendom(kNr, kNavn, gnr, bnr, areal, eier);
            } catch (Exception err) {
                err.printStackTrace();
                returInfo = "Noe gikk galt, prøv på nytt";
            }
        }
        return returInfo;
    }

    /**
     * Finn en spesifikk eiendom i listen
     * @return Info om eiendom hvis funnet, "ikke funnet" hvis ikke
     */
    public String finnEiendom() {
        System.out.println("Tast inn info om eiendommen du vil finne:");
        System.out.print("Kommunenummer: "); int kNr = in.nextInt();
        System.out.print("Gårdsnummer: "); int gnr = in.nextInt();
        System.out.print("Bruksnummer: "); int bnr = in.nextInt();
        if (register.finnEiendom(kNr, gnr, bnr) != null) {
            return register.finnEiendom(kNr, gnr, bnr).toString();
        } else {
            return "Finner ingen eiendommer i register som stemmer med opplysningene du oppga";
        }
    }

    /**
     * Skriv ut alle eiendommer i listen
     */
    public void printAlle() {
        System.out.println(register.getAlle());
    }
}
