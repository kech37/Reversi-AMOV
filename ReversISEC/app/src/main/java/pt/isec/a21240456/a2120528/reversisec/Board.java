package pt.isec.a21240456.a2120528.reversisec;

import java.util.Arrays;

public class Board {

    private final int boardSize = 8;
    private int[][] cells = new int[boardSize][boardSize];

    public Board() {
        newGame();
    }

    public void newGame(){
        for (int[] row: cells)
            Arrays.fill(row, 0);

        addPiece('w', 3, 3);
        addPiece('b', 3, 4);
        addPiece('b', 4, 3);
        addPiece('w', 4, 4);
    }

    public int getCell(int i, int j) {
        return cells[i][j];
    }

    public boolean addPiece(char color, int line, int row) {

        if(cells[line][row] == 0) {
            if (color == 'b') {
                cells[line][row] = 1;
                return true;
            } else if(color == 'w'){
                cells[line][row] = 2;
                return true;
            }
        }

        return false;
    }




}

