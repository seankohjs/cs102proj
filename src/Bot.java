import java.util.List;

public abstract class Bot extends Player {

    public abstract Card discard();
    public abstract Card takeCard(List<Card> candidates);

    public Bot(String botName) {
        super(botName);
    }

}