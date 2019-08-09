package com.bnd.math.business.rand;

import java.{lang => jl}

import com.bnd.math.domain.rand.RandomDistribution
import com.bnd.math.domain.rand.ShapeLocationDistribution
import com.bnd.math.domain.rand.UniformDistribution
import com.bnd.math.domain.rand.RandomDistributionType
import com.bnd.math.domain.rand.DiscreteDistribution
import com.bnd.math.domain.rand.UniformDiscreteDistribution
import com.bnd.math.domain.rand.RepeatedDistribution
import com.bnd.math.domain.rand.CompositeDistribution
import com.bnd.math.domain.rand.CompositeDistribution.CompositeFunction
import com.bnd.core.NumericConversions
import com.bnd.core.ClassUtil
import com.bnd.core.BndRuntimeException

import scala.reflect.runtime.universe._

object RandomDistributionProviderFactory {

    val booleanClazz = classOf[Boolean]
    val javaBooleanClazz = classOf[jl.Boolean]

    def apply[T](randomDistribution : RandomDistribution[T]) : RandomDistributionProvider[T] =
		if (randomDistribution == null)
			null
		// synchronized block is needed here due to lack of Scala runtime/reflection thread safety
		// https://issues.scala-lang.org/browse/SI-6240
		else this.synchronized {
		    if (randomDistribution.getValueType.equals(javaBooleanClazz)) 
		        createBooleanInstance(randomDistribution.asInstanceOf[RandomDistribution[jl.Boolean]]).asInstanceOf[RandomDistributionProvider[T]]
		    else {
		    	val valueType = ClassUtil.toType(randomDistribution.getValueType)
		    	if (valueType <:< typeOf[Number])
		    	    createNumberInstance[Number](randomDistribution.asInstanceOf[RandomDistribution[Number]]).asInstanceOf[RandomDistributionProvider[T]] 
		    	else if (valueType <:< typeOf[AnyVal])
		    	    createAnyValInstance[AnyVal](randomDistribution.asInstanceOf[RandomDistribution[AnyVal]]).asInstanceOf[RandomDistributionProvider[T]]
		    	else throw new BndRuntimeException("Random Distribution value type " + randomDistribution.getValueType() + " not recognized.");
		    }
		}

	private def createNumberInstance[T <: Number](randomDistribution : RandomDistribution[T]) : RandomDistributionProvider[T] =
		if (randomDistribution == null)
			null
		else (randomDistribution.getType) match {
			case RandomDistributionType.Uniform =>
				new NumberUniformDistributionProvider(randomDistribution.asInstanceOf[UniformDistribution[T]])
			case RandomDistributionType.Normal =>
				new NumberNormalDistributionProvider(randomDistribution.asInstanceOf[ShapeLocationDistribution[T]])
			case RandomDistributionType.LogNormal =>
				new NumberLogNormalDistributionProvider(randomDistribution.asInstanceOf[ShapeLocationDistribution[T]])
			case RandomDistributionType.PositiveNormal =>
				new NumberPositiveNormalDistributionProvider[T](
						randomDistribution.getValueType(),
						new NumberNormalDistributionProvider[T](randomDistribution.asInstanceOf[ShapeLocationDistribution[T]]))
			case RandomDistributionType.Discrete =>
				new DiscreteDistributionProvider[T](randomDistribution.asInstanceOf[DiscreteDistribution[T]], { x : T => x.doubleValue})
			case RandomDistributionType.UniformDiscrete =>
				new UniformDiscreteDistributionProvider[T](randomDistribution.asInstanceOf[UniformDiscreteDistribution[T]], { x : T => x.doubleValue})
			case RandomDistributionType.Composite => {
					val compositeDistribution = randomDistribution.asInstanceOf[CompositeDistribution[T]]
					val provider1 = apply(compositeDistribution.getDistribution1)
					val provider2 = apply(compositeDistribution.getDistribution2)
					compositeDistribution.getFunction match {
					    case CompositeFunction.PLUS => new SumDistributionProvider[T](NumericConversions.plus(provider1.getValueType))(provider1, provider2)
					    case CompositeFunction.TIMES => new ProductDistributionProvider[T](NumericConversions.times(provider1.getValueType))(provider1, provider2)
					}
				}
			case RandomDistributionType.Repeated =>
				new RepeatedDistributionProvider[T](randomDistribution.asInstanceOf[RepeatedDistribution[T]], { x : T => x.doubleValue})
			case _ =>
				throw new BndRuntimeException("Random Distribution type " + randomDistribution.getType() + " not recognized.");
		}

	private def createBooleanInstance(randomDistribution : RandomDistribution[jl.Boolean]) : RandomDistributionProvider[jl.Boolean] =
		if (randomDistribution == null)
			null
		else (randomDistribution.getType) match {
		    case RandomDistributionType.Discrete =>
					new DiscreteDistributionProvider[jl.Boolean](randomDistribution.asInstanceOf[DiscreteDistribution[jl.Boolean]],
				        { x : jl.Boolean => if (x) 1d else 0d})
				case RandomDistributionType.UniformDiscrete =>
					new UniformDiscreteDistributionProvider[jl.Boolean](randomDistribution.asInstanceOf[UniformDiscreteDistribution[jl.Boolean]],
								{ x : jl.Boolean => if (x) 1d else 0d})
		    case RandomDistributionType.Repeated =>
		      new RepeatedDistributionProvider[jl.Boolean](randomDistribution.asInstanceOf[RepeatedDistribution[jl.Boolean]],
				        { x : jl.Boolean => if (x) 1d else 0d})
		    case RandomDistributionType.BooleanDensityUniform =>
		      new BooleanDensityUniformDistributionProvider
			case RandomDistributionType.Uniform | RandomDistributionType.Normal | RandomDistributionType.LogNormal | RandomDistributionType.PositiveNormal =>
			    throw new BndRuntimeException("Random Distribution type " + randomDistribution.getType() + " is illegal for Boolean type.");
			case _ =>
				throw new BndRuntimeException("Random Distribution type " + randomDistribution.getType() + " not recognized.");
		}
	
	private def toDoubleAsBoolean[T](x : T) = if (x.asInstanceOf[Boolean]) 1d : jl.Double else 0d : jl.Double
	private def toDoubleAsAnyVal[T <: AnyVal](clazz : Class[T])(x : T) = {
		val num = NumericConversions.liftAnyValToNumericFromClass(clazz)
		num.toDouble(x) : jl.Double
	}

	private def createAnyValInstance[T <: AnyVal](randomDistribution : RandomDistribution[T]) : RandomDistributionProvider[T] =
		if (randomDistribution == null)
			null
		else (randomDistribution.getType) match {

			case RandomDistributionType.Uniform =>
				new AnyValUniformDistributionProvider(randomDistribution.asInstanceOf[UniformDistribution[T]])

			case RandomDistributionType.Normal =>
				new AnyValNormalDistributionProvider(randomDistribution.asInstanceOf[ShapeLocationDistribution[T]])

			case RandomDistributionType.LogNormal =>
				new AnyValLogNormalDistributionProvider(randomDistribution.asInstanceOf[ShapeLocationDistribution[T]])

			case RandomDistributionType.PositiveNormal =>
				new AnyValPositiveNormalDistributionProvider[T](
						randomDistribution.getValueType(),
						new AnyValNormalDistributionProvider[T](randomDistribution.asInstanceOf[ShapeLocationDistribution[T]]))

			case RandomDistributionType.Discrete => {
				val toDoubleFun = 
				    (randomDistribution.getValueType) match {
				    	case `booleanClazz` => toDoubleAsBoolean[T]_
				    	case _ => toDoubleAsAnyVal[T](randomDistribution.getValueType)_
			    	}
			    new DiscreteDistributionProvider[T](randomDistribution.asInstanceOf[DiscreteDistribution[T]], toDoubleFun)
			}

			case RandomDistributionType.UniformDiscrete => {
				val toDoubleFun =
					(randomDistribution.getValueType) match {
						case `booleanClazz` => toDoubleAsBoolean[T]_
						case _ => toDoubleAsAnyVal[T](randomDistribution.getValueType)_
					}
				new UniformDiscreteDistributionProvider[T](randomDistribution.asInstanceOf[UniformDiscreteDistribution[T]], toDoubleFun)
			}

			case _ =>
				throw new BndRuntimeException("Random Distribution type " + randomDistribution.getType() + " not recognized.");
		}

    private def costOne[T](el : T) = 1d

    def applyDiscrete[T](randomDistribution : DiscreteDistribution[T]) : RandomDistributionProvider[T] =
      new DiscreteDistributionProvider[T](randomDistribution.asInstanceOf[DiscreteDistribution[T]], costOne[T])
}