package com.bnd.network.domain

import java.{util => ju}

/**
 * @author © Peter Banda
 * @since 2014
 */
class CustomNetworkFunction[T] extends NetworkFunction[T] {

  private var _weightFunction : Option[(ju.List[T],ju.List[T]) => T] = None
  private var _activationFunction : Option[T => T] = None

  private var _perNodeActivationFunctionWithParams: Option[Stream[(ActivationFunctionType, Seq[T])]] = None

  def weightFunction = _weightFunction
  def weightFunction_=(weightFunction : (ju.List[T],ju.List[T]) => T) = _weightFunction = Some(weightFunction)

  def activationFunction = _activationFunction
  def activationFunction_=(activationFunction : T => T) = _activationFunction = Some(activationFunction)

  def perNodeActivationFunctionWithParams = _perNodeActivationFunctionWithParams
  def perNodeActivationFunctionWithParams_=(perNodeActivationFunctionWithParams : Stream[(ActivationFunctionType, Seq[T])]) = _perNodeActivationFunctionWithParams = Some(perNodeActivationFunctionWithParams)
}