package pt.isec.a21240456.a2120528.reversisec;

public class GameRegist {
    static final int SINGLE_PLAY = 1;
    static final int LOCAL_MULTIPLAYER = 2;
    static final int NETWORK_MULTIPLAYER = 3;

    String player1Name, player2Name;
    int player1Score, player2Score;
    int gameMode;

    public GameRegist(int gameMode, String player1Name, int player1Score, String player2Name, int player2Score) {
        this.gameMode = gameMode;
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        this.player1Score = player1Score;
        this.player2Score = player2Score;
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public int getPlayer1Score() {
        return player1Score;
    }

    public int getPlayer2Score() {
        return player2Score;
    }

    public int getGameMode() {
        return gameMode;
    }
}
