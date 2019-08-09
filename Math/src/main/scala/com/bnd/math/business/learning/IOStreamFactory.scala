package com.bnd.math.business.learning

import java.{lang => jl, util => ju}

import com.bnd.core.CollectionElementsConversions._
import com.bnd.core.DoubleConvertible.DoubleAsDoubleConvertible
import com.bnd.core.util.RandomUtil._
import com.bnd.function.business.ScalaFunctionEvaluatorConversions._
import com.bnd.function.evaluator.FunctionEvaluatorFactory
import com.bnd.function.{domain => fd}
import com.bnd.math.BndMathException
import com.bnd.math.business.learning.IOStream
import com.bnd.math.business.rand.RandomDistributionProviderFactory
import com.bnd.math.domain.learning._
import com.bnd.math.domain.rand.RandomDistribution

import scala.collection.JavaConversions._

class IOStreamFactory(private val feFactory : FunctionEvaluatorFactory) extends Serializable {

	def createInstance[T](
	    trainingSet : TrainingSet[T])(
	    selectionType : TrainingSampleSelectionType
	) : IOStream[T] = {
		val allTrainingPairs = trainingSet.getTrainingPairs
		val allTrainingPairsArray : Array[TrainingPair[T]] = trainingSet.getTrainingPairs
		val trainingPairs = selectionType match {
			case TrainingSampleSelectionType.GivenOrder => Stream.continually(allTrainingPairs).flatten

			case TrainingSampleSelectionType.RandomPermutation => Stream.continually(nextPermutation(allTrainingPairs)).flatten			    

			case TrainingSampleSelectionType.Random => Stream.continually(nextElement(allTrainingPairsArray))

			case _ => throw new BndMathException("Not recognized machine learning selection type " + selectionType)
		}

		new IOStream[T](
		    trainingPairs.map(_.getInput()),
		    trainingPairs.map(_.getDesiredOutput()), 1)
	}


	def createInstance1DPair[T <: Number](
	    fun : (T,T) => T,
			zero : T,
			outputShift : Int = 2
	) = createInstance1D[T](pairFunToIterableFun(fun))(zero, 2, outputShift)_

	def createStatic1DPair[T <: Number](
		fun : (T,T) => T,
		zero : T,
		outputShift : Int = 2
	) = createInstance1D[T](pairFunToIterableFun(fun))(zero, 2, outputShift)_

	def createInstance1DTriple[T <: Number](
		fun : (T,T,T) => T,
		zero : T,
		outputShift : Int = 3
	) = createInstance1D[T](tripleFunToIterableFun(fun))(zero, 3, outputShift)_

	def createInstance1DVarargs[T <: Number](fun : (T*) => T) = createInstance1D[T](varargsFunToIterableFun(fun))_

	def createInstance1DFun[T <: Number](
	    fun : fd.Function[T, T]) = createInstance1D[T](feFactory.createInstance(fun))_

	def createInstance1D[T](
	    fun : Iterable[T] => T)(
			zero : T,
			inputOrder : Int,
	    outputShift : Int)(
	    inputDistribution : RandomDistribution[T] 
	) : IOStream[T] = {
			val inputs = createRandomStream(inputDistribution)
	    createInstance1DWithInputs[T](fun)(zero, inputOrder, outputShift)(inputs)
	}

	def createInstance1DWithInputs[T](
	    fun : Iterable[T] => T)(
			zero : T,
		  inputOrder : Int,
	    outputShift : Int)(
	    inputs : Stream[T]
	) = {
		val xx = if (inputOrder > outputShift) Stream.fill(inputOrder - outputShift )(zero) #::: inputs else inputs
		new IOStream[T](
	    	inputs.map(Seq(_)),
				xx.sliding(inputOrder).toStream.map(x => Seq(fun(x))),
	    	outputShift)
	}

	def createInstance1DStream[T](
			fun : Stream[T] => Stream[T])(
	    outputShift : Int)(
	    inputDistribution : RandomDistribution[T]
	) : IOStream[T] = {
			val inputs = createRandomStream(inputDistribution)
			createInstance1DStreamWithInputs[T](fun)(outputShift)(inputs)
	}

	def createInstance1DStreamWithInputs[T](
	    fun : Stream[T] => Stream[T])(
	    outputShift : Int)(
	    inputs : Stream[T]
	) : IOStream[T] = new IOStream[T](
	    	inputs.map(Seq(_)),
	    	fun(inputs).map(Seq(_)),
	    	outputShift)

	def createRandomStream[T](inputDistribution : RandomDistribution[T]) = {
		val inputDP = RandomDistributionProviderFactory.apply(inputDistribution)
		Stream.continually(inputDP.next)
	}

	def createInstanceFun[T <: Number](
	    fun : fd.Function[Iterable[T],Iterable[T]]) = createInstance[T](feFactory.createInstance(fun))_

	def createInstance[T <: Number](
	    fun : Iterable[Iterable[T]] => Iterable[T])(
	    windowSize : Int)(
	    inputDistribution : RandomDistribution[T],
			inputSize : Int
	) : IOStream[T] = {
			val inputDP = RandomDistributionProviderFactory.apply(inputDistribution)
			val inputs = Stream.continually(inputDP.nextList(inputSize) : Seq[T])
			createInstanceWithInputs[T](fun)(windowSize)(inputs)
	}

	def createStaticInstance[T <: Number](
		fun : Iterable[T] => T)(
		inputDistribution : RandomDistribution[T],
		inputSize : Int
	) : IOStream[T] = {
		val inputDP = RandomDistributionProviderFactory.apply(inputDistribution)
		val inputs = Stream.continually(inputDP.nextList(inputSize) : Seq[T])
		new IOStream[T](inputs, inputs.map(x => Seq(fun(x))), 0)
	}

  def createInstance1DPredict[T](
    predictShift : Int,
    washoutSize : Int)(
    history : Iterable[T]
  ) : IOStream[T] = new IOStream[T](
    history.map(Seq(_)).toStream,
    history.map(Seq(_)).drop(predictShift + washoutSize).toStream,
    washoutSize)

  def createInstancePredict[T](
    predictShift : Int,
    washoutSize : Int)(
    input : Iterable[Seq[T]],
    history : Iterable[T]
  ) : IOStream[T] = new IOStream[T](
    input.toStream,
    history.map(Seq(_)).drop(predictShift + washoutSize).toStream,
    washoutSize)

  def createInstanceWithInputs[T](
	    fun : Iterable[Iterable[T]] => Iterable[T])(
	    windowSize : Int)(
	    inputs : Stream[Seq[T]]
	) : IOStream[T] = new IOStream[T](
	    	inputs,
	    	inputs.sliding(windowSize).toStream.map(x => fun(x).toSeq),
	    	windowSize)
		
// Narma

	def createNarmaInstance[T <: Number](order : Int, initInputStreamWithZeroes : Boolean) = {
		val outputShift = if (initInputStreamWithZeroes) 0 else order
		createInstance1DStream(javaNarma(order, initInputStreamWithZeroes))(outputShift)_
	}

	def createNarmaTanhInstance[T <: Number](order : Int, initInputStreamWithZeroes : Boolean) = {
		val outputShift = if (initInputStreamWithZeroes) 0 else order
		createInstance1DStream(javaNarmaTanh(order, initInputStreamWithZeroes))(outputShift)_
	}

	private val dadc = DoubleAsDoubleConvertible

	def javaNarma(order : Int, initInputStreamWithZeroes : Boolean)(x : Stream[jl.Double]) : Stream[jl.Double] =
		narma(order, initInputStreamWithZeroes)(x)

	def javaNarmaTanh(order : Int, initInputStreamWithZeroes : Boolean)(x : Stream[jl.Double]) : Stream[jl.Double] =
		narmaTanh(order, initInputStreamWithZeroes)(x)

  def narma(order : Int, initInputStreamWithZeroes : Boolean = true)(x : Stream[Double]) = inputRecursiveStream(narmaFun)(0d, initInputStreamWithZeroes)(order, order)(x)
	def narmaTanh(order : Int, initInputStreamWithZeroes : Boolean = true)(x : Stream[Double]) = inputRecursiveStream(narmaTanhFun)(0d, initInputStreamWithZeroes)(order, order)(x)

	private def narmaFun(xs : Seq[Double],ys : Seq[Double]) = 0.3 * ys.last + 0.05 * ys.last * ys.sum + 1.5 * xs.last * xs.head + 0.1

	private def narmaTanhFun(xs : Seq[Double],ys : Seq[Double]) = math.tanh(narmaFun(xs,ys))

// helper functions

	def createDelayLineIOStream[T](
			zero : T)(
		  ioStream : IOStream[T],
			dlSize : Int
	) = {
			val newOutputShift = ioStream.outputShift - dlSize
			val inputStream = if (newOutputShift < 0)
							Stream.fill(-newOutputShift)(Seq(zero)) #::: ioStream.inputStream
					else
							ioStream.inputStream

			new IOStream[T](
					inputStream.sliding(dlSize).toStream.map(x => x.toList.flatten),
					ioStream.outputStream,
					if (newOutputShift < 0) 0 else newOutputShift)
	}

	def inputRecursiveStream[T](
		fun : (Seq[T], Seq[T]) => T)(
		zero : T,
		initInputStreamWithZeroes : Boolean = false)(
		order : Int,
		inputOrder : Int)(
		x : Stream[T]
	) : Stream[T] = {
		def slideForward(stream : Stream[T]) = slideStream(stream)(order).tail

		var xx = if (initInputStreamWithZeroes) Stream.fill(inputOrder)(zero) #::: x else x
		lazy val stream : Stream[T] = Stream.fill(order + 1)(zero) #::: slideForward(stream).map { ys =>
			val y = fun(xx.take(inputOrder), ys)
			xx = xx.tail
			y
		}
		stream.drop(order + 1)
	}

	def recursiveStream[T](
		fun: Seq[T] => T)(
		zero: T)(
		order: Int
	): Stream[T] = {
    def slideForward(stream: Stream[T]) = slideStream(stream)(order).tail
    lazy val stream: Stream[T] = Stream.fill(order + 1)(zero) #::: slideForward(stream).map(fun)
    stream.drop(order + 1)
  }

	private val fibs: Stream[BigInt] = BigInt(0) #:: BigInt(1) #:: BigInt(1) #:: slideStream(fibs)(2).tail.map(_.sum)

  private def slideStream[T](s : Stream[T])(size : Int) : Stream[Seq[T]] =
    transposeStreams(stackSlidedStream(s)(size))

  private def stackSlidedStream[T](s: Stream[T])(size: Int): List[Stream[T]] =
		size match {
      case 0 => List()
      case _ => s :: stackSlidedStream(s.tail)(size - 1)
    }

  private def transposeStreams[T](ss : Seq[Stream[T]]) : Stream[Seq[T]] =
    ss.map(_.head) #:: transposeStreams[T](ss.map(_.tail))  

	private def varargsFunToIterableFun[T,Q](fun : (T*) => Q) : Iterable[T] => Q = {
	    x : Iterable[T] => fun(x.toList : _*)}

	private def pairFunToIterableFun[T,Q](fun : (T,T) => Q) : Iterable[T] => Q = {
	    x : Iterable[T] => {val xx = x.toSeq; fun(xx(0), xx(1))}}

	private def tripleFunToIterableFun[T,Q](fun : (T,T,T) => Q) : Iterable[T] => Q = {
	    x : Iterable[T] => {val xx = x.toSeq; fun(xx(0), xx(1), xx(2))}}
}