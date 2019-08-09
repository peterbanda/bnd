package com.bnd.chemistry.domain;

public class AcTranslationItemHistory extends AcItemHistory<AcTranslatedRun> {

	private AcTranslationVariable variable;

	public AcTranslationItemHistory() {
		super();
		// no-op
	}

	public AcTranslationItemHistory(AcTranslationVariable variable) {
		this();
		this.variable = variable;
	}

	public AcTranslationVariable getVariable() {
		return variable;
	}

	public void setVariable(AcTranslationVariable variable) {
		this.variable = variable;
	}

	@Override
	public String getLabel() {
		return variable != null ? variable.getLabel() : null;
	}

//	/**
//	 * @see java.lang.Object#equals(java.lang.Object)
//	 */
//	@Override
//	public boolean equals(Object object) {
//		boolean result = super.equals(object);
//		if (!result) {
//			return false;
//		}
//		AcTranslationItemHistory itemHistory = (AcTranslationItemHistory) object;
//		return (ObjectUtil.areObjectsEqual(getVariable(), itemHistory.getVariable()));
//	}
}