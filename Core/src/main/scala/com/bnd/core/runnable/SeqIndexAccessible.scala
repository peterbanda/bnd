package com.bnd.core.runnable

import java.{util => ju}
import scala.collection.IterableLike
import scala.collection.JavaConversions.asScalaIterator
import scala.collection.JavaConversions.asScalaBuffer
import java.util.Collections
import scala.collection.mutable.WrappedArray

object SeqIndexAccessible {

	trait ExtraImplicits {
		implicit def infixSeqIndexAccessibleOps[S[X], A : Manifest](seq: S[A])(implicit e: SeqIndexAccessible[S]): SeqIndexAccessible[S]#Ops[A] = new e.Ops[A](seq)
	}

	object Implicits extends ExtraImplicits { }

  implicit object JavaListSeqIndexAccessible extends JavaListSeqIndexAccessible with Serializable
  implicit object ArraySeqIndexAccessible extends ArraySeqIndexAccessible with Serializable

  trait JavaListSeqIndexAccessible extends SeqIndexAccessible[ju.List] {

		override def update[A](seq : ju.List[A], idx: Int, elem: A) : Unit = seq.set(idx, elem)
		override def apply[A : Manifest](seq : ju.List[A], idxes : Iterable[Int]) = {
			val list = new ju.ArrayList[A]
			idxes.foreach(index => list.add(seq.get(index)))
			list
		}

		override def apply[A](seq : ju.List[A], index : Int)  = seq.get(index)
		override def copy[A](seq : ju.List[A]) : ju.List[A] = new ju.ArrayList[A](seq)
		override def iterator[A](seq : ju.List[A]) = asScalaIterator(seq.iterator)
		override def toSeq[A](seq : ju.List[A]) = asScalaBuffer(seq)
		override def size[A](seq : ju.List[A]) = seq.size

		//    	override def fill[A : Manifest](num : Int, elem : A) = new ju.ArrayList[A](Collections.nCopies(num, elem))
		override def fill[A : Manifest](num : Int, elem : A) = {
			val list = new ju.ArrayList[A](num)
			while (list.size() < num) list.add(elem)
			list
		}

		override def empty[A : Manifest] = new ju.ArrayList[A]
	}

  trait ArraySeqIndexAccessible extends SeqIndexAccessible[Array] {

		override def update[A](seq : Array[A], idx: Int, elem: A) : Unit = seq.update(idx, elem)
		override def apply[A : Manifest](seq : Array[A], idxes : Iterable[Int]) = {
			val array = Array.ofDim[A](idxes.size)
			idxes.zipWithIndex.foreach{ case (index, i) => array.update(i, seq(index))}
			array
		}

		override def apply[A](seq : Array[A], idx : Int)  = seq(idx)
		override def copy[A](seq : Array[A]) : Array[A] = seq.clone
		override def iterator[A](seq : Array[A]) = new ArrayIterator[A] (seq)
		override def toSeq[A](seq : Array[A]) = seq
		override def size[A](seq : Array[A]) = seq.length

		final class ArrayIterator[A](underlying : Array[A]) extends Iterator[A] {
			var index = -1
			var maxIndex = underlying.length - 1

			override def hasNext = index < maxIndex
			override def next = {index = index + 1; underlying(index)}
		}

		override def fill[A : Manifest](num : Int, elem : A) = Array.fill(num)(elem)
		override def empty[A : Manifest] = Array.empty[A]
	}
}

trait SeqIndexAccessible[S[X]] extends Copyable[S] {

	def update[A](seq : S[A], idx: Int, elem: A) : Unit
	def apply[A : Manifest](seq : S[A], idxes : Iterable[Int]) : S[A]
	def apply[A](seq : S[A], idx : Int) : A
	def iterator[A](seq : S[A]) : Iterator[A]
	def toSeq[A](seq : S[A]) : Seq[A]
	def size[A](seq : S[A]) : Int

	def fill[A : Manifest](num : Int, elem : A) : S[A]
	def empty[A : Manifest] : S[A]

	final class Ops[A : Manifest](seq : S[A]) {
		def update(idx: Int, elem: A) = SeqIndexAccessible.this.update(seq, idx, elem)
		def apply(idxes : Iterable[Int]) = SeqIndexAccessible.this.apply(seq, idxes)
		def apply(idx : Int) = SeqIndexAccessible.this.apply(seq, idx)
		def copy() = SeqIndexAccessible.this.copy(seq)
		def toSeq() = SeqIndexAccessible.this.toSeq(seq)
		def size() = SeqIndexAccessible.this.size(seq)
		def iterator() = SeqIndexAccessible.this.iterator(seq)
	}

	implicit def mkSeqIndexAccessibleOps[A : Manifest](seq: S[A]): Ops[A] = new Ops[A](seq)
}

trait Copyable[S[X]] {
	def copy[A](seq : S[A]) : S[A]
}