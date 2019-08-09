package com.bnd.math.business.dynamics

import java.{util=>ju}
import scala.collection.JavaConversions._
import com.bnd.core.CollectionElementsConversions._
import scala.math.Numeric.Implicits._
import scala.math._
import com.bnd.core.DoubleConvertible.Implicits
import com.bnd.core._

abstract class PerturbationAnalysis[T : Numeric](val perturbationStrength : T) {

    protected def perturbate(
        referencePoint : Seq[T],
        perturbVectors : Iterable[Seq[T]]) : Iterable[Seq[T]] = {
    	for (perturbVector <- perturbVectors) yield
                    (perturbVector,referencePoint).zipped.map(perturbationStrength * _ + _)
    }

    protected def perturbate(
        referencePoint : Seq[T],
        perturbVector : Seq[T]) : Seq[T] = {
        (perturbVector,referencePoint).zipped.map(perturbationStrength * _ + _)
    }

    protected def diff(
        referencePoint : Seq[T],
        otherPoints : Iterable[Seq[T]]) : Iterable[Seq[T]] = {
        for (otherPoint <- otherPoints) yield
                    (otherPoint,referencePoint).zipped.map(_-_)
    }

    protected def diff[C <: Iterable[T]](
        referencePoint : C,
        otherPoint : C) : C = {
        (otherPoint,referencePoint).zipped.map(_-_).asInstanceOf[C]
    }

    protected def add[C <: Iterable[T]](
        referencePoint : C,
        otherPoint : C) : C = {
        (otherPoint,referencePoint).zipped.map(_+_).asInstanceOf[C]
    }
}