package com.bnd.network.business.function

import java.{lang => jl}
import com.bnd.network.BndNetworkException
import com.bnd.network.domain.ActivationFunctionType

/**
 * @author © Peter Banda
 * @since 2015
 */
trait ActivationFunctionFactory[T] {

	type ActivationFunction[T] = T => T

	def apply(integratorType : ActivationFunctionType, params : Option[Seq[T]]) : ActivationFunction[T]
	def derivate(integratorType : ActivationFunctionType, params : Option[Seq[T]]) : ActivationFunction[T]
}

private[business] class DoubleActivationFunctionFactory extends ActivationFunctionFactory[Double] with Serializable {

	def apply(outputFunctionType : ActivationFunctionType, params : Option[Seq[Double]]) : ActivationFunction[Double] = {
    val seq = params.getOrElse(Nil)
    def param(i: Int) = if (i < seq.size) seq(i) else 0d

    outputFunctionType match {
      case ActivationFunctionType.Linear => linear(param(0))
      case ActivationFunctionType.Signum => signum
      case ActivationFunctionType.Threshold => threshold(param(0), param(1), param(2))
      case ActivationFunctionType.Tanh => tanh
      case ActivationFunctionType.Sigmoid => sigmoid(param(0))
      case ActivationFunctionType.Gaussian => gaussian(param(0))
      case ActivationFunctionType.SymmetricGaussian => symmetricGaussian(param(0))
      case ActivationFunctionType.ElliotTanh => elliotTanh(param(0))
      case ActivationFunctionType.ElliotSigmoid => elliotSigmoid(param(0))
      case ActivationFunctionType.Sinus => sin(param(0), param(1), param(2))
      case ActivationFunctionType.Cosinus => cos(param(0), param(1), param(2))
      case ActivationFunctionType.Softplus => softplus(param(0), param(1))
    }
  }

	// TODO
	def derivate(outputFunctionType : ActivationFunctionType, params : Option[Seq[Double]]) : ActivationFunction[Double] = {
    val seq = params.getOrElse(Nil)
    def param(i: Int) = if (i < seq.size) seq(i) else 0d

    outputFunctionType match {
      case ActivationFunctionType.Linear => dlinear(param(0))
      case ActivationFunctionType.Signum => throw new BndNetworkException(outputFunctionType + " does not have a derivative defined.")
      case ActivationFunctionType.Threshold => throw new BndNetworkException(outputFunctionType + " does not have a derivative defined.")
      case ActivationFunctionType.Tanh => dtanh
      case ActivationFunctionType.Sigmoid => dsigmoid(param(0))
      case ActivationFunctionType.Gaussian => dgaussian(param(0))
      case ActivationFunctionType.SymmetricGaussian => throw new BndNetworkException(outputFunctionType + " does not have a derivative defined.")
      case ActivationFunctionType.ElliotTanh => throw new BndNetworkException(outputFunctionType + " does not have a derivative defined.")
      case ActivationFunctionType.ElliotSigmoid => throw new BndNetworkException(outputFunctionType + " does not have a derivative defined.")
      case ActivationFunctionType.Sinus => dsin(param(0), param(1), param(2))
      case ActivationFunctionType.Cosinus => dcos(param(0), param(1), param(2))
      case ActivationFunctionType.Softplus => dsoftplus(param(0), param(1))
    }
  }

  private def linear(slope : Double)(x : Double) = x * slope
  private def dlinear(slope : Double)(y : Double) = slope

  private def signum(x : Double) = x match {
		case a if a < 0 => -1
		case 0 => 0
		case _ => -1
	}

	// step function
	private def threshold(threshold : Double, low : Double, high : Double)(x : Double) =  if (x > threshold) high else low

	private def tanh(x : Double) = math.tanh(x)
  private def dtanh(y : Double) = 1 - y * y

	// logistic function
	private def sigmoid(k : Double)(x : Double) = 1 / (1 + math.exp(-k * x))
  private def dsigmoid(k : Double)(y : Double) = k * y * (1 - y)

  private def gaussian(std : Double)(x : Double) = math.exp(- x * x / (2 * std * std))
  // TODO
//  private def dgaussian(std : Double)(y : Double) = - x * std * std
  private def dgaussian(std : Double)(y : Double) = - math.sqrt(-math.log(y)) * std * std * std * math.sqrt(2)

  private def symmetricGaussian(std : Double)(x : Double) = 2 * gaussian(std)(x) - 1

  private def elliotTanh(k : Double)(x : Double) = (x * k) / (1 + math.abs(x * k))

  private def elliotSigmoid(k : Double)(x : Double) =  0.5 + 0.5 * elliotTanh(k)(x)

  private def sin(a : Double, b : Double, c : Double)(x : Double) = a * math.sin(b * x  + c)
  // TODO
  private def dsin(a : Double, b : Double, c : Double)(x : Double) = a * b * math.cos(b * x  + c)

  private def cos(a : Double, b : Double, c : Double)(x : Double) = a * math.cos(b * x  + c)
  // TODO
  private def dcos(a : Double, b : Double, c : Double)(x : Double) = - a * b * math.sin(b * x  + c)

  private def softplus(a : Double, b : Double)(y : Double) = a * math.log(1 + math.exp(b * y))

  // TODO
  private def dsoftplus(a : Double, b : Double)(y : Double) = a * math.log(1 + math.exp(b * y))
}

private[business] class JavaDoubleActivationFunctionFactory extends ActivationFunctionFactory[jl.Double] with Serializable {

  val doubleFactory = new DoubleActivationFunctionFactory

	def apply(outputFunctionType : ActivationFunctionType, params : Option[Seq[jl.Double]]) : ActivationFunction[jl.Double] =
    {input : jl.Double => doubleFactory(outputFunctionType, transformParams(params))(input)}

	def derivate(outputFunctionType : ActivationFunctionType, params : Option[Seq[jl.Double]]) : ActivationFunction[jl.Double] =
  {input : jl.Double => doubleFactory.derivate(outputFunctionType, transformParams(params))(input)}

  private def transformParams(params : Option[Seq[jl.Double]]) =
    if (params.isDefined) Some(params.get.map(param =>  param : Double)) else None
}