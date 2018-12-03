package pt.isec.a21240456.a2120528.reversisec;

public class Player {
    private String name;
    private boolean bot;

    public Player(String name, boolean bot) {
        this.name = name;
        this.bot = bot;
    }

    public String getName() {
        return name;
    }

    public boolean isBot() {
        return bot;
    }
}
