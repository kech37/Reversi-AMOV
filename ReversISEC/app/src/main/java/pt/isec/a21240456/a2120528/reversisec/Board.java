package pt.isec.a21240456.a2120528.reversisec;

import java.util.Arrays;

//0 -> empty board
//1 -> black piece
//2 -> white piece
//3 -> possible move
public class Board {
    static final int EMPTY = 0;
    static final int WHITE = 1;
    static final int BLACK = 2;

    private final int boardSize = 8;
    private int[][] cells = new int[boardSize][boardSize];
    int turn = BLACK;
    int nPlays = 0;

    public Board() {
        newGame();
    }

    public void newGame() {
        for (int[] row : cells)
            Arrays.fill(row, 0);

        addPiece(WHITE, 3, 3);
        addPiece(BLACK, 3, 4);
        addPiece(BLACK, 4, 3);
        addPiece(WHITE, 4, 4);
    }

    public int getCell(int i, int j) {
        return cells[i][j];
    }

    public boolean makeMove(int row, int col) {

        if(checkMove(turn, row, col)) {
            addPiece(turn, row, col);
            if(turn == BLACK)
                turn = WHITE;
            else
                turn = WHITE;

            return true;
        }
        else
            return false;
    }

    public void addPiece(int turn, int row, int col) {
        cells[row][col] = turn;
    }

    public boolean checkMove(int turn, int row, int col) {
        boolean nw, nn, ne, ww, ee, sw, ss, se;

        if(cells[row][col] != EMPTY)
            return false;

        nw = validMove(turn, row, col, -1, -1);
        nn = validMove(turn, row, col, 1, 0);
        ne = validMove(turn, row, col, 1, 1);
        ww = validMove(turn, row, col, 0, -1);
        ee = validMove(turn, row, col, 0, 1);
        sw = validMove(turn, row, col, 1, -1);
        ss = validMove(turn, row, col, 1, 0);
        se = validMove(turn, row, col, 1, 1);

        if(nw || nn || ne || ww || ee || sw || ss || se)
            return true;
        else
            return false;
    }

    public boolean validMove(int turn, int row, int col, int drow, int dcol) {

        boolean firstCheck = true;
        char other;
        int lastPiece = EMPTY;

        if (turn == BLACK)
            other = WHITE;
        else if (turn == WHITE)
            other = BLACK;
        else
            return false;

        if(row + drow < 0 || row + drow > 7)
            return false;
        if(col + dcol < 0 || col + dcol > 7)
            return false;
        if(cells[row + drow][col + dcol] != other)
            return false;

        for(int i = row + drow, j=col + dcol; i<boardSize || j<boardSize; i+=drow, j+=dcol) {
            if(cells[i][j] == other) {
                firstCheck = false;
                continue;
            }
            else if(!firstCheck && cells[i][j] == turn){
                lastPiece = cells[i][j];
                break;
            }
            else
                break;
        }

        if(lastPiece == turn) {
            for (int i = row + drow, j = col + dcol; i < boardSize || j < boardSize; i += drow, j += dcol) {
                if(cells[i][j] == other)
                    cells[i][j] = turn;

                if(cells[i][j] == turn)
                    break;
            }
            return true;
        }



        return false;
    }

}