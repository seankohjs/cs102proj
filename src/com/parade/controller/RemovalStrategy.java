package com.parade.controller;

import java.util.*;
import com.parade.model.Card;

public class RemovalStrategy {
    public static List<Card> determineRemovalChoice(Card playedCard, List<Card> paradeCards) {
        // Return an empty list if parade line has no cards
        List<Card> removalModeCards = new ArrayList<>();
        if (paradeCards.size() == 0) {
            return removalModeCards;
        }
        // Get size of parade line before latest card played
        List<Card> lineBeforePlayed = paradeCards.subList(0, paradeCards.size() - 1);
        int lineSizeBeforePlayed = lineBeforePlayed.size();
        
        // Return an empty list if 
        if (lineSizeBeforePlayed <= playedCard.getValue()) {
            return removalModeCards;
        }

        // Cards in removal mode: those with a position number larger than playedCard.getValue()
        for (int i = 0; i < lineSizeBeforePlayed; i++) {
            if (lineSizeBeforePlayed - i > playedCard.getValue()) {
                removalModeCards.add(lineBeforePlayed.get(i));
            }
        }

        // Determine which cards to remove based on color and value
        Iterator<Card> it = removalModeCards.iterator();
        while (it.hasNext()) {
            Card card = it.next();
            if (!(card.getColor() == playedCard.getColor() || card.getValue() <= playedCard.getValue())) {
                it.remove();
            }
        }

        return removalModeCards;
    }
}
