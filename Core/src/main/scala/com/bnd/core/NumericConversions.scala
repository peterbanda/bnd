package com.bnd.core

import com.bnd.core.BndRuntimeException
import com.bnd.core.util.ConversionUtil

object NumericConversions {

    val byteClazz = classOf[Byte]
    val shortClazz = classOf[Short]
    val intClazz = classOf[Int]
    val longClazz = classOf[Long]
    val charClazz = classOf[Char]
    val floatClazz = classOf[Float]
    val doubleClazz = classOf[Double]
    val booleanClazz = classOf[Boolean]
    val unitClazz = classOf[Unit]

    def liftAnyValToNumeric[T <: AnyVal](implicit m: Manifest[T]): Numeric[T] = {
        val clazz = ClassUtil.extract[T]
        liftAnyValToNumericFromClass(clazz)
    }

    def liftAnyValToNumericFromClass[T <: AnyVal](clazz : Class[T]): Numeric[T] =
        clazz match {
            case `byteClazz` => implicitly[Numeric[Byte]].asInstanceOf[Numeric[T]]
            case `shortClazz` => implicitly[Numeric[Short]].asInstanceOf[Numeric[T]]
            case `intClazz` => implicitly[Numeric[Int]].asInstanceOf[Numeric[T]]
            case `longClazz` => implicitly[Numeric[Long]].asInstanceOf[Numeric[T]]
            case `charClazz` => implicitly[Numeric[Char]].asInstanceOf[Numeric[T]]
            case `floatClazz` => implicitly[Numeric[Float]].asInstanceOf[Numeric[T]]
            case `doubleClazz` => implicitly[Numeric[Double]].asInstanceOf[Numeric[T]]
            case `booleanClazz` => throw new BndRuntimeException("Sorry, Boolean is a Numeric type.")
            case `unitClazz` => throw new BndRuntimeException("Sorry, Unit is not a Numeric type.") 
        }

	/**
	 * Nasty conversion of given value to the one of the AnyVal instances
	 * 
	 * @param value
	 * @param inTypeClass
	 * @return
	 */
	def convertAsNumeric[T <: AnyVal](value : AnyVal)(implicit m: Manifest[T]) : T = {
	    val clazz = ClassUtil.extract[T]
	    convertAsNumericFromClass(clazz, value)
	}

	/**
	 * Nasty conversion of given value to the one of the Number instances
	 * 
	 * @param value
	 * @param inTypeClass
	 * @return
	 */
	def convertAsNumericFromClass[T <: AnyVal](clazz : Class[T], value : AnyVal) : T = {
		if (value == null) null.asInstanceOf[T]
		else {
		    val num = liftAnyValToNumericFromClass(value.getClass.asInstanceOf[Class[AnyVal]])
		    clazz match {
            	case `byteClazz` => num.toInt(value).toByte.asInstanceOf[T] 
            	case `shortClazz` => num.toInt(value).toShort.asInstanceOf[T]
            	case `intClazz` => num.toInt(value).asInstanceOf[T]
            	case `longClazz` => num.toLong(value).asInstanceOf[T]
            	case `charClazz` => num.toInt(value).toChar.asInstanceOf[T]
            	case `floatClazz` => num.toFloat(value).asInstanceOf[T]
            	case `doubleClazz` => num.toDouble(value).asInstanceOf[T]
            	case `booleanClazz` => throw new BndRuntimeException("Sorry, Boolean is a Numeric type.")
            	case `unitClazz` => throw new BndRuntimeException("Sorry, Unit is not a Numeric type.") 
		    }
		}
	}

	def plus[T <: Number](clazz : Class[T])(a : T, b : T) : T = ConversionUtil.convert(a.doubleValue() + b.doubleValue(), clazz)

	def times[T <: Number](clazz : Class[T])(a : T, b : T) : T = ConversionUtil.convert(a.doubleValue() * b.doubleValue(), clazz)
}