package com.bnd.core

import scala.Array._
import scala.collection.JavaConversions._
// import scala.collection.JavaConverters._
import java.{lang => jl, util => ju}

import scala.collection.Iterable
import java.{lang => jl}
import java.{util => ju}

import com.bnd.core.util.ConversionUtil

object CollectionElementsConversions {

  implicit def javaListToJavaArray[T <: AnyRef](list : ju.List[T])(implicit m: ClassManifest[T]) : Array[T] = list.toArray[T](new Array[T](0))

  def arrayToJavaList[T](array : Array[T]) : ju.List[T] = {
    val list = new ju.ArrayList[T]
    for (a <- array) list.add(a)
    list
  }

  implicit def iterableToArray[T : Manifest](
    list : Iterable[T]
  ) : Array[T] = list.toArray[T]

  implicit def iterableMatrixToArrayMatrix[T : Manifest](
    matrix : Iterable[Iterable[T]]
  ) : Array[Array[T]] = (matrix map (_.toArray[T])).toArray[Array[T]]

  implicit def arrayMatrixToIterableMatrix[T](
    matrix : Array[Array[T]]
  ) : Iterable[Iterable[T]] = {
    val a = Predef.refArrayOps(matrix).map(_.toList)
    a
  }

  implicit def javaNumberIterableToJavaNumberIterable[A <: jl.Number, C[X] <: Iterable[X]]
  (list: C[_ <: Number])
    (implicit m: Manifest[A])
  : C[A] = {
    val clazz = ClassUtil.extract[A]
    (list map (ConversionUtil.convert(_, clazz))).asInstanceOf[C[A]]
  }

  implicit def scalaAnyValIterableToJavaNumberIterable[A <: jl.Number, B <: AnyVal, C[X] <: Iterable[X]]
  (list: C[B])
    (implicit m: Manifest[A], elementConvertor : B => jl.Number)
  : C[A] = {
    val clazz = ClassUtil.extract[A]
    (list map (x => ConversionUtil.convert(elementConvertor(x), clazz))).asInstanceOf[C[A]]
  }

  implicit def scalaBooleanIterableToJavaBooleanIterable[C[X] <: Iterable[X]]
  (list: C[Boolean])
  : C[jl.Boolean] = {
    (list map (x => x : jl.Boolean)).asInstanceOf[C[jl.Boolean]]
  }

  implicit def javaBooleanIterableToScalaBooleanIterable[C[X] <: Iterable[X]]
  (list: C[_ <: jl.Boolean])
  : C[Boolean] = {
    (list map (x => x : Boolean)).asInstanceOf[C[Boolean]]
  }

  implicit def scalaDoubleIterableToJavaNumberIterable[C[X] <: Iterable[X]]
  (list: C[_ <: Double])
  : C[jl.Double] = {
    (list map (x => x: jl.Double)).asInstanceOf[C[jl.Double]]
  }

  implicit def javaDoubleIterableToScalaDoubleIterable[C[X] <: Iterable[X]]
  (list: C[_ <: jl.Double])
  : C[Double] = {
    (list map (x => x: Double)).asInstanceOf[C[Double]]
  }

  implicit def scalaIntegerIterableToJavaIntegerIterable[C[X] <: Iterable[X]]
  (list: C[_ <: Int])
  : C[jl.Integer] = {
    (list map (x => x: jl.Integer)).asInstanceOf[C[jl.Integer]]
  }

  //    implicit def scalaDoubleIterableMatrixTojavaDoubleIterableMatrix[C[X] <: Iterable[X], D[Y] <: Iterable[Y]]
  //    	(list: C[D[Double]]) : C[D[jl.Double]] = {
  //        (list map (x => x: D[jl.Double])).asInstanceOf[C[D[jl.Double]]]
  //    }

  implicit def scalaDoubleIterableMatrixTojavaDoubleIterableMatrix[C[X] <: Iterable[X], D[Y] <: Iterable[Y]]
  (list: D[C[_ <: Double]]) : D[C[jl.Double]] = {
    (list map (x => x: C[jl.Double])).asInstanceOf[D[C[jl.Double]]]
  }

  implicit def javaDoubleIterableMatrixToScalaDoubleIterableMatrix[C[X] <: Iterable[X], D[Y] <: Iterable[Y]]
  (list: C[D[jl.Double]]): C[D[Double]] = {
    (list map (x => x: D[Double])).asInstanceOf[C[D[Double]]]
  }

  implicit def javaListMatrixToScalaSeqMatrix[T,C[X] <: Iterable[X]]
  (matrix: C[ju.List[T]]): C[Seq[T]] = {
    (matrix map (x => asScalaBuffer(x): Seq[T])).asInstanceOf[C[Seq[T]]]
  }

  implicit def scalaDoubleConvertibleMatrixToScalaDoubleMatrix[T : DoubleConvertible]
  (list: Iterable[Iterable[T]]): Iterable[Iterable[Double]] = {
    val converter = implicitly[DoubleConvertible[T]]
    (list map (_ map converter.toDouble))
  }

  implicit def scalaDoubleConvertibleIterableToScalaDoubleIteable[T : DoubleConvertible, C[X] <: Iterable[X]]
  (list: C[T]): C[Double] = {
    val converter = implicitly[DoubleConvertible[T]]
    (list map converter.toDouble).asInstanceOf[C[Double]]
  }

  //    implicit def scalaDoubleConvertibleMatrixToScalaDoubleMatrix[T : DoubleConvertible, C[X] <: Iterable[X], D[X] <: Iterable[X]] // ,
  //    	(list: Iterable[Iterable[T]]): Iterable[Iterable[Double]] = {
  //        val converter = implicitly[DoubleConvertible[T]]
  //        (list map (_ map converter.toDouble)).asInstanceOf[Iterable[Iterable[Double]]]
  //    }

  implicit def javaIterableToScalaIterable[T](
    list : jl.Iterable[T]
  ) : Iterable[T] = iterableAsScalaIterable(list)

  implicit def scalaIterableToJavaIterable[T](
    list : Iterable[T]
  ) : jl.Iterable[T] = asJavaIterable(list)

  implicit def javaMatrixIterableToScalaMatrixIterable[T](
    list : jl.Iterable[_ <: jl.Iterable[T]]
  ) : Iterable[Iterable[T]] = iterableAsScalaIterable(list).map(x => iterableAsScalaIterable(x))

  implicit def scalaMatrixIterableToJavaMatrixIterable[T](
    list : Iterable[_ <: Iterable[T]]
  ) : jl.Iterable[jl.Iterable[T]] = asJavaIterable(list.map(x => asJavaIterable(x)))

  // TODO: Move it somewhere else
  def drop[T](i: Int)(list: Iterable[T]) =
    list.zipWithIndex.foldLeft(List[T]()) { ((acc, tuple) =>
      tuple match {
        case (x, pos) =>
          if ((pos + 1) % i == 0) acc else acc ::: List(x)
      })
    }

  def take[T](i: Int)(list: Iterable[T]) =
    list.zipWithIndex.foldLeft(List[T]()) { ((acc, tuple) =>
      tuple match {
        case (x, pos) =>
          if ((pos + 1) % i == 0) acc ::: List(x) else acc
      })
    }

  def dropIndex[T](xs: List[T], n: Int) = {
    val (l1, l2) = xs splitAt n
    l1 ::: (l2 drop 1)
  }

  def extractIndex[T](xs: List[T], n: Int) : (T, List[T]) =
    (xs(n), dropIndex(xs, n))

  def splitWhile[T](pred : T => Boolean)(list: List[T]) : (List[T], List[T]) = {
    list match {
      case Nil => (List.empty[T], List.empty[T])
      case _ =>  if (pred(list.head)) {
        val (x, y) = splitWhile(pred)(list.tail)
        (list.head :: x, y)
      } else (List.empty[T], list)
    }
  }
}