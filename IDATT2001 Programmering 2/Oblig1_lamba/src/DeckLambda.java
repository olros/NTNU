import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

public class DeckLambda {

    private ArrayList<Card> cards;

    public DeckLambda() {
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

    public static void main(String[] args) {
        DeckLambda deck = new DeckLambda();

        ArrayList<Card> rCards = deck.assign(10);

        rCards.stream().forEach(c -> System.out.println(c + " alle"));

        // Skriv ut spar
        IDeck<ArrayList<Card>> printSpades = (rc) -> rc.stream().filter(c -> c.getSuit() == 'S').collect(Collectors.toCollection(ArrayList::new));
        printSpades.get(rCards).stream().forEach(c -> System.out.println(c + " spar"));

        // Samle hjerter-kort i ny liste
        IDeck<ArrayList<Card>> getHeartsLambda = (rc) -> rc.stream().filter(c -> c.getSuit() == 'H').collect(Collectors.toCollection(ArrayList::new));
        getHeartsLambda.get(rCards).stream().forEach(c -> System.out.println(c));

        // Ny liste med kortenes farge
        IDeck<ArrayList<String>> getColors = (rc) -> rc.stream().map(c -> {if ('H' == c.getSuit() || 'C' == c.getSuit())  return "Red";  else  return "Black";}).collect(Collectors.toCollection(ArrayList::new));
        getColors.get(rCards).stream().forEach(c -> System.out.println(c));

        // Summen av kortverdiene
        IDeck<Integer> getSum = (rc) -> rc.stream().map(Card::getFace).reduce((a, b) -> a+b).get();
        System.out.println(getSum.get(rCards));

        // Sjekker om spar-dame er trukket ut
        IDeck<Boolean> getSpadesOfQueen = (rc) -> rc.stream().anyMatch(c -> c.getSuit() == 'S' && c.getFace() == 12);
        System.out.println(getSpadesOfQueen.get(rCards));

        // Sjekker om det eksiterer mer enn 5 av en av sortene
        IDeck<Boolean> isFlush = (rc) -> ((rc.stream().filter(c -> c.getSuit() == 'S').count() > 4) || (rc.stream().filter(c -> c.getSuit() == 'H').count() > 4) || (rc.stream().filter(c -> c.getSuit() == 'D').count() > 4) || (rc.stream().filter(c -> c.getSuit() == 'C').count() > 4));
        System.out.println(isFlush.get(rCards));
    }

    public ArrayList<Card> assign(int amountOfCards) {
        if (amountOfCards < 1 || amountOfCards > 52) return null;
        ArrayList<Card> randomCards = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < amountOfCards; i++) {
            int index = r.nextInt(cards.size());
            randomCards.add(cards.get(index));
            cards.remove(index);
        }
        return randomCards;
    }
}
