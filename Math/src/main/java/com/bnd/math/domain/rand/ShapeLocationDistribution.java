package com.bnd.math.domain.rand;

import com.bnd.core.BndRuntimeException;

/** 
 * @author © Peter Banda
 * @since 2011
 */
public class ShapeLocationDistribution<T> extends RandomDistribution<T> {

	private Class<T> valueType = (Class<T>) Double.class;
	private RandomDistributionType type;
	private Double location;
	private Double shape;

	public ShapeLocationDistribution() {
		// no-op
	}

	public ShapeLocationDistribution(RandomDistributionType type) {
		this.type = type;
	}

	public ShapeLocationDistribution(RandomDistributionType type, Double location, Double shape) {
		this.type = type;
		this.location = location;
		this.shape = shape;
	}

	public ShapeLocationDistribution(RandomDistributionType type, Double location, Double shape, Class<T> valueType) {
		this(type, location, shape);
		this.valueType = valueType;
	}

	public Double getLocation() {
		return location;
	}

	public void setLocation(Double location) {
		this.location = location;
	}

	public Double getShape() {
		return shape;
	}

	public void setShape(Double shape) {
		this.shape = shape;
	}

	protected void setType(RandomDistributionType type) {
		this.type = type;
	}

	@Override
	public RandomDistributionType getType() {
		return type;
	}

	@Override
	public Class<T> getValueType() {
		return valueType;
	}

	public void setValueType(Class<T> valueType) {
		this.valueType = valueType;		
	}

	public void setValueTypeName(String valueTypeName) {
		try {
			if (valueTypeName == null) {
				valueType = null;
				return;
			}
			valueType = (Class<T>) Class.forName(valueTypeName);
		} catch (ClassNotFoundException e) {
			throw new BndRuntimeException("Distribution value type of function '" + valueTypeName + "' not recognized as valid Java class.", e);
		}
	}

	public String getValueTypeName() {
		return valueType != null ? valueType.getName() : null;
	}
}