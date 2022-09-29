package Oblig2_arv;

import java.time.LocalDate;

public class GoldMember extends BonusMember {
    public GoldMember(int memberNo, Personals personals, LocalDate enrolledDate, int startPoints) {
        super(memberNo, personals, enrolledDate, startPoints);
    }

    @Override
    public void registerPoints(int points) {
        super.registerPoints((int) (points * FACTOR_GOLD));
    }
}
