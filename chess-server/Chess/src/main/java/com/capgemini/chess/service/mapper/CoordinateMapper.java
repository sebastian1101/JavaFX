package com.capgemini.chess.service.mapper;

import com.capgemini.chess.dataaccess.entities.CoordinateEntity;
import com.capgemini.chess.service.to.CoordinateTO;

/**
 * Mapper for converting {@link CoordinateEntity} and {@link CoordinateTO}.
 * 
 * @author Michal Bejm
 *
 */
public class CoordinateMapper {
	
	public static CoordinateTO map(CoordinateEntity coordinateEntity) {
		if (coordinateEntity != null) {
			return new CoordinateTO(coordinateEntity.getX(), coordinateEntity.getY());
		}
		return null;
	}

	public static CoordinateEntity map(CoordinateTO coordinateTO) {
		if (coordinateTO != null) {
			return new CoordinateEntity(coordinateTO.getX(), coordinateTO.getY());
		}
		return null;
	}
}
