package Oblig2_arv;

import java.time.*;

public class BonusMember {

    public static final double FACTOR_SILVER = 1.2;
    public static final double FACTOR_GOLD = 1.5;
    private final int memberNo;
    private final Personals personals;
    private final LocalDate enrolledDate;
    private int points = 0;

    public BonusMember(int memberNo, Personals personals, LocalDate enrolledDate) {
        this.memberNo = memberNo;
        this.personals = personals;
        this.enrolledDate = enrolledDate;
    }
    public BonusMember(int memberNo, Personals personals, LocalDate enrolledDate, int startPoints) {
        this.memberNo = memberNo;
        this.personals = personals;
        this.enrolledDate = enrolledDate;
        this.points = startPoints;
    }

    public int getMemberNo() {
        return this.memberNo;
    }
    public Personals getPersonals() {
        return this.personals;
    }
    public LocalDate getEnrolledDate() {
        return this.enrolledDate;
    }
    public int getPoints() {
        return this.points;
    }

    public int findQualificationPoints(LocalDate currentDate) {
        return Period.between(this.enrolledDate, currentDate).getYears() < 1 ? this.points : 0;
    }

    public boolean okPassword(String password) {
        return this.personals.okPassword(password);
    }

    public void registerPoints(int points) {
        this.points += points;
    }

    @Override
    public String toString() {
        return "BonusMember{" +
                "memberNo=" + memberNo +
                ", personals=" + personals +
                ", enrolledDate=" + enrolledDate +
                ", points=" + points +
                '}';
    }
}
