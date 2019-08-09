package com.bnd.chemistry.domain;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import com.bnd.core.domain.TechnicalDomainObject;
import com.bnd.core.util.ObjectUtil;

public class AcTranslation extends TechnicalDomainObject {

	public static class AcTranslationFromTimeComparator implements Comparator<AcTranslation> {

		@Override
		public int compare(AcTranslation translation1, AcTranslation translation2) {
			return ObjectUtil.compareObjects(translation1.getFromTime(), translation2.getFromTime());
		}		
	}

	private Integer fromTime;
	private Integer toTime;
	private AcTranslationSeries translationSeries;
	private Set<AcTranslationItem> translationItems = new HashSet<AcTranslationItem>();

	public Integer getFromTime() {
		return fromTime;
	}

	public void setFromTime(Integer fromTime) {
		this.fromTime = fromTime;
	}

	public Integer getToTime() {
		return toTime;
	}

	public void setToTime(Integer toTime) {
		this.toTime = toTime;
	}

	public Set<AcTranslationItem> getTranslationItems() {
		return translationItems;
	}

	public void setTranslationItems(Set<AcTranslationItem> translationItems) {
		this.translationItems = translationItems;
	}

	public void addTranslationItem(AcTranslationItem translationItem) {
		translationItems.add(translationItem);
		translationItem.setTranslation(this);
	}

	public void removeTranslationItem(AcTranslationItem translationItem) {
		translationItems.remove(translationItem);
		translationItem.setTranslation(null);
	}

	public Set<String> getItemLabels() {
		Set<String> itemLabels = new HashSet<String>();
		for (AcTranslationItem translationItem : translationItems) {
			itemLabels.add(translationItem.getLabel());
		}
		return itemLabels;
	}

	public AcTranslationSeries getTranslationSeries() {
		return translationSeries;
	}

	public void setTranslationSeries(AcTranslationSeries actionSeries) {
		this.translationSeries = actionSeries;
	}

	public Collection<AcSpecies> getSpecies() {
		return translationSeries.getSpecies();
	}

	public AcSpeciesSet getSpeciesSet() {
		return translationSeries.getSpeciesSet();
	}

	public Set<Integer> getReferencedSpeciesIndeces() {
		Set<Integer> itemLabels = new HashSet<Integer>();
		for (AcTranslationItem translationItem : getTranslationItems()) {
			itemLabels.addAll(translationItem.getTranslationFunction().getReferencedVariableIndeces());
		}
		return itemLabels;
	}

	/**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	if (translationSeries != null) {
        	sb.append(translationSeries.getName());
        	sb.append("/");
    	}
    	sb.append(fromTime);
    	if (toTime != null) {
    		sb.append("-" + toTime);
    	}
    	return sb.toString();
    }
}