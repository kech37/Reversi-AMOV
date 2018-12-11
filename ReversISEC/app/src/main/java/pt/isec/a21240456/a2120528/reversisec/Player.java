package pt.isec.a21240456.a2120528.reversisec;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Player implements Serializable {
    public static final int ALREADY_USED= 0;
    public static final int IN_USE = 1;
    public static final int NEVER_USED = 2;

    private String name;
    private int color = -1;
    private boolean bot;
    private Bitmap image;

    private int playAgainCard = NEVER_USED;
    private int skipAgainCard = NEVER_USED;

    public Player(String name, Bitmap image, boolean bot) {
        this.name = name;
        this.bot = bot;
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
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

    public int getPlayAgainCard() {
        return playAgainCard;
    }

    public void setPlayAgainCard(int playAgainCard) {
        this.playAgainCard = playAgainCard;
    }

    public int getSkipAgainCard() {
        return skipAgainCard;
    }

    public void setSkipAgainCard(int skipAgainCard) {
        this.skipAgainCard = skipAgainCard;
    }
}
