package com.bnd.chemistry.business

import java.io.Serializable
import java.{lang => jl, util => ju}

import com.bnd.chemistry.domain._
import com.bnd.core.runnable.{SeqIndexAccessible, StateEvent, StateInterpretationResultCollector, StateInterpreter, TimeRunnable}
import com.bnd.function.evaluator.FunctionEvaluatorFactory
import com.bnd.core.runnable.{StateInterpretationEvent, TimeRunnable}

import scala.collection.JavaConversions._
import scala.collection.mutable.{ListBuffer, Publisher}

class ChemistryInterpretationFactory(private val functionEvaluatorFactory : FunctionEvaluatorFactory) extends Serializable {

    // handy type expansions

    type Chemistry = TimeRunnable
    type StatePublisher[S[X]] = Publisher[StateEvent[jl.Double, S]]

    private val acInterpretationFactory = new AcInterpretationFactory(functionEvaluatorFactory)

    def apply[S[X] : SeqIndexAccessible](
		chemistry : Chemistry,
		statePublisher : StatePublisher[S],
		translationSeries : AcTranslationSeries
	) : TimeRunnable with AcTranslatedRunHolder = {
        val interpretations = acInterpretationFactory.apply(translationSeries)
        val interpreter = StateInterpreter(chemistry, statePublisher, interpretations, Some((translationSeries.getVariables() : Iterable[AcTranslationVariable], translationSeries.getDefaultVariableValue)))
        return new ChemistryInterpretation(interpreter, translationSeries)
	}
}

private class ChemistryInterpretation(
    chemistryInterpreter : TimeRunnable with Publisher[StateInterpretationEvent[jl.Double, AcSpecies, AcTranslationVariable]],
    translationSeries : AcTranslationSeries) extends TimeRunnable with AcTranslatedRunHolder {

    val variableMap = translationSeries.getVariables.map(v => (v.getLabel, v)).toMap
    val interpretedValuesMap = translationSeries.getVariables.map(v => (v, ListBuffer[jl.Double]())).toMap
    val interpretationCollector = new StateInterpretationResultCollector[jl.Double, AcSpecies, AcTranslationVariable]
    chemistryInterpreter.subscribe(interpretationCollector)

    override def nextTimeStepSize = chemistryInterpreter.nextTimeStepSize

    override def currentTime = chemistryInterpreter.currentTime

    override def runFor(timeDiff : BigDecimal) = chemistryInterpreter.runFor(timeDiff)

    override def runUntil(finalTime : BigDecimal) = chemistryInterpreter.runUntil(finalTime)

    override def getAcTranslatedRun = {
        for (result <- interpretationCollector.collected; item <- result.items) {
            val interpretedValues = interpretedValuesMap.get(item.interpretationItem.variable).get
            interpretedValues += item.interpretedValue
        }
 
        val histories = new ju.ArrayList[AcTranslationItemHistory]
        for ((variable, interpretedValues) <- interpretedValuesMap) { 
        	val history = new AcTranslationItemHistory(variable)
        	history.setSequence(interpretedValues)
        	histories.add(history)
        }

        val translatedRun = new AcTranslatedRun
        translatedRun.setTranslationSeries(translationSeries)
        translatedRun.setItemHistories(histories)
        translatedRun
    }
}