package com.bnd.chemistry.domain;

import java.util.HashMap;
import java.util.Map;

import com.bnd.core.domain.ValueBound;

public enum AcRateConstantType {
	Global("Glob"),
	CatalysisGeneral("CatG"),
	CatalysisSubstrate("CatS"),
	NoncooperativeInhibitition("InhN"),
	Permeability("Perm");

	private final String label;

	AcRateConstantType(String label) {
		this.label = label;
	}

	public String toString() {
		return label;
	}

	public static Map<AcRateConstantType, ValueBound<Double>> getDefaultRateConstantBoundMap() {
		Map<AcRateConstantType, ValueBound<Double>> defaultRateConstantBoundMap = new HashMap<AcRateConstantType, ValueBound<Double>>();
		defaultRateConstantBoundMap.put(AcRateConstantType.Global, new ValueBound<Double>(0d, 0.3));
		defaultRateConstantBoundMap.put(AcRateConstantType.CatalysisGeneral, new ValueBound<Double>(0d, 5d));
		defaultRateConstantBoundMap.put(AcRateConstantType.CatalysisSubstrate, new ValueBound<Double>(0d, 1d));
		defaultRateConstantBoundMap.put(AcRateConstantType.NoncooperativeInhibitition, new ValueBound<Double>(0d, 40d));
		defaultRateConstantBoundMap.put(AcRateConstantType.Permeability, new ValueBound<Double>(0d, 1d));
		return defaultRateConstantBoundMap;
	}
}
