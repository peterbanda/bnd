package com.bnd.core.runnable

import com.bnd.core.runnable.SeqIndexAccessible.Implicits._

import scala.collection.mutable.{ListBuffer, Publisher, Subscriber, Map => MMap}

private class StateInterpreter[T : Manifest, C, H, S[X] : SeqIndexAccessible](
	runnable : TimeRunnable,
	publisher : Publisher[StateEvent[T, S]],
	variablesWithDefaultValue : Option[(Iterable[H], T)],
	private var interpretations : Stream[StateInterpretation[T, C, H, _]]
) extends TimeRunnable with Subscriber[StateEvent[T, S], Publisher[StateEvent[T, S]]] with Publisher[StateInterpretationEvent[T, C, H]] {

	publisher.subscribe(this)

	private val variableEnvironment = {
		val environment = MMap[H, T]()
		if (variablesWithDefaultValue.isDefined) {
			val defaultValue = variablesWithDefaultValue.get._2
			variablesWithDefaultValue.get._1.foreach(environment.update(_, defaultValue))
		}
		environment
	}

	private var componentIndeces : Option[Map[C, Int]] = None
	private var collect = false
	private var activeInterpretation : StateInterpretation[T, C, H, _] = _
	private var collectedStates : Map[C, ListBuffer[T]] = _

	def notify(pub: Publisher[StateEvent[T, S]], event: StateEvent[T, S]) =
		if (collect)
			event match {
				case StateUpdatedEvent(time : BigDecimal, components_ : Iterable[C], state : S[T]) =>
					if (!componentIndeces.isDefined) componentIndeces = Some(components_.zipWithIndex.toMap)
					if (collectedStates.isEmpty) collectedStates = componentIndeces.get.map(t => (t._1, ListBuffer[T]()))
					for (item <- activeInterpretation.items.asInstanceOf[Iterable[StateInterpretationItem[T, C, H]]]; component <- item.components) {
						val index = componentIndeces.get.get(component).get
						collectedStates.get(component).get += state(index)
					}

				case _ =>
			}

	private def interpretCollectedStates = if (collectedStates.nonEmpty) {
		val newItemValues = for (item <- activeInterpretation.items.asInstanceOf[Iterable[StateInterpretationItem[T, C, H]]]) yield
			(item, item match {
				case i : RangeStateInterpretationItem[T, C, H] => i.fun(collectedStates, variableEnvironment)
				case i : PointStateInterpretationItem[T, C, H] => i.fun(collectedStates.filter{ case (a,b) => b.nonEmpty}.map{
					case (a,b) => (a, b.head)}, variableEnvironment)
			})

		val items = newItemValues.map{ case (item, value) =>
			variableEnvironment.update(item.variable, value)
			new StateInterpretationResultItem(item, value)
		}

		publish(new StateInterpretationEvent(new StateInterpretationResult(activeInterpretation, items)))
	}

	override def runFor(timeDiff : BigDecimal) = runUntil(timeDiff + currentTime)

	override def runUntil(finalTime : BigDecimal) = {
		var applicableInterpretations = interpretations.takeWhile(_.startTime < finalTime).toList

		interpretations = interpretations.dropWhile(_.startTime < finalTime)

		applicableInterpretations.foreach{ applicableInterpretation => {
			collect = false
			if (applicableInterpretation.startTime > currentTime)
				runnable.runUntil(applicableInterpretation.startTime)
			activeInterpretation = applicableInterpretation
			collectedStates = Map[C, ListBuffer[T]]()
			collect = true
			runnable.runFor(applicableInterpretation.timeLength.min(finalTime - currentTime))
			interpretCollectedStates
		}}
		runnable.runFor(0d)
	}

	override def nextTimeStepSize = runnable.nextTimeStepSize

	override def currentTime = runnable.currentTime
}

object StateInterpreter {

	def apply[T : Manifest, C, H, S[X] : SeqIndexAccessible](
		runnableWithPublisher : TimeRunnable with Publisher[StateEvent[T, S]],
		interpretations : Stream[StateInterpretation[T, C, H, _]],
		variablesWithDefaultValue : Option[(Iterable[H], T)]
	) : TimeRunnable with Publisher[StateInterpretationEvent[T, C, H]] = apply(runnableWithPublisher, runnableWithPublisher, interpretations, variablesWithDefaultValue)

	def apply[T : Manifest, C, H, S[X] : SeqIndexAccessible](
		runnable : TimeRunnable,
		publisher : Publisher[StateEvent[T, S]],
		interpretations : Stream[StateInterpretation[T, C, H, _]],
		variablesWithDefaultValue : Option[(Iterable[H], T)]
	) : TimeRunnable with Publisher[StateInterpretationEvent[T, C, H]] = new StateInterpreter(runnable, publisher, variablesWithDefaultValue, interpretations)
}