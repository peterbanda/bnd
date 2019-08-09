package com.bnd.chemistry.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.bnd.core.domain.TechnicalDomainObject;
import com.bnd.core.util.ObjectUtil;

public abstract class AcItemHistory<H extends AcItemHistoriesHolder> extends TechnicalDomainObject {

	public static class AcItemHistoryLabelComparator implements Comparator<AcItemHistory<?>> {

		@Override
		public int compare(AcItemHistory<?> atih1, AcItemHistory<?> atih2) {
			return ObjectUtil.compareObjects(atih1.getLabel(), atih2.getLabel());
		}
	}

	public static class AcItemHistoryLabelComparator2 implements Comparator<AcItemHistory<?>> {

		@Override
		public int compare(AcItemHistory<?> atih1, AcItemHistory<?> atih2) {
			return ObjectUtil.compareObjects(atih1.getLabel(), atih2.getLabel());
		}
	}

	private List<Double> sequence = new ArrayList<Double>();
	private H holder;

	public void setSequence(List<Double> sequence) {
		this.sequence = sequence;
	}

	public List<Double> getSequence() {
		return sequence;
	}

	public H getHolder() {
		return holder;
	}

	protected void setHolder(H holder) {
		this.holder = holder;
	}
	
	public abstract String getLabel();

	/**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	if (getLabel() != null) {
    		sb.append(getLabel());
        	sb.append(" / ");
    	}
    	sb.append(getSequence().size());
    	return sb.toString();
    }
}