package com.capgemini.chess.service.to;

import java.util.ArrayList;
import java.util.List;

import com.capgemini.chess.dataaccess.enumerations.Piece;
import com.capgemini.chess.service.enumerations.BoardState;

/**
 * Transport object which represents chess board.
 * 
 * @author Michal Bejm
 *
 */
public class BoardTO {
	
public static final int SIZE = 8;
	
	private Piece[][] pieces = new Piece[SIZE][SIZE];
	private List<MoveTO> moveHistory = new ArrayList<>();
	private BoardState state;

	public List<MoveTO> getMoveHistory() {
		return moveHistory;
	}

	public Piece[][] getPieces() {
		return pieces;
	}

	public BoardState getState() {
		return state;
	}

	public void setState(BoardState state) {
		this.state = state;
	}
	
	/**
	 * Sets chess piece on board based on given coordinates
	 * 
	 * @param piece chess piece
	 * @param board chess board
	 * @param coordinate given coordinates
	 */
	public void setPieceAt(Piece piece, CoordinateTO coordinate) {
		pieces[coordinate.getX()][coordinate.getY()] = piece;
	}
	
	/**
	 * Gets chess piece from board based on given coordinates
	 * 
	 * @param coordinate given coordinates
	 * @return chess piece
	 */
	public Piece getPieceAt(CoordinateTO coordinate) {
		return pieces[coordinate.getX()][coordinate.getY()];
	}
	
	/**
	 * Checks if filed is empty
	 * 
	 * @param board chess board
	 * @param field coordinated of checked field
	 * @return true is filed is empty, false otherwise
	 */
	public boolean isFieldEmpty(CoordinateTO field) {
		return getPieceAt(field) == null;
	}
}
