package com.bnd.function.enumerator

import java.{lang => jl, util => ju}

class ScalaRangeListEnumerator[T](
    private val allowRepetitions: Boolean,
    private val rangeFrom: T,
    private val rangeTo: T,
    ++ : T => T)
    extends RangeListEnumerator[T](allowRepetitions, rangeFrom, rangeTo) {

    override protected def inc(element: T): T = ++(element) 
}

class ScalaVariousRangeListEnumerator[T](
    private val allowRepetitions: Boolean,
    private val rangeFrom: ju.List[T],
    private val rangeTo: ju.List[T],
    ++ : T => T)
    extends VariousRangeListEnumerator[T](allowRepetitions) {

    override protected def inc(element: T): T = ++(element)

    override protected def getRangeFrom(i: Int) = rangeFrom.get(i)

    override protected def getRangeTo(i: Int) = rangeTo.get(i)
}