package pt.isec.a21240456.a2120528.reversisec;

import java.io.Serializable;
import java.util.Arrays;

//0 -> empty board
//1 -> black piece
//2 -> white piece
//3 -> possible move
public class Board implements Serializable {
	static final int EMPTY = 0;
	static final int WHITE = 1;
	static final int BLACK = 2;
	static final int POSSIBLE_MOVE = 3;
	
	static final int USER_MOVE = 0;
	static final int DRAW_POSSIBLE_MOVES = 1;
	static final int BOT_DECISION = 2;
	
	private final int boardSize = 8;
	private int[][] cells = new int[boardSize][boardSize];
	int maxRow, maxCol, maxCounter, maxPieces;
	
	public Board() {
		newGame();
	}
	
	public void newGame() {
		for(int[] row : cells)
			Arrays.fill(row, 0);
		
		addPiece(WHITE, 3, 3);
		addPiece(BLACK, 3, 4);
		addPiece(BLACK, 4, 3);
		addPiece(WHITE, 4, 4);
		
		checkNextTurnPossibleMoves(BLACK);
	}
	
	public int getCell(int i, int j) {
		return cells[i][j];
	}
	
	public boolean makeMove(int turn, int row, int col) {
		
		if(checkMove(turn, row, col, USER_MOVE)) {
			addPiece(turn, row, col);
			if(turn == BLACK)
				turn = WHITE;
			else
				turn = BLACK;
			
			checkNextTurnPossibleMoves(turn);
			
			return true;
		} else
			return false;
	}
	
	public void addPiece(int turn, int row, int col) {
		cells[row][col] = turn;
	}
	
	public boolean checkMove(int turn, int row, int col, int resolve) {
		boolean nw, nn, ne, ww, ee, sw, ss, se;
		
		if(cells[row][col] != EMPTY && cells[row][col] != POSSIBLE_MOVE)
			return false;
		
		nw = validMoveAndResolve(turn, row, col, -1, -1, resolve);
		nn = validMoveAndResolve(turn, row, col, -1, 0, resolve);
		ne = validMoveAndResolve(turn, row, col, 1, 1, resolve);
		ww = validMoveAndResolve(turn, row, col, 0, -1, resolve);
		ee = validMoveAndResolve(turn, row, col, 0, 1, resolve);
		sw = validMoveAndResolve(turn, row, col, 1, -1, resolve);
		ss = validMoveAndResolve(turn, row, col, 1, 0, resolve);
		se = validMoveAndResolve(turn, row, col, 1, 1, resolve);
		
		if(nw || nn || ne || ww || ee || sw || ss || se)
			return true;
		else
			return false;
	}
	
	public boolean validMoveAndResolve(int turn, int row, int col, int drow, int dcol, int resolve) {
		
		boolean firstCheck = true;
		char other;
		int lastPiece = EMPTY;
		
		if(turn == BLACK)
			other = WHITE;
		else if(turn == WHITE)
			other = BLACK;
		else
			return false;
		
		if(row + drow < 0 || row + drow > 7)
			return false;
		if(col + dcol < 0 || col + dcol > 7)
			return false;
		if(cells[row + drow][col + dcol] != other)
			return false;
		
		for(int i = row + drow, j = col + dcol; i < boardSize || j < boardSize; i += drow, j += dcol) {
			if(i < 0 || i > 7 || j < 0 || j > 7)
				return false;
			
			if(cells[i][j] == other) {
				firstCheck = false;
				continue;
			} else if(!firstCheck && cells[i][j] == turn) {
				lastPiece = cells[i][j];
				break;
			} else
				break;
		}
		
		if(resolve == USER_MOVE && lastPiece == turn) {
			for(int i = row + drow, j = col + dcol; i < boardSize || j < boardSize; i += drow, j += dcol) {
				if(cells[i][j] == other)
					cells[i][j] = turn;
				else if(cells[i][j] == turn)
					break;
			}
			return true;
		} else if(resolve == DRAW_POSSIBLE_MOVES && lastPiece == turn)
			return true;
		else if(resolve == BOT_DECISION && lastPiece == turn) {
			maxCounter = 0;
			for(int i = row + drow, j = col + dcol; i < boardSize || j < boardSize; i += drow, j += dcol) {
				if(cells[i][j] == other)
					maxCounter++;
				else if(cells[i][j] == turn)
					break;
			}
			if(maxCounter > maxPieces) {
				maxRow = row;
				maxCol = col;
				maxPieces = maxCounter;
			}
			return true;
		} else
			return false;
	}
	
	public boolean checkNextTurnPossibleMoves(int turn) {
		int counter = 0;
		
		for(int i = 0; i < boardSize; i++) {
			for(int j = 0; j < boardSize; j++) {
				if(checkMove(turn, i, j, DRAW_POSSIBLE_MOVES)) {
					cells[i][j] = POSSIBLE_MOVE;
					counter++;
				} else if(cells[i][j] == POSSIBLE_MOVE)
					cells[i][j] = EMPTY;
			}
			
		}
		
		if(counter == 0)
			return false;
		else
			return true;
	}
	
	public int getPieces(int color) {
		int count = 0;
		
		for(int i = 0; i < boardSize; i++) {
			for(int j = 0; j < boardSize; j++) {
				if(cells[i][j] == color) {
					count++;
				}
			}
		}
		return count;
	}
	
	public boolean intelligentBotMove(int turn) {
		
		maxRow = 8;
		maxCol = 8;
		maxPieces = 0;
		
		for(int i = 0; i < boardSize; i++) {
			for(int j = 0; j < boardSize; j++) {
				
				if(cells[i][j] == POSSIBLE_MOVE) {
					checkMove(turn, i, j, BOT_DECISION);
				}
			}
		}
		
		if(maxRow >= 0 && maxRow < 8 && maxCol >= 0 && maxRow < 8) {
			return makeMove(turn, maxRow, maxCol);
		} else
			return false;
	}
	
}