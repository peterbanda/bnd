package com.bnd.chemistry.business;

import com.bnd.core.util.ObjectUtil;

public enum MassActionType {
	NoIntermediates, CatalystSubstrateComplex, CatalystSubstrateAndCatalystProductComplex;

	public static MassActionType fromString(String string) {
		if (string == null) {
			return null;
		}
		for (MassActionType option : values()) {
			if (ObjectUtil.areObjectsEqual(option.toString(), string)) {
				return option;
			}
		}
		return null;
	}
}