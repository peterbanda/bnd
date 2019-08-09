package com.bnd.chemistry.business

import java.util.ArrayList
import java.util.Collections

import scala.collection.JavaConversions._
import scala.collection.Map
import com.bnd.function.business.ScalaFunctionEvaluatorConversions._
import com.bnd.function.evaluator.FunctionEvaluatorFactory
import java.{lang => jl}
import java.{util => ju}

import com.bnd.chemistry.domain.AcInteraction
import com.bnd.chemistry.domain.AcInteractionSeries
import com.bnd.chemistry.domain.AcEvaluatedActionSeries
import com.bnd.chemistry.domain.AcEvaluatedAction
import com.bnd.chemistry.domain.AcVariable
import com.bnd.core.runnable.StateAlternation
import com.bnd.core.runnable.StateAssignmentAlternation
import com.bnd.core.runnable.StateAlternationItem
import com.bnd.chemistry.domain.AcInteractionVariable
import com.bnd.core.runnable.StateAlternationWrite
import com.bnd.core.runnable.StateAlternationRepeatFirst._
import com.bnd.core.runnable.StateAlternationRepeatFirstInflux
import com.bnd.core.DoubleConvertible.JavaDoubleAsDoubleConvertible
import com.bnd.chemistry.domain.AcSimulationConfig
import java.io.Serializable

import com.bnd.core.dynamics.StateAlternationType

class AcScriptFactory(private val funEvaluatorFactory : FunctionEvaluatorFactory) extends Serializable {

    def apply(evaluatedActionSeries : AcEvaluatedActionSeries) : Stream[StateAlternation[jl.Double, AcVariable[_], Nothing]]= {
		val evalActions = new ArrayList[AcEvaluatedAction](evaluatedActionSeries.getEvaluatedActions)
		Collections.sort(evalActions)
		toAlternations(evalActions).toStream
    }

	def apply(simConfig : AcSimulationConfig)(actionSeries : AcInteractionSeries) : Stream[StateAlternation[jl.Double, AcVariable[_], AcInteractionVariable]]= {
	    val componentFunIndexMap : Map[AcVariable[_], Int] = actionSeries.getSpecies.map(
	            species => (species, species.getVariableIndex : Int) ).toMap

	    val variableFunIndexMap : Map[AcInteractionVariable, Int] = actionSeries.getVariables.map(
	            variable => (variable, variable.getVariableIndex : Int) ).toMap

		val interactions = new ArrayList[AcInteraction](actionSeries.getActions())
		Collections.sort(interactions)
		val initPart = toAlternations(simConfig)(interactions, componentFunIndexMap, variableFunIndexMap).toStream
		val periodicPart = if (actionSeries.isPeriodic) {
		    val periodicAlternators = repeat(
		            toAlternationFactoryFuns(simConfig)(interactions.drop(actionSeries.getRepeatFromElementSafe), componentFunIndexMap, variableFunIndexMap),
		            actionSeries.getPeriodicity().toDouble)
		    if (actionSeries.hasRepetitions)
		        periodicAlternators.takeWhile(_.applyStartTime < actionSeries.getRepetitions() * actionSeries.getPeriodicity())
		     else 
		        periodicAlternators
		} else Stream[StateAlternation[jl.Double, AcVariable[_], AcInteractionVariable]]()
		initPart.toStream #::: periodicPart
	}

	private def repeat[T, C, H](
	    factoryFuns : Iterable[Double => StateAlternation[T, C, H]],
	    periodicity : Double
	)  = {
		def repeatAux(timeShift : Double) : Stream[StateAlternation[T, C, H]] =
		    factoryFuns.map(_(timeShift)).toStream #::: repeatAux(timeShift + periodicity)
		repeatAux(periodicity)
	}

	private def toAlternations(
		simConfig : AcSimulationConfig)(
		interactions : Iterable[AcInteraction],
		componentFunIndexMap : Map[AcVariable[_], Int],
		variableAssignmentFunIndexMap : Map[AcInteractionVariable, Int]
	) = interactions.map(interaction => toAlternation(simConfig)(interaction, componentFunIndexMap, variableAssignmentFunIndexMap)(0d))

	private def toAlternationFactoryFuns(
		simConfig : AcSimulationConfig)(
		interactions : Iterable[AcInteraction],
		componentFunIndexMap : Map[AcVariable[_], Int],
		variableAssignmentFunIndexMap : Map[AcInteractionVariable, Int]
	) = interactions.map(interaction => toAlternation(simConfig)(interaction, componentFunIndexMap, variableAssignmentFunIndexMap)(_))

	private def toAlternation(
	    simConfig : AcSimulationConfig)(
		interaction : AcInteraction,
		componentFunIndexMap : Map[AcVariable[_], Int],
		variableAssignmentFunIndexMap : Map[AcInteractionVariable, Int])(
		timeShift : Double
	) = {
	    val startTime = interaction.getStartTime.doubleValue + timeShift
		val items = interaction.getSpeciesActions().map(speciesAction => {
			val funEval = funEvaluatorFactory.createInstance(speciesAction.getSettingFunction())
		    val stateAlternationFun = functionEvaluatorToScalaDoubleMapFunction(funEval, componentFunIndexMap, variableAssignmentFunIndexMap)
		    val stateAlternationIgnoreTimeStep = (a : Map[AcVariable[_], jl.Double], b : Map[AcInteractionVariable, jl.Double], timeStep : Option[Double]) => stateAlternationFun(a,b)
		    new StateAlternationItem[jl.Double, AcVariable[_], AcInteractionVariable](speciesAction.getSpecies, stateAlternationIgnoreTimeStep)
		})
		val cacheWrites = interaction.getVariableAssignments.map(variableAssignment => {
		    val funEval = funEvaluatorFactory.createInstance(variableAssignment.getSettingFunction())
		    val variableAssignmentFun = functionEvaluatorToScalaDoubleMapFunction(funEval, componentFunIndexMap, variableAssignmentFunIndexMap)
		    new StateAlternationWrite[jl.Double, AcVariable[_], AcInteractionVariable](variableAssignment.getVariable, variableAssignmentFun)
		})
		val newAlternationFun = interaction.getAlternationType match {
			case StateAlternationType.Replacement => newReplacement[jl.Double, AcVariable[_], AcInteractionVariable]
			case StateAlternationType.Addition => newDoubleAddition[jl.Double, AcVariable[_], AcInteractionVariable]
			case StateAlternationType.Influx => StateAlternationRepeatFirstInflux[AcVariable[_], AcInteractionVariable](
			        simConfig.getOdeSolverType,
			        simConfig.getTimeStep,
			        if (simConfig.getTolerance != null) Some(simConfig.getTolerance) else None)_
		}
	    newAlternationFun(startTime, interaction.getTimeLength : Double, items, cacheWrites)
	}

	private def toAlternations(
		evalActions : Iterable[AcEvaluatedAction]
	) = evalActions.map(evalAction => {
			val interaction = evalAction.getAction
		    val startTime : Double = interaction.getStartTime.doubleValue()
		    val items = evalAction.getEvaluatedSpeciesActions().map(evalSpeciesAction => 
		        (evalSpeciesAction.getSpecies, evalSpeciesAction.getValue))
		    new StateAssignmentAlternation[jl.Double, AcVariable[_]](startTime, interaction.getTimeLength : Double, items)
		})
}