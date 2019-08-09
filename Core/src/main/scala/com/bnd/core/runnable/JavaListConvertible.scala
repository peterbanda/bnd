package com.bnd.core.runnable

import java.{util => ju}
import scala.collection.JavaConversions._
import com.bnd.core.CollectionElementsConversions._

object JavaListConvertible {
  implicit object JavaListJavaListConvertible extends JavaListJavaListConvertible with Serializable
  implicit object ArrayJavaListConvertible extends ArrayJavaListConvertible with Serializable
}

trait JavaListConvertible[S[X]] {
  def toJavaList[T](a : S[T]) : ju.List[T]

  def fromJavaList[T : Manifest](a : ju.List[T]) : S[T]

  class FromJavaListOp[T : Manifest](a : ju.List[T]) {
    def fromJavaList : S[T] = JavaListConvertible.this.fromJavaList(a)
  }

  implicit def mkFromDoubleOp[T : Manifest](a : ju.List[T]) : FromJavaListOp[T] = new FromJavaListOp(a)
}

trait JavaListJavaListConvertible extends JavaListConvertible[ju.List] {
  override def toJavaList[T](a : ju.List[T]) = a
  override def fromJavaList[T : Manifest](a : ju.List[T]) = a
}

trait ArrayJavaListConvertible extends JavaListConvertible[Array] {
  override def toJavaList[T](a : Array[T]) = arrayToJavaList(a)
  override def fromJavaList[T  : Manifest](a : ju.List[T]) = (a : Seq[T]).toArray
}