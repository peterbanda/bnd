package com.bnd.core

import java.{lang=>jl}
import java.{lang => jl}

object DoubleConvertible {
    trait ExtraImplicits {
    	implicit def infixNumericOps[T](x: Double)(implicit d: DoubleConvertible[T]): DoubleConvertible[T]#FromDoubleOp = new d.FromDoubleOp(x)
    	implicit def toDouble[T](x: T)(implicit d: DoubleConvertible[T]) = d.toDouble(x) 
    	implicit def addAsDouble[T](a : T, b : T)(implicit d: DoubleConvertible[T]) = d.fromDouble((a : Double) + (b : Double))
    	implicit def subAsDouble[T](a : T, b : T)(implicit d: DoubleConvertible[T]) = d.fromDouble((a : Double) - (b : Double))
    	implicit def minusAsDouble[T](a : T)(implicit d: DoubleConvertible[T]) = d.fromDouble(-a : Double)
    }
    object Implicits extends ExtraImplicits { }

    implicit object IntAsDoubleConvertible extends IntDoubleConvertible
    implicit object ShortAsDoubleConvertible extends ShortDoubleConvertible
    implicit object ByteAsDoubleConvertible extends ByteDoubleConvertible
    implicit object LongAsDoubleConvertible extends LongDoubleConvertible
    implicit object FloatAsDoubleConvertible extends FloatDoubleConvertible
    implicit object BooleanAsDoubleConvertible extends BooleanDoubleConvertible
    implicit object BigIntAsDoubleConvertible extends BigIntDoubleConvertible
    implicit object DoubleAsDoubleConvertible extends DoubleDoubleConvertible
    implicit object JavaDoubleAsDoubleConvertible extends JavaDoubleDoubleConvertible
    implicit object JavaBooleanAsDoubleConvertible extends JavaBooleanDoubleConvertible
}

trait DoubleConvertible[T] {
	def toDouble(a : T) : Double

	def fromDouble(a : Double) : T

	class FromDoubleOp(a : Double) {
		def fromDouble : T = DoubleConvertible.this.fromDouble(a)
	}
	
	implicit def mkFromDoubleOp(a : Double): FromDoubleOp = new FromDoubleOp(a)
}

trait IntDoubleConvertible extends DoubleConvertible[Int] {
    override def toDouble(a : Int) = a : Double

    override def fromDouble(a : Double) = a.toInt
}

trait BigIntDoubleConvertible extends DoubleConvertible[BigInt] {
    override def toDouble(a : BigInt) = a.doubleValue

    override def fromDouble(a : Double) = a.toInt
}

trait ShortDoubleConvertible extends DoubleConvertible[Short] {
    override def toDouble(a : Short) = a : Double

    override def fromDouble(a : Double) = a.toShort
}

trait ByteDoubleConvertible extends DoubleConvertible[Byte] {
    override def toDouble(a : Byte) = a : Double

    override def fromDouble(a : Double) = a.toByte
}

trait LongDoubleConvertible extends DoubleConvertible[Long] {
    override def toDouble(a : Long) = a : Double

    override def fromDouble(a : Double) = a.toLong
}

trait FloatDoubleConvertible extends DoubleConvertible[Float] {
    override def toDouble(a : Float) = a : Double

    override def fromDouble(a : Double) = a.toFloat
}

trait BooleanDoubleConvertible extends DoubleConvertible[Boolean] {
    override def toDouble(a : Boolean) = if (a) 1 else 0

    override def fromDouble(a : Double) = a.equals(1.0D)
}

trait DoubleDoubleConvertible extends DoubleConvertible[Double] {
    override def toDouble(a : Double) = a
    override def fromDouble(a : Double) = a
}

trait JavaDoubleDoubleConvertible extends DoubleConvertible[jl.Double] {
    override def toDouble(a : jl.Double) = a
    override def fromDouble(a : Double) = a
}

trait JavaBooleanDoubleConvertible extends DoubleConvertible[jl.Boolean] {
    override def toDouble(a : jl.Boolean) = if (a) 1 else 0
    override def fromDouble(a : Double) = a.equals(1.0D)
}