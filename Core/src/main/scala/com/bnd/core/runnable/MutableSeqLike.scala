package com.bnd.core.runnable

import java.{util => ju}
import scala.collection.mutable.Cloneable
import scala.collection.SeqLike
import scala.collection.mutable.Builder
import scala.collection.generic.CanBuildFrom
import scala.collection.JavaConversions._

trait MutableSeqLike[A, +This] extends SeqLike[A, This] {

    /**
     * Replaces element at given index with a new value.
     *
     *  @param idx      the index of the element to replace.
     *  @param elem     the new value.
     *  @throws   IndexOutOfBoundsException if the index is not valid.
     */
    def update(idx: Int, elem: A)

    def copy() : This

    def apply(is: Iterable[Int]) : This
}

class JavaListMutableSeqLike[A](val underlying : ju.List[A]) extends MutableSeqLike[A, JavaListMutableSeqLike[A]] {

    override def length = underlying.size
    override def isEmpty = underlying.isEmpty
    override def apply(i: Int) = underlying.get(i)
    override def apply(is: Iterable[Int]) = {
        val list = new ju.ArrayList[A]
        is.foreach(index => list.add(apply(index)))
        new JavaListMutableSeqLike[A](list)
    }
    override def update(i: Int, elem: A) = underlying.set(i, elem)
    override def copy() : Self = (new JavaListMutableSeqLike(new ju.ArrayList[A](underlying))).asInstanceOf[Self]
    override def newBuilder = new JavaListMutableSeqLikeBuilder[A]
    override def seq : Seq[A] = underlying : Seq[A]
    override def iterator : Iterator[A] = asScalaIterator(underlying.iterator)

//    override def repr : ju.List[A] = underlying
    override def thisCollection = seq
    override def toCollection(repr: JavaListMutableSeqLike[A]): Seq[A] = repr.underlying : Seq[A]
}

class JavaListBuilder[A] extends Builder[A, ju.List[A]] {
    private[this] val b : ju.List[A] = new ju.ArrayList[A]

    override def += (x: A) = { b.add(x); this }
    override def clear = b.clear
    override def result = b
}

class JavaListMutableSeqLikeBuilder[A] extends Builder[A, JavaListMutableSeqLike[A]] {
    private[this] val b : JavaListMutableSeqLike[A] = new JavaListMutableSeqLike[A](new ju.ArrayList[A])

    override def += (x: A) = { b.underlying.add(x); this }
    override def clear = b.underlying.clear
    override def result = b
}

object MutableSeqLike {

    implicit def javaListCanBuildFrom[A] = new CanBuildFrom[ju.List[_], A, ju.List[A]] {
        def apply(from: ju.List[_]) = this.apply()
        def apply() = new JavaListBuilder[A]
    }

    implicit def javaListSeqLikeCanBuildFrom[A] = new CanBuildFrom[JavaListMutableSeqLike[_], A, JavaListMutableSeqLike[A]] {
        def apply(from: JavaListMutableSeqLike[_]) = this.apply()
        def apply() = new JavaListMutableSeqLikeBuilder[A]
    }

    implicit def toSeqLike[A] = new JavaListMutableSeqLike[A](_ : ju.List[A])

//    implicit def canBuildFrom[T] : CanBuildFrom[MutableSeqLike[_, _], T, MutableSeqLike[T, ju.List[T]]] =
//    	new CanBuildFrom[MutableSeqLike[_, _], T, MutableSeqLike[T, ju.List[T]]] {
//
//    		def apply(from: MutableSeqLike[_, _]): Builder[T, MutableSeqLike[T, ju.List[T]]] =
//    				ArrayBuilder.make[T]()(m) mapResult WrappedArray.make[T]
//
//    		def apply: Builder[T, WrappedArray[T]] =
//    		ArrayBuilder.make[T]()(m) mapResult WrappedArray.make[T]
  }