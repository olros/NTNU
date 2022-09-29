package MemberArchive;

public class Personals {
    private final String surname;
    private final String firstname;
    private final String ePostadr;
    private String password;

    /**
     * Constructor:
     * Creates a new personals object
     *
     * @param firstname the firstname
     * @param surname   the surname
     * @param ePostadr  the e postadr
     * @param password  the password
     * @throws IllegalArgumentException the illegal argument exception
     */
    public Personals(String firstname, String surname, String ePostadr, String password) {
        if (firstname == null || surname == null || ePostadr == null || password == null ||
                firstname.trim().equals("") || surname.trim().equals("") ||
                ePostadr.trim().equals("") || password.trim().equals("")) {
            throw new IllegalArgumentException("One or more of the parameters are null or empty");
        }
        this.firstname = firstname.trim();
        this.surname = surname.trim();
        this.ePostadr = ePostadr.trim();
        this.password = password.trim();
    }

    /**
     * Gets firstname.
     *
     * @return the firstname
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * Gets surname.
     *
     * @return the surname
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Gets e postadr.
     *
     * @return the email
     */
    public String getEPostadr() {
        return ePostadr;
    }

    /**
     * Metoden returnerer true dersom passordet er korrekt.
     * Passordkontrollen skiller ikke mellom store og små bokstaver.
     *
     * @param password the password
     * @return boolean - true if success
     */
    public boolean okPassword(String password) {
        if (password == null || password.trim().equals("")) {
            throw new IllegalArgumentException("The password is null or empty");
        }
        return this.password.equalsIgnoreCase(password);
    }

    /**
     * Set new password, if different from the old one.
     *
     * @param oldPassword the old password
     * @param newPassword the new password
     * @return boolean - true if success
     */
    public boolean changePassword(String oldPassword, String newPassword) {
        if (oldPassword == null || newPassword == null) {
            throw new IllegalArgumentException("The password can't be null");
        }
        if (!okPassword(oldPassword.trim()) || !oldPassword.trim().equalsIgnoreCase(newPassword.trim())) {
            throw new IllegalArgumentException("The old password isn't correct or equals the new password");
        } else {
            password = newPassword.trim();
            return true;
        }
    }

    @Override
    public String toString() {
        return firstname + ' ' + surname;
    }
}