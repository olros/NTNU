package Oblig2_arv;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;

public class MemberArchive {

    public static final int SILVER_LIMIT = 25000;
    public static final int GOLD_LIMIT = 75000;
    private static final int MAKS_TRY = 1;
    private static final Random  RANDOM_NUMBER = new Random();
    private ArrayList<BonusMember> memberList;

    public MemberArchive() {
        this.memberList = new ArrayList<>();
    }

    public int findPoints(int memberNo, String password) {
        BonusMember m = findMember(memberNo);
        if (m != null && m.okPassword(password)) {
            return m.getPoints();
        }
        return -1;
    }

    public boolean registerPoints(int no, int points) {
        BonusMember m = findMember(no);
        if (m != null) {
            m.registerPoints(points);
            return true;
        }
        return false;
    }

    public int newMember(Personals personals, LocalDate localDate) {
        int registeredMemberNo = findAvailableNo();
        memberList.add(new BasicMember(registeredMemberNo, personals, localDate));
        return registeredMemberNo;
    }

    private int findAvailableNo() {
        boolean notFoundAvailableNo = true;
        int randomNo = 0;

        while (notFoundAvailableNo) {
            randomNo = Math.abs(RANDOM_NUMBER.nextInt());
            if (findMember(randomNo) != null) continue;
            notFoundAvailableNo = false;
        }
        return randomNo;
    }

    public void checkMembers(LocalDate localDate) {
        for (int i = 0; i < memberList.size(); i++) {
            BonusMember m = memberList.get(i);
            if (m instanceof BasicMember) {
                memberList.set(i, checkSilverLimit(m.getMemberNo(), localDate));
                memberList.set(i, checkGoldLimit(m.getMemberNo(), localDate));
            } else if (m instanceof SilverMember) {
                memberList.set(i, checkGoldLimit(m.getMemberNo(), localDate));
            }
        }
    }

    private BonusMember checkSilverLimit(int memberNo, LocalDate localDate) {
        BonusMember member = findMember(memberNo);
        if (member != null) {
            if (member.findQualificationPoints(localDate) >= SILVER_LIMIT) {
                SilverMember silverMember = new SilverMember(member.getMemberNo(), member.getPersonals(), member.getEnrolledDate(), member.getPoints());
                return silverMember;
            }
        }
        return member;
    }

    private BonusMember checkGoldLimit(int memberNo, LocalDate localDate) {
        BonusMember member = findMember(memberNo);
        if (member != null) {
            if (member.findQualificationPoints(localDate) >= GOLD_LIMIT) {
                GoldMember goldMember = new GoldMember(member.getMemberNo(), member.getPersonals(), member.getEnrolledDate(), member.getPoints());
                return goldMember;
            }
        }
        return member;
    }

    private BonusMember findMember(int memberNo) {
        for (BonusMember m: memberList) {
            if (m.getMemberNo() == memberNo) {
                return m;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        MemberArchive memberArchive = new MemberArchive();
        memberArchive.test();
    }

    public void test() {
        Personals ole = new Personals("Ole", "Olsen", "ole.olsen@dot.com", "ole");
        Personals tove = new Personals("Tove", "Hansen", "tove.hansen@dot.com", "tove");
        Personals arne = new Personals("Arne", "Arnesen", "arne.arnesen@dot.com", "arne");
        LocalDate testdato = LocalDate.of(2008, 2, 10);

        // Legg til 3 personer som BasicMember og 0 poeng - 1 med enroll for over ett år siden
        memberList.add(new BasicMember(1, ole, LocalDate.of(2006, 2, 15)));
        memberList.add(new BasicMember(2, tove, LocalDate.of(2007, 2, 11)));
        memberList.add(new BasicMember(3, arne, LocalDate.of(2008, 1, 15)));

        // Registrer poeng 30.000 til 2 og 80.000 til en
        registerPoints(3, 50000);
        for (int i = 1; i < 4; i++) {
            registerPoints(i, 30000);
            System.out.println(findMember(i).getPersonals().getFirstname() + ", " + findMember(i).getClass() + ", " + findPoints(i, findMember(i).getPersonals().getFirstname()));
        }
        System.out.println("");

        // Sjekk antall poeng og evt oppgrader medlemskap
        checkMembers(testdato);

        // Registrer 10.000 poeng på alle
        for (int i = 1; i < 4; i++) {
            registerPoints(i, 10000);
            System.out.println(findMember(i).getPersonals().getFirstname() + ", " + findMember(i).getClass() + ", " + findPoints(i, findMember(i).getPersonals().getFirstname()));
        }
    }
}
