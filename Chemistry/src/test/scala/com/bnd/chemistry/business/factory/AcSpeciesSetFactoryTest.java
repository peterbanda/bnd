package com.bnd.chemistry.business.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Test;

import com.bnd.chemistry.business.ArtificialChemistryUtil;
import com.bnd.chemistry.domain.*;
import com.bnd.core.util.RandomUtil;
import com.bnd.math.domain.rand.RandomDistribution;


public class AcSpeciesSetFactoryTest extends TestCase {

	private Set<String> speciesLabels = new HashSet<String>();
	private int speciesNum = 96;
	private int internalSpeciesNum = 54;
	private int inputSpeciesNumber = 2;
	private int outputSpeciesNum = 2;
	private int weightSpeciesNum = 3;

	private AcSpeciesSetFactory speciesSetFactory = AcSpeciesSetFactory.getInstance();
	private final ArtificialChemistryUtil acUtil = ArtificialChemistryUtil.getInstance();

	@Override
	protected void setUp() {
		for (int i = 0; i < 100; i++) {
			speciesLabels.add(RandomUtil.nextString(4));
		}
	}

	@Test
	public void testCreateDNAStrands() {
		AcDNAStrandSpec spec = new AcDNAStrandSpec();
		spec.setUpperToLowerStrandRatio(2D);
		spec.setComplementaryStrandsRatio(0.5);
		spec.setUpperStrandPartialBindingDistribution(RandomDistribution.createNormalDistribution(Integer.class, 1D, 0.5D));
		spec.setMirrorComplementarity(true);
		spec.setConstantSpeciesRatio(0.1);
		spec.setSingleStrandsNum(4);

		AcDNAStrandSpeciesSet speciesSet = speciesSetFactory.createDNAStrandSpeciesSet(spec);

		final Collection<AcSpecies> upperStrands = speciesSet.getUpperStrands();
		final Collection<AcSpecies> lowerStrands = speciesSet.getLowerStrands();
		final int upperStrandsNum = speciesSet.getUpperStrands().size();
		final int lowerStrandsNum = speciesSet.getLowerStrands().size();

		checkBasics(speciesSet);
		assertEquals(upperStrandsNum + lowerStrandsNum, spec.getSingleStrandsNum().intValue());
		assertEquals((double) upperStrandsNum / lowerStrandsNum, spec.getUpperToLowerStrandRatio());

//		int constantSpeciesCounter = 0;
//		for (AcSpecies species : speciesSet.getVariables()) {
//			if (species.isConstantFlag()) {
//				constantSpeciesCounter++;
//			}
//		}
//		assertEquals(spec.getConstantSpeciesRatio() * speciesSet.getVariablesNumber(), constantSpeciesCounter);

		Collection<AcCompositeSpecies> doubleStrands = new ArrayList<AcCompositeSpecies>();
		doubleStrands.addAll(speciesSet.getFullDoubleStrands());
		doubleStrands.addAll(speciesSet.getPartialDoubleStrands());
		for (AcCompositeSpecies doubleStrand : doubleStrands) {
			assertEquals(doubleStrand.getComponents().size(), 2);
			assertTrue(upperStrands.contains(doubleStrand.getComponents().get(0)));
			assertTrue(lowerStrands.contains(doubleStrand.getComponents().get(1)));
		}

		System.out.println(acUtil.getDNAStrandSpeciesSetAsString(speciesSet, "\n"));
	}

	@Test
	public void testCreateFixedOrderWithLabels() {
		AcSpeciesSet speciesSet = speciesSetFactory.createFixedOrder(speciesLabels);
		checkBasics(speciesSet);
		assertEquals(speciesLabels.size(), speciesSet.getVariablesNumber());
		for (AcSpecies species : speciesSet.getVariables()) {
			assertTrue(speciesLabels.contains(species.getLabel()));
		}
	}

	@Test
	public void testCreateRandomOrderWithLabels() {
		AcSpeciesSet speciesSet = speciesSetFactory.createRandomOrder(speciesLabels);
		checkBasics(speciesSet);
		assertEquals(speciesLabels.size(), speciesSet.getVariablesNumber());
		for (AcSpecies species : speciesSet.getVariables()) {
			assertTrue(speciesLabels.contains(species.getLabel()));
		}
	}

	@Test
	public void testCreateFixedOrder() {
		AcSpeciesSet speciesSet = speciesSetFactory.createFixedOrder(speciesNum);
		checkBasics(speciesSet);
		assertEquals(speciesNum, speciesSet.getVariablesNumber());
	}

	@Test
	public void testCreateRandomOrder() {
		AcSpeciesSet speciesSet = speciesSetFactory.createRandomOrder(speciesNum);
		checkBasics(speciesSet);
		assertEquals(speciesNum, speciesSet.getVariablesNumber());
	}

	@Test
	public void testCreateFixedOrderComplexWithFeedback() {
		AcSpeciesSet speciesSet = speciesSetFactory.createFixedOrder(internalSpeciesNum, inputSpeciesNumber, outputSpeciesNum, weightSpeciesNum);
		checkBasics(speciesSet);
		assertEquals(internalSpeciesNum + inputSpeciesNumber + weightSpeciesNum + 2 * outputSpeciesNum, speciesSet.getVariablesNumber());
		assertEquals(internalSpeciesNum, speciesSet.getInternalSpecies().size());
		assertEquals(inputSpeciesNumber, speciesSet.getSpeciesNumber(AcSpeciesType.Input));
		assertEquals(outputSpeciesNum, speciesSet.getSpeciesNumber(AcSpeciesType.Output));
		assertEquals(outputSpeciesNum, speciesSet.getSpeciesNumber(AcSpeciesType.Feedback));
		assertEquals(weightSpeciesNum, speciesSet.getSpeciesNumber(AcSpeciesType.Functional));
	}

	@Test
	public void testCreateFixedOrderComplexWithoutFeedback() {
		AcSpeciesSet speciesSet = speciesSetFactory.createFixedOrder(internalSpeciesNum, inputSpeciesNumber, outputSpeciesNum, 0);
		checkBasics(speciesSet);
		assertEquals(internalSpeciesNum + inputSpeciesNumber + outputSpeciesNum, speciesSet.getVariablesNumber());
		assertEquals(internalSpeciesNum, speciesSet.getInternalSpecies().size());
		assertEquals(inputSpeciesNumber, speciesSet.getSpeciesNumber(AcSpeciesType.Input));
		assertEquals(outputSpeciesNum, speciesSet.getSpeciesNumber(AcSpeciesType.Output));
		assertEquals(0, speciesSet.getSpeciesNumber(AcSpeciesType.Feedback));
		assertEquals(0, speciesSet.getSpeciesNumber(AcSpeciesType.Functional));
	}

	private void checkBasics(AcSpeciesSet speciesSet) {
		assertNotNull(speciesSet);
		assertNotNull(speciesSet.getParameterSet());
		assertNotNull(speciesSet.getParameterSet().getVariables());
		assertNotNull(speciesSet.getVarSequenceNum());
		assertTrue(speciesSet.getVarSequenceNum() > 1);
	}
}