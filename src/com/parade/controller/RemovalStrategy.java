package com.parade.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.parade.model.*;

public class RemovalStrategy {
    public static List<Card> determineRemovalChoice(Card playedCard, List<Card> paradeCards) {
        List<Card> removalModeCards = new ArrayList<>();
        if (paradeCards.size() <= 1) {
            return removalModeCards;
        }

        List<Card> lineBeforePlayed = paradeCards.subList(0, paradeCards.size() - 1);
        int lineSizeBeforePlayed = lineBeforePlayed.size();

        if (lineSizeBeforePlayed <= playedCard.getValue()) {
            return removalModeCards;
        }

        // Cards in removal mode: those with a position number larger than playedCard.getValue()
        for (int i = 0; i < lineSizeBeforePlayed; i++) {
            if (lineSizeBeforePlayed - i > playedCard.getValue()) {
                removalModeCards.add(lineBeforePlayed.get(i));
            }
        }

        // Determine candidates based on color and value
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
