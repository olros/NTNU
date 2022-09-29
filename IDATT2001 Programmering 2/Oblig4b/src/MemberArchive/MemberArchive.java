package MemberArchive;

import Logger.MyLogger;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;

public class MemberArchive {

    public static final int SILVER_LIMIT = 25000;
    public static final int GOLD_LIMIT = 75000;
    private static final int MAKS_TRY = 1;
    private static final Random  RANDOM_NUMBER = new Random();
    private ArrayList<BonusMember> memberList;
    private MyLogger myLogger = new MyLogger();

    /**
     * Instantiates a new Member archive.
     */
    public MemberArchive() throws IOException {
        this.memberList = new ArrayList<>();
    }

    /**
     * Gets member list.
     *
     * @return the member list
     */
    public ArrayList<BonusMember> getMemberList() {
        return memberList;
    }

    /**
     * Find points int.
     *
     * @param memberNo the member no
     * @param password the password
     * @return the points of the member
     * @throws IllegalArgumentException if members was not found or password is incorrect
     */
    public int findPoints(int memberNo, String password) throws IllegalArgumentException {
        BonusMember m = findMember(memberNo);
        if (m != null && m.okPassword(password)) {
            return m.getPoints();
        } else {
            return -1;
        }
    }

    /**
     * Register points
     *
     * @param memberNo the member no
     * @param points   the points
     * @return boolean - true if registration was successful
     */
    public boolean registerPoints(int memberNo, int points) {
        try {
            BonusMember m = findMember(memberNo);
            if (m != null) {
                m.registerPoints(points);
                return true;
            }
        } catch (IllegalArgumentException e) {
            myLogger.getLogger().log(Level.FINE,e.getMessage());
        }
        return false;
    }

    /**
     * Register new member
     *
     * @param personals the personals
     * @param localDate the local date
     * @return the memberno of the new member
     * @throws IllegalArgumentException if personals or localdate is null
     */
    public int newMember(Personals personals, LocalDate localDate) throws IllegalArgumentException {
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

    /**
     * Check members.
     *
     * @param localDate the local date
     */
    public void checkMembers(LocalDate localDate) {
        for (int i = 0; i < memberList.size(); i++) {
            try {
                BonusMember m = memberList.get(i);
                if (m instanceof BasicMember) {
                    memberList.set(i, checkSilverLimit(m.getMemberNo(), localDate));
                    memberList.set(i, checkGoldLimit(m.getMemberNo(), localDate));
                } else if (m instanceof SilverMember) {
                    memberList.set(i, checkGoldLimit(m.getMemberNo(), localDate));
                }
            } catch (IllegalArgumentException e) {
                myLogger.getLogger().log(Level.FINE,e.getMessage());
            }
        }
    }

    private BonusMember checkSilverLimit(int memberNo, LocalDate localDate) throws IllegalArgumentException {
        if (localDate == null) {
            throw new IllegalArgumentException("Localdate cannot be null");
        }
        BonusMember member = findMember(memberNo);
        if (member != null) {
            if (member.findQualificationPoints(localDate) >= SILVER_LIMIT) {
                SilverMember silverMember = new SilverMember(member.getMemberNo(), member.getPersonals(), member.getEnrolledDate(), member.getPoints());
                return silverMember;
            }
        }
        return member;
    }

    private BonusMember checkGoldLimit(int memberNo, LocalDate localDate) throws IllegalArgumentException {
        if (localDate == null) {
            throw new IllegalArgumentException("Localdate cannot be null");
        }
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
}
