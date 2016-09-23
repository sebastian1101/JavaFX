package com.capgemini.chess.service.to;
import com.capgemini.chess.dataaccess.entities.CoordinateEntity;

/**
 * Transport object which represents {@link CoordinateEntity}.
 * 
 * @author Michal Bejm
 *
 */
public class CoordinateTO {

	private int x;
    private int y;

    public CoordinateTO() {
    }
    
    public CoordinateTO(int x, int y) {
    	this.x = x;
    	this.y = y;
    }
    
    public int getX() {
    	return x;
    }

    public int getY() {
    	return y;
    }
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CoordinateTO other = (CoordinateTO) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
}
