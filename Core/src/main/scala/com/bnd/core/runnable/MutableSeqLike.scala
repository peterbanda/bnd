package com.bnd.core.runnable

import java.{util => ju}
import scala.collection.mutable.Cloneable
import scala.collection.{IterableFactory, SeqLike, mutable}
import scala.collection.mutable.Builder
import scala.collection.generic.CanBuildFrom
import scala.jdk.CollectionConverters._

trait MutableSeqLike[A, This] extends SeqLike[A, This] {

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
    override def copy(): JavaListMutableSeqLike[A] = new JavaListMutableSeqLike(new ju.ArrayList[A](underlying))

    override protected def newSpecificBuilder: mutable.Builder[A, JavaListMutableSeqLike[A]] = new JavaListMutableSeqLikeBuilder[A]

    // overrides nothing
 //   override def seq : Seq[A] = underlying.asScala.toSeq
    override def iterator : Iterator[A] = underlying.iterator.asScala

//    override def repr : ju.List[A] = underlying
    // nothing to override
    // override def thisCollection = seq

    // nothing to override
    // override def toCollection(repr: JavaListMutableSeqLike[A]): Seq[A] = repr.underlying : Seq[A]

    override def toIterable: Iterable[A] = underlying.asScala

    override protected def coll: JavaListMutableSeqLike[A] = this

    override protected def fromSpecific(coll: IterableOnce[A]): JavaListMutableSeqLike[A] = {
        val list = new ju.ArrayList[A]()
        coll.iterator.foreach(list.add)
        new JavaListMutableSeqLike(list)
    }

    override def iterableFactory: IterableFactory[collection.Seq] = mutable.Seq
}

class JavaListBuilder[A] extends Builder[A, ju.List[A]] {
    private[this] val b : ju.List[A] = new ju.ArrayList[A]

    // originally +=
    override def addOne(x: A) = { b.add(x); this }
    override def clear = b.clear
    override def result = b
}

class JavaListMutableSeqLikeBuilder[A] extends Builder[A, JavaListMutableSeqLike[A]] {
    private[this] val b : JavaListMutableSeqLike[A] = new JavaListMutableSeqLike[A](new ju.ArrayList[A])

    // originally +=
    override def addOne(x: A) = { b.underlying.add(x); this }
    override def clear = b.underlying.clear
    override def result = b
}

object MutableSeqLike {

    implicit def javaListCanBuildFrom[A] = new CanBuildFrom[ju.List[_], A, ju.List[A]] {
        override def newBuilder(from: ju.List[_]): mutable.Builder[A, ju.List[A]] = this.apply()
        override def fromSpecific(from: ju.List[_])(it: IterableOnce[A]): ju.List[A] = it.toList.asJava
        def apply() = new JavaListBuilder[A]
    }

    implicit def javaListSeqLikeCanBuildFrom[A] = new CanBuildFrom[JavaListMutableSeqLike[_], A, JavaListMutableSeqLike[A]] {
        override def newBuilder(from: JavaListMutableSeqLike[_]): mutable.Builder[A, JavaListMutableSeqLike[A]] = this.apply()
        override def fromSpecific(from: JavaListMutableSeqLike[_])(it: IterableOnce[A]): JavaListMutableSeqLike[A] = new JavaListMutableSeqLike(it.toList.asJava)
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