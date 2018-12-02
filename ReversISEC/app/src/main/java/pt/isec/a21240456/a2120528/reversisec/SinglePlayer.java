package pt.isec.a21240456.a2120528.reversisec;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import pt.isec.a21240456.a2120528.reversisec.Board;

public class SinglePlayer extends AppCompatActivity {

    private final int maxN = 8;
    private ImageView[][] ivCells = new ImageView[maxN][maxN];
    private Context context;
    private Drawable[] drawCells = new Drawable[3];
    private Board board = new Board();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player);

        context = this;

        loadResources();

        designGameBoard();
    }

    private void loadResources() {
        drawCells[0] = ContextCompat.getDrawable(context, R.drawable.cell_empty_bg);
        drawCells[1] = ContextCompat.getDrawable(context, R.drawable.cell_black_player_bg);
        drawCells[2] = ContextCompat.getDrawable(context, R.drawable.cell_white_player_bg);

    }

    private void designGameBoard(){

        LinearLayout llGameBoard = findViewById(R.id.gameBoard);

        int cellSize = Math.round(getScreenSizeMinusPadding() / maxN);

        LinearLayout.LayoutParams llRow = new LinearLayout.LayoutParams(cellSize * maxN, cellSize);
        LinearLayout.LayoutParams llCell = new LinearLayout.LayoutParams(cellSize, cellSize);

        for (int i = 0; i < maxN; i++) {
            LinearLayout linRow = new LinearLayout(context);
            for (int j = 0; j < maxN; j++) {
                ivCells[i][j] = new ImageView(context);

                switch(board.getCell(i, j)) {
                    case 0:
                        ivCells[i][j].setBackground(drawCells[0]);
                        break;
                    case 1:
                        ivCells[i][j].setBackground(drawCells[1]);
                        break;
                    case 2:
                        ivCells[i][j].setBackground(drawCells[2]);
                        break;
                }

                linRow.addView(ivCells[i][j], llCell);
            }
            llGameBoard.addView(linRow, llRow);
        }

        /*for (int i = 0; i < maxN; i++) {
            LinearLayout linRow = new LinearLayout(context);
            for (int j = 0; j < maxN; j++) {
                ivCells[i][j] = new ImageView(context);
                ivCells[i][j].setBackground(drawCells[0]);
                linRow.addView(ivCells[i][j], llCell);
            }
            llGameBoard.addView(linRow, llRow);
        }*/


    }

    private float getScreenSizeMinusPadding(){
        return context.getResources().getDisplayMetrics().widthPixels - dpToPixels(30, context);
    }

    public static int dpToPixels(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
