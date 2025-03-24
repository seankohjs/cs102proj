import java.util.*;

class ParadeLine {
    private List<Card> cardsInLine;

    public ParadeLine() {
        cardsInLine = new ArrayList<>();
    }

    public void addCardToLine(Card card) {
        cardsInLine.add(card);
    }

    public void removeCards(List<Card> cardsToRemove) {
        cardsInLine.removeAll(cardsToRemove);
    }

    public List<Card> getParadeLineCards() {
        return new ArrayList<>(cardsInLine);
    }

}
