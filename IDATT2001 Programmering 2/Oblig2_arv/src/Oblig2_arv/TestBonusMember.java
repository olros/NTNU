package Oblig2_arv;

import java.time.LocalDate;

class TestBonusMember {
    public static void main(String[] args) throws Exception {
        Personals ole = new Personals("Olsen", "Ole", "ole.olsen@dot.com", "ole");
        Personals tove = new Personals("Hansen", "Tove", "tove.hansen@dot.com", "tove");
        LocalDate testdato = LocalDate.of(2008, 2, 10);
        System.out.println("Totalt antall tester: 8");
        BasicMember b1 = new BasicMember(100, ole, LocalDate.of(2006, 2, 15));
        b1.registerPoints(30000);
        if (b1.findQualificationPoints(testdato) == 0 && b1.getPoints() == 30000) {
            System.out.println("Test 1 ok");
        } else {
            System.out.println(b1.findQualificationPoints(testdato) + ", " + b1.getPoints());
        }
        b1.registerPoints(15000);
        if (b1.findQualificationPoints(testdato) == 0 && b1.getPoints() == 45000) {
            System.out.println("Test 2 ok");
        }
        BasicMember b2 = new BasicMember(110, tove, LocalDate.of(2007, 3, 5));
        b2.registerPoints(30000);
        if (b2.findQualificationPoints(testdato) == 30000 && b2.getPoints() == 30000) {
            System.out.println("Test 3 ok");
        }
        SilverMember b3 = new SilverMember(b2.getMemberNo(), b2.getPersonals(),
                b2.getEnrolledDate(), b2.getPoints());
        b3.registerPoints(50000);
        if (b3.findQualificationPoints(testdato) == 90000 && b3.getPoints() == 90000) {
            System.out.println("Test 4 ok");
        }
        GoldMember b4 = new GoldMember(b3.getMemberNo(), b3.getPersonals(),
                b3.getEnrolledDate(), b3.getPoints());
        b4.registerPoints(30000);
        if (b4.findQualificationPoints(testdato) == 135000 && b4.getPoints() == 135000) {
            System.out.println("Test 5 ok");
        }
        testdato = LocalDate.of(2008, 12, 10);
        if (b4.findQualificationPoints(testdato) == 0 && b4.getPoints() == 135000) {
            System.out.println("Test 6 ok");
        }
        if (!ole.okPassword("OOO")) {
            System.out.println("Test 7 ok");
        }
        if (tove.okPassword("tove")) {
            System.out.println("Test 8 ok");
        }
    }
}
