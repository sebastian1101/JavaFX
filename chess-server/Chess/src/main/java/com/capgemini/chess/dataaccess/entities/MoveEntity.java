package com.capgemini.chess.dataaccess.entities;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.capgemini.chess.dataaccess.enumerations.MoveType;
import com.capgemini.chess.dataaccess.enumerations.Piece;

/**
 * Chess move representation.
 * 
 * @author Michal Bejm
 *
 */
@Entity
public class MoveEntity {
	
	@Id @GeneratedValue
	private long id;
	private long sequenceNumber;
	@Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "x", column = @Column(name = "from_x")),                       
        @AttributeOverride(name = "y", column = @Column(name = "from_y"))
    })
	private CoordinateEntity from;
	@Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "x", column = @Column(name = "to_x")),                       
        @AttributeOverride(name = "y", column = @Column(name = "to_y"))
    })
	private CoordinateEntity to;
	@Enumerated(EnumType.STRING)
	private MoveType type;
	@Enumerated(EnumType.STRING)
	private Piece movedPiece;
	@Enumerated(EnumType.STRING)
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
	
	public CoordinateEntity getFrom() {
		return from;
	}
	
	public void setFrom(CoordinateEntity from) {
		this.from = from;
	}
	
	public CoordinateEntity getTo() {
		return to;
	}
	
	public void setTo(CoordinateEntity to) {
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
