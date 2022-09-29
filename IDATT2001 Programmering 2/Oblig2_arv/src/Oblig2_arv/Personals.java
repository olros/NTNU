package Oblig2_arv;

/**
 * Personalia.java  2010‐01‐18
 *
 * Klasse med personopplysninger: fornavn, etternavn, epostadresse og passord.
 * Passordet kan endres, men da må det nye være forskjellig fra det gamle.
 * Passordkontrollen skiller ikke mellom store og små bokstaver.
 */
class Personals {
    private final String surname;
    private final String firstname;
    private final String ePostadr;
    private String password;
    /**
     * Konstruktør:
     * Alle data må oppgis: fornavn, etternavn, ePostadr, passord
     * Ingen av dataene kan være null eller blanke strenger.
     */
    public Personals(String firstname, String surname, String ePostadr, String password) {
        if (firstname == null || surname == null || ePostadr == null || password == null ||
                firstname.trim().equals("") || surname.trim().equals("") ||
                ePostadr.trim().equals("") || password.trim().equals("")) {
            throw new IllegalArgumentException("Et eller flere konstruktrør argumenter er null og/eller blanke.");
        }
        this.firstname = firstname.trim();
        this.surname = surname.trim();
        this.ePostadr = ePostadr.trim();
        this.password = password.trim();
    }
    public String getFirstname() {
        return firstname;
    }
    public String getSurname() {
        return surname;
    }
    public String getEPostadr() {
        return ePostadr;
    }
    /**
     * Metoden returnerer true dersom passordet er korrekt.
     * Passordkontrollen skiller ikke mellom store og små bokstaver.
     */
    public boolean okPassword(String password) {
        return this.password.equalsIgnoreCase(password);
    }
    /**
     * Metoden setter nytt passord, dersom det er forskjellig fra
     * det gamle. To passord betraktes som like dersom det kun er
     * forskjeller i store/små bokstaver.
     *
     * Metoden returnerer true dersom passordet ble endret, ellers false.
     */
    public boolean changePassword(String oldPassword, String newPassword) {
        if (oldPassword == null || newPassword == null) {
            return false;
        }
        if (!password.equalsIgnoreCase(oldPassword.trim())) {
            return false;
        } else {
            password = newPassword.trim();
            return true;
        }
    }

    @Override
    public String toString() {
        return "Personals{" +
                "surname='" + surname + '\'' +
                ", firstname='" + firstname + '\'' +
                ", ePostadr='" + ePostadr + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}