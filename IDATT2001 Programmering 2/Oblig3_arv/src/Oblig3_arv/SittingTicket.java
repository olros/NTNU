package Oblig3_arv;

class SittingTicket extends Ticket {
    private final int row;
    private final int place;
    public SittingTicket(String tribuneName, int price, int row, int place) {
        super(tribuneName, price);
        if (row < 0 || place < 0) {
            throw new IllegalArgumentException("Verken rad eller plass kan være negativ.\n"
                    + "Oppgitte verdier: " + row +
                    ", " + place);
        }
        this.row = row;
        this.place = place;
    }
    public int getRad() {
        return row;
    }
    public int getPlass() {
        return place;
    }
    public String toString() {
        String res = super.toString();
        res += " Rad: "+ row + " Plass: " + place;
        return res;
    }
}
