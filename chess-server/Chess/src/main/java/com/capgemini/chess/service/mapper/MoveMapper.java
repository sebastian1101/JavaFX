package com.capgemini.chess.service.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.capgemini.chess.dataaccess.entities.MoveEntity;
import com.capgemini.chess.service.to.MoveTO;

/**
 * Mapper for converting {@link MoveEntity} and {@link MoveTO}.
 * 
 * @author Michal Bejm
 *
 */
public class MoveMapper {

	public static MoveTO map(MoveEntity moveEntity) {
		if (moveEntity != null) {
			MoveTO moveTO = new MoveTO();
			moveTO.setCapturedPiece(moveEntity.getCapturedPiece());
			moveTO.setFrom(CoordinateMapper.map(moveEntity.getFrom()));
			moveTO.setId(moveEntity.getId());
			moveTO.setMovedPiece(moveEntity.getMovedPiece());
			moveTO.setSequenceNumber(moveEntity.getSequenceNumber());
			moveTO.setTo(CoordinateMapper.map(moveEntity.getTo()));
			moveTO.setType(moveEntity.getType());
			return moveTO;
		}
		return null;
	}

	public static MoveEntity map(MoveTO moveTO) {
		if (moveTO != null) {
			MoveEntity moveEntity = new MoveEntity();
			moveEntity.setCapturedPiece(moveTO.getCapturedPiece());
			moveEntity.setFrom(CoordinateMapper.map(moveTO.getFrom()));
			moveEntity.setId(moveTO.getId());
			moveEntity.setMovedPiece(moveTO.getMovedPiece());
			moveEntity.setSequenceNumber(moveTO.getSequenceNumber());
			moveEntity.setTo(CoordinateMapper.map(moveTO.getTo()));
			moveEntity.setType(moveTO.getType());
			return moveEntity;
		}
		return null;
	}
	
	public static List<MoveTO> map2TOs(List<MoveEntity> moveEntities) {
		return moveEntities.stream().map(MoveMapper::map).collect(Collectors.toList());
	}

	public static List<MoveEntity> map2Entities(List<MoveTO> moveTOs) {
		return moveTOs.stream().map(MoveMapper::map).collect(Collectors.toList());
	}
}
