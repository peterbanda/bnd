package com.bnd.network.business.integrator

import java.util.Collection
import java.{lang => jl, util => ju}

import com.bnd.core.converter.ConverterFactory
import com.bnd.network.domain.StatesWeightsIntegratorType
import StatesWeightsIntegratorDef.StatesWeightsIntegrator
import com.bnd.core.converter.{Converter, ConverterFactory}

/**
 * @author © Peter Banda
 * @since 2012  
 */
private class DoubleConvertibleSWIntegratorFactoryImpl[T](
	doubleStatesWeightsIntegratorFactory : StatesWeightsIntegratorFactory[jl.Double],
	doubleConverter : Option[Converter[T,jl.Double]],
	doubleCollectionConverter : Option[Converter[ju.Collection[T],ju.Collection[jl.Double]]]
) extends StatesWeightsIntegratorFactory[T] {

	override def apply(integratorType : StatesWeightsIntegratorType) : StatesWeightsIntegrator[T] = {
		val doubleIntegrator = doubleStatesWeightsIntegratorFactory.apply(integratorType)
		if (doubleCollectionConverter == null)
			// no double converter provided => assuming the type is double
			doubleIntegrator.asInstanceOf[StatesWeightsIntegrator[T]]
		else
			// if not of type double we have to wrap it into adapter
			integrateAsDouble(doubleIntegrator)
	}

	private def integrateAsDouble(
		doubleIntegrator : StatesWeightsIntegrator[jl.Double])(
		states: Collection[T],
		weights: Collection[T]
	) = {
		val doubleResult = doubleIntegrator(
			doubleCollectionConverter.get.convert(states).asInstanceOf[ju.List[jl.Double]],
			doubleCollectionConverter.get.convert(weights).asInstanceOf[ju.List[jl.Double]])
		doubleConverter.get.reconvert(doubleResult)
	}
}

object DoubleConvertibleSWIntegratorFactory{

	private val converterFactory = ConverterFactory.getInstance

	def apply[T](
		doubleStatesWeightsIntegratorFactory : StatesWeightsIntegratorFactory[jl.Double],
		doubleConverter : Converter[T,jl.Double]
	) : StatesWeightsIntegratorFactory[T] = new DoubleConvertibleSWIntegratorFactoryImpl(
		doubleStatesWeightsIntegratorFactory,
		Some(doubleConverter),
		Some(converterFactory.createCollectionConverter(doubleConverter)))

	def apply[T](
		doubleStatesWeightsIntegratorFactory: StatesWeightsIntegratorFactory[jl.Double],
		clazz: Class[T]
	) : StatesWeightsIntegratorFactory[T] =
		if (clazz != classOf[jl.Double]) {
			val doubleConverter = converterFactory.createConverter(clazz, classOf[jl.Double])
			val doubleCollectionConverter = converterFactory.createCollectionConverter(doubleConverter)
			new DoubleConvertibleSWIntegratorFactoryImpl(doubleStatesWeightsIntegratorFactory, Some(doubleConverter), Some(doubleCollectionConverter))
		} else
			new DoubleConvertibleSWIntegratorFactoryImpl(doubleStatesWeightsIntegratorFactory, None, None)
}