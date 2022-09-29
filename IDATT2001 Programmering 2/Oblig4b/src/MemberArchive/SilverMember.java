package MemberArchive;

import java.time.LocalDate;

public class SilverMember extends BonusMember {
    public SilverMember(int memberNo, Personals personals, LocalDate enrolledDate, int startPoints) {
        super(memberNo, personals, enrolledDate, startPoints);
    }

    @Override
    public void registerPoints(int points) {
        super.registerPoints((int) (points * FACTOR_SILVER));
    }
}
