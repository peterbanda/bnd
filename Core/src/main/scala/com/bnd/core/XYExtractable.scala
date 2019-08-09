package com.bnd.core

object XYExtractable {
    implicit def singleValueToXYExtractable[T] = new SingleValueXYExtractable[T]
    implicit def tupleToXYExtractable[X,Y] = new TupleXYExtractable[X,Y]
    implicit def traversableToXYExtractable[T] = new TraversableXYExtractable[T]
}

trait XYExtractable[T,X,Y] {
    def x(a : T) : X

    def y(a : T) : Y
}

class SingleValueXYExtractable[T] extends XYExtractable[T,T,T] {
    override def x(a : T) = a

    override def y(a : T) = a
}

class TupleXYExtractable[X,Y] extends XYExtractable[(X,Y),X,Y] {
    override def x(a : (X,Y)) = a._1

    override def y(a : (X,Y)) = a._2
}

class TraversableXYExtractable[T] extends XYExtractable[Traversable[T],T,T] {
    override def x(a : Traversable[T]) = a.head

    override def y(a : Traversable[T]) = a.tail.head
}