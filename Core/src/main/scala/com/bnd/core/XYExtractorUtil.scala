package com.bnd.core

import com.bnd.core.CollectionElementsConversions._
import scala.collection.JavaConverters._
import com.bnd.core.DoubleConvertible.Implicits.toDouble
import scala.collection.mutable.ListBuffer

object XYExtractorUtil {

	def compressSlots[T : DoubleConvertible, O](
		slotSize : Double,
		data : Traversable[T],
		compress :  (Double, Traversable[T]) => O,
		explMinX : Option[T] = None,
		explMaxX : Option[T] = None
	): Seq[(Double,O)] = compressXYSlots(slotSize, data, compress, explMinX, explMaxX)

	def compressXYTupleSlots[X : DoubleConvertible, Y, O](
		slotSize : Double,
		xyData : Traversable[(X,Y)],
		compress : (Double, Traversable[Y]) => O,
		explMinX : Option[X] = None,
		explMaxX : Option[X] = None
	): Seq[(Double,O)] = compressXYSlots(slotSize, xyData, compress, explMinX, explMaxX)

	def compressXYIterableSlots[T : DoubleConvertible, O](
		slotSize : Double,
		xyData : Traversable[Traversable[T]],
		compress : (Double, Traversable[T]) => O,
		explMinX : Option[T] = None,
		explMaxX : Option[T] = None
	): Seq[(Double, O)] = compressXYSlots(slotSize, xyData, compress, explMinX, explMaxX)

	private def compressXYSlots[T, X : DoubleConvertible, Y, O](
		slotSize: Double,
		xyData: Traversable[T],
		compress: (Double, Traversable[Y]) => O,
		explMinX: Option[X] = None,
		explMaxX: Option[X] = None
	)(implicit xyExtractor : XYExtractable[T,X,Y]): Seq[(Double,O)] = {
		def x(t : T) = xyExtractor.x(t)
		def y(t : T) = xyExtractor.y(t)

		val doubleXYData = xyData.map(t => (x(t) : Double, y(t)))

		val minX = if (explMinX.isEmpty) doubleXYData.minBy(_._1)._1 else explMinX.get : Double
		val maxX = if (explMaxX.isEmpty) doubleXYData.maxBy(_._1)._1 else explMaxX.get : Double
		val s = Math.ceil((maxX - minX) / slotSize).toInt
		val size = if (!explMaxX.isEmpty) s else s + 1

		var slots = ListBuffer.fill(size)(List.empty[Y])
		for ((x,y) <- doubleXYData) {
			val index = Math.floor((x - minX) / slotSize).toInt
			slots.update(index, slots(index) :+ y)
		}
		slots.zipWithIndex.map { case (slot,index) =>
			val slotValue = minX + (index * slotSize)
			(slotValue, compress(slotValue, slot))
		}
	}
}