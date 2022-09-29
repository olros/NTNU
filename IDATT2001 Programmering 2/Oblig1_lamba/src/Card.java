public class Card {

    private final char suit;
    private final int face;

    // initialises with suit ('S'=spade, 'H'=heart, 'D'=diamonds, 'C'=clubs)
    // and face (1=ace, 2, ... 10, 11=knight, 12=queen and 13=king).
    public Card(char suit, int face) {
        this.suit = suit;
        this.face = face;
    }

    @Override
    public String toString() {
        return String.format("%s%s", suit, face);
    }

    public char getSuit() {
        return suit;
    }

    public int getFace() {
        return face;
    }
}