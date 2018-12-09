package pt.isec.a21240456.a2120528.reversisec;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
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
    private ImageView ivProfilePicture, ivClosePopupServerIP;
    private Dialog dialog;
    private EditText etServerIP;
    private Button btnConnect, btnTryAgain, btnYesss;

    private int playerTurn;

    private String serverIP;

    private Bitmap defaultImagebitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

        context = this;
        tvPlayerTurn = (TextView) findViewById(R.id.tvPlayerTurn);
        ivProfilePicture = (ImageView) findViewById(R.id.ivProfilePicture);

        dialog = new Dialog(this);

        Intent intent = getIntent();
        if(intent != null){
            gameMode = intent.getIntExtra("mode", SINGLE_PLAYER);
        }

        /*TODO Resolve profile bugs
        if(!getIntent().hasExtra("playername") && getIntent().hasExtra("profilepicturepath")){
            tvPlayerTurn.setText(getIntent().getStringExtra("playername"));
            if(getIntent().getStringExtra("profilepicturepath").equalsIgnoreCase("<none>")){
                File imgFile = new File(getIntent().getStringExtra("profilepicturepath"));
                defaultImagebitmap = drawableToBitmap(getDrawable(R.drawable.ic_person_black_208dp));
                if(imgFile.exists()){
                    players[0] = new Player(tvPlayerTurn.getText().toString(), BitmapFactory.decodeFile(imgFile.getAbsolutePath()), false);
                }else{
                    players[0] = new Player(tvPlayerTurn.getText().toString(), defaultImagebitmap, false);
                }
            }
        }*/

        players[0] = new Player("Reis", defaultImagebitmap, false);



        switch (gameMode){
            case SINGLE_PLAYER:
                players[1] = new Player("Bot", defaultImagebitmap, true);
                tvPlayerTurn.setText(players[0].getName());
                ivProfilePicture.setImageBitmap(players[0].getImage());
                break;
            case LOCAL_MULTYPLAYER:
                players[1] = new Player(intent.getStringExtra("player2Name"), defaultImagebitmap, false);
                break;
            case NETWORK_MULTYPLAYER:
                if(getIntent().getBooleanExtra("createServer", true)){
                    //TODO Cria server para outro jogador se conectar - (Verificar o exemplo do professor do pedra, papel e tesoura
                }else{
                    //TODO Junta-se a um servidor

                    // Chama uma dialog para o utilizador inserir o IP
                    // que se deve conectar (como no exemplo do
                    // professor do pedra, papel e tesoura).
                    // o IP é guardado em serverIP
                    showServerIPInput();
                }

                //TODO tem que receber a informação do jogador adversario de modo a apresentar no ecrã de jogo
                players[1] = new Player("Receber INFO", defaultImagebitmap, false);
                break;
        }

        initBoardGame();
        drawBoard();
        initGame();

    }

    private void showServerIPInput(){
        dialog.setContentView(R.layout.dialog_input_server_ip);
        ivClosePopupServerIP = (ImageView) dialog.findViewById(R.id.ivClosePopupServerIP);
        btnConnect = (Button) dialog.findViewById(R.id.btnConnect);
        etServerIP = (EditText)  dialog.findViewById(R.id.etServerIP);

        ivClosePopupServerIP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serverIP = etServerIP.getText().toString();
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void initGame() {
        Random random = new Random();
        if((random.nextInt(100) + 1) <= 50){
            playerTurn = 0;
            players[0].setColor(Board.BLACK);
            players[1].setColor(Board.WHITE);
        }else{
            playerTurn = 1;
            players[0].setColor(Board.WHITE);
            players[1].setColor(Board.BLACK);
            if(gameMode == SINGLE_PLAYER) {
                board.intelligentBotMove(Board.BLACK);
                drawBoard();
            }
        }
        ivProfilePicture.setImageBitmap(players[playerTurn].getImage());
        tvPlayerTurn.setText(players[playerTurn].getName());
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
                ivCells[i][j].setOnClickListener( new View.OnClickListener() {
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
                if(players[0].getPlayAgainCard() == Player.IN_USE) {
                    if(board.makeMove(players[0].getColor(), x, y)) {
                        players[0].setPlayAgainCard(Player.ALREADY_USED);
                        drawBoard();
                    }
                }
                else if(players[0].getSkipAgainCard() == Player.IN_USE) {
                    board.intelligentBotMove(players[1].getColor());
                    players[0].setSkipAgainCard(Player.ALREADY_USED);
                    drawBoard();
                }
                else if(board.makeMove(players[0].getColor(), x, y)) {
                    board.intelligentBotMove(players[1].getColor());
                    drawBoard();
                }
                break;
            case LOCAL_MULTYPLAYER:
                if(!players[0].isBot() && !players[1].isBot()) {
                    if(board.makeMove(players[playerTurn].getColor(), x, y)) {
                        playerTurn = (playerTurn + 1) % 2;
                        tvPlayerTurn.setText(players[playerTurn].getName());
                        ivProfilePicture.setImageBitmap(players[playerTurn].getImage());
                        drawBoard();
                    }
                }
                else if(players[0].isBot()) {
                    if(board.makeMove(players[1].getColor(), x, y)) {
                        board.intelligentBotMove(players[0].getColor());
                        tvPlayerTurn.setText(players[playerTurn].getName());
                        ivProfilePicture.setImageBitmap(players[playerTurn].getImage());
                        drawBoard();
                    }
                }
                else if(players[1].isBot()) {
                    if(board.makeMove(players[0].getColor(), x, y)) {
                        board.intelligentBotMove(players[1].getColor());
                        tvPlayerTurn.setText(players[playerTurn].getName());
                        ivProfilePicture.setImageBitmap(players[playerTurn].getImage());
                        drawBoard();
                    }
                }
                break;
        }

    }

    public Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public void onWin(View view) {
        dialog.setContentView(R.layout.dialog_player_win);
        btnYesss = (Button) dialog.findViewById(R.id.btnYesss);

        btnYesss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void onLose(View view) {
        dialog.setContentView(R.layout.dialog_player_lose);
        btnTryAgain = (Button) dialog.findViewById(R.id.btnTryAgain);

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}
