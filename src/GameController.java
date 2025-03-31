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

    public GameController(List<String> playerNames, List<Boolean> isBot,List<Integer> botDifficulties, Scanner scanner) {
        this.deck = new Deck();
        this.paradeLine = new ParadeLine();
        this.players = new ArrayList<>();
        this.scanner = scanner;
        this.view = new GameView(scanner);

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
            view.clearScreen();
            Player currentPlayer = turnManager.getCurrentPlayer();
            view.displayTurnHeader(currentPlayer);
            view.displayGameState(deck, paradeLine, isLastRound);
            view.displayPlayerCollections(currentPlayer);
            view.displayOtherPlayersCollections(players, currentPlayer);

            if (!currentPlayer.getHand().isEmpty()) {
                Card cardToPlay = view.getPlayerCardChoice(currentPlayer, paradeLine, players);
                playTurn(cardToPlay);
            } else {
                view.displayMessage(Print.BOLD + Print.GREEN + currentPlayer.getPlayerName() + Print.RESET +
                                    Print.BOLD + " HAS NO CARDS TO PLAY! Passing Turn..." + Print.RESET);
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

    public void playTurn(Card playedCard) {
        Player currentPlayer = turnManager.getCurrentPlayer();
        view.displayMessage(Print.BOLD + Print.GREEN + currentPlayer.getPlayerName() + Print.RESET +
                            Print.BOLD + " PLAYS:\n" + Print.RESET + playedCard);
        paradeLine.addCardToLine(playedCard);

        List<Card> cardsToRemove = RemovalStrategy.determineRemovalChoice(playedCard, paradeLine.getParadeLineCards());

        if (!cardsToRemove.isEmpty()) {
            view.displayMessage(Print.BOLD + Print.GREEN + currentPlayer.getPlayerName() + Print.RESET +
                                Print.BOLD + " takes the following Cards from the Parade.");
            view.displayMessage(GameUtils.cardsToString(cardsToRemove));
            paradeLine.removeCards(cardsToRemove);
            currentPlayer.addCollectedCards(cardsToRemove);
        } else {
            view.displayMessage(Print.BOLD + Print.GREEN + currentPlayer.getPlayerName() + Print.RESET +
                                Print.BOLD + " takes NO Card from the Parade.");
        }

        if (!isLastRound) {
            Card drawnCard = deck.drawCard();
            if (drawnCard != null) {
                currentPlayer.addToHand(drawnCard);
                view.displayMessage(Print.BOLD + Print.GREEN + currentPlayer.getPlayerName() + Print.RESET + " Draws a Card.");
            } else {
                view.displayMessage(Print.BOLD + Print.RED + "Deck is EMPTY, NO Card Drawn." + Print.RESET);
            }
            view.displayMessage("\n" + Print.BOLD + Print.GREEN + currentPlayer.getPlayerName() + Print.RESET + "'s Current Hand: ");
            view.displayMessage(GameUtils.handToString(currentPlayer.getHand()));
        } else {
            view.displayMessage(Print.BOLD + Print.GREEN + currentPlayer.getPlayerName() + Print.RESET + " DOES NOT Draw a Card in the Last Round.");
        }
        view.displayPlayerCollections(currentPlayer);
    }

    public void endGame() {
        view.clearScreen();
        view.displayMessage(Print.BOLD + Print.RED + "\nGAME OVER!" + Print.RESET);
        view.displayMessage(Print.BOLD + "\n■■■■■ *** SCORING STAGE -" + Print.RED + " DISCARD " + Print.RESET + Print.BOLD + "2 Cards from Hand *** ■■■■■" + Print.RESET);

        for (Player player : players) {
            //view.clearScreen();
            view.displayMessage("\n" + player.getPlayerName() + ", it's time to" + Print.RED + Print.BOLD + " DISCARD " + Print.RESET + "2 Cards from Your Hand.");
            view.displayMessage(Print.BOLD + Print.GREEN + player.getPlayerName() + "'s " + Print.RESET + "Current Collection:");
            view.displayPlayerCollections(player);
            view.interactiveDiscardTwoCards(player);
            player.addHandToCollection();
            view.displayMessage("\nUpdated Game State for " + player.getPlayerName() + ":");
            view.displayPlayerCollections(player);
            view.promptForNextTurn(player);
        }

        // Show the final scoreboard with clear formatting
        displayFinalScoreboard();

        // Wait for user acknowledgment before returning comenu
        System.out.print("\n" + Print.BOLD + "GAME COMPLETE! Press [ENTER] to Return to the Main Menu.." + Print.RESET);
        scanner.nextLine(); // Wait for user to press Enter
    }

    private void displayFinalScoreboard() {
        //view.clearScreen();

        System.out.println("\n" + Print.BOLD + "■■■■■     FINAL RESULTS     ■■■■■" + Print.RESET);
        System.out.println("\n■■■■■ *** Final Player Collections *** ■■■■■");

        for (Player player : players) {
            view.displayPlayerCollections(player);
            System.out.println(); // Add spacing between players
        }

        // Calculate and display all scores
        Map<Color, List<Player>> colorMajorities = ScoreCalculator.determineColorMajorities(players);

        System.out.println(Print.BOLD + "■■■■■     FINAL SCORES     ■■■■■" + Print.RESET);
        System.out.println();
        System.out.println(Print.BOLD + Print.ORANGE + "PLAYER               SCORE" + Print.RESET);
        System.out.println("■■■■■■■■■■■■■■■■■■■■■■■■■■");

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
        System.out.println(Print.BOLD + Print.GREEN + "WINNER: " + winner.getPlayerName() + " WITH " + winnerScore + " POINTS!" + Print.RESET);

        // Display color majorities
        System.out.println();
        System.out.println(Print.BOLD + "■■■■■     SUIT MAJORITIES     ■■■■■" + Print.RESET);
        System.out.println();
        for (Color color : Color.values()) {
            List<Player> majorityPlayers = colorMajorities.get(color);
            if (majorityPlayers != null && !majorityPlayers.isEmpty()) {
                System.out.print(Card.getDisplayColor(color) + color + Print.RESET + ": ");
                for (int i = 0; i < majorityPlayers.size(); i++) {
                    System.out.print(majorityPlayers.get(i).getPlayerName());
                    if (i < majorityPlayers.size() - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.println();
            } else {
                System.out.println(Card.getDisplayColor(color) + color + Print.RESET + ": No Majority");
            }
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
        view.displayMessage("\n■■■■■ *** LAST ROUND STARTED *** ■■■■■");
        if (sixColors) {
            view.displayMessage("Triggered By a Player Collecting 6 Colors.");
        } else {
            view.displayMessage("Triggered By Deck Exhaustion.");
        }
    }
}
