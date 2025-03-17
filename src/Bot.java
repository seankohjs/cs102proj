public abstract class Bot extends Player {

    public abstract Card discard();

    public Bot(String botName) {
        super(botName);
    }

}