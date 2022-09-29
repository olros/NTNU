package Oblig3_arv;

public class Stand extends Tribune {

    private int noSoldTickets;

    public Stand(String tribuneName, int capacity, int price) {
        super(tribuneName, capacity, price);
        this.noSoldTickets = 0;
    }

    @Override
    public int findNumberOfSoldTickets() {
        return this.noSoldTickets;
    }

    @Override
    public int findIncome() {
        return this.findNumberOfSoldTickets() * super.getPrice();
    }

    @Override
    public Ticket[] buyTicket(int noTickets) {
        if (super.getCapacity() - findNumberOfSoldTickets() >= noTickets) {
            Ticket[] tickets = new Ticket[noTickets];
            noSoldTickets += noTickets;
            for (int i = 0; i < noTickets; i++) {
                tickets[i] = new StandingTicket(super.getTribuneName(), super.getPrice());
            }
            return tickets;
        } else {
            return null;
        }
    }

    @Override
    public Ticket[] buyTicket(String[] names) {
        int noTickets = names.length;
        if (super.getCapacity() - findNumberOfSoldTickets() >= noTickets) {
            Ticket[] tickets = new Ticket[noTickets];
            noSoldTickets += noTickets;
            for (int i = 0; i < noTickets; i++) {
                tickets[i] = new StandingTicket(super.getTribuneName(), super.getPrice());
            }
            return tickets;
        } else {
            return null;
        }
    }
}
