package com.bnd.core

trait Multipliable[T] {
    def * (b: T): T
}

object MultipliableConversions {
    implicit def numeric2Multipliable[T](a : T)(implicit num: Numeric[T]): Multipliable[T] = 
        new Multipliable[T]{def *(b: T) = num.times(a, b)}

    implicit def double2Multipliable(a : Double): Multipliable[Double] =
    	new Multipliable[Double]{def *(b: Double) = a * b}
}