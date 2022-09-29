package Oblig3_arv;

import java.util.Arrays;
import java.util.Comparator;

public class Test {
    private Tribune[] tribuner = new Tribune[4];

    public static void main(String[] args) {
        Test t = new Test();
        t.test();
    }
    public void test() {
        Stand the_kop = new Stand("The Kop", 40, 30);
        Stand main_stand = new Stand("Main stand", 50, 20);
        Sit dalglish_stand = new Sit("Sir Kenny Dalglish stand", 40, 40, 4);
        VIP anfield_road_stand = new VIP("Anfield road stand", 44, 50, 11);

        Ticket[] kopTickets = the_kop.buyTicket(3);
        Ticket[] mainTickets = main_stand.buyTicket(1);
        Ticket[] dalglishTickets = dalglish_stand.buyTicket(3);
//        Ticket[] dalglishTickets2 = dalglish_stand.buyTicket(9);
        Ticket[] roadTickets = anfield_road_stand.buyTicket(new String[]{"Arne", "Tom", "Egil"});
//        Ticket[] roadTickets2 = anfield_road_stand.buyTicket(new String[]{"Arne2", "Tom2", "Egil2", "Erna"});

        System.out.println("");
        System.out.println("Billetter:");
        Arrays.stream(kopTickets).forEach(x -> System.out.println(x));
        Arrays.stream(mainTickets).forEach(x -> System.out.println(x));
        Arrays.stream(dalglishTickets).forEach(x -> System.out.println(x));
//        Arrays.stream(dalglishTickets2).forEach(x -> System.out.println(x));
        Arrays.stream(roadTickets).forEach(x -> System.out.println(x));
//        Arrays.stream(roadTickets2).forEach(x -> System.out.println(x));

        System.out.println("");
        System.out.println("Tribuner:");
        tribuner[0] = the_kop;
        tribuner[1] = main_stand;
        tribuner[2] = dalglish_stand;
        tribuner[3] = anfield_road_stand;
        Arrays.stream(tribuner).forEach(x -> System.out.println(x));

        System.out.println("");
        System.out.println("Tribuner sortert etter inntekt:");
        Arrays.stream(tribuner).sorted(Comparator.comparingInt(Tribune::findIncome).reversed()).forEach(x -> System.out.println(x));
    }
}
