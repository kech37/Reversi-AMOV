package pt.isec.a21240456.a2120528.reversisec;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class PlayGame extends AppCompatActivity {
    public static final int SINGLE_PLAYER = 1;
    public static final int LOCAL_MULTYPLAYER = 2;
    public static final int NETWORK_MULTYPLAYER = 3;

    private final int maxN = 8;

    private ImageView[][] ivCells = new ImageView[maxN][maxN];
    private Drawable[] drawCells = new Drawable[4];
    private Player[] players = new Player[2];

    private int gameMode;
    private Context context;
    private Board board = new Board();

    private TextView tvPlayerTurn;

    private int playerTurn;
    private char turnColor = 'b';

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

        context = this;
        tvPlayerTurn = (TextView) findViewById(R.id.tvPlayerTurn);

        Intent intent = getIntent();
        if(intent != null){
            gameMode = intent.getIntExtra("mode", SINGLE_PLAYER);
        }

        players[0] = new Player("Player 1", false);
        players[1] = new Player("Bot", true);

        initBoardGame();
        drawBoard();
        initGame();
    }

    private void initGame() {
        Random random = new Random();
        playerTurn = random.nextInt(1);
        players[playerTurn].setColor(0);
        tvPlayerTurn.setText(players[playerTurn].getName() + " (" + players[playerTurn].getColor() + ")");
    }

    private void initBoardGame() {
        drawCells[0] = ContextCompat.getDrawable(context, R.drawable.cell_empty_bg);
        drawCells[1] = ContextCompat.getDrawable(context, R.drawable.cell_black_player_bg);
        drawCells[2] = ContextCompat.getDrawable(context, R.drawable.cell_white_player_bg);
        drawCells[3] = ContextCompat.getDrawable(context, R.drawable.cell_possible_move);

        LinearLayout llGameBoard = findViewById(R.id.gameBoard);
        int cellSize = Math.round(getScreenSizeMinusPadding() / maxN);

        LinearLayout.LayoutParams llRow = new LinearLayout.LayoutParams(cellSize * maxN, cellSize);
        LinearLayout.LayoutParams llCell = new LinearLayout.LayoutParams(cellSize, cellSize);

        for (int i = 0; i < maxN; i++) {
            LinearLayout linRow = new LinearLayout(context);
            for (int j = 0; j < maxN; j++) {
                ivCells[i][j] = new ImageView(context);

                final int x = i;
                final int y = j;
                ivCells[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        makeMove(x, y);
                        Toast.makeText(context, "(" + x + ", " + y + ")", Toast.LENGTH_SHORT).show();
                    }
                });
                linRow.addView(ivCells[i][j], llCell);
            }
            llGameBoard.addView(linRow, llRow);
        }
    }

    private float getScreenSizeMinusPadding(){
        return context.getResources().getDisplayMetrics().widthPixels - dpToPixels(32, context);
    }

    private int dpToPixels(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    private void drawBoard() {
        for (int i = 0; i < maxN; i++) {
            for (int j = 0; j < maxN; j++) {
                switch(board.getCell(i, j)) {
                    case Board.EMPTY:
                        ivCells[i][j].setBackground(drawCells[0]);
                        break;
                    case Board.BLACK:
                        ivCells[i][j].setBackground(drawCells[1]);
                        break;
                    case Board.WHITE:
                        ivCells[i][j].setBackground(drawCells[2]);
                        break;
                    case Board.POSSIBLE_MOVE:
                        ivCells[i][j].setBackground(drawCells[3]);
                        break;
                }
            }
        }
    }

    private void makeMove(int x, int y){
        switch (gameMode){
            case SINGLE_PLAYER:
                    if(players[playerTurn].isBot()){
                        //TODO: Bot logic
                    }
                    board.makeMove(x, y);
                    tvPlayerTurn.setText(players[playerTurn].getName() + " (" + players[playerTurn].getColor() + ")");
                    drawBoard();
                break;
        }

    }
}
