package com.parade.ai;

import java.util.*;
import com.parade.model.*;
import com.parade.controller.RemovalStrategy;

public class BotPlayer extends Player {
    private int difficulty; // 1: Easy (random), 2: Hard
    private Random random;

    public BotPlayer(String playerName, int difficulty) {
        super(playerName);
        this.difficulty = difficulty;
        this.random = new Random();
    }

    public Card selectCard(ParadeLine paradeLine, List<Player> players) {
        List<Card> hand = getHand();
        if (hand.isEmpty())
            return null;

        // Return different card based on difficulty
        switch (difficulty) {
            case 2:
                return makeHardDecision(paradeLine, players);
            case 1:
            default:
                return makeEasyDecision();
        }
    }

    private Card makeEasyDecision() {
        // Random card selection
        int cardIndex = random.nextInt(getHand().size());
        return getHand().get(cardIndex);
    }

    private Card makeHardDecision(ParadeLine paradeLine, List<Player> players) {
        // Simple strategy: play the card that will remove the fewest cards
        Card bestCard = null;
        int minCardsRemoved = Integer.MAX_VALUE;

        for (Card card : getHand()) {
            List<Card> cardsToRemove = RemovalStrategy.determineRemovalChoice(
                    card, paradeLine.getParadeLineCards());

            if (cardsToRemove.size() < minCardsRemoved) {
                minCardsRemoved = cardsToRemove.size();
                bestCard = card;
            }
        }

        return bestCard != null ? bestCard : makeEasyDecision();
    }

    public Card selectCardToDiscard() {
        List<Card> hand = getHand();
        if (hand.isEmpty())
            return null;

        switch (difficulty) {
            case 2: // Hard difficulty
                return discardHighestValueCard();
            case 1: // Easy difficulty
            default:
                return discardRandomCard();
        }
    }

    private Card discardRandomCard() {
        // Random card selection - same as makeEasyDecision()
        int cardIndex = random.nextInt(getHand().size());
        return getHand().get(cardIndex);
    }

    private Card discardHighestValueCard() {
        // Find and return the highest value card
        Card highestCard = null;
        int highestValue = -1;

        for (Card card : getHand()) {
            if (card.getValue() > highestValue) {
                highestValue = card.getValue();
                highestCard = card;
            }
        }

        return highestCard != null ? highestCard : discardRandomCard();
    }
}