package Oblig3_arv;

public class Sit extends Tribune {

    private int[] noBusy;

    public Sit(String tribuneName, int capacity, int price, int rows) {
        super(tribuneName, capacity, price);
        this.noBusy = new int[rows];
    }

    @Override
    public int findNumberOfSoldTickets() {
        int sold = 0;
        for (int i = 0; i < noBusy.length; i++) {
            sold += noBusy[i];
        }
        return sold;
    }

    @Override
    public int findIncome() {
        return this.findNumberOfSoldTickets() * super.getPrice();
    }

    private int findRow(int noTickets) {
        int seatsPerRow = super.getCapacity() / noBusy.length;
        for (int i = 0; i < noBusy.length; i++) {
            if (seatsPerRow - noBusy[i] >= noTickets) {
                return i;
            }
        }
        return -1;
    }

    private int reserveSeat(int row) {
        noBusy[row]++;
        return noBusy[row] - 1;
    }

    @Override
    public Ticket[] buyTicket(int noTickets) {
        int row = findRow(noTickets);
        if (row >= 0) {
            Ticket[] tickets = new Ticket[noTickets];
            for (int i = 0; i < noTickets; i++) {
                int seat = reserveSeat(row);
                tickets[i] = new SittingTicket(super.getTribuneName(), super.getPrice(), row + 1, seat + 1);
            }
            return tickets;
        } else {
            return null;
        }
    }

    @Override
    public Ticket[] buyTicket(String[] names) {
        int noTickets = names.length;
        int row = findRow(noTickets);
        if (row >= 0) {
            Ticket[] tickets = new Ticket[noTickets];
            for (int i = 0; i < noTickets; i++) {
                int seat = reserveSeat(row);
                tickets[i] = new SittingTicket(super.getTribuneName(), super.getPrice(), row + 1, seat + 1);
            }
            return tickets;
        } else {
            return null;
        }
    }
}
