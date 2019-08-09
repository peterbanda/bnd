package com.bnd.chemistry.business;

import java.util.Comparator;

import com.bnd.chemistry.BndChemistryException;
import com.bnd.chemistry.domain.AcCompartment;
import com.bnd.chemistry.domain.AcCompartmentAssociation;
import com.bnd.core.util.ObjectUtil;

public class AcCompartmentDepthComparator implements Comparator<AcCompartment> {

	@Override
	public int compare(AcCompartment compartment1, AcCompartment compartment2) {
		if (compartment1 == compartment2) {
			return 0;
		}
		int result = ObjectUtil.compareObjects(depth(compartment1), depth(compartment2));
		if (result == 0) {
			// siblings
			final AcCompartmentAssociation parentAssoc1 = ObjectUtil.getFirst(compartment1.getParentCompartmentAssociations());
			final AcCompartmentAssociation parentAssoc2 = ObjectUtil.getFirst(compartment2.getParentCompartmentAssociations());
			if (parentAssoc1.getParentCompartment() != parentAssoc2.getParentCompartment())
				throw new BndChemistryException("Corrupted compartment hierarchy. The same parent expected for siblings.");
			result = ObjectUtil.compareObjects(parentAssoc1.getOrder(), parentAssoc2.getOrder());
		}
		return result;
	}

	private int depth(AcCompartment compartment) {
		int depth = 0;
		// assume only one parent
		while (!compartment.getParentCompartments().isEmpty()) {
			compartment = ObjectUtil.getFirst(compartment.getParentCompartments());
			depth++;
		}
		return depth;
	}
}