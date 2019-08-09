package com.bnd.chemistry.business.factory;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Test;

import com.bnd.chemistry.business.ArtificialChemistryUtil;
import com.bnd.chemistry.business.factory.AcTranslationSeriesFactoryTestDataGenerator.AcSimplePeriodicFullTSData;
import com.bnd.chemistry.business.factory.AcTranslationSeriesFactoryTestDataGenerator.AcSimplePeriodicInputOutputFeedbackTSData;
import com.bnd.chemistry.domain.*;
import com.bnd.core.util.ObjectUtil;


public class AcTranslationSeriesFactoryTest extends TestCase {

	private AcTranslationSeriesFactoryTestDataGenerator testDataGenerator = new AcTranslationSeriesFactoryTestDataGenerator();
	private AcTranslationSeriesFactory actionSeriesFactory = AcTranslationSeriesFactory.getInstance();
	private ArtificialChemistryUtil acUtil = ArtificialChemistryUtil.getInstance();

	@Test
	public void testCreateSimplePeriodicInputOutputFeedbackTS() {
		for (AcSimplePeriodicInputOutputFeedbackTSData testData : testDataGenerator.getSimplePeriodicInputOutputFeedbackTSData()) {
			AcTranslationSeries translationSeries = actionSeriesFactory.createSimplePeriodicInputOutputFeedbackTS(
					testData.inputSpeciesStateTransFunction,
					testData.outputSpeciesStateTransFunction,
					testData.feedbackSpeciesStateTransFunction,
					testData.inputConcentrationLevel,
					testData.outputConcentrationLevel,
					testData.feedbackConcentrationLevel,
					testData.quasiPeriodicActionSeries);
			checkTranslationSeries(translationSeries, false);
		}
	}

	@Test
	public void testCreateSimplePeriodicFullTS() {
		for (AcSimplePeriodicFullTSData testData : testDataGenerator.getSimplePeriodicFullTSData()) {
			AcTranslationSeries translationSeries = actionSeriesFactory.createSimplePeriodicFullTS(
					testData.inputSpeciesStateTransFunction,
					testData.outputSpeciesStateTransFunction,
					testData.feedbackSpeciesStateTransFunction,
					testData.inputConcentrationLevel,
					testData.outputConcentrationLevel,
					testData.feedbackConcentrationLevel,
					testData.internalSpeciesStateTransFunction,
					testData.quasiPeriodicActionSeries);
			checkTranslationSeries(translationSeries, true);
		}
	}
	
	private void checkTranslationSeries(AcTranslationSeries translationSeries, boolean internalSpeciesIncluded) {
		AcSpeciesSet speciesSet = translationSeries.getSpeciesSet();
		Collection<AcSpecies> internalSpecies = speciesSet.getInternalSpecies();
		Collection<AcSpecies> nonInternalSpecies = speciesSet.getNonInternalSpecies();
		int expectedVariablesNum = 2;
		if (speciesSet.hasSpecies(AcSpeciesType.Feedback)) {
			expectedVariablesNum += 1;
		}
		if (internalSpeciesIncluded) {
			expectedVariablesNum += speciesSet.getInternalSpecies().size();  
		}

		assertNotNull(translationSeries);
		assertNotNull(translationSeries.getSpeciesSet());
		assertNotNull(translationSeries.getVariables());
		assertEquals(1, translationSeries.getTranslations().size());
		assertEquals(0, translationSeries.getRepeatFromElementSafe());
		assertEquals(expectedVariablesNum, translationSeries.getVariables().size());

		AcTranslation rangeTranslation = ObjectUtil.getFirst(translationSeries.getTranslations());
		assertNotNull(rangeTranslation.getFromTime());
		assertNotNull(rangeTranslation.getToTime());
		assertTrue(rangeTranslation.getFromTime() > 0);
		assertTrue(ObjectUtil.compareObjects(rangeTranslation.getToTime(), rangeTranslation.getFromTime()) == 1);
		assertEquals(expectedVariablesNum, rangeTranslation.getTranslationItems().size());

		Map<Integer, AcSpecies> indexSpeciesMap = acUtil.getIndexVariableMap(speciesSet);
		for (AcTranslationItem translationItem : rangeTranslation.getTranslationItems()) {
			assertNotNull(translationItem.getVariable());
			assertNotNull(translationItem.getTranslationFunction());
			Set<Integer> refSpeciesIndeces = translationItem.getTranslationFunction().getReferencedVariableIndeces();
			Iterator<Integer> refSpeciesIndexIterator = refSpeciesIndeces.iterator();
			if (refSpeciesIndeces.size() == 1) {
				AcSpecies refSpecies = indexSpeciesMap.remove(refSpeciesIndexIterator.next());
				assertNotNull(refSpecies);
				assertTrue(internalSpeciesIncluded || !internalSpecies.contains(refSpecies));
			} else if (refSpeciesIndeces.size() == 2) {
				AcSpecies refSpecies1 = indexSpeciesMap.remove(refSpeciesIndexIterator.next());
				AcSpecies refSpecies2 = indexSpeciesMap.remove(refSpeciesIndexIterator.next());
				assertNotNull(refSpecies1);
				assertNotNull(refSpecies2);
				assertTrue(nonInternalSpecies.contains(refSpecies1));
				assertTrue(nonInternalSpecies.contains(refSpecies2));
				assertTrue(internalSpeciesIncluded || !internalSpecies.contains(refSpecies1));
				assertTrue(internalSpeciesIncluded || !internalSpecies.contains(refSpecies2));
			} else {
				assertTrue("One or two species expected, but got '" + refSpeciesIndeces.size() + "'.", false);
			}
		}
	}
}