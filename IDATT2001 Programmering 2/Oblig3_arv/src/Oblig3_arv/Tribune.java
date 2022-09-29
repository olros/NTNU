package Oblig3_arv;

public abstract class Tribune {
    private final String tribuneName;
    private final int capacity;
    private final int price;

    public Tribune(String tribuneName, int capacity, int price) {
        this.tribuneName = tribuneName;
        this.capacity = capacity;
        this.price = price;
    }

    public String getTribuneName() {
        return this.tribuneName;
    }
    public int getCapacity() {
        return this.capacity;
    }
    public int getPrice() {
        return this.price;
    }

    public abstract int findNumberOfSoldTickets();
    public int findIncome() {
        return this.price * findNumberOfSoldTickets();
    }

    public abstract Ticket[] buyTicket(int noTickets);
    public abstract Ticket[] buyTicket(String[] names);

    @Override
    public String toString() {
        return "Tribune {" +
                "navn='" + tribuneName + '\'' +
                ", kapasitet=" + capacity +
                ", solgte billetter=" + findNumberOfSoldTickets() +
                ", inntekt=" + findIncome() +
                '}';
    }
}
