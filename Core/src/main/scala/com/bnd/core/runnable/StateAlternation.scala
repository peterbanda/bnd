package com.bnd.core.runnable

import java.{lang => jl}

import scala.collection.mutable.{Map => MMap}
import scala.collection.Map
import scala.math.Numeric
import scala.math.Numeric.Implicits._
import scala.collection.JavaConversions._
import com.bnd.core.CollectionElementsConversions._
import java.{lang => jl}

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Publisher
import com.bnd.core.DoubleConvertible.Implicits.addAsDouble
import com.bnd.core.DoubleConvertible
import com.bnd.core.dynamics.ODESolver

class StateAlternation[T, C, H](
	val applyStartTime : BigDecimal,
	val timeLength : BigDecimal,
	val items : Iterable[StateAlternationItem[T, C, H]],
	val cacheWrites : Iterable[StateAlternationWrite[T, C, H]])

class StateAlternationItem[T, C, H](
    val component : C,
    val fun : (Map[C, T], Map[H, T], Option[Double]) => T)

class StateAlternationWrite[T, C, H](
    val variable : H,
    val fun : (Map[C, T], Map[H, T]) => T)

class StateAssignmentAlternation[T, C](
		applyStartTime : BigDecimal,
		timeLength : BigDecimal,
		assignments : Iterable[(C, T)]
	) extends StateAlternation[T, C, Nothing](
	        applyStartTime, timeLength,
	        assignments.map{
	            case (component, newValue) => new StateAlternationItem(component,
	                    { (_ : Map[C, T], _ : Map[_, T], _ : Option[Double]) => newValue })
	        },
	        List())

class StateChangeFunAlternation[T, C](
		applyStartTime : BigDecimal,
		timeLength : BigDecimal,
		changeFuns : Iterable[(C, T => T)]
	) extends StateAlternation[T, C, Nothing](
	        applyStartTime, timeLength,
	        changeFuns.map{
	            case (component, changeFun) => new StateAlternationItem(component,
	                    { (environment : Map[C, T], _ : Map[_, T], _ : Option[Double]) => changeFun(environment.get(component).get)})
	        },
	        List())

class StateConstantChangeFunAlternation[T, C, H](
		applyStartTime : BigDecimal,
		timeLength : BigDecimal,
		changes : Iterable[(C, T)],
		changeFun : (T, T) => T
	) extends StateChangeFunAlternation[T, C](
	        applyStartTime, timeLength,
	        changes.map{
	            case (component, change) => (component, changeFun(change, _ : T))
	        })

object StateAlternationRepeatFirst {

    def newGeneral[T, C, H](
        changeFun : (T, T) => T)(
    	applyStartTime : BigDecimal,
    	timeLength : BigDecimal,
    	items : Iterable[StateAlternationItem[T, C, H]],
    	cacheWrites : Iterable[StateAlternationWrite[T, C, H]]
    ) = {
        var firstEvaluations = MMap[C, T]()
        new StateAlternation[T, C, H](
            applyStartTime, timeLength,
	        items.map{
	            item => new StateAlternationItem(item.component,
	            		{ (environment : Map[C, T], cache : Map[H, T], timeStep : Option[Double]) => {
	            			val changeVal = firstEvaluations.getOrElseUpdate(item.component, item.fun(environment, cache, timeStep))
	            			changeFun(environment.get(item.component).get, changeVal)
	            		}})
            },
            cacheWrites)
    }

    def newReplacement[T, C, H] =
        newGeneral({(_ : T, a : T) => a})(_ : BigDecimal, _ : BigDecimal, _ : Iterable[StateAlternationItem[T, C, H]], _ : Iterable[StateAlternationWrite[T, C, H]])

    def newNumericAddition[T, C, H](implicit num : Numeric[T]) =
        newGeneral(num.plus)(_ : BigDecimal, _ : BigDecimal, _ : Iterable[StateAlternationItem[T, C, H]], _ : Iterable[StateAlternationWrite[T, C, H]])

    def newDoubleAddition[T, C, H](implicit d : DoubleConvertible[T]) =
        newGeneral(addAsDouble(_ : T, _ : T)(d))(_ : BigDecimal, _ : BigDecimal, _ : Iterable[StateAlternationItem[T, C, H]], _ : Iterable[StateAlternationWrite[T, C, H]])
}