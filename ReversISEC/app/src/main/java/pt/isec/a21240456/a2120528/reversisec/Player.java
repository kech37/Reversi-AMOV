package pt.isec.a21240456.a2120528.reversisec;

public class Player {
    public int uid;
    private String name;
    private int color = 1;
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

    public void setColor(int value) {
       color = value;
    }

    public int getColor() {
        return color;
    }
}
