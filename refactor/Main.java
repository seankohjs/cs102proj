public class Main {
    public static void main(String[] args) {
        // Create the view first
        TerminalView terminalView = new TerminalView();

        // Create the game logic controller, passing the view
        Game paradeGame = new Game(terminalView);

        // Start the game execution
        paradeGame.start();

        // The view's scanner closing is handled within Game.start()'s finally block
        System.out.println("Game has ended. Exiting application.");
    }
}