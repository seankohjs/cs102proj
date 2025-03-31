import java.util.*;

public class GameController {
    private Deck deck;
    private ParadeLine paradeLine;
    private List<Player> players;
    private TurnManager turnManager;
    private boolean isLastRound;
    private Scanner scanner;
    private int extraTurnCount = 0;
    private GameView view;

    public GameController(List<String> playerNames, List<Boolean> isBot,
            List<Integer> botDifficulties, Scanner scanner) {
        this.deck = new Deck();
        this.paradeLine = new ParadeLine();
        this.players = new ArrayList<>();
        this.scanner = scanner;

        // Create the appropriate player types
        for (int i = 0; i < playerNames.size(); i++) {
            if (isBot.get(i)) {
                players.add(new BotPlayer(playerNames.get(i), botDifficulties.get(i)));
            } else {
                players.add(new Player(playerNames.get(i)));
            }
        }

        this.turnManager = new TurnManager(players);
        this.isLastRound = false;
        this.view = new GameView(scanner);

        // Deal initial parade line of 6 cards
        for (int i = 0; i < 6; i++) {
            Card card = deck.drawCard();
            if (card != null) {
                paradeLine.addCardToLine(card);
            }
        }
        // Deal 5 cards to each player
        for (Player player : players) {
            for (int i = 0; i < 5; i++) {
                Card card = deck.drawCard();
                if (card != null) {
                    player.addToHand(card);
                }
            }
        }
    }

    public void startGame() {
        while (true) {
            GameView.clearScreen();
            Player currentPlayer = turnManager.getCurrentPlayer();
            view.displayTurnHeader(currentPlayer);
            view.displayGameState(deck, paradeLine, isLastRound);
            view.displayPlayerCollections(currentPlayer);
            view.displayOtherPlayersCollections(players, currentPlayer);

            if (!currentPlayer.getHand().isEmpty()) {
                Card cardToPlay = view.getPlayerCardChoice(currentPlayer, paradeLine, players);
                playTurn(cardToPlay);
            } else {
                view.displayMessage(currentPlayer.getPlayerName() + " has no cards to play! Passing turn.");
            }

            view.promptForNextTurn(currentPlayer);

            if (!isLastRound) {
                checkGameEndConditions();
                turnManager.nextPlayer();
            } else {
                turnManager.nextPlayer();
                extraTurnCount++;
                if (extraTurnCount >= players.size()) {
                    // All players have had their final turn - end the game
                    break;
                }
            }
        }

        // Call endGame() after exiting the main game loop
        endGame();
    }

    private void playTurn(Card playedCard) {
        Player currentPlayer = turnManager.getCurrentPlayer();
        view.displayMessage(currentPlayer.getPlayerName() + " Plays:\n" + playedCard);
        paradeLine.addCardToLine(playedCard);

        List<Card> cardsToRemove = RemovalStrategy.determineRemovalChoice(playedCard,
                paradeLine.getParadeLineCards());

        if (!cardsToRemove.isEmpty()) {
            view.displayMessage(currentPlayer.getPlayerName() + " takes the following card from the parade.");
            System.out.println(GameUtils.cardsToString(cardsToRemove));
            paradeLine.removeCards(cardsToRemove);
            currentPlayer.addCollectedCards(cardsToRemove);
        } else {
            view.displayMessage(currentPlayer.getPlayerName() + " takes no card from the parade.");
        }

        if (!isLastRound) {
            Card drawnCard = deck.drawCard();
            if (drawnCard != null) {
                currentPlayer.addToHand(drawnCard);
                view.displayMessage(currentPlayer.getPlayerName() + " draws a card.");
            } else {
                view.displayMessage("Deck is empty, no card drawn.");
            }
            view.displayMessage("\n" + currentPlayer.getPlayerName() + "'s Current Hand: "
                    + GameUtils.handToString(currentPlayer.getHand()));
        } else {
            view.displayMessage(currentPlayer.getPlayerName() + " does not draw a card in last round.");
        }
        view.displayPlayerCollections(currentPlayer);
    }

    private void endGame() {
        view.clearScreen();
        view.displayMessage("\nGame Over!");
        view.displayMessage("\n--- Discarding 2 Hand Cards for Scoring ---");

        for (Player player : players) {
            view.clearScreen();
            view.displayMessage("\n" + player.getPlayerName() + ", it's time to" + Print.RED + Print.BOLD + " DISCARD" + Print.RESET + "2 Cards from your Hand.");
            view.displayMessage(player.getPlayerName() + "'s Current Collection:");
            view.displayPlayerCollections(player);
            view.interactiveDiscardTwoCards(player);
            view.displayMessage(player.getPlayerName() + " DISCARD hand to: "
                    + GameUtils.handToString(player.getHand()));
            player.addHandToCollection();
            view.displayMessage("\nUpdated Game State for " + player.getPlayerName() + ":");
            view.displayPlayerCollections(player);
            view.promptForNextTurn(player);
        }

        // Show the final scoreboard with clear formatting
        displayFinalScoreboard();

        // Wait for user acknowledgment before returning to menu
        System.out
                .println("\n" + Print.BOLD + "GAME COMPLETE! Press [ENTER] to Return to the Main Menu." + Print.RESET);
        scanner.nextLine(); // Wait for user to press Enter
    }

    private void displayFinalScoreboard() {
        view.clearScreen();

        System.out.println("\n" + Print.BOLD + "■■■■■ FINAL RESULTS ■■■■■" + Print.RESET);
        System.out.println("\n--- Final Player Collections ---");

        for (Player player : players) {
            view.displayPlayerCollections(player);
            System.out.println(); // Add spacing between players
        }

        // Calculate and display all scores
        Map<Color, List<Player>> colorMajorities = ScoreCalculator.determineColorMajorities(players);

        System.out.println(Print.BOLD + "■■■■■ FINAL SCORES ■■■■■" + Print.RESET);
        System.out.println("\nPlayer               Score");
        System.out.println("------------------------------");

        // Store scores for determining winner
        Map<Player, Integer> playerScores = new HashMap<>();

        for (Player player : players) {
            int score = ScoreCalculator.calculatePlayerFinalScore(player, colorMajorities);
            playerScores.put(player, score);

            // Format the score line with proper spacing
            String scoreLine = String.format("%-20s %d", player.getPlayerName(), score);
            System.out.println(scoreLine);
        }

        // Determine and announce the winner
        Player winner = ScoreCalculator.determineWinner(players);
        int winnerScore = playerScores.get(winner);

        System.out.println();
        System.out.println(Print.BOLD + Print.GREEN + "WINNER: " +
                winner.getPlayerName() + " WITH " + winnerScore + " POINTS!" + Print.RESET);

        // Display color majorities
        System.out.println();
        System.out.println(Print.BOLD + "■■■■■ SUIT MAJORITIES ■■■■■" + Print.RESET);
        System.out.println();
        for (Color color : Color.values()) {
            List<Player> majorityPlayers = colorMajorities.get(color);
            if (majorityPlayers != null && !majorityPlayers.isEmpty()) {
                System.out.print(getColorColor(color) + color + Print.RESET + ": ");
                for (int i = 0; i < majorityPlayers.size(); i++) {
                    System.out.print(majorityPlayers.get(i).getPlayerName());
                    if (i < majorityPlayers.size() - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.println();
            } else {
                System.out.println(getColorColor(color) + color + Print.RESET + ": No Majority");
            }
        }
    }

    // Helper method to get color for color display
    private String getColorColor(Color color) {
        switch (color) {
            case RED:
                return Print.RED;
            case BLUE:
                return Print.BLUE;
            case GREEN:
                return Print.GREEN;
            case ORANGE:
                return Print.ORANGE;
            case PURPLE:
                return Print.PURPLE;
            case GREY:
                return Print.GREY;
            default:
                return "";
        }
    }

    private void checkGameEndConditions() {
        if (!isLastRound) {
            if (hasAnyoneCollectedSixColors()) {
                startLastRound(true);
            } else if (deck.isEmpty()) {
                startLastRound(false);
            }
        }
    }

    private boolean hasAnyoneCollectedSixColors() {
        for (Player player : players) {
            Set<Color> collectedColors = new HashSet<>();
            for (Card card : player.getCollectedCards()) {
                collectedColors.add(card.getColor());
            }
            if (collectedColors.size() >= 6) {
                return true;
            }
        }
        return false;
    }

    private void startLastRound(boolean sixColors) {
        isLastRound = true;
        extraTurnCount = 0;
        view.displayMessage("\n--- Last Round Started! ---");
        if (sixColors) {
            view.displayMessage("Triggered by a player collecting 6 colors.");
        } else {
            view.displayMessage("Triggered by deck exhaustion.");
        }
    }
}
