package pt.isec.a21240456.a2120528.reversisec;

import android.graphics.Bitmap;

public class Player {
    public int uid;
    private String name;
    private int color = -1;
    private boolean bot;
    private Bitmap image;

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
}
