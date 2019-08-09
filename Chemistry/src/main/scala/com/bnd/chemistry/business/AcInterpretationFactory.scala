package com.bnd.chemistry.business

import java.util.ArrayList
import java.util.Collections
import scala.collection.JavaConversions._
import com.bnd.function.business.ScalaFunctionEvaluatorConversions._
import com.bnd.core.CollectionElementsConversions._
import com.bnd.function.evaluator.FunctionEvaluatorFactory
import java.{lang => jl}
import java.{util => ju}
import com.bnd.chemistry.domain.AcTranslationSeries
import com.bnd.chemistry.domain.AcSpecies
import com.bnd.core.runnable.StateInterpretationItem
import com.bnd.core.runnable.StateInterpretation
import com.bnd.chemistry.domain.AcTranslationVariable
import scala.collection.Map
import com.bnd.function.evaluator.FunctionEvaluator
import com.bnd.core.runnable.RangeStateInterpretationItem
import com.bnd.core.runnable.PointStateInterpretation
import com.bnd.chemistry.domain.AcTranslation
import com.bnd.chemistry.domain.AcTranslation.AcTranslationFromTimeComparator
import com.bnd.core.runnable.RangeStateInterpretation
import com.bnd.core.runnable.PointStateInterpretationItem
import java.io.Serializable

class AcInterpretationFactory(private val funEvaluatorFactory : FunctionEvaluatorFactory) extends Serializable {

	def apply(translationSeries : AcTranslationSeries) : Stream[StateInterpretation[jl.Double, AcSpecies, AcTranslationVariable, _]] = {
	    val componentFunIndexMap : Map[AcSpecies, Int] = translationSeries.getSpecies().map(
	            species => (species, species.getVariableIndex : Int) ).toMap

	    val variableFunIndexMap : Map[AcTranslationVariable, Int] = translationSeries.getVariables.map(
	    		variable => (variable, variable.getVariableIndex : Int) ).toMap

		val translations = new ArrayList[AcTranslation](translationSeries.getTranslations)
		Collections.sort(translations, new AcTranslationFromTimeComparator())

		val initPart = toInterpretations(translations, componentFunIndexMap, variableFunIndexMap).toStream
		val periodicPart = if (translationSeries.isPeriodic) {
		    val periodicAlternators = repeat(
		            toInterpretations(translations.drop(translationSeries.getRepeatFromElementSafe), componentFunIndexMap, variableFunIndexMap),
		            translationSeries.getPeriodicity.toDouble)
		    if (translationSeries.hasRepetitions)
		        periodicAlternators.takeWhile(_.startTime < translationSeries.getRepetitions * translationSeries.getPeriodicity)
		     else
		        periodicAlternators
		} else Stream[StateInterpretation[jl.Double, AcSpecies, AcTranslationVariable, _]]()
		initPart.toStream #::: periodicPart
	}

	private def repeat[T, C, H](
	    stateInterpretations : Iterable[StateInterpretation[T, C, H, _ <: StateInterpretationItem[T, C, H]]],
	    periodicity : Double
	)  = {
		def repeatAux(
			stateInterpretations : Iterable[StateInterpretation[T, C, H, _ <: StateInterpretationItem[T, C, H]]],
			startingTime : Double
		) : Stream[StateInterpretation[T, C, H, _]] = {
		    val shifted = stateInterpretations.map(s => copy(s, startingTime)).toStream 
			shifted #::: repeatAux(stateInterpretations, startingTime + periodicity)
		}
		repeatAux(stateInterpretations, periodicity)
	}

	private def copy[T, C, H](
	    interpretation : StateInterpretation[T, C, H, _ <: StateInterpretationItem[T, C, H]],
	    startingTime : Double
	) : StateInterpretation[T, C, H, _ <: StateInterpretationItem[T, C, H]] = {
		val ss : StateInterpretation[T, C, H, _ <: StateInterpretationItem[T, C, H]] = interpretation match {
			case s : RangeStateInterpretation[T, C, H] => new RangeStateInterpretation[T, C, H](s.startTime + startingTime, s.timeLength, s.items)
			case s : PointStateInterpretation[T, C, H] => new PointStateInterpretation[T, C, H](s.startTime + startingTime, s.items)
		}
		ss
	}

	private def toInterpretations(
	    translations : Iterable[AcTranslation],
		speciesIndexMap : Map[AcSpecies, Int],
		variableIndexMap : Map[AcTranslationVariable, Int]
	) : Iterable[StateInterpretation[jl.Double, AcSpecies, AcTranslationVariable, _ <: StateInterpretationItem[jl.Double, AcSpecies, AcTranslationVariable]]] = {
	    val indexSpeciesMap = speciesIndexMap.map{case (species,index) => (2 * index, species)}
	    translations.map(translation => {
		    val startTime : Double = translation.getFromTime.doubleValue
		    if (translation.getToTime != null) {
		    	val items = translation.getTranslationItems.map(translationItem => {
		    		val fun = translationItem.getTranslationFunction
		    		val referencedSpecies = fun.getReferencedVariableIndeces.flatMap(index => {
		    			val species = indexSpeciesMap.get(index)
		    			if (species.isDefined) Some(species.get) else None
		    		})
		    		val funEval = funEvaluatorFactory.createInstance(fun).asInstanceOf[FunctionEvaluator[Any, jl.Double]]
		    		val scalaFun = functionEvaluatorToScalaDoubleMapFunction[Any, jl.Double, AcSpecies, AcTranslationVariable](funEval, speciesIndexMap, variableIndexMap) 

		    		new RangeStateInterpretationItem[jl.Double, AcSpecies, AcTranslationVariable](translationItem.getVariable, referencedSpecies, interpretationFun(scalaFun)_)
		      	  })
		      	  RangeStateInterpretation[jl.Double, AcSpecies, AcTranslationVariable](startTime, translation.getToTime - translation.getFromTime, items)
		    } else {
		    	val items = translation.getTranslationItems.map(translationItem => {
		    		val fun = translationItem.getTranslationFunction
		    		val referencedSpecies = fun.getReferencedVariableIndeces.flatMap(index => {
		    			val species = indexSpeciesMap.get(index)
		    			if (species.isDefined) Some(species.get) else None
		    		})
		    		val funEval = funEvaluatorFactory.createInstance(fun).asInstanceOf[FunctionEvaluator[Any, jl.Double]]
		    		val scalaFun = functionEvaluatorToScalaDoubleMapFunction[Any, jl.Double, AcSpecies, AcTranslationVariable](funEval, speciesIndexMap, variableIndexMap) 

		    		new PointStateInterpretationItem[jl.Double, AcSpecies, AcTranslationVariable](translationItem.getVariable, referencedSpecies, interpretationFun2(scalaFun)_)
		      	})
		      	PointStateInterpretation[jl.Double, AcSpecies, AcTranslationVariable](startTime, items)
		    }
		})
	}

	private def interpretationFun[T : Manifest, C, H](arrayFun : (Map[C, Any], Map[H, Any]) => T)(input : Map[C, Seq[T]], env : Map[H, T]) =
		arrayFun(input.map(s => (s._1, s._2 : Array[T])).toMap, env)

	private def interpretationFun2[T : Manifest, C, H](fub : (Map[C, Any], Map[H, Any]) => T)(input : Map[C, T], env : Map[H, T]) =
		fub(input.map(s => (s._1, s._2)).toMap, env)
}