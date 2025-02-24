import java.util.*;


enum Suit {
    RED, BLUE, GREEN, YELLOW, PURPLE, GREY
}

class Card {
    private Suit suit;
    private int value;

    public Card(Suit suit, int value) {
        this.suit = suit;
        this.value = value;
    }

    public Suit getSuit() {
        return suit;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return suit.toString().charAt(0) + suit.toString().substring(1).toLowerCase() + " " + value;
    }

}

class Deck {
    private List<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        Suit[] suits = Suit.values();
        for (Suit suit : suits) {
            for (int value = 0; value <= 10; value++) {
                cards.add(new Card(suit, value));
            }
        }
        shuffle();
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card drawCard() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(0);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public int getCardCount() {
        return cards.size();
    }
}

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

class Player {
    private String playerName;
    private List<Card> hand;
    private List<Card> collectedCards;

    public int calculateScore() { // <--- Add this method back into Player class
        int score = 0;
        Map<Suit, Integer> suitCounts = new HashMap<>();
        for (Card card : collectedCards) {
            Suit suit = card.getSuit();
            suitCounts.put(suit, suitCounts.getOrDefault(suit, 0) + 1);
        }
        // For now, just sum up face values in Player class calculateScore()
        // Majority logic is handled in Game class
        for (Card card : collectedCards) {
            score += card.getValue();
        }
        return score;
    }

    public Player(String playerName) {
        this.playerName = playerName;
        this.hand = new ArrayList<>();
        this.collectedCards = new ArrayList<>();
    }

    public String getPlayerName() {
        return playerName;
    }

    public List<Card> getHand() {
        return hand;
    }

    public List<Card> getCollectedCards() {
        return collectedCards;
    }

    public void addToHand(Card card) {
        hand.add(card);
    }

    public void removeFromHand(Card card) {
        hand.remove(card);
    }

    public void addCollectedCards(List<Card> cards) {
        collectedCards.addAll(cards);
    }

    public void discardHandToSize(int maxSize) {
        // In PvP, player chooses discard, in simple AI, discard first
        while (hand.size() > maxSize) {
            hand.remove(0); // Simple discard strategy for example
        }
    }

    public int getCardCountInSuit(Suit suit) {
        int count = 0;
        for (Card card : collectedCards) {
            if (card.getSuit() == suit) {
                count++;
            }
        }
        return count;
    }

    public void addHandToCollection() { // For scoring, add remaining hand cards
        collectedCards.addAll(hand);
        hand.clear(); // Hand is now empty after scoring
    }

    public void discardTwoHandCards() { // Discard 2 cards after game end, player chooses ideally
        while (hand.size() > 2) {
            hand.remove(0); // Simple discard strategy for example - improve in UI version
        }
    }

}

public class Game {
    private Deck deck;
    private ParadeLine paradeLine;
    private List<Player> players;
    private int currentPlayerIndex;
    private boolean isLastRound;
    private boolean sixColorsCollectedBySomeone;
    private boolean deckExhaustedLastRound; // Flag for deck exhaustion last round
    private Scanner scanner;

    public Game(List<String> playerNames) {
        this.deck = new Deck();
        this.paradeLine = new ParadeLine();
        this.players = new ArrayList<>();
        for (String name : playerNames) {
            this.players.add(new Player(name));
        }
        this.currentPlayerIndex = 0;
        this.isLastRound = false;
        this.sixColorsCollectedBySomeone = false;
        this.deckExhaustedLastRound = false; // Initialize flag
        this.scanner = new Scanner(System.in);

        // Deal initial parade line (6 cards)
        for (int i = 0; i < 6; i++) {
            Card card = deck.drawCard();
            if (card != null) {
                paradeLine.addCardToLine(card);
            } else {
                break;
            }
        }

        // Deal 5 cards to each player
        for (Player player : players) {
            for (int i = 0; i < 5; i++) {
                Card card = deck.drawCard();
                if (card != null) {
                    player.addToHand(card);
                } else {
                    break;
                }
            }
        }
    }

    public void startGame() {
        while (!isGameOver()) {
            Player currentPlayer = getCurrentPlayer();
            System.out.println("\n=======================================");
            System.out.println("It's " + currentPlayer.getPlayerName() + "'s turn!");
            printGameState();
            printPlayerCollections(currentPlayer);

            if (!currentPlayer.getHand().isEmpty()) {
                Card cardToPlay = getPlayerCardChoice(currentPlayer);
                playTurn(cardToPlay);
            } else {
                System.out.println(currentPlayer.getPlayerName() + " has no cards to play! Passing turn.");
                nextPlayer();
            }

            if (!isLastRound) { // Only check for game end if not already in last round
                checkGameEndConditions(); // Combined check for 6 colors and deck exhaustion
            }

            if (!isLastRound) {
                nextPlayer();
            } else if (isLastRound && getCurrentPlayerIndex() == 0) {
                break; // Game loop ends after last round completes
            }
        }
        endGame();
        scanner.close();
    }

    private void checkGameEndConditions() {
        if (!isLastRound) { // Only check if last round not already triggered
            if (hasAnyoneCollectedSixColors()) {
                startLastRound(true); // 6th color triggered
            } else if (deck.isEmpty()) {
                startLastRound(false); // Deck exhausted triggered
                deckExhaustedLastRound = true; // Set the flag
            }
        }
    }

    private boolean hasAnyoneCollectedSixColors() {
        for (Player player : players) {
            if (hasCollectedSixColors(player)) {
                return true;
            }
        }
        return false;
    }

    public void playTurn(Card playedCard) {
        Player currentPlayer = getCurrentPlayer();
        System.out.println(currentPlayer.getPlayerName() + " plays " + playedCard);
        paradeLine.addCardToLine(playedCard);

        RemovalChoice removalChoice = determineCardsToRemove(playedCard);
        List<Card> cardsToRemoveThisTurn = new ArrayList<>();
        Card cardToRemove;

        if (!removalChoice.sameSuitCandidates.isEmpty()) {
            cardToRemove = getPlayerRemovalChoice(scanner, currentPlayer, removalChoice.sameSuitCandidates,
                    "same suit");
            if (cardToRemove != null) {
                cardsToRemoveThisTurn.add(cardToRemove);
            }
        }

        if (!removalChoice.lowerValueCandidates.isEmpty()) {
            List<Card> lowerValueCandidatesAfterSameSuit = new ArrayList<>(removalChoice.lowerValueCandidates);
            lowerValueCandidatesAfterSameSuit.removeAll(cardsToRemoveThisTurn);

            if (!lowerValueCandidatesAfterSameSuit.isEmpty()) {
                cardToRemove = getPlayerRemovalChoice(scanner, currentPlayer, lowerValueCandidatesAfterSameSuit,
                        "lower value");
                if (cardToRemove != null) {
                    cardsToRemoveThisTurn.add(cardToRemove);
                }
            }
        }

        if (!cardsToRemoveThisTurn.isEmpty()) {
            System.out.println(currentPlayer.getPlayerName() + " takes " + cardListToString(cardsToRemoveThisTurn)
                    + " from the parade.");
            paradeLine.removeCards(cardsToRemoveThisTurn);
            currentPlayer.addCollectedCards(cardsToRemoveThisTurn);
        } else {
            System.out.println(currentPlayer.getPlayerName() + " takes no card from the parade.");
        }

        if (!isLastRound) { // Only draw if not in last round
            Card drawnCard = deck.drawCard();
            if (drawnCard != null) {
                currentPlayer.addToHand(drawnCard);
                System.out.println(currentPlayer.getPlayerName() + " draws a card.");
            } else {
                System.out.println("Deck is empty, no card drawn.");
            }
            currentPlayer.discardHandToSize(7);
        } else {
            System.out.println(currentPlayer.getPlayerName() + " does not draw card in last round."); // Indicate no
                                                                                                      // draw in last
                                                                                                      // round
        }

        printPlayerCollections(currentPlayer);
    }

    private void endGame() {
        System.out.println("\nGame Over!");

        System.out.println("\n--- Player Collections Before Scoring ---"); // ADDED THIS SECTION
        for (Player player : players) { // LOOP THROUGH PLAYERS
            printPlayerCollections(player); // PRINT COLLECTION FOR EACH PLAYER
        } // END OF ADDED SECTION

        if (!deckExhaustedLastRound) { // Only discard hand cards if not deck exhaustion end

            System.out.println("\n--- Discarding 2 Hand Cards for Scoring ---");
            for (Player player : players) {
                System.out.println("\n" + player.getPlayerName() + ", choose 2 cards to discard from hand: "
                        + handToString(player.getHand()));
                player.discardTwoHandCards(); // In UI, let player choose, for now auto-discard
                System.out.println(player.getPlayerName() + " discards hand to: " + handToString(player.getHand()));
                player.addHandToCollection(); // Add remaining hand cards to collection for score
            }
        } else {
            System.out.println("\n--- Hand cards are NOT discarded because game ended due to deck exhaustion ---");
            for (Player player : players) {
                player.addHandToCollection(); // Still add remaining hand cards to collection for score
            }
        }

        calculateFinalScores();
        determineWinner();
    }

    private void calculateFinalScores() {
        System.out.println("\n--- Scores ---");
        Map<Suit, Player> suitMajorities = determineSuitMajorities();

        for (Player player : players) {
            int baseScore = 0;
            int majorityScore = 0;
            for (Suit suit : Suit.values()) {
                int suitCardCount = player.getCardCountInSuit(suit);
                if (suitMajorities.get(suit) == player) {
                    majorityScore += suitCardCount;
                } else {
                    for (Card card : player.getCollectedCards()) {
                        if (card.getSuit() == suit) {
                            baseScore += card.getValue();
                        }
                    }
                }
            }
            int totalScore = baseScore + majorityScore;
            System.out.println(player.getPlayerName() + ": Base Score = " + baseScore + ", Majority Score = "
                    + majorityScore + ", Total Score = " + totalScore);
            player.calculateScore(); // Recalculate and store score in player object if needed for tie-breaker later.
        }
    }

    private void startLastRound(boolean sixColors) {
        isLastRound = true;
        System.out.println("\n--- Last Round Started! ---");
        if (sixColors) {
            System.out.println("Triggered by a player collecting 6 colors.");
        } else {
            System.out.println("Triggered by deck exhaustion.");
        }
        if (sixColors) {
            currentPlayerIndex = (currentPlayerIndex + players.size() - 1) % players.size(); // To ensure everyone
                                                                                             // including trigger player
                                                                                             // gets last turn
        }
    }

    public boolean isGameOver() {
        if (isLastRound) {
            return getCurrentPlayerIndex() == 0; // Game Over after last round completes full circle
        }
        return false;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of players (2-6): ");
        int numPlayers = 0;
        while (numPlayers < 2 || numPlayers > 6) {
            if (scanner.hasNextInt()) {
                numPlayers = scanner.nextInt();
                scanner.nextLine();
                if (numPlayers < 2 || numPlayers > 6) {
                    System.out.println("Invalid number of players. Please enter a number between 2 and 6.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }

        List<String> playerNames = new ArrayList<>();
        for (int i = 1; i <= numPlayers; i++) {
            System.out.print("Enter name for Player " + i + ": ");
            playerNames.add(scanner.nextLine());
        }

        Game game = new Game(playerNames);
        game.startGame();
    }

    private Card getPlayerCardChoice(Player currentPlayer) {
        while (true) { // ... (same as before) ... }
            System.out.println("\nYour hand: " + handToString(currentPlayer.getHand()));
            System.out.print("Enter the index of the card you want to play (0 to "
                    + (currentPlayer.getHand().size() - 1) + "): ");
            if (scanner.hasNextInt()) {
                int cardIndex = scanner.nextInt();
                scanner.nextLine();
                if (cardIndex >= 0 && cardIndex < currentPlayer.getHand().size()) {
                    return currentPlayer.getHand().remove(cardIndex);
                } else {
                    System.out.println("Invalid card index. Please enter a valid index.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    private RemovalChoice determineCardsToRemove(Card playedCard) {
        List<Card> sameSuitCards = new ArrayList<>();
        List<Card> lowerValueCards = new ArrayList<>();
        List<Card> currentParadeLine = paradeLine.getParadeLineCards();
        int playedCardValue = playedCard.getValue();

        if (currentParadeLine.size() <= 1) { // No cards to remove if parade line has 0 or 1 card
            return new RemovalChoice(sameSuitCards, lowerValueCards); // Return empty lists
        }

        List<Card> lineBeforePlayed = currentParadeLine.subList(0, currentParadeLine.size() - 1);
        int lineSizeBeforePlayed = lineBeforePlayed.size();

        if (lineSizeBeforePlayed <= playedCardValue) { // If line size <= played value, no removal
            return new RemovalChoice(sameSuitCards, lowerValueCards); // Return empty lists
        }

        List<Card> removalModeCards = new ArrayList<>();
        // Identify cards in "removal mode" based on position
        for (int i = 0; i < lineSizeBeforePlayed; i++) {
            if (lineSizeBeforePlayed - i > playedCardValue) { // Position is lineSizeBeforePlayed - i (counting from
                                                              // end, 1-indexed)
                removalModeCards.add(lineBeforePlayed.get(i)); // These are in removal mode
            }
        }

        // Check for same suit and lower value ONLY among cards in 'removalModeCards'
        for (Card cardInRemovalMode : removalModeCards) {
            if (cardInRemovalMode.getSuit() == playedCard.getSuit()) {
                sameSuitCards.add(cardInRemovalMode);
            }
            if (cardInRemovalMode.getValue() <= playedCardValue) {
                lowerValueCards.add(cardInRemovalMode);
            }
        }

        return new RemovalChoice(sameSuitCards, lowerValueCards);
    }

    public static class RemovalChoice {
        public List<Card> sameSuitCandidates;
        public List<Card> lowerValueCandidates;

        public RemovalChoice(List<Card> sameSuitCandidates, List<Card> lowerValueCandidates) {
            this.sameSuitCandidates = sameSuitCandidates;
            this.lowerValueCandidates = lowerValueCandidates;
        }

        public boolean hasCandidates() {
            return !sameSuitCandidates.isEmpty() || !lowerValueCandidates.isEmpty();
        }
    }

    private Card getPlayerRemovalChoice(Scanner scanner, Player currentPlayer, List<Card> candidates,
            String choiceType) {
        System.out.println("\nEligible cards to take (" + choiceType + "): " + cardListToStringWithIndices(candidates));
        if (candidates.isEmpty()) {
            return null;
        }

        while (true) {
            System.out.print("Enter the index of the card you want to take (0 to " + (candidates.size() - 1)
                    + "), or -1 to take none: ");
            if (scanner.hasNextInt()) {
                int cardIndex = scanner.nextInt();
                scanner.nextLine();
                if (cardIndex == -1) {
                    return null;
                } else if (cardIndex >= 0 && cardIndex < candidates.size()) {
                    return candidates.get(cardIndex);
                } else {
                    System.out.println("Invalid card index. Please enter a valid index or -1.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    private void nextPlayer() { // ... (same as before) ...}
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public Player getCurrentPlayer() { // ... (same as before) ...}
        return players.get(currentPlayerIndex);
    }

    public int getCurrentPlayerIndex() { // ... (same as before) ...}
        return currentPlayerIndex;
    }

    private boolean hasCollectedSixColors(Player player) { // ... (same as before) ...}
        List<Suit> collectedSuits = new ArrayList<>();
        for (Card card : player.getCollectedCards()) {
            if (!collectedSuits.contains(card.getSuit())) {
                collectedSuits.add(card.getSuit());
            }
        }
        return collectedSuits.size() >= 6;
    }

    private Map<Suit, Player> determineSuitMajorities() { // ... (same as before) ...}
        Map<Suit, Player> majorities = new HashMap<>();
        for (Suit suit : Suit.values()) {
            Player majorityPlayer = null;
            int maxCount = -1;
            List<Player> tiedPlayers = new ArrayList<>();

            for (Player player : players) {
                int count = player.getCardCountInSuit(suit);
                if (count > maxCount) {
                    maxCount = count;
                    majorityPlayer = player;
                    tiedPlayers.clear();
                    tiedPlayers.add(player);
                } else if (count == maxCount && count > 0) {
                    tiedPlayers.add(player);
                }
            }
            if (tiedPlayers.size() == 1) {
                majorities.put(suit, majorityPlayer);
            } else {
                majorities.put(suit, null);
            }
        }
        return majorities;
    }

    private void determineWinner() { // ... (same as before) ...}
        Player winner = null;
        int minScore = Integer.MAX_VALUE;
        List<Player> tiedWinners = new ArrayList<>();

        for (Player player : players) {
            int playerScore = calculatePlayerFinalScore(player, determineSuitMajorities());
            if (playerScore < minScore) {
                minScore = playerScore;
                winner = player;
                tiedWinners.clear();
                tiedWinners.add(player);
            } else if (playerScore == minScore) {
                tiedWinners.add(player);
            }
        }

        if (tiedWinners.size() == 1) {
            System.out.println("\nWinner: " + winner.getPlayerName() + " with score " + minScore + "!");
        } else {
            System.out.println("\nTie between: " + playerListToString(tiedWinners) + " with score " + minScore + "!");
            Player tiebreakerWinner = determineTiebreakerWinner(tiedWinners);
            if (tiebreakerWinner != null) {
                System.out.println("Tiebreaker Winner (fewest collected cards): " + tiebreakerWinner.getPlayerName());
            } else {
                System.out.println("Tiebreaker could not be resolved or not implemented.");
            }
        }
    }

    private int calculatePlayerFinalScore(Player player, Map<Suit, Player> suitMajorities) { // ... (same as before)
                                                                                             // ...}
        int baseScore = 0;
        int majorityScore = 0;
        for (Suit suit : Suit.values()) {
            int suitCardCount = player.getCardCountInSuit(suit);
            if (suitMajorities.get(suit) == player) {
                majorityScore += suitCardCount;
            } else {
                for (Card card : player.getCollectedCards()) {
                    if (card.getSuit() == suit) {
                        baseScore += card.getValue();
                    }
                }
            }
        }
        return baseScore + majorityScore;
    }

    private Player determineTiebreakerWinner(List<Player> tiedPlayers) { // ... (same as before) ...}
        Player tiebreakerWinner = null;
        int minCollectedCardsCount = Integer.MAX_VALUE;

        for (Player player : tiedPlayers) {
            int collectedCardCount = player.getCollectedCards().size();
            if (collectedCardCount < minCollectedCardsCount) {
                minCollectedCardsCount = collectedCardCount;
                tiebreakerWinner = player;
            }
        }
        return tiebreakerWinner;
    }

    private String cardListToString(List<Card> cardList) { // ... (same as before) ...}
        StringBuilder sb = new StringBuilder();
        for (Card card : cardList) {
            sb.append(card.toString()).append(", ");
        }
        if (sb.length() > 2) {
            sb.setLength(sb.length() - 2);
        }
        return "[" + sb.toString() + "]";
    }

    private String playerListToString(List<Player> playerList) { // ... (same as before) ...}
        StringBuilder sb = new StringBuilder();
        for (Player player : playerList) {
            sb.append(player.getPlayerName()).append(", ");
        }
        if (sb.length() > 2) {
            sb.setLength(sb.length() - 2);
        }
        return "[" + sb.toString() + "]";
    }

    private String handToString(List<Card> hand) { // ... (same as before) ...}
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < hand.size(); i++) {
            sb.append(i).append(":").append(hand.get(i).toString());
            if (i < hand.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private String cardListToStringWithIndices(List<Card> cardList) { // ... (same as before) ...}
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < cardList.size(); i++) {
            sb.append(i).append(":").append(cardList.get(i).toString());
            if (i < cardList.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private void printGameState() { // ... (same as before) ...}
        System.out.println("\n--- Game State ---");
        System.out.println("Parade Line: " + cardListToString(paradeLine.getParadeLineCards()));
        System.out.println("Cards in Deck: " + deck.getCardCount());
    }

    private void printPlayerCollections(Player player) { // ... (same as before) ...}
        System.out.println("\n--- " + player.getPlayerName() + "'s Collected Cards ---");
        Map<Suit, List<Card>> collectionsBySuit = new HashMap<>();
        for (Suit suit : Suit.values()) {
            collectionsBySuit.put(suit, new ArrayList<>());
        }
        for (Card card : player.getCollectedCards()) {
            collectionsBySuit.get(card.getSuit()).add(card);
        }

        for (Suit suit : Suit.values()) {
            List<Card> cardsOfSuit = collectionsBySuit.get(suit);
            if (!cardsOfSuit.isEmpty()) {
                System.out.println(suit.toString().charAt(0) + suit.toString().substring(1).toLowerCase() + ": "
                        + cardListToString(cardsOfSuit));
            }
        }
        if (player.getCollectedCards().isEmpty()) {
            System.out.println("None collected yet.");
        }
    }
}