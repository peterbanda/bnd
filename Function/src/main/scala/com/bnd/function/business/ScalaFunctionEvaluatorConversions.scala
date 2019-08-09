package com.bnd.function.business;

import com.bnd.function.evaluator.FunctionEvaluator
import scala.collection.JavaConversions._
import com.bnd.core.CollectionElementsConversions._
import java.{lang => jl, util => ju}
import java.util.Arrays
import scala.collection.Map
import com.bnd.function.BndFunctionException

/**
 * @author © Peter Banda
 * @since 2013 
 */
object ScalaFunctionEvaluatorConversions {

    implicit def scalaFunctionToFunctionEvaluator[IN, OUT](
    	fun: jl.Iterable[IN] => OUT,
    	arity: Int
    ) : FunctionEvaluator[IN, OUT] = new ScalaFunctionEvaluator[IN, OUT](fun, arity)

    implicit def scalaArrayFunctionToFunctionEvaluator[IN <: AnyRef, OUT](
    	fun: Array[IN] => OUT,
    	arity: Int
    ) : FunctionEvaluator[IN, OUT] = new ScalaArrayFunctionEvaluator[IN, OUT](fun, arity)

    implicit def functionEvaluatorToScalaFunction[IN, OUT](
        funEvaluator : FunctionEvaluator[IN, OUT]
    ) : Iterable[IN] => OUT = inputs => funEvaluator.evaluate(asJavaIterable(inputs))

    implicit def functionEvaluatorToScalaListFunction[IN, OUT](
        funEvaluator : FunctionEvaluator[IN, OUT]
    ) : ju.List[IN] => OUT = inputs => funEvaluator.evaluate(inputs)

    private def createIndexedEnvironment[A, B](
        environment : Map[A, B],
        recognizedVariableIndexMap : Map[A, Int],
        indexFun : Int => Integer
    ) = environment.foldLeft(Map[Integer, B]()){
            case (map,(component, state)) => {
            	val index = recognizedVariableIndexMap.get(component)
            	if (index.isDefined)
            		map + ((indexFun(index.get), state))
            	else
            	    map
            }
    	}

    implicit def functionEvaluatorToScalaMapFunction[IN, OUT, C](
        funEvaluator : FunctionEvaluator[IN, OUT],
        recognizedComponentIndexMap : Map[C, Int]
    ) : Map[C, IN] => OUT = environment => {
        val indexedEnvironemnt = createIndexedEnvironment(environment, recognizedComponentIndexMap, {index : Int => index : Integer})
        funEvaluator.evaluate(indexedEnvironemnt)
    }

    def functionEvaluatorToScalaDoubleMapFunction[IN, OUT, C1, C2](
        funEvaluator : FunctionEvaluator[IN, OUT],
        recognizedComponentIndexMap1 : Map[C1, Int],
        recognizedComponentIndexMap2 : Map[C2, Int]
    ) : (Map[C1, IN], Map[C2, IN]) => OUT = (environment1, environment2) => {
        val indexed1Environemnt = createIndexedEnvironment(environment1, recognizedComponentIndexMap1, {index : Int => 2 * index : Integer})
        val indexed2Environemnt = createIndexedEnvironment(environment2, recognizedComponentIndexMap2, {index : Int => 2 * index + 1 : Integer})
        funEvaluator.evaluate(indexed1Environemnt ++ indexed2Environemnt)
    }
}

private class ScalaFunctionEvaluator[IN, OUT](
	private val fun: jl.Iterable[IN] => OUT,
	private val arity: Int) extends AbstractFunctionEvaluator[IN, OUT] {

	override def evaluate(inputs : jl.Iterable[IN]) : OUT = fun(inputs)

	override def getArity() : Integer = arity
}

private class ScalaArrayFunctionEvaluator[IN <: AnyRef, OUT](
	private val fun: Array[IN] => OUT,
	private val arity: Int) extends AbstractFunctionEvaluator[IN, OUT] {

	override def evaluate(inputs : Array[IN with Object]) : OUT = fun(inputs)

	override def getArity() : Integer = arity
}