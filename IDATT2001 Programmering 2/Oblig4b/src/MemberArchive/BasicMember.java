package MemberArchive;

import java.time.LocalDate;

public class BasicMember extends BonusMember {
    public BasicMember(int memberNo, Personals personals, LocalDate enrolledDate) {
        super(memberNo, personals, enrolledDate);
    }
}
