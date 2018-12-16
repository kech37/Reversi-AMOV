package pt.isec.a21240456.a2120528.reversisec;

public class Packet {

    boolean play;
    private Player player;
    private int row, col;
    private int playAgain;
    private int skip;

    public Packet() {}

    public Packet(Player player) {
        play = false;
        this.player = player;
    }

    public Packet(int row, int col, int playAgain, int skip) {
        play = true;
        Player player = null;
        this.row = row;
        this.col = col;
        this.playAgain = playAgain;
        this.skip = skip;
    }

    public boolean isPlay() {
        return play;
    }

    public Player getPlayer() {
        return player;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getPlayAgain() {
        return playAgain;
    }

    public int getSkip() {
        return skip;
    }
}
