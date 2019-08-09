package com.bnd.function.business

import java.util.Map

import com.bnd.function.domain.{BooleanFunction, Expression, TransitionTable}
import com.bnd.function.enumerator.ListEnumeratorFactory
import com.bnd.function.evaluator.{FunctionEvaluator, FunctionEvaluatorFactory}
import com.bnd.function.{BndFunctionException, domain => fd}

/**
 * @author © Peter Banda
 * @since 2012  
 */
private[bnd] class ExpressionSupportedFunctionEvaluatorFactoryImpl(
		private val listEnumeratorFactory : ListEnumeratorFactory
	) extends FunctionEvaluatorFactoryImpl(listEnumeratorFactory) with Serializable {

	override def createInstance[IN, OUT](
	    function : fd.Function[IN, OUT]
	) : FunctionEvaluator[IN, OUT] = createInstance(function, null)

	override def createInstance[IN, OUT](
		function : fd.Function[IN, OUT],
		variableIndexConversionMap : Map[java.lang.Integer, java.lang.Integer]
	) : FunctionEvaluator[IN, OUT] =
		function match {
	  	case _:Expression[IN,OUT] => createExpressionEvaluator(function.asInstanceOf[Expression[IN, OUT]], variableIndexConversionMap)
	    case _ => super.createInstance(function, variableIndexConversionMap)
		}

	private def createExpressionEvaluator[IN, OUT](
		expression : Expression[IN, OUT],
		variableIndexConversionMap : Map[Integer, Integer]
	) : FunctionEvaluator[IN,OUT] =
		if (variableIndexConversionMap != null) 
				new JepExpressionEvaluator[IN, OUT](expression, variableIndexConversionMap) 
			else
				new JepExpressionEvaluator[IN, OUT](expression)
}