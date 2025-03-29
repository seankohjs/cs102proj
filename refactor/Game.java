package refactor;

import java.util.*; // Import necessary utilities
import java.util.stream.Collectors;

public class Game {

    private final Deck deck;
    private final List<Player> players;
    private final Parade parade;
    private final TerminalView view; // Use the view for I/O
    private int currentPlayerIndex;
    private boolean isGameOver;

    // Constants
    private static final int INITIAL_HAND_SIZE = 6; // Standard Parade rule
    private static final int INITIAL_PARADE_SIZE = 6; // Standard Parade rule

    public Game(TerminalView view) {
        this.view = Objects.requireNonNull(view, "TerminalView cannot be null");
        this.deck = new Deck(); // Deck initializes itself
        this.players = new ArrayList<>();
        this.parade = new Parade();
        this.currentPlayerIndex = 0;
        this.isGameOver = false;
    }

    public void start() {
        view.displayWelcome();
        try {
            setupGame();
            if (!isGameOver && !players.isEmpty()) { // Check if setup failed or no players
                gameLoop();
                concludeGame();
            } else if (players.isEmpty()) {
                view.displayMessage("No players were added. Exiting game.");
            }
        } catch (Exception e) {
            // Catch potential unexpected errors during game flow
            view.displayMessage("\nAn unexpected error occurred: " + e.getMessage());
            e.printStackTrace(); // Log stack trace for debugging
        } finally {
            view.close(); // Ensure scanner is closed even if errors occur
        }
    }

    private void setupGame() {
        view.displayMessage("Setting up the game...");
        // Deck is already initialized and filled in its constructor
        deck.shuffle();
        view.displayMessage("Deck shuffled.");

        int numPlayers = view.getNumberOfPlayers();
        for (int i = 0; i < numPlayers; i++) {
            String name = view.getPlayerName(i + 1);
            players.add(new Player(name));
        }
        view.displayMessage("Players created: " + players.stream().map(Player::getName).collect(Collectors.joining(", ")));


        // Deal initial hands
        view.displayMessage("Dealing initial hands (" + INITIAL_HAND_SIZE + " cards each)...");
        for (int i = 0; i < INITIAL_HAND_SIZE; i++) {
            for (Player player : players) {
                Card dealtCard = deck.dealCard();
                if (dealtCard != null) {
                    player.addCardToHand(dealtCard);
                } else {
                    // This is highly unlikely with standard rules/deck size but possible
                    view.displayMessage("Error: Deck ran out of cards while dealing initial hands!");
                    isGameOver = true; // Cannot continue
                    return;
                }
            }
        }

        // Set up initial parade
        view.displayMessage("Setting up initial parade (" + INITIAL_PARADE_SIZE + " cards)...");
        for (int i = 0; i < INITIAL_PARADE_SIZE; i++) {
            Card paradeCard = deck.dealCard();
            if (paradeCard != null) {
                parade.addCard(paradeCard);
            } else {
                view.displayMessage("Error: Deck ran out of cards while setting up the initial parade!");
                isGameOver = true; // Cannot continue
                return;
            }
        }
        view.displayMessage("Game setup complete.");
    }

    private void gameLoop() {
        view.displayMessage("\n--- Starting Game Loop ---");
        while (!isGameOver) {
            Player currentPlayer = players.get(currentPlayerIndex);
            playTurn(currentPlayer);

            // Check end condition *after* the turn is fully completed (including draw)
            checkEndCondition();

            if (!isGameOver) {
                advancePlayer();
            }
        }
        view.displayMessage("--- Game Loop Ended ---");
    }

    private void playTurn(Player currentPlayer) {
        // Display current game state before the player makes a choice
        view.displayGameState(parade, players, currentPlayer);

        // Check if player can play (has cards) - should only happen if game ends exactly as their turn starts
        if (currentPlayer.hasEmptyHand() && deck.isEmpty()) {
             view.displayMessage(currentPlayer.getName() + " has no cards and the deck is empty. Turn skipped.");
             // End condition check will handle this scenario
             return;
        }
         // Handle the edge case where a player might start their turn with an empty hand but the deck isn't empty yet
         // This shouldn't normally happen if end conditions are checked correctly, but defensively:
         if (currentPlayer.hasEmptyHand() && !deck.isEmpty()) {
             view.displayMessage(currentPlayer.getName() + " has no cards. Drawing a card.");
             Card drawnCard = deck.dealCard();
             if (drawnCard != null) {
                 currentPlayer.addCardToHand(drawnCard);
                 view.displayDrawCard(currentPlayer, drawnCard);
                 // Allow turn to proceed if card was drawn
                 view.displayGameState(parade, players, currentPlayer); // Re-display with the new card
             } else {
                 view.displayMessage("Deck became empty unexpectedly. Cannot draw.");
                 // End condition check will handle this
                 return;
             }
         }

        // Player chooses a card
        int cardIndex = view.getCardChoice(currentPlayer);
        Card playedCard = currentPlayer.playCard(cardIndex); // Player removes card from hand

        view.displayPlayedCard(currentPlayer, playedCard);

        // Process capturing *before* adding the played card to the parade
        List<Card> capturedThisTurn = determineCapturedCards(playedCard);
        if (!capturedThisTurn.isEmpty()) {
            parade.removeCards(capturedThisTurn); // Remove from parade
            for(Card captured : capturedThisTurn) {
                currentPlayer.addCapturedCard(captured); // Add to player's capture pile
            }
        }
        view.displayCapturedCardsInfo(currentPlayer, capturedThisTurn);

        // Add the played card to the *end* of the parade
        parade.addCard(playedCard);

        // Player draws a card if the deck is not empty
        if (!deck.isEmpty()) {
            Card drawnCard = deck.dealCard();
            currentPlayer.addCardToHand(drawnCard); // drawnCard might be null if deck runs out exactly here
            view.displayDrawCard(currentPlayer, drawnCard);
        } else {
            // Deck is empty, player does not draw.
             view.displayDrawCard(currentPlayer, null); // Indicate deck is empty
        }

        view.displayTurnEnd(currentPlayer);
    }

    // Encapsulates the logic for determining which cards are captured from the parade
    private List<Card> determineCapturedCards(Card playedCard) {
        List<Card> potentiallyCaptured = new ArrayList<>();
        List<Card> currentParadeCards = parade.getCards(); // Get a snapshot (unmodifiable list)
        int paradeSize = currentParadeCards.size();

        // Rule 1: Card is "safe" if its rank is >= the number of cards currently in the parade.
        if (playedCard.getRank() >= paradeSize) {
            return Collections.emptyList(); // Nothing is captured
        }

        // Iterate through the parade cards to see which ones might be captured
        // The played card's rank determines how many cards at the *end* of the parade are immune.
        int immuneCount = playedCard.getRank();
        int checkUntilIndex = paradeSize - immuneCount; // Check cards from index 0 up to (but not including) this index

        for (int i = 0; i < checkUntilIndex; i++) {
            Card paradeCard = currentParadeCards.get(i);

            // Rule 2: Card is captured if its rank is <= played card's rank.
            boolean rankMatch = paradeCard.getRank() <= playedCard.getRank();
            // Rule 3: Card is captured if its suit is == played card's suit.
            boolean suitMatch = paradeCard.getSuit() == playedCard.getSuit();

            if (rankMatch || suitMatch) {
                potentiallyCaptured.add(paradeCard);
            }
        }

        return potentiallyCaptured;
    }


    private void advancePlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    // Checks if the game should end based on Parade rules
    private void checkEndCondition() {
        // Condition 1: Deck is empty.
        boolean deckEmpty = deck.isEmpty();

        // Condition 2: Any player has collected all 6 suits in their capture pile.
        // This rule seems less common online, verify if it applies to your version.
        // Let's omit this unless specifically required.

        // Condition 3: The game ends *immediately* after the turn where a player
        //              plays their last card OR the deck runs out.
        //              The current turn completes fully (play, capture, add to parade, attempt draw).
        boolean anyPlayerHandEmpty = players.stream().anyMatch(Player::hasEmptyHand);


        // The game ends if the deck is empty OR if any player's hand became empty *after their turn*.
        // The loop condition `!isGameOver` handles stopping. We just need to set the flag.
        if (deckEmpty || anyPlayerHandEmpty) {
            if (!isGameOver) { // Set flag only once
                 isGameOver = true;
                 view.displayMessage("\n--- Game Over Condition Met ---");
                 if (deckEmpty) view.displayMessage("Reason: Deck is empty.");
                 if (anyPlayerHandEmpty) view.displayMessage("Reason: A player's hand is empty.");
                 // The game loop will terminate after this check.
            }
        }
    }

   // Handles scoring and determining the winner after the game loop ends
   private void concludeGame() {
        view.displayMessage("\n--- Concluding Game ---");

        // Last step: Each player chooses one card from their remaining hand to add to their captured pile.
        // The rest of the hand cards are discarded (ignored for scoring).
        if (players.stream().anyMatch(p -> !p.hasEmptyHand())) { // Check if anyone has cards left
             view.displayMessage("Each player with cards remaining chooses one to keep for scoring...");
             for (Player player : players) {
                 if (!player.hasEmptyHand()) {
                     int finalChoiceIndex = view.getFinalCardChoice(player);
                     // We need the actual Card object to add to captured cards
                     // Since Player.getHand() returns unmodifiable, get it before removing
                     if (finalChoiceIndex >= 0 && finalChoiceIndex < player.getHand().size()) {
                         // Need a way to get the card at the index without modifying the hand directly yet
                         // Let's modify Player to allow retrieving a card by index or peek.
                         // OR, we can just get the card from the *original* hand before calling playCard again?
                         // Simpler: Get the card directly from the unmodifiable list.
                         Card cardToKeep = player.getHand().get(finalChoiceIndex);
                         player.addCapturedCard(cardToKeep); // Add the chosen card to score pile
                     }
                     // The remaining cards in hand are now irrelevant for scoring.
                     // We don't strictly need to clear the hand, just ignore it.
                 }
             }
        } else {
             view.displayMessage("All players' hands are empty. Proceeding to scoring.");
        }


        // Calculate Scores
        view.displayMessage("\nCalculating final scores...");
        Map<Suit, Integer> suitCounts = countAllCapturedSuits();
        Set<Suit> majoritySuits = findMajoritySuits(suitCounts);

        view.displayMessage("Total captured card counts per suit: " + suitCounts);
        if (!majoritySuits.isEmpty()) {
             view.displayMessage("Suit(s) with the most cards captured (score 1 point each): " + majoritySuits);
        } else {
             view.displayMessage("No cards were captured, or no single suit had a majority.");
        }


        for (Player player : players) {
            player.calculateFinalScore(majoritySuits); // Player calculates its score
        }

        // Determine Winner(s)
        List<Player> winners = determineWinners();

        // Display Results
        // Sort players by score for final display (lowest score first)
        players.sort(Comparator.comparingInt(Player::getScore));
        view.displayFinalScores(players);

        // Announce Winner(s)
        if (winners.isEmpty()) {
            // Should not happen if there are players
            view.displayMessage("Error: Could not determine a winner.");
        } else if (winners.size() == 1) {
            view.displayWinner(winners.get(0));
        } else {
            // Multiple winners means a tie even after tie-breaker rule
            view.displayDraw(winners);
        }
    }

    // Helper to count all captured cards by suit across all players
    private Map<Suit, Integer> countAllCapturedSuits() {
        Map<Suit, Integer> counts = new EnumMap<>(Suit.class);
        // Initialize map
        for (Suit s : Suit.values()) {
            counts.put(s, 0);
        }
        // Count cards
        for (Player p : players) {
            for (Card c : p.getCapturedCards()) { // Uses getter for unmodifiable list
                counts.put(c.getSuit(), counts.get(c.getSuit()) + 1);
            }
        }
        return counts;
    }

    // Helper to find which suit(s) have the most cards captured overall
    private Set<Suit> findMajoritySuits(Map<Suit, Integer> suitCounts) {
        Set<Suit> majority = EnumSet.noneOf(Suit.class);
        int maxCount = 0; // Start at 0, suits with 0 count won't be majority unless all are 0

        for (Map.Entry<Suit, Integer> entry : suitCounts.entrySet()) {
             int currentCount = entry.getValue();
             if (currentCount > maxCount) {
                maxCount = currentCount;
                majority.clear(); // Found a new highest count
                majority.add(entry.getKey());
            } else if (currentCount == maxCount && maxCount > 0) { // Ties for the current max (and max > 0)
                majority.add(entry.getKey());
            }
        }
        // If maxCount remains 0 (no cards captured), the set will be empty.
        // In this case, the rule implies no suit is 'majority', so all cards score their rank.
        // An empty set correctly signals this to calculateFinalScore.
        return majority;
    }


    // Finds player(s) with the lowest score, applying tie-breaker rule
    private List<Player> determineWinners() {
        List<Player> potentialWinners = new ArrayList<>();
        int lowestScore = Integer.MAX_VALUE;

        // Find all players with the minimum score
        for (Player player : players) {
            int score = player.getScore();
            if (score < lowestScore) {
                lowestScore = score;
                potentialWinners.clear();
                potentialWinners.add(player);
            } else if (score == lowestScore) {
                potentialWinners.add(player);
            }
        }

        // Apply tie-breaker: Fewest captured cards among those tied for lowest score
        if (potentialWinners.size() > 1) {
            List<Player> finalWinners = new ArrayList<>();
            int fewestCards = Integer.MAX_VALUE;

            for (Player tiedPlayer : potentialWinners) {
                int cardCount = tiedPlayer.getCapturedCards().size(); // Use getter
                if (cardCount < fewestCards) {
                    fewestCards = cardCount;
                    finalWinners.clear();
                    finalWinners.add(tiedPlayer);
                } else if (cardCount == fewestCards) {
                    finalWinners.add(tiedPlayer);
                }
            }
            return finalWinners; // Return the list after applying tie-breaker
        } else {
            // Only one player had the lowest score initially
            return potentialWinners;
        }
    }
}