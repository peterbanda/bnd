package com.bnd.function.business

import java.{lang => jl}
import java.lang.{Iterable => JavaIterable}
import java.util.Map
import java.util.ArrayList
import java.util.HashMap

import scala.collection.JavaConversions._
import com.bnd.core.ListKeyNode
import com.bnd.core.BooleanArrayListKeyInnerNode
import com.bnd.core.ScalaBooleanArrayListKeyInnerNode
import com.bnd.function.domain.BooleanFunctionType
import com.bnd.function.domain.Expression
import com.bnd.function.domain.BooleanFunction
import com.bnd.function.domain.TransitionTable
import com.bnd.function.enumerator.ListEnumeratorFactory
import com.bnd.function.BndFunctionException
import com.bnd.function.evaluator.FunctionEvaluatorFactory
import com.bnd.function.evaluator.FunctionEvaluator
import com.bnd.function.{domain => fd}
import com.bnd.core.{BooleanArrayListKeyInnerNode, ListKeyNode, ListKeyTreeMap}

/**
 * @author © Peter Banda
 * @since 2012  
 */
private[bnd] class FunctionEvaluatorFactoryImpl(
		private val listEnumeratorFactory : ListEnumeratorFactory
	) extends FunctionEvaluatorFactory with Serializable {

	override def createInstance[IN, OUT](
	    function : fd.Function[IN, OUT]
	) : FunctionEvaluator[IN, OUT] = createInstance(function, null)

	override def createInstance[IN, OUT](
		function : fd.Function[IN, OUT],
		variableIndexConversionMap : Map[java.lang.Integer, java.lang.Integer]
	) : FunctionEvaluator[IN, OUT] =
	    function match {
	        case _:TransitionTable[IN,OUT] => createTransitionTableEvaluator(function.asInstanceOf[TransitionTable[IN, OUT]]).asInstanceOf[FunctionEvaluator[IN,OUT]]

	        case _:BooleanFunction => createBooleanFunctionEvaluator(function.asInstanceOf[BooleanFunction]).asInstanceOf[FunctionEvaluator[IN,OUT]]

					case _:Expression[IN,OUT] => throw new BndFunctionException("Expression type is not supported by the default factory. Pls. use ExpressionSupportedFunctionEvaluatorFactoryImpl.")

					case _=> throw new BndFunctionException(function.getClass().getName() + " is not valid function type!")
		}

    val booleanClazz = classOf[Boolean]
	val javaBooleanClazz = classOf[jl.Boolean]

	def createTransitionTableEvaluator[IN, OUT](transitionTable : TransitionTable[IN, OUT]) : FunctionEvaluator[IN,OUT] = {
		var rangeFrom = transitionTable.getRangeFrom
		var rangeTo = transitionTable.getRangeTo

		// default ranges for boolean input type if needed
		// TODO: move somewhere else
		if (transitionTable.getRangeFrom == null)
		    transitionTable.getInputClazz match {
            	case `booleanClazz` => {
            	    rangeFrom = false.asInstanceOf[IN]
            	    rangeTo = true.asInstanceOf[IN]            	    
            	}
            	case `javaBooleanClazz` => {
            	    rangeFrom = jl.Boolean.FALSE.asInstanceOf[IN]
            	    rangeTo = jl.Boolean.TRUE.asInstanceOf[IN]            	    
            	}
            	case _ => throw new BndFunctionException("Range from expected for transition table '" + transitionTable.getId + "'.")
		    }

		// create input enumerator
		val inputEnumerator = listEnumeratorFactory.createInstance(true, rangeFrom, rangeTo)

	    // enumerate inputs
		val inputs = inputEnumerator.enumerate(transitionTable.getArity())

	    // check inputs / outputs compatibility
		val outputs = transitionTable.getOutputs
		if (inputs.size() != outputs.size())
			throw new BndFunctionException("The number of outputs is not equal the number of inputs of transition table (" +
					                         outputs.size() + " vs. " + inputs.size() + ").")

		var table : Map[JavaIterable[IN], OUT] = null
		// check if it's constant
		if (transitionTable.getArity() == 0) {
			table = new HashMap[JavaIterable[IN], OUT](1)
			table.put(new ArrayList[IN](), transitionTable.getOutputs().get(0))
		} else {
		    table = createTransitionTableMap(transitionTable.getRangeFrom())
		    // map inputs to outputs in order
		    val outputIterator : Iterator[OUT] = outputs.iterator()
		    for (input <- inputs) {
		    	table.put(input, outputIterator.next())
		    }
		}

		new TransitionTableEvaluator[IN, OUT](table, transitionTable.getArity())
	}

	private def createTransitionTableMap[IN,OUT](in : IN) : Map[JavaIterable[IN], OUT] =
	    //	final int initialCapacity = (int) (inputs.size() / 0.75);
		in match {
		  	case _:Boolean => new ListKeyTreeMap[IN, OUT](new ScalaBooleanArrayListKeyInnerNode[OUT]().asInstanceOf[ListKeyNode[IN, OUT]]).asInstanceOf[Map[JavaIterable[IN], OUT]]

		    case _:jl.Boolean => new ListKeyTreeMap[IN, OUT](new BooleanArrayListKeyInnerNode[OUT]().asInstanceOf[ListKeyNode[IN, OUT]]).asInstanceOf[Map[JavaIterable[IN], OUT]]

		    case _=> new ListKeyTreeMap[IN, OUT]().asInstanceOf[Map[JavaIterable[IN], OUT]]
		}	    

	def createBooleanFunctionEvaluator(function : BooleanFunction) : FunctionEvaluator[jl.Boolean, jl.Boolean] =
	    (function.getType()) match {
			case BooleanFunctionType.AND => createANDFunctionEvaluator
			case BooleanFunctionType.OR => createORFunctionEvaluator
			case BooleanFunctionType.NOT => createNOTFunctionEvaluator
			case BooleanFunctionType.XOR => createXORFunctionEvaluator
			case BooleanFunctionType.Parity => createParityFunctionEvaluator
			case _ => throw new BndFunctionException("Boolean function '" + function.getType() + "' not recognized.")
		}

	private def createANDFunctionEvaluator : FunctionEvaluator[jl.Boolean, jl.Boolean] =
		new BooleanFunctionEvaluator() {

			override def evaluate(inputs : JavaIterable[jl.Boolean]) : jl.Boolean = {
				for (input <- inputs) {
					if (input == null || !input) {
						false
					}
				}
				true
			}
		}

	private def createORFunctionEvaluator : FunctionEvaluator[jl.Boolean, jl.Boolean] =
		new BooleanFunctionEvaluator() {

			override def evaluate(inputs : JavaIterable[jl.Boolean]) : jl.Boolean = {
				for (input <- inputs) {
					if (input != null && input) {
						true
					}
				}
				false
			}
		}

	private def createNOTFunctionEvaluator : FunctionEvaluator[jl.Boolean, jl.Boolean] =
		new BooleanFunctionEvaluator() {

			override def evaluate(inputs : JavaIterable[jl.Boolean]) : jl.Boolean = {
				for (input <- inputs) {
					if (input != null) {
						!input
					}
				}
				throw new BndFunctionException("NULL(s) provided to NOT function.")
			}
		}

	private def createXORFunctionEvaluator : FunctionEvaluator[jl.Boolean, jl.Boolean] =
		new BooleanFunctionEvaluator() {

			override def evaluate(inputs : JavaIterable[jl.Boolean]) : jl.Boolean = {
				var firstValue : jl.Boolean = null
				for (input <- inputs) {
					if (input != null) {
						if (firstValue == null) {
							firstValue = input
						} else if (!firstValue.equals(input)) {
							true
						}
					}
				}
				false
			}
		}

	private def createParityFunctionEvaluator : FunctionEvaluator[jl.Boolean, jl.Boolean] =
		new BooleanFunctionEvaluator() {

			override def evaluate(inputs : JavaIterable[jl.Boolean]) : jl.Boolean = {
				var parityFlag = true
				for (input <- inputs) {
					if (input != null && input) {
						parityFlag = !parityFlag
					}
				}
				parityFlag
			}
		}

//	public static void main(String[] args) {
//		FunctionEvaluatorFactory fef = new FunctionEvaluatorFactory(new ListEnumeratorFactoryImpl())
//		FunctionEvaluator[jl.Boolean, Boolean] funEvaluator = fef.createBooleanFunctionEvaluator(new BooleanFunction(BooleanFunctionType.AND))
//		List[jl.Boolean] inputs = new ArrayList[jl.Boolean]()
//		inputs.add(Boolean.TRUE)
//		inputs.add(Boolean.TRUE)
//		inputs.add(Boolean.FALSE)
//		Boolean output = funEvaluator.evaluate(inputs)
//		System.out.println("Output: " + output)
//	}
}