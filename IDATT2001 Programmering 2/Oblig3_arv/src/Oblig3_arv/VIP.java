package Oblig3_arv;

import java.util.Arrays;

public class VIP extends Tribune {

    private String[][] spectator;

    public VIP(String tribuneName, int capacity, int price, int rows) {
        super(tribuneName, capacity, price);
        this.spectator = new String[rows][(capacity / rows)];
    }

    @Override
    public int findNumberOfSoldTickets() {
        int sold = 0;
        for (int i = 0; i < spectator.length; i++) {
            for (int j = 0; j < spectator[i].length; j++) {
                if (spectator[i][j] != null) sold++;
            }
        }
        return sold;
    }

    @Override
    public int findIncome() {
        return this.findNumberOfSoldTickets() * super.getPrice();
    }

    private int findRow(int noTickets) {
        for (int i = 0; i < spectator.length; i++) {
            int availableSeatsOnRow = (int) Arrays.stream(spectator[i]).filter(x -> x == null).count();
            if (availableSeatsOnRow >= noTickets) {
                return i;
            }
        }
        return -1;
    }

    private int reserveSeat(String name, int row) {
        for (int j = 0; j < spectator[row].length; j++) {
            if (spectator[row][j] == null) {
                spectator[row][j] = name;
                return j;
            }
        }
        return -1;
    }

    @Override
    public Ticket[] buyTicket(int noTickets) {
        return null;
    }

    @Override
    public Ticket[] buyTicket(String[] names) {
        int noTickets = names.length;
        int row = findRow(noTickets);
        if (row >= 0) {
            Ticket[] tickets = new Ticket[noTickets];
            for (int i = 0; i < noTickets; i++) {
                int seat = reserveSeat(names[i], row);
                tickets[i] = new SittingTicket(super.getTribuneName(), super.getPrice(), row + 1, seat + 1);
            }
            return tickets;
        }
        return null;
    }
}
