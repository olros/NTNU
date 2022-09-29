import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Deck {
    
    private ArrayList<Card> cards;

    public static void main(String[] args) {
        Deck deck = new Deck();
        deck.createDeck();

        ArrayList<Card> rCards = deck.assign(12);

        deck.printAllSpades(rCards);
        deck.getAllHearts(rCards).stream().forEach(c -> System.out.println(c));
        deck.getColors(rCards).stream().forEach(c -> System.out.println(c));
        System.out.println(deck.getSum(rCards));
        System.out.println(deck.spadesOfQueen(rCards));
        System.out.println(deck.isFlush(rCards));
    }

    public void createDeck() {
        cards = new ArrayList<>();
        for (int i = 1; i < 14; i++) {
            cards.add(new Card('S', i));
        }
        for (int i = 1; i < 14; i++) {
            cards.add(new Card('H', i));
        }
        for (int i = 1; i < 14; i++) {
            cards.add(new Card('D', i));
        }
        for (int i = 1; i < 14; i++) {
            cards.add(new Card('C', i));
        }
    }

    public ArrayList<Card> assign(int amountOfCards) {
        if (amountOfCards < 1 || amountOfCards > 52) return null;
        ArrayList<Card> randomCards = new ArrayList<>();
        Random r = new Random();
        outerloop:
        for (int i = 0; i < amountOfCards; i++) {
            Card rCard = cards.get(r.nextInt(52));
            for (Card c: randomCards) {
                if (rCard.getSuit() == c.getSuit() && rCard.getFace() == c.getFace()) {
                    i--;
                    continue outerloop;
                }
            }
            randomCards.add(rCard);
        }
        return randomCards;
    }

    public void printAllSpades(ArrayList<Card> rCards) {
        rCards.stream().filter(c -> c.getSuit() == 'S').forEach(c -> System.out.println(c));
    }

    public ArrayList<Card> getAllHearts(ArrayList<Card> rCards) {
        ArrayList<Card> hearts = rCards.stream().filter(c -> c.getSuit() == 'H').collect(Collectors.toCollection(ArrayList::new));
        return hearts;
    }

    public ArrayList<String> getColors(ArrayList<Card> rCards) {
        ArrayList<String> result = rCards.stream().map(c -> {
            if ('H' == c.getSuit() || 'C' == c.getSuit()) return "Red";
            else return "Black";
        }).collect(Collectors.toCollection(ArrayList::new));
        return result;
    }

    public int getSum(ArrayList<Card> rCards) {
        return rCards.stream().map(Card::getFace).reduce((a, b) -> a+b).get();
    }

    public boolean spadesOfQueen(ArrayList<Card> rCards) {
        return rCards.stream().anyMatch(c -> c.getSuit() == 'S' && c.getFace() == 12);
    }

    public boolean isFlush(ArrayList<Card> rCards) {
        if (rCards.stream().filter(c -> c.getSuit() == 'S').count() > 4) return true;
        if (rCards.stream().filter(c -> c.getSuit() == 'H').count() > 4) return true;
        if (rCards.stream().filter(c -> c.getSuit() == 'D').count() > 4) return true;
        if (rCards.stream().filter(c -> c.getSuit() == 'C').count() > 4) return true;
        return false;
    }
}
