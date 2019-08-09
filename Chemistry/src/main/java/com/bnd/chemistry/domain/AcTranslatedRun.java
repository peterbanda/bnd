package com.bnd.chemistry.domain;

import java.util.Collection;

import com.bnd.core.Pair;
import com.bnd.core.domain.ComponentRunTrace;

public class AcTranslatedRun extends AcItemHistoriesHolder<AcTranslationItemHistory> {

	private ComponentRunTrace<Double, Pair<AcCompartment, AcSpecies>> acRunTrace;
	private AcTranslationSeries translationSeries;

	public AcTranslatedRun() {
		super();
	}

	public AcTranslatedRun(Long id) {
		super(id);
	}

	public ComponentRunTrace<Double, Pair<AcCompartment, AcSpecies>> getAcRunTrace() {
		return acRunTrace;
	}

	public void setAcRunTrace(ComponentRunTrace<Double, Pair<AcCompartment, AcSpecies>> acRunTrace) {
		this.acRunTrace = acRunTrace;
	}

	public AcTranslationSeries getTranslationSeries() {
		return translationSeries;
	}

	public void setTranslationSeries(AcTranslationSeries translationSeries) {
		this.translationSeries = translationSeries;
	}

    public Long getTranslationSeriesId() {
		return translationSeries.getId();
	}

	public Collection<AcSpecies> getSpecies() {
		return translationSeries.getSpecies();
	}

	public void initItemHistories() {
		clearItemHistories();
		for (AcTranslationVariable translationVariable : translationSeries.getVariables()) {
			addItemHistory(new AcTranslationItemHistory(translationVariable));
		}
	}
	
	/**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	sb.append(getCreateTime());
    	return sb.toString();
    }
}