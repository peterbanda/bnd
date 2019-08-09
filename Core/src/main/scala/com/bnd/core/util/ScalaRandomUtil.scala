package com.bnd.core.util

import com.bnd.core.BndRuntimeException
import com.bnd.core.domain.ValueBound

import scala.util.Random
import com.bnd.core.NumericConversions
import com.bnd.core.BndRuntimeException
import com.bnd.core.reflection.ReflectionUtil
import com.bnd.core.util.ObjectUtil

/**
 * @author © Peter Banda
 * @since 2013
 */
object ScalaRandomUtil {

	val random = new Random

    def nextBoolean = random.nextBoolean
    def nextLong = random.nextLong
    def nextInt = random.nextInt
    def nextByte = random.nextInt(Byte.MaxValue).toByte
    def nextDouble = random.nextDouble
	def nextFloat = random.nextFloat

	def nextLong(from : Long, to : Long) : Long = from + (to - from) * nextLong
	def nextInt(from : Int, to : Int) : Int = from + (to - from) * nextInt
	def nextByte(from : Byte, to : Byte) : Byte = (from + (to - from) * nextByte).toByte
	def nextDouble(from : Double, to : Double) : Double = from + (to - from) * nextDouble
	def nextFloat(from : Float, to : Float) : Float = from + (to - from) * nextFloat

	def nextNormal(location : Double, shape : Double) = location + random.nextGaussian * shape
	def nextLogNormal(location : Double, shape : Double)  = math.exp(nextNormal(location, shape))

	/**
	 * Generic random generation for number types.
	 * 
	 * @param numberClazz
	 * @param from
	 * @param to
	 * @return
	 */
	def next[T <: AnyVal](numberClazz : Class[T], from : T, to : T) : T = {
		if (ObjectUtil.areObjectsEqual(from, to)) from
		else {
			if (numberClazz == classOf[Long])
				nextLong(from.asInstanceOf[Long], to.asInstanceOf[Long]).asInstanceOf[T]
			else if (numberClazz == classOf[Integer])
				nextInt(from.asInstanceOf[Int], to.asInstanceOf[Int]).asInstanceOf[T]
			else if (numberClazz == classOf[Byte])
				nextByte(from.asInstanceOf[Byte], to.asInstanceOf[Byte]).asInstanceOf[T]
			else if (numberClazz == classOf[Double])
				nextDouble(from.asInstanceOf[Double], to.asInstanceOf[Double]).asInstanceOf[T]
			else if (numberClazz == classOf[Float])
				nextFloat(from.asInstanceOf[Float], to.asInstanceOf[Float]).asInstanceOf[T]
			else if (numberClazz == classOf[Char])
				nextByte(from.asInstanceOf[Char].toByte, to.asInstanceOf[Char].toByte).toChar.asInstanceOf[T]
			else
			    throw new BndRuntimeException("Class " + numberClazz + " not recognized for generic random number generation.")
		}
	}

	/**
	 * Nasty conversion of given value to the one of the Number instances
	 * 
	 * @param value
	 * @param inTypeClass
	 * @return
	 */
	def probabilityConvert[T <: AnyVal](numberClazz : Class[T], value : AnyVal) : T = {
		if (value == null) null.asInstanceOf[T]
		else 
			if (numberClazz == classOf[Long] || numberClazz == classOf[Integer] || numberClazz == classOf[Byte]) {
				val doubleVal = NumericConversions.convertAsNumeric[Double](value)
				val lowerVal  = math.floor(doubleVal)
				val upperVal  = math.ceil(doubleVal)

				if (nextDouble > doubleVal - lowerVal)
					NumericConversions.convertAsNumericFromClass(numberClazz, lowerVal)
				else
				    NumericConversions.convertAsNumericFromClass(numberClazz, upperVal)
			} else 
			    NumericConversions.convertAsNumericFromClass(numberClazz, value)
	}


		/**
	 * Generic random Gaussian generation for number types.
	 * 
	 * @param numberClazz
	 * @param from
	 * @param to
	 * @return
	 */
	def nextNormal[T <: AnyVal](numberClazz : Class[T], mean : Double, variance : Double) : T = {
		val value = nextNormal(mean, variance)
		probabilityConvert(numberClazz, value)
	}

	/**
	 * Generic random log Gaussian generation for number types.
	 * 
	 * @param numberClazz
	 * @param from
	 * @param to
	 * @return
	 */
	def nextLogNormal[T <: AnyVal](numberClazz : Class[T], location : Double, shape : Double) : T = {
		val value = nextLogNormal(location, shape)
		probabilityConvert(numberClazz, value)
	}
}