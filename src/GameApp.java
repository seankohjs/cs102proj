import java.util.*;

public class GameApp {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            GameMenu menu = new GameMenu(scanner);
            menu.readOption();
        } catch (NullPointerException e) {
            System.exit(1);
        }
    }
}
