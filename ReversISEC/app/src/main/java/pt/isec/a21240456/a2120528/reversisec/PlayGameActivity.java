package pt.isec.a21240456.a2120528.reversisec;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

public class PlayGameActivity extends AppCompatActivity {
	public static final int SINGLE_PLAYER = 1;
	public static final int LOCAL_MULTYPLAYER = 2;
	public static final int NETWORK_MULTYPLAYER = 3;
	
	private final int maxN = 8;
	
	private ImageView[][] ivCells = new ImageView[maxN][maxN];
	private Drawable[] drawCells = new Drawable[4];
	private Player[] players = new Player[2];
	
	private int gameMode;
	private int suggestedMoves;
	private Context context;
	private Board board = new Board();
	
	private TextView tvPlayerTurn, mBlackPieces, mWhitePieces;
	private ImageView ivProfilePicture;
	private Dialog dialog;
	private Button mBtnPlayAgain, mBtnSkipTurn,dialogOkbtn;
	private LinearLayout backScoreBoard, whiteScoreBoard;
	
	private boolean buttonsActivated;
	private int totalTurns;
	private int playerTurn;

	private Network network;
	private String serverIP;

	private Bitmap defaultImagebitmap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_game);

		context = this;
		tvPlayerTurn = findViewById(R.id.tvPlayerTurn);
		ivProfilePicture = findViewById(R.id.ivProfilePicture);
		backScoreBoard = findViewById(R.id.blackScoreBoard);
		whiteScoreBoard = findViewById(R.id.whiteScoreBoard);

		dialog = new Dialog(this);
		
		Intent intent = getIntent();
		if(intent != null) {
			gameMode = intent.getIntExtra("mode", SINGLE_PLAYER);
			suggestedMoves = intent.getIntExtra("suggestedMoves", 0);
		}
		
		
		if(intent.hasExtra("playername") && intent.hasExtra("profilepicturepath")) {
			tvPlayerTurn.setText(intent.getStringExtra("playername"));
			if(!intent.getStringExtra("profilepicturepath").equalsIgnoreCase("<none>")) {
				File imgFile = new File(intent.getStringExtra("profilepicturepath"));
				defaultImagebitmap = drawableToBitmap(getDrawable(R.drawable.ic_person_black_208dp));
				if(imgFile.exists()) {
					players[0] = new Player(tvPlayerTurn.getText().toString(), BitmapFactory.decodeFile(imgFile.getAbsolutePath()), false);
				} else {
					players[0] = new Player(tvPlayerTurn.getText().toString(), defaultImagebitmap, false);
				}
			}
		}
		
		buttonsActivated = false;
		totalTurns = 0;
		
		switch(gameMode) {
			case SINGLE_PLAYER:
				players[1] = new Player("Bot", defaultImagebitmap, true);
				break;
			case LOCAL_MULTYPLAYER:
				players[1] = new Player(intent.getStringExtra("player2Name"), defaultImagebitmap, false);
				break;
			case NETWORK_MULTYPLAYER:
				if(intent.getBooleanExtra("createServer", true)) {
                    network = new Network(context, Network.SERVER);
                    network.startNetwork();
                    network.startServerSide();
				} else {
					serverIP = "10.0.2.2";
					network = new Network(context, Network.CLIENT);
					network.startNetwork();
					network.startClientSide(serverIP);
				}
				players[1] = new Player(intent.getStringExtra("player2Name"), defaultImagebitmap, false);
				break;
		}
		
		initBoardGame();

        if(gameMode != NETWORK_MULTYPLAYER || network.getMode() == Network.SERVER)
		    initGame();
        }
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable("boardState", board);
		outState.putSerializable("players", players);
		outState.putInt("gameMode", gameMode);
		outState.putBoolean("buttonsActivated", buttonsActivated);
		outState.putInt("totalTurns", totalTurns);
		outState.putInt("playerTurn", playerTurn);
		outState.putString("serverIP", serverIP);
		
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		
		board = (Board) savedInstanceState.getSerializable("boardState");
		players = (Player[]) savedInstanceState.getSerializable("players");
		gameMode = savedInstanceState.getInt("gameMode");
		buttonsActivated = savedInstanceState.getBoolean("buttonsActivated");
		totalTurns = savedInstanceState.getInt("totalTurns");
		playerTurn = savedInstanceState.getInt("playerTurn");
		serverIP = savedInstanceState.getString("serverIP");
		
		
		drawBoard();
	}

	private void initGame() {
        Random random = new Random();
        if ((random.nextInt(100) + 1) <= 50) {
            playerTurn = 0;
            players[0].setColor(Board.BLACK);
            players[1].setColor(Board.WHITE);
        } else {
            playerTurn = 1;
            players[0].setColor(Board.WHITE);
            players[1].setColor(Board.BLACK);
            if (gameMode == SINGLE_PLAYER) {
                board.intelligentBotMove(Board.BLACK);
                totalTurns++;
                playerTurn = 0;
            }
        }

		drawBoard();
	}
	
	private void initBoardGame() {
		drawCells[0] = ContextCompat.getDrawable(context, R.drawable.cell_empty_bg);
		drawCells[1] = ContextCompat.getDrawable(context, R.drawable.cell_black_player_bg);
		drawCells[2] = ContextCompat.getDrawable(context, R.drawable.cell_white_player_bg);
		drawCells[3] = ContextCompat.getDrawable(context, R.drawable.cell_possible_move);
		
		LinearLayout llGameBoard = findViewById(R.id.gameBoard);
		int cellSize = 0;
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			cellSize = Math.round(getScreenSizeMinusPadding() / maxN);
			if(cellSize > 160){
				cellSize = 160;
			}
		} else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			cellSize = Math.round(getScreenSizeLandScapeMinusPadding() / maxN);
			if(cellSize > 110){
				cellSize = 110;
			}
		}
		
		LinearLayout.LayoutParams llRow = new LinearLayout.LayoutParams(cellSize * maxN, cellSize);
		LinearLayout.LayoutParams llCell = new LinearLayout.LayoutParams(cellSize, cellSize);
		
		for(int i = 0; i < maxN; i++) {
			LinearLayout linRow = new LinearLayout(context);
			for(int j = 0; j < maxN; j++) {
				ivCells[i][j] = new ImageView(context);
				
				final int x = i;
				final int y = j;
				ivCells[i][j].setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						makeMove(x, y);
					}
				});
				linRow.addView(ivCells[i][j], llCell);
			}
			llGameBoard.addView(linRow, llRow);
		}
		
		mBtnPlayAgain = findViewById(R.id.btnPlayAgain);
		mBtnPlayAgain.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(players[playerTurn].getPlayAgainCard() == Player.NEVER_USED && totalTurns >= 8) {
					players[playerTurn].setPlayAgainCard(Player.IN_USE);
					drawBoard();
				}
			}
		});
		
		mBtnSkipTurn = findViewById((R.id.btnSkipTurn));
		mBtnSkipTurn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(gameMode) {
					case SINGLE_PLAYER:
						if(players[0].getSkipAgainCard() == Player.NEVER_USED && totalTurns >= 8) {
							board.checkNextTurnPossibleMoves(players[1].getColor());
							board.intelligentBotMove(players[1].getColor());
							players[0].setSkipAgainCard(Player.ALREADY_USED);
						}
						break;
					case LOCAL_MULTYPLAYER:
						if(players[playerTurn].getSkipAgainCard() == Player.NEVER_USED && totalTurns >= 8) {
							players[playerTurn].setSkipAgainCard(Player.ALREADY_USED);
							playerTurn = (playerTurn + 1) % 2;
							board.checkNextTurnPossibleMoves(players[playerTurn].getColor());
						}
						break;
					case NETWORK_MULTYPLAYER:
						break;
				}
				drawBoard();
			}
		});
		drawBoard();
	}
	
	private float getScreenSizeMinusPadding() {
		return context.getResources().getDisplayMetrics().widthPixels - dpToPixels(32, context);
	}
	
	private float getScreenSizeLandScapeMinusPadding() {
		return context.getResources().getDisplayMetrics().heightPixels - dpToPixels(64, context);
	}
	
	private int dpToPixels(float dp, Context context) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
	}
	
	public void drawBoard() {
		tvPlayerTurn.setText(players[playerTurn].getName());
		ivProfilePicture.setImageBitmap(players[playerTurn].getImage());
		if(players[playerTurn].getColor() == Board.BLACK) {
			backScoreBoard.setBackground(getDrawable(R.drawable.cell_empty_bg));
			whiteScoreBoard.setBackground(getDrawable(R.drawable.cell_possible_move));
		} else {
			backScoreBoard.setBackground(getDrawable(R.drawable.cell_possible_move));
			whiteScoreBoard.setBackground(getDrawable(R.drawable.cell_empty_bg));
		}

		for(int i = 0; i < maxN; i++) {
			for(int j = 0; j < maxN; j++) {
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
						if(suggestedMoves == 0)
							ivCells[i][j].setBackground(drawCells[0]);
						else
							ivCells[i][j].setBackground(drawCells[3]);
						break;
				}
			}
		}
		
		switch(gameMode) {
			case SINGLE_PLAYER:
				if(totalTurns >= 8) {
					if(players[0].getPlayAgainCard() == Player.NEVER_USED)
						mBtnPlayAgain.setBackground(getResources().getDrawable(R.drawable.button_input, null));
					else
						mBtnPlayAgain.setBackground(getResources().getDrawable(R.drawable.button_pressed, null));
					
					if(players[0].getSkipAgainCard() == Player.NEVER_USED)
						mBtnSkipTurn.setBackground(getResources().getDrawable(R.drawable.button_input, null));
					else
						mBtnSkipTurn.setBackground(getResources().getDrawable(R.drawable.button_pressed, null));
				}
				break;
			default:
				if(totalTurns >= 8) {
					if(players[playerTurn].getPlayAgainCard() == Player.NEVER_USED)
						mBtnPlayAgain.setBackground(getResources().getDrawable(R.drawable.button_input, null));
					else
						mBtnPlayAgain.setBackground(getResources().getDrawable(R.drawable.button_pressed, null));
					
					if(players[playerTurn].getSkipAgainCard() == Player.NEVER_USED)
						mBtnSkipTurn.setBackground(getResources().getDrawable(R.drawable.button_input, null));
					else
						mBtnSkipTurn.setBackground(getResources().getDrawable(R.drawable.button_pressed, null));
				}
				break;
		}
		
		mBlackPieces = findViewById(R.id.TextViewBlackPieces);
		mBlackPieces.setText(String.valueOf(board.getPieces(board.BLACK)));
		mWhitePieces = findViewById(R.id.TextViewWhitePieces);
		mWhitePieces.setText(String.valueOf(board.getPieces(board.WHITE)));
		
	}
	
	private void makeMove(int x, int y) {
		switch(gameMode) {
			case SINGLE_PLAYER:
				if(players[0].getPlayAgainCard() == Player.IN_USE) {
					if(board.makeMove(players[0].getColor(), x, y)) {
						players[0].setPlayAgainCard(Player.ALREADY_USED);
						if(!board.checkNextTurnPossibleMoves(players[0].getColor()))
							resolveEndGame();
						playerTurn = 0;
						drawBoard();
					}
				} else if(board.makeMove(players[0].getColor(), x, y)) {
					totalTurns++;
					if(!board.intelligentBotMove(players[1].getColor()))
						resolveEndGame();
					totalTurns++;
					playerTurn = 0;
					if(!board.checkNextTurnPossibleMoves(players[0].getColor()))
						resolveEndGame();
					drawBoard();
				}
				break;
			case LOCAL_MULTYPLAYER:
				if(players[playerTurn].getPlayAgainCard() == Player.IN_USE) {
					if(board.makeMove(players[playerTurn].getColor(), x, y)) {
						players[playerTurn].setPlayAgainCard(Player.ALREADY_USED);
						if(!board.checkNextTurnPossibleMoves(players[playerTurn].getColor()))
							resolveEndGame();
						drawBoard();
					}
				}
                else if(board.makeMove(players[playerTurn].getColor(), x, y)) {
                    totalTurns++;
                    playerTurn = (playerTurn + 1) % 2;
                    if(!board.checkNextTurnPossibleMoves(players[playerTurn].getColor()))
                        resolveEndGame();
                    drawBoard();
                }
				break;
            case NETWORK_MULTYPLAYER:
				    board.addPiece(players[0].getColor(), x, y);
					sendGamePacket(x,y);
					totalTurns++;
					drawBoard();
				break;
		}
	}

    public void onChangeMode(View view) {
        if(gameMode == LOCAL_MULTYPLAYER) {
            gameMode = SINGLE_PLAYER;
            if(playerTurn == 1) {
                if(!board.intelligentBotMove(players[1].getColor()))
                    resolveEndGame();
                totalTurns++;
                playerTurn = 0;
                if(!board.checkNextTurnPossibleMoves(players[0].getColor()))
                    resolveEndGame();
                drawBoard();
            }
        }
        else {
            gameMode = LOCAL_MULTYPLAYER;

        }
    }
	
	private Bitmap drawableToBitmap(Drawable drawable) {
		Bitmap bitmap;
		
		if(drawable instanceof BitmapDrawable) {
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
	
	private void resolveEndGame() {
		int winner;
		
		if(board.getPieces(board.BLACK) == board.getPieces(board.WHITE))
			winner = 3;
		else if(board.getPieces(board.BLACK) > board.getPieces(board.WHITE))
			winner = board.BLACK;
		else
			winner = board.WHITE;
		
		saveGame(gameMode, players[0].getName(), board.getPieces(players[0].getColor()), players[1].getName(), board.getPieces(players[1].getColor()));
		
		switch(gameMode) {
			case SINGLE_PLAYER:
				if(winner == 3) {
					showOnTieDialog();
				} else if(players[0].getColor() == winner)
					showOnWinDialog(null, -1);
				else
					showOnLoseDialog();
				break;
			case LOCAL_MULTYPLAYER:
				if(winner == 3) {
					showOnTieDialog();
				} else {
					if(players[0].getColor() == winner) {
						showOnWinDialog(players[0].getName(), 0);
					} else {
						showOnWinDialog(players[1].getName(), 1);
					}
				}
				break;
		}
	}
	
	private void showOnWinDialog(String name, int winner) {
		dialog.setContentView(R.layout.dialog_player_win);
		Button btnYesss = dialog.findViewById(R.id.btnYesss);
		TextView tvWinPieces = dialog.findViewById(R.id.tvWinPieces);
		TextView tvWinnerNameDisplay = dialog.findViewById(R.id.tvWinnerNameDisplay);
		
		if(name != null) {
			tvWinnerNameDisplay.setText(tvWinnerNameDisplay.getText() + " " + name + "!");
			tvWinPieces.setText(board.getPieces(players[winner].getColor()) + " " + tvWinPieces.getText().toString());
		} else {
			tvWinPieces.setText(board.getPieces(players[0].getColor()) + " " + tvWinPieces.getText().toString());
		}
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
	
	private void showOnLoseDialog() {
		dialog.setContentView(R.layout.dialog_player_lose);
		Button btnTryAgain = dialog.findViewById(R.id.btnTryAgain);
		TextView tvLosePieces = dialog.findViewById(R.id.tvLosePieces);
		
		tvLosePieces.setText(board.getPieces(players[0].getColor()) + " " + tvLosePieces.getText().toString());
		
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
	
	private void showOnTieDialog() {
		dialog.setContentView(R.layout.dialog_player_tie);
		Button btnTie = dialog.findViewById(R.id.btnTie);
		
		btnTie.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
				finish();
			}
		});
		
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialog.show();
	}
	
	private void saveGame(int gameMode, String player1Name, int player1Score, String player2Name, int player2Score) {
		try {
			File historicFile = new File(getApplicationContext().getFilesDir(), "historicFile");
			if(!historicFile.exists()) {
				historicFile.createNewFile();
			}
			OutputStream stream = new FileOutputStream(historicFile, true);
			stream.write((gameMode + ";" + player1Name + ";" + player1Score + ";" + player2Name + ";" + player2Score + "\n").getBytes());
			stream.flush();
			stream.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	



	Handler NetReadyHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			dialog.setContentView(R.layout.dialog_network_is_ready);
			dialogOkbtn = dialog.findViewById(R.id.btnOk);
			dialogOkbtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					((PlayGameActivity)context).sendProfilePacket();
					dialog.dismiss();
				}
			});
			dialog.show();		}
	};

	private String serializePacket(Packet p){
		Gson gson = new Gson();
		String json = gson.toJson(p);
		return json;
    }

    private Packet deserializePacket(String json){
		Gson gson = new Gson();
		Packet p = gson.fromJson(json, Packet.class);
	    return p;
    }

    public void sendProfilePacket(){
		Packet p = new Packet(players[0]);
		String s = serializePacket(p);
		network.sendSerializedPacket(s);
	}

	private void sendGamePacket(int row, int col){
		Packet p = new Packet(row, col,
				players[0].getPlayAgainCard(),
				players[0].getSkipAgainCard());
		String s = serializePacket(p);
		network.sendSerializedPacket(s);
	}

	public void readPacket(String inputString) {
	    Packet p = new Packet();
		p = deserializePacket(inputString);
        if(p != null) {
            if (!p.isPlay())
                readProfilePacket(p);
            else
                readMovePacket(p);
        }
	}

	private void readProfilePacket(Packet p) {
		players[1] = new Player(p.getPlayer().getName(),
				p.getPlayer().getImage(),
				p.getPlayer().isBot());
		if(network.getMode() == Network.CLIENT) {
			if (players[1].getColor() == Board.BLACK) {
				players[0].setColor(Board.WHITE);
				playerTurn = 1;
				Toast.makeText(context, "I'm white", Toast.LENGTH_SHORT).show();
			}
			else {
				players[0].setColor(Board.BLACK);
				playerTurn = 0;
				Toast.makeText(context, "I'm black", Toast.LENGTH_SHORT).show();
			}
		}
		else if(network.getMode() == Network.SERVER) {
		    if (players[0].getColor() == Board.WHITE) {
                players[1].setColor(Board.BLACK);
                playerTurn = 1;
            }
		    else {
                players[1].setColor(Board.WHITE);
                playerTurn = 0;
            }
        }
	}

    private void readMovePacket(Packet p) {
	    players[1].setPlayAgainCard(p.getPlayAgain());
        players[1].setSkipAgainCard(p.getSkip());
	    board.addPiece(players[1].getColor(), p.getRow(), p.getCol());
    }

	
}
