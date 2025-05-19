package com.bnd.chemistry.business

import java.util.ArrayList
import java.util.Collections
import scala.jdk.CollectionConverters._
import com.bnd.function.business.ScalaFunctionEvaluatorConversions._
import com.bnd.function.evaluator.FunctionEvaluatorFactory
import java.{lang => jl}
import java.{util => ju}
import com.bnd.chemistry.domain.AcTranslationSeries
import com.bnd.chemistry.domain.AcSpecies
import com.bnd.chemistry.domain.AcTranslationVariable
import scala.collection.Map
import com.bnd.function.evaluator.FunctionEvaluator
import com.bnd.chemistry.domain.AcTranslation
import com.bnd.chemistry.domain.AcTranslation.AcTranslationFromTimeComparator
import java.io.Serializable
import scala.collection.mutable.ListBuffer
import com.bnd.core.runnable.{StateInterpretation, StateInterpretationItem, RangeStateInterpretation, PointStateInterpretation, RangeStateInterpretationItem, PointStateInterpretationItem}

class AcInterpretationFactory(private val funEvaluatorFactory : FunctionEvaluatorFactory) extends Serializable {

	def apply(translationSeries : AcTranslationSeries) : Stream[StateInterpretation[jl.Double, AcSpecies, AcTranslationVariable, _]] = {
	    val componentFunIndexMap : Map[AcSpecies, Int] = translationSeries.getSpecies().asScala.map(
	            species => (species, species.getVariableIndex : Int) ).toMap

	    val variableFunIndexMap : Map[AcTranslationVariable, Int] = translationSeries.getVariables.asScala.map(
	    		variable => (variable, variable.getVariableIndex : Int) ).toMap

		val translations = new ArrayList[AcTranslation](translationSeries.getTranslations)
		Collections.sort(translations, new AcTranslationFromTimeComparator())

		val initPart = toInterpretations(translations, componentFunIndexMap, variableFunIndexMap).toStream
		val periodicPart = if (translationSeries.isPeriodic) {
		    // Create a filtered list with elements after getRepeatFromElementSafe
		    val translationsScala = translations.asScala.toList
		    val dropCount = translationSeries.getRepeatFromElementSafe
		    val filteredList = new ArrayList[AcTranslation]()
		    for (i <- dropCount until translationsScala.size) {
		        filteredList.add(translationsScala(i))
		    }
		    
		    val periodicAlternators = repeat(
		            toInterpretations(filteredList, componentFunIndexMap, variableFunIndexMap),
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
	    translations : ju.Collection[AcTranslation],
		speciesIndexMap : Map[AcSpecies, Int],
		variableIndexMap : Map[AcTranslationVariable, Int]
	) : Iterable[StateInterpretation[jl.Double, AcSpecies, AcTranslationVariable, _ <: StateInterpretationItem[jl.Double, AcSpecies, AcTranslationVariable]]] = {
	    val indexSpeciesMap = speciesIndexMap.map{case (species,index) => (2 * index, species)}
	    translations.asScala.map(translation => {
		    val startTime : Double = translation.getFromTime.doubleValue
		    if (translation.getToTime != null) {
		    	val items = translation.getTranslationItems.asScala.map(translationItem => {
		    		val fun = translationItem.getTranslationFunction
		    		val referencedSpecies = fun.getReferencedVariableIndeces.asScala.flatMap(index => {
		    			val species = indexSpeciesMap.get(index)
		    			if (species.isDefined) Some(species.get) else None
		    		})

		    		val funEval = funEvaluatorFactory.createInstance(fun).asInstanceOf[FunctionEvaluator[Any, jl.Double]]
		    		val scalaFun = functionEvaluatorToScalaDoubleMapFunction[Any, jl.Double, AcSpecies, AcTranslationVariable](funEval, speciesIndexMap, variableIndexMap) 

		    		new RangeStateInterpretationItem[jl.Double, AcSpecies, AcTranslationVariable](
						translationItem.getVariable,
						referencedSpecies,
						(input: Map[AcSpecies, ListBuffer[jl.Double]], env: Map[AcTranslationVariable, jl.Double]) => 
							interpretationFun(scalaFun)(input.map { case (key, value) => (key, value.toSeq) }, env)
					)
				})
		      	RangeStateInterpretation[jl.Double, AcSpecies, AcTranslationVariable](startTime, translation.getToTime - translation.getFromTime, items)
		    } else {
		    	val items = translation.getTranslationItems.asScala.map(translationItem => {
		    		val fun = translationItem.getTranslationFunction
		    		val referencedSpecies = fun.getReferencedVariableIndeces.asScala.flatMap(index => {
		    			val species = indexSpeciesMap.get(index)
		    			if (species.isDefined) Some(species.get) else None
		    		})
		    		val funEval = funEvaluatorFactory.createInstance(fun).asInstanceOf[FunctionEvaluator[Any, jl.Double]]
		    		val scalaFun = functionEvaluatorToScalaDoubleMapFunction[Any, jl.Double, AcSpecies, AcTranslationVariable](funEval, speciesIndexMap, variableIndexMap) 

		    		new PointStateInterpretationItem[jl.Double, AcSpecies, AcTranslationVariable](
							translationItem.getVariable,
							referencedSpecies,
							(input: Map[AcSpecies, jl.Double], env: Map[AcTranslationVariable, jl.Double]) =>
								interpretationFun2(scalaFun)(input, env)
						)
		     })

		     PointStateInterpretation[jl.Double, AcSpecies, AcTranslationVariable](startTime, items)
		    }
		})
	}

	private def interpretationFun[T : Manifest, C, H](arrayFun : (Map[C, Any], Map[H, Any]) => T)(input : Map[C, Seq[T]], env : Map[H, T]) = {
		val arrayMap = input.map { case (k, v) => 
			val array = java.lang.reflect.Array.newInstance(manifest.runtimeClass, v.length).asInstanceOf[Array[T]]
			v.zipWithIndex.foreach { case (elem, i) => array(i) = elem }
			(k, array)
		}
		arrayFun(arrayMap, env)
	}

	private def interpretationFun2[T : Manifest, C, H](fub : (Map[C, Any], Map[H, Any]) => T)(input : Map[C, T], env : Map[H, T]) =
		fub(input, env)
}