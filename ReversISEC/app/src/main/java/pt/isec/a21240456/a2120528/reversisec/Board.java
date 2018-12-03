package pt.isec.a21240456.a2120528.reversisec;

import java.util.Arrays;

public class Board {

    private final int boardSize = 8;
    private char[][] cells = new char[boardSize][boardSize];
    char turn;

    public Board() {
        newGame();
    }

    public void newGame() {
        for (char[] row : cells)
            Arrays.fill(row,' ');

        turn = 'b';

        addPiece('w', 3, 3);
        addPiece('b', 3, 4);
        addPiece('b', 4, 3);
        addPiece('w', 4, 4);
    }

    public char getCell(int i, int j) {
        return cells[i][j];
    }

    public boolean makeMove(char turn , int row, int col) {

        if(checkMove(turn, row, col)) {
            addPiece(turn, row, col);
            return true;
        }
        else
            return false;
    }

    public void addPiece(char turn, int row, int col) {
            cells[row][col] = turn;
    }

    public boolean checkMove(char turn, int row, int col) {
        boolean nw, nn, ne, ww, ee, sw, ss, se;

        if(cells[row][col] == ' ')
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

    public boolean validMove(char turn, int row, int col, int drow, int dcol) {

        boolean firstCheck = true;
        char other;
        char lastPiece = ' ';

        if (turn == 'b')
            other = 'w';
        else if (turn == 'w')
            other = 'b';
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
                continue;
            }
            else if(!firstCheck){
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





















