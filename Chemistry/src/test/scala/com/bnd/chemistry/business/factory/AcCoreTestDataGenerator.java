package com.bnd.chemistry.business.factory;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.bnd.chemistry.BndChemistryException;
import com.bnd.chemistry.domain.AcConcentrationLevel;
import com.bnd.chemistry.domain.AcSpecies;
import com.bnd.chemistry.domain.AcSpeciesSet;
import com.bnd.core.util.RandomUtil;
import com.bnd.function.domain.AggregateFunction;
import com.bnd.function.domain.BooleanFunction.BooleanFunctionType;
import com.bnd.math.domain.rand.RandomDistribution;
import com.bnd.math.domain.rand.UniformDistribution;

public class AcCoreTestDataGenerator {

	protected AcSpeciesSetFactory speciesSetFactory = AcSpeciesSetFactory.getInstance();

	public AcSpeciesSet createSpeciesSetWithGroups(boolean feedbackSpeciesIncluded) {
		return speciesSetFactory.createFixedOrder(
				randomInt(5, 100),
				randomInt(1, 3),
				randomInt(1, 3),
				feedbackSpeciesIncluded ? randomInt(1, 4) : 0);
	}

	public AcSpeciesSet createSpeciesSet(int speciesNum, Double constantSpeciesRatio) {
		return speciesSetFactory.createRandomOrder(speciesNum); 
	}

	public Collection<AcSpecies> createSpecies(int speciesNum) {
		return createSpeciesSet(speciesNum, null).getVariables(); 
	}

	public AcSpeciesSet createSpeciesSet(int speciesNumber, int startingSequenceNum, int startingLabel) {
		return speciesSetFactory.createRandomOrder(speciesNumber, startingSequenceNum, startingLabel);
	}

	public AcConcentrationLevel createConcentrationLevel(int domainSpeciesNum) {
		AcConcentrationLevel concentrationLevel = null;
		if (domainSpeciesNum == 1) {
			concentrationLevel = new AcConcentrationLevel(randomDouble(0.05d, 0.2d), randomDouble(1.5d, 2.5d));	
		} else if (domainSpeciesNum == 2) {
			concentrationLevel = new AcConcentrationLevel(randomDouble(0.9d, 1.1d));
		} else {
			throw new BndChemistryException("The number of domain species for concentration level setting must be 1 or 2, but got '" + domainSpeciesNum + "'.");
		}
		return concentrationLevel;
	}

	protected int randomInt(int from, int to) {
		return RandomUtil.nextInt(from, to);
	}

	protected double randomDouble(double from, double to) {
		return RandomUtil.nextDouble(from, to);
	}

	protected boolean randomBoolean() {
		return RandomUtil.nextBoolean();
	}

	protected <T> T randomElement(T[] objects) {
		return RandomUtil.nextElement(objects);
	}

	protected AggregateFunction getRandomAggregateFunctionBut(AggregateFunction[] exceptFunctions) {
		Set<AggregateFunction> functions = new HashSet<AggregateFunction>();
		functions.addAll(Arrays.asList(AggregateFunction.values()));
		for (AggregateFunction exceptFunction : exceptFunctions) {
			functions.remove(exceptFunction);
		}
		return randomElement(functions.toArray(new AggregateFunction[0]));
	}

	protected AggregateFunction getRandomAggregateFunction() {
		return randomElement(AggregateFunction.values());
	}

	protected BooleanFunctionType getRandomBooleanFunctionType() {
		return randomElement(BooleanFunctionType.values());
	}

	protected RandomDistribution<Double> createRandomDistribution() {
		return RandomUtil.nextBoolean() ?
				new UniformDistribution<Double>(randomDouble(0.2, 0.6), randomDouble(2d, 4.5)) :
				RandomDistribution.createNormalDistribution(randomDouble(2d, 2.5), randomDouble(0.3, 0.7));
	}
}