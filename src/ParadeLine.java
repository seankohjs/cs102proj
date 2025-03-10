import java.util.*;

class ParadeLine {
    private List<Card> cardsInLine;

    public ParadeLine() {
        cardsInLine = new ArrayList<>();
    }

    public void addCardToLine(Card card) {
        cardsInLine.add(card);
    }

    public int getLineSize() {
        return cardsInLine.size();
    }

    public Card getCardAt(int index) {
        if (index >= 0 && index < cardsInLine.size()) {
            return cardsInLine.get(index);
        }
        return null;
    }

    public void removeCards(List<Card> cardsToRemove) {
        cardsInLine.removeAll(cardsToRemove);
    }

    public List<Card> getParadeLineCards() {
        return new ArrayList<>(cardsInLine);
    }

    public void clearLine() {
        cardsInLine.clear();
    }
}
