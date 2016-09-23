package com.capgemini.chess.service.manager;

import java.util.Arrays;
import java.util.List;

import com.capgemini.chess.dataaccess.enumerations.Color;
import com.capgemini.chess.dataaccess.enumerations.MoveType;
import com.capgemini.chess.dataaccess.enumerations.Piece;
import com.capgemini.chess.dataaccess.enumerations.PieceType;
import com.capgemini.chess.service.enumerations.BoardState;
import com.capgemini.chess.service.exceptions.InvalidMoveException;
import com.capgemini.chess.service.exceptions.KingInCheckException;
import com.capgemini.chess.service.to.BoardTO;
import com.capgemini.chess.service.to.CoordinateTO;
import com.capgemini.chess.service.to.MoveTO;

/**
 * Class for managing of basic operations on the Chess Board.
 * 
 * @author Michal Bejm
 *
 */
public class BoardManager {
	
	private BoardTO board = new BoardTO();

	public BoardManager() {
		initBoard();
	}
	
	public BoardManager(List<MoveTO> moves) {
		initBoard();
		for (MoveTO move : moves) {
			addMove(move);
		}
	}
	
	public BoardManager(BoardTO board) {
		this.board = board;
	}
	
	/**
	 * Getter for generated board
	 * 
	 * @return board
	 */
	public BoardTO getBoard() {
		return board;
	}
	
	/**
	 * Performs move of the chess piece on the chess board from one field to another.
	 * 
	 * @param from coordinates of 'from' field
	 * @param to coordinates of 'to' field
	 * @return move object which includes moved piece and move type
	 * @throws InvalidMoveException in case move is not valid
	 */
	public MoveTO performMove(CoordinateTO from, CoordinateTO to) 
			throws InvalidMoveException {
		
		MoveTO move = validateMove(from, to);
		
		addMove(move);
		
		return move;
	}
	
	/**
	 * Calculates state of the chess board.
	 * 
	 * @return state of the chess board
	 */
	public BoardState updateBoardState() {
		
		Color nextMoveColor = calculateNextMoveColor();
		
		boolean isKingInCheck = isKingInCheck(nextMoveColor);
		boolean isAnyMoveValid = isAnyMoveValid(nextMoveColor);
		
		BoardState boardState;
		if (isKingInCheck) {
			if (isAnyMoveValid) {
				boardState = BoardState.CHECK;
			}
			else {
				boardState = BoardState.CHECK_MATE;
			}
		}
		else {
			if (isAnyMoveValid) {
				boardState = BoardState.REGULAR;
			}
			else {
				boardState = BoardState.STALE_MATE;
			}
		}
		board.setState(boardState);
		return boardState;
	}
	
	/**
	 * Checks threefold repetition rule (one of the conditions to end the chess game with a draw).
	 * 
	 * @return true if current state repeated at list two times, false otherwise
	 */
	public boolean checkThreefoldRepetitionRule() {
		
		// there is no need to check moves that where before last capture/en passant/castling
		int lastNonAttackMoveIndex = findLastNonAttackMoveIndex();
		List<MoveTO> omittedMoves = board.getMoveHistory().subList(0, lastNonAttackMoveIndex);
		BoardManager simulatedBoardManager = new BoardManager(omittedMoves);
		
		int counter = 0;
		for (int i = lastNonAttackMoveIndex; i < board.getMoveHistory().size(); i++) {
			MoveTO moveToAdd = board.getMoveHistory().get(i);
			simulatedBoardManager.addMove(moveToAdd);
			boolean areBoardsEqual = Arrays.deepEquals(board.getPieces(), 
					simulatedBoardManager.getBoard().getPieces());
			if (areBoardsEqual) {
				counter++;
			}
		}
		
		return counter >= 2;
	}

	/**
	 * Checks 50-move  rule (one of the conditions to end the chess game with a draw).
	 * 
	 * @return true if no pawn was moved or not capture was 
	 * 			performed during last 50 moves, false otherwise
	 */
	public boolean checkFiftyMoveRule() {
		
		// for this purpose a "move" consists of a player completing his turn 
		// followed by his opponent completing his turn
		if (board.getMoveHistory().size() < 100) {
			return false;
		}
		
		for (int i = board.getMoveHistory().size()-1; i >= board.getMoveHistory().size() - 100; i--) {
			MoveTO currentMove = board.getMoveHistory().get(i);
			PieceType currentPieceType = currentMove.getMovedPiece().getType();
			if (currentMove.getType() != MoveType.ATTACK || currentPieceType == PieceType.PAWN) {
				return false;
			}
		}
		
		return true;
	}
	
	private void initBoard() {
		
		board.setPieceAt(Piece.BLACK_ROOK, new CoordinateTO(0, 7));
		board.setPieceAt(Piece.BLACK_KNIGHT, new CoordinateTO(1, 7));
		board.setPieceAt(Piece.BLACK_BISHOP, new CoordinateTO(2, 7));
		board.setPieceAt(Piece.BLACK_QUEEN, new CoordinateTO(3, 7));
		board.setPieceAt(Piece.BLACK_KING, new CoordinateTO(4, 7));
		board.setPieceAt(Piece.BLACK_BISHOP, new CoordinateTO(5, 7));
		board.setPieceAt(Piece.BLACK_KNIGHT, new CoordinateTO(6, 7));
		board.setPieceAt(Piece.BLACK_ROOK, new CoordinateTO(7, 7));
        
        for (int x = 0; x < BoardTO.SIZE; x++) {
        	board.setPieceAt(Piece.BLACK_PAWN, new CoordinateTO(x, 6));
        }
		
        board.setPieceAt(Piece.WHITE_ROOK, new CoordinateTO(0, 0));
        board.setPieceAt(Piece.WHITE_KNIGHT, new CoordinateTO(1, 0));
        board.setPieceAt(Piece.WHITE_BISHOP, new CoordinateTO(2, 0));
        board.setPieceAt(Piece.WHITE_QUEEN, new CoordinateTO(3, 0));
        board.setPieceAt(Piece.WHITE_KING, new CoordinateTO(4, 0));
        board.setPieceAt(Piece.WHITE_BISHOP, new CoordinateTO(5, 0));
        board.setPieceAt(Piece.WHITE_KNIGHT, new CoordinateTO(6, 0));
        board.setPieceAt(Piece.WHITE_ROOK, new CoordinateTO(7, 0));

        for (int x = 0; x < BoardTO.SIZE; x++) {
        	board.setPieceAt(Piece.WHITE_PAWN, new CoordinateTO(x, 1));
        }
	}
	
	private void addMove(MoveTO move) {
		
		addRegularMove(move);
		
		if (move.getType() == MoveType.CASTLING) {
			addCastling(move);
		}
		else if (move.getType() == MoveType.EN_PASSANT) {
			addEnPassant(move);
		}
		
		board.getMoveHistory().add(move);
	}
	
	private void addRegularMove(MoveTO move) {
		Piece movedPiece = board.getPieceAt(move.getFrom());
		board.setPieceAt(null, move.getFrom());
		board.setPieceAt(movedPiece, move.getTo());
		
		performPromotion(move, movedPiece);
	}

	private void performPromotion(MoveTO move, Piece movedPiece) {
		if (movedPiece == Piece.WHITE_PAWN && move.getTo().getY() == (BoardTO.SIZE - 1)) {
			board.setPieceAt(Piece.WHITE_QUEEN, move.getTo());
		}
		if (movedPiece == Piece.BLACK_PAWN && move.getTo().getY() == 0) {
			board.setPieceAt(Piece.BLACK_QUEEN, move.getTo());
		}
	}

	private void addCastling(MoveTO move) {
		if (move.getFrom().getX() > move.getTo().getX()) {
			Piece rook = board.getPieceAt(new CoordinateTO(0, move.getFrom().getY()));
			board.setPieceAt(null, new CoordinateTO(0, move.getFrom().getY()));
			board.setPieceAt(rook, new CoordinateTO(move.getTo().getX()+1, move.getTo().getY()));
		}
		else {
			Piece rook = board.getPieceAt(new CoordinateTO(BoardTO.SIZE-1, move.getFrom().getY()));
			board.setPieceAt(null, new CoordinateTO(BoardTO.SIZE-1, move.getFrom().getY()));
			board.setPieceAt(rook, new CoordinateTO(move.getTo().getX()-1, move.getTo().getY()));
		}
	}

	private void addEnPassant(MoveTO move) {
		MoveTO lastMove = board.getMoveHistory().get(board.getMoveHistory().size()-1);
		board.setPieceAt(null, lastMove.getTo());
	}
	
	private MoveTO validateMove(CoordinateTO from, CoordinateTO to) throws InvalidMoveException, KingInCheckException {
		MoveTO move = new MoveTO();
		move.setFrom(from);
		move.setTo(to);
		
		Piece movedPiece = determineMovedPiece(from, to);
		validatePieceColor(movedPiece);
		move.setMovedPiece(movedPiece);
		
		MoveType moveType = determineMoveType(movedPiece, from, to);
		move.setType(moveType);
		
		BoardManager simulatedBoardManager = simulateBoard(move);
		if (simulatedBoardManager.isKingInCheck(movedPiece.getColor())) {
			throw new KingInCheckException();
		}
		return move;
	}
	
	private boolean isKingInCheck(Color kingColor) {
		CoordinateTO kingPosition = findKingPosition(kingColor);
		
		return (kingPosition != null && isFieldAttacked(kingPosition, kingColor));
	}

	private Piece determineMovedPiece(CoordinateTO from, CoordinateTO to)
			throws InvalidMoveException {
		
		if (from.getX() >= BoardTO.SIZE || from.getX() < 0 
				|| from.getY() >= BoardTO.SIZE || from.getY() < 0
				|| to.getX() >= BoardTO.SIZE || to.getY() < 0
				|| to.getY() >= BoardTO.SIZE || to.getY() < 0) {
			throw new InvalidMoveException();
		}
		
		Piece movedPiece = board.getPieceAt(from);
		
		if (movedPiece == null) {
			throw new InvalidMoveException();
		}
		
		return movedPiece;
	}
	
	private void validatePieceColor(Piece movedPiece) throws InvalidMoveException {
		
		if ((board.getMoveHistory().size() % 2 == 0 && movedPiece.getColor() == Color.BLACK)
				|| (board.getMoveHistory().size() % 2 == 1 && movedPiece.getColor() == Color.WHITE)) {
			throw new InvalidMoveException();
		}
	}
	
	private MoveType determineMoveType(Piece movedPiece, CoordinateTO from, CoordinateTO to) 
			throws InvalidMoveException {
		if (isCastling(movedPiece, from, to)) {
			return MoveType.CASTLING;
		}
		else if (isEnPassant(movedPiece, from, to)) {
			return MoveType.EN_PASSANT;
		}
		else if (isCapture(movedPiece, to)
				&& isMoveValid(movedPiece, from, to)) {
			return MoveType.CAPTURE;
		}
		else if (isMoveValid(movedPiece, from, to)) {
			return MoveType.ATTACK;
		}
		throw new InvalidMoveException();
	}
	
	private boolean isCapture(Piece movedPiece, CoordinateTO destination) {
		Piece capturedPiece = board.getPieceAt(destination);
		return capturedPiece != null && capturedPiece.getColor() != movedPiece.getColor();
	}

	private boolean isMoveValid(Piece movedPiece,
			CoordinateTO from, CoordinateTO to) {
		if (!isDestinationValid(movedPiece, to)) {
			return false;
		}
		if (!isDirectionAndDistanceValid(movedPiece, from, to)) {
			return false;
		}
		if (movedPiece.getType() != PieceType.KNIGHT && !isPathEmpty(from, to)) {
			return false;
		}
		return true;
	}

	private boolean isDestinationValid(Piece movedPiece, CoordinateTO destination) {
		return isFieldEmpty(destination) || isCapture(movedPiece, destination);
	}
	
	private boolean isDirectionAndDistanceValid(Piece movedPiece,
			CoordinateTO from, CoordinateTO to) {
		
		boolean isCapture = isCapture(movedPiece, to);
		
		switch (movedPiece.getType()) {
		case BISHOP:
			return isDiagonalMove(from, to);
		case ROOK:
			return isStraightMove(from, to);
		case QUEEN:
			return isDiagonalMove(from, to) || isStraightMove(from, to);
		case KING:
			return isKingsMoveValid(from, to);
		case KNIGHT:
			return isKnightsMoveValid(from, to);
		case PAWN:
			return isPawnsMoveValid(from, to, movedPiece.getColor(), isCapture);
		default:
			return false;
		}
	}
	
	private boolean isPathEmpty(CoordinateTO from, CoordinateTO to) {
		
		int horizontalAddend = (int) Math.signum(to.getX() - from.getX());
		int verticalAddend = (int) Math.signum(to.getY() - from.getY());
		
		CoordinateTO analysedField = new CoordinateTO(from.getX() + horizontalAddend, 
				from.getY() + verticalAddend);
		
		while (!analysedField.equals(to)) {
			if (!isFieldEmpty(analysedField)) {
				return false;
			}
			analysedField = new CoordinateTO(analysedField.getX() + horizontalAddend, 
					analysedField.getY() + verticalAddend);
		}
		
		return true;
	}

	private boolean isPawnsMoveValid(CoordinateTO from, CoordinateTO to, 
			Color pawnColor, boolean isCapture) {
		if (!isPawnsVerticalDirectionValid(from, to, pawnColor)) {
			return false;
		}
		
		int horizontalDistance = calculateHorizontalDistance(from, to);
		int verticalDistance = calculateVerticalDistance(from, to);
		
		// capture - one field diagonally
		if (isCapture && horizontalDistance == 1 && verticalDistance == 1) {
			return true;
		}
		// normal move - one field vertically or two for it's first move
		if (!isCapture) {
			if (verticalDistance == 1 && horizontalDistance == 0) {
				return true;
			}
			else if (verticalDistance == 2 && horizontalDistance == 0
					&& pawnColor == Color.WHITE && from.getY() == 1) {
				return true;
			}
			else if (verticalDistance == 2 && horizontalDistance == 0
					&& pawnColor == Color.BLACK && from.getY() == 6) {
				return true;
			}
		}
		return false;
	}

	private boolean isPawnsVerticalDirectionValid(CoordinateTO from, CoordinateTO to, Color pawnColor) {
		// white go up, black go down
		if (pawnColor == Color.WHITE && from.getY() < to.getY()) {
			return true;
		}
		if (pawnColor == Color.BLACK && from.getY() > to.getY()) {
			return true;
		}
		return false;
	}

	private boolean isKnightsMoveValid(CoordinateTO from, CoordinateTO to) {
		int horizontalDistance = calculateHorizontalDistance(from, to);
		int verticalDistance = calculateVerticalDistance(from, to);
		return (horizontalDistance == 1 && verticalDistance == 2)
				|| (horizontalDistance == 2 && verticalDistance == 1);
	}

	private boolean isKingsMoveValid(CoordinateTO from, CoordinateTO to) {
		int horizontalDistance = calculateHorizontalDistance(from, to);
		int verticalDistance = calculateVerticalDistance(from, to);
		return horizontalDistance <= 1 && verticalDistance <= 1;
	}

	private boolean isDiagonalMove(CoordinateTO from, CoordinateTO to) {
		return Math.abs(from.getX() - to.getX()) == Math.abs(from.getY() - to.getY());
	}
	
	private boolean isStraightMove(CoordinateTO from, CoordinateTO to) {
		return from.getX() == to.getX() || from.getY() == to.getY();
	}

	private boolean isEnPassant(Piece movedPiece, CoordinateTO from, CoordinateTO to) {
		
		if (board.getMoveHistory().size() == 0) {
			return false;
		}
		
		MoveTO previousMove = board.getMoveHistory().get(board.getMoveHistory().size()-1);
		if (movedPiece.getType() != PieceType.PAWN ||
				previousMove.getMovedPiece().getType() != PieceType.PAWN) {
			return false;
		}
		
		if (!isPawnsVerticalDirectionValid(from, to, movedPiece.getColor())) {
			return false;
		}
		
		// pawn has to move 1 field diagonally
		int horizontalDistance = calculateHorizontalDistance(from, to);
		int verticalDistance = calculateVerticalDistance(from, to);
		if (horizontalDistance != 1 || verticalDistance != 1) {
			return false;
		}
		
		// pawn in the previous move must have omitted destination (to) field
		int previousMoveVerticalDistance = calculateVerticalDistance(
				previousMove.getFrom(), previousMove.getTo());
		if (previousMoveVerticalDistance != 2) {
			return false;
		}
		CoordinateTO ommitedField = new CoordinateTO(
				previousMove.getTo().getX(), 
				(previousMove.getFrom().getY() + previousMove.getTo().getY())/2);
		
		return ommitedField.equals(to);
	}

	private boolean isCastling(Piece movedPiece, CoordinateTO from, CoordinateTO to) {
		
		// castling is performed by King only
		if (movedPiece.getType() != PieceType.KING) {
			return false;
		}
		
		int horizontalDistance = calculateHorizontalDistance(from, to);
		int verticalDistance = calculateVerticalDistance(from, to);
		
		// king moves two fields horizontally
		if (horizontalDistance != 2 || verticalDistance != 0) {
			return false;
		}
		
		// king may not be in check
		if (board.getState() == BoardState.CHECK) {
			return false;
		}
		
		// king might not have been moved
		if (didPieceMove(movedPiece)) {
			return false;
		}
		
		CoordinateTO rookCoordinates = findRookCoordinatesInCaslting(from, to);
		// rook might not have been moved
		if (didPieceMove(rookCoordinates)) {
			return false;
		}
		
		// all fields between king and rook have to be empty
		if (!isPathEmpty(from, rookCoordinates)) {
			return false;
		}
		
		CoordinateTO omittedField = new CoordinateTO(
				(from.getX() + to.getX())/2, from.getY()); 
		// king does not pass through a square that is attacked by an enemy piece
		if (isFieldAttacked(omittedField, movedPiece.getColor())) {
			return false;
		}
		
		return true;
	}

	private CoordinateTO findRookCoordinatesInCaslting(CoordinateTO from, CoordinateTO to) {
		if (from.getX() > to.getX()) {
			return new CoordinateTO(0, from.getY());
		}
		else {
			return new CoordinateTO(BoardTO.SIZE-1, from.getY());
		}
	}
	
	private boolean didPieceMove(Piece movedPiece) {
		for (MoveTO move : board.getMoveHistory()) {
			if (move.getMovedPiece() == movedPiece) {
				return true;
			}
		}
		return false;
	}
	
	private boolean didPieceMove(CoordinateTO coordinate) {
		for (MoveTO move : board.getMoveHistory()) {
			if (move.getFrom().equals(coordinate)) {
				return true;
			}
		}
		return false;
	}

	private int calculateHorizontalDistance(CoordinateTO from, CoordinateTO to) {
		return (int) Math.abs(from.getX() - to.getX());
	}
	
	private int calculateVerticalDistance(CoordinateTO from, CoordinateTO to) {
		return (int) Math.abs(from.getY() - to.getY());
	}
	
	private boolean isAnyMoveValid(Color nextMoveColor) {
		
		for (int x = 0; x < BoardTO.SIZE; x++) {
			for (int y = 0; y < BoardTO.SIZE; y++) {
				Piece piece = board.getPieceAt(new CoordinateTO(x, y));
				if (piece != null && piece.getColor() == nextMoveColor) {
					if (isAnyMoveValid(new CoordinateTO(x, y))) {
						return true;
					}
				}
			}
		}
		
		return false;
	}

	private boolean isAnyMoveValid(CoordinateTO from) {
		for (int x = 0; x < BoardTO.SIZE; x++) {
			for (int y = 0; y < BoardTO.SIZE; y++) {
				try {
					validateMove(from, new CoordinateTO(x, y));
					return true;
				} catch (InvalidMoveException e) {
					// move is not valid - let's look further
				}
			}
		}
		
		return false;
	}

	private Color calculateNextMoveColor() {
		if (board.getMoveHistory().size() % 2 == 0) {
			return Color.WHITE;
		}
		else {
			return Color.BLACK;
		}
	}
	
	private boolean isFieldAttacked(CoordinateTO field, Color kingColor) {
		
		for (int x = 0; x < BoardTO.SIZE; x++) {
			for (int y = 0; y < BoardTO.SIZE; y++) {
				Piece piece = board.getPieceAt(new CoordinateTO(x, y));
				if (piece != null && piece.getColor() != kingColor) {
					// if capturing is valid - field attacked
					if (isMoveValid(piece, new CoordinateTO(x, y), field)) {
						return true;
					}
				}
			} 
		}
		return false;
	}

	private CoordinateTO findKingPosition(Color kingColor) {
		for (int x = 0; x < BoardTO.SIZE; x++) {
			for (int y = 0; y < BoardTO.SIZE; y++) {
				Piece piece = board.getPieceAt(new CoordinateTO(x, y));
				if (piece != null && piece.getColor() == kingColor 
						&& piece.getType() == PieceType.KING) {
					return new CoordinateTO(x, y);
				}
			} 
		}
		return null;
	}
	
	private int findLastNonAttackMoveIndex() {
		int counter = 0;
		int lastNonAttackMoveIndex = 0;
		
		for (MoveTO move : board.getMoveHistory()) {
			if (move.getType() != MoveType.ATTACK) {
				lastNonAttackMoveIndex = counter;
			}
			counter++;
		}
		
		return lastNonAttackMoveIndex;
	}
	
	private BoardManager simulateBoard(MoveTO move) {
		BoardManager simulatedBoardManager = new BoardManager(new BoardTO());
		simulatedBoardManager.getBoard()
			.getMoveHistory().addAll(board.getMoveHistory());
		for (int y = 0; y < BoardTO.SIZE; y++) {
			for (int x = 0; x < BoardTO.SIZE; x++) {
				Piece piece = board.getPieceAt(new CoordinateTO(x, y));
				simulatedBoardManager.getBoard().setPieceAt(
						piece, new CoordinateTO(x, y));
			}
		}
		simulatedBoardManager.addMove(move);
		return simulatedBoardManager;
	}
	
	private boolean isFieldEmpty(CoordinateTO field) {
		return board.getPieceAt(field) == null;
	}
}
