package com.bnd.chemistry.domain;

public class AcSpecies extends AcVariable<AcSpeciesSet> {

	private String structure;
	private AcSpeciesGroup group;

	public AcSpecies() {
		super();
	}

	public AcSpecies(Long id) {
		super(id);
	}

	public AcSpecies(String label) {
		super(label);
	}

	public String getStructure() {
		return structure;
	}

	public void setStructure(String structure) {
		this.structure = structure;
	}

	public AcSpeciesGroup getGroup() {
		return group;
	}

	protected void setGroup(AcSpeciesGroup group) {
		this.group = group;
	}

	/**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	sb.append(super.toString());
    	if (structure != null && !structure.isEmpty()) {
    		sb.append(" / ");
    		sb.append(structure);
    	}
    	return sb.toString();
    }
}