package MemberArchive;

import java.time.LocalDate;
import java.time.Period;

public class BonusMember {

    public static final double FACTOR_SILVER = 1.2;
    public static final double FACTOR_GOLD = 1.5;
    private final int memberNo;
    private final Personals personals;
    private final LocalDate enrolledDate;
    private int points = 0;

    /**
     * Instantiates a new Bonus member.
     *
     * @param memberNo     the member no
     * @param personals    the personals
     * @param enrolledDate the enrolled date
     * @throws IllegalArgumentException if personals or enrolled date is null
     */
    public BonusMember(int memberNo, Personals personals, LocalDate enrolledDate) throws IllegalArgumentException {
        if (personals == null || enrolledDate == null) {
            throw new IllegalArgumentException("Personals or enrolled date is null");
        }
        this.memberNo = memberNo;
        this.personals = personals;
        this.enrolledDate = enrolledDate;
    }

    /**
     * Instantiates a new Bonus member.
     *
     * @param memberNo     the member no
     * @param personals    the personals
     * @param enrolledDate the enrolled date
     * @param startPoints  the start points
     * @throws IllegalArgumentException  if personals or enrolled date is null or startpoints is below zero
     */
    public BonusMember(int memberNo, Personals personals, LocalDate enrolledDate, int startPoints) throws IllegalArgumentException {
        if (personals == null || enrolledDate == null || startPoints < 0) {
            throw new IllegalArgumentException("Personals or enrolled date is null or startpoints is below zero");
        }
        this.memberNo = memberNo;
        this.personals = personals;
        this.enrolledDate = enrolledDate;
        this.points = startPoints;
    }

    /**
     * Gets member no.
     *
     * @return the member no
     */
    public int getMemberNo() {
        return this.memberNo;
    }

    /**
     * Gets personals.
     *
     * @return the personals
     */
    public Personals getPersonals() {
        return this.personals;
    }

    /**
     * Gets enrolled date.
     *
     * @return the enrolled date
     */
    public LocalDate getEnrolledDate() {
        return this.enrolledDate;
    }

    /**
     * Gets points.
     *
     * @return the points
     */
    public int getPoints() {
        return this.points;
    }

    /**
     * Find qualification points int.
     *
     * @param currentDate the current date
     * @return the int
     */
    public int findQualificationPoints(LocalDate currentDate) {
        if (currentDate == null) {
            throw new IllegalArgumentException("Current date cannot be null");
        }
        return Period.between(this.enrolledDate, currentDate).getYears() < 1 ? this.points : 0;
    }

    /**
     * Ok password boolean.
     *
     * @param password the password
     * @return the boolean
     * @throws IllegalArgumentException if the password is null or empty
     */
    public boolean okPassword(String password) throws IllegalArgumentException {
        return this.personals.okPassword(password);
    }

    /**
     * Register points.
     *
     * @param points the points
     */
    public void registerPoints(int points) {
        if (points < 0) {
            throw new IllegalArgumentException("The points to be added cannot be below zero");
        }
        this.points += points;
    }

    @Override
    public String toString() {
        return personals +
                "\nMemberNo: " + memberNo +
                "\nEnrolled: " + enrolledDate +
                "\nPoints: " + points;
    }
}
