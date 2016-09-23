package com.capgemini.chess.service.to;

import com.capgemini.chess.dataaccess.entities.MoveEntity;
import com.capgemini.chess.dataaccess.enumerations.MoveType;
import com.capgemini.chess.dataaccess.enumerations.Piece;

/**
 * Transport object which represents {@link MoveEntity}.
 * 
 * @author Michal Bejm
 *
 */
public class MoveTO {
	
	private long id;
	private long sequenceNumber;
	private CoordinateTO from;
	private CoordinateTO to;
	private MoveType type;
	private Piece movedPiece;
	private Piece capturedPiece;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public long getSequenceNumber() {
		return sequenceNumber;
	}
	
	public void setSequenceNumber(long sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	
	public CoordinateTO getFrom() {
		return from;
	}
	
	public void setFrom(CoordinateTO from) {
		this.from = from;
	}
	
	public CoordinateTO getTo() {
		return to;
	}
	
	public void setTo(CoordinateTO to) {
		this.to = to;
	}
	
	public MoveType getType() {
		return type;
	}
	
	public void setType(MoveType type) {
		this.type = type;
	}
	
	public Piece getMovedPiece() {
		return movedPiece;
	}
	
	public void setMovedPiece(Piece movedPiece) {
		this.movedPiece = movedPiece;
	}
	
	public Piece getCapturedPiece() {
		return capturedPiece;
	}
	
	public void setCapturedPiece(Piece capturedPiece) {
		this.capturedPiece = capturedPiece;
	}
}
