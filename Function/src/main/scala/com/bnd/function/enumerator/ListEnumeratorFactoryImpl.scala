package com.bnd.function.enumerator;

import java.{lang => jl, util => ju}

import com.bnd.core.NumericConversions
import java.io.Serializable

import com.bnd.core.BndRuntimeException

/**
 * @author © Peter Banda
 * @since 2013
 */
final private[bnd] class ListEnumeratorFactoryImpl extends ListEnumeratorFactory with Serializable {

    implicit def liftAnyValToNumeric[T <: AnyVal](implicit m: Manifest[T]) = NumericConversions.liftAnyValToNumeric[T]

    override def createInstance[T](
        allowRepetitions: jl.Boolean,
        rangeFrom: T,
        rangeTo: T
    ) : ListEnumerator[T] =

        rangeFrom match {
            case _:jl.Boolean => new ScalaRangeListEnumerator[jl.Boolean](
                allowRepetitions, rangeFrom.asInstanceOf[jl.Boolean], rangeTo.asInstanceOf[jl.Boolean], (x => !x)).asInstanceOf[ListEnumerator[T]]

            case _:jl.Integer => new ScalaRangeListEnumerator[jl.Integer](
                allowRepetitions, rangeFrom.asInstanceOf[jl.Integer], rangeTo.asInstanceOf[jl.Integer], (_ + 1)).asInstanceOf[ListEnumerator[T]]

            case _:jl.Byte => new ScalaRangeListEnumerator[jl.Byte](
                allowRepetitions, rangeFrom.asInstanceOf[jl.Byte], rangeTo.asInstanceOf[jl.Byte], (x => (x + 1).byteValue())).asInstanceOf[ListEnumerator[T]]

            case _:jl.Long => new ScalaRangeListEnumerator[jl.Long](
                allowRepetitions, rangeFrom.asInstanceOf[jl.Long], rangeTo.asInstanceOf[jl.Long], (_ + new jl.Long(1))).asInstanceOf[ListEnumerator[T]]

            case _:Boolean => new ScalaRangeListEnumerator[Boolean](
                allowRepetitions, rangeFrom.asInstanceOf[Boolean], rangeTo.asInstanceOf[Boolean], (x => !x)).asInstanceOf[ListEnumerator[T]]

            case x if x.getClass().isAssignableFrom(classOf[AnyVal]) => {
                createNumericInstance[AnyVal](allowRepetitions, rangeFrom.asInstanceOf[AnyVal], rangeTo.asInstanceOf[AnyVal]).asInstanceOf[ListEnumerator[T]]
            }

            case _ => throw new BndRuntimeException(rangeFrom.getClass().getSimpleName() + " is not valid list enumerator type!") 
        }

    override def createInstance[T](
        allowRepetitions: jl.Boolean,
        rangeFrom: ju.List[T],
        rangeTo: ju.List[T]
    ) : ListEnumerator[T] =

        rangeFrom.get(0) match {
            case _:jl.Boolean => new ScalaVariousRangeListEnumerator[jl.Boolean](
                allowRepetitions, rangeFrom.asInstanceOf[ju.List[jl.Boolean]], rangeTo.asInstanceOf[ju.List[jl.Boolean]], (x => !x)).asInstanceOf[ListEnumerator[T]]

            case _:jl.Integer => new ScalaVariousRangeListEnumerator[jl.Integer](
                allowRepetitions, rangeFrom.asInstanceOf[ju.List[jl.Integer]], rangeTo.asInstanceOf[ju.List[jl.Integer]], (_ + 1)).asInstanceOf[ListEnumerator[T]]

            case _:jl.Byte => new ScalaVariousRangeListEnumerator[jl.Byte](
                allowRepetitions, rangeFrom.asInstanceOf[ju.List[jl.Byte]], rangeTo.asInstanceOf[ju.List[jl.Byte]], (x => (x + 1).byteValue())).asInstanceOf[ListEnumerator[T]]

            case _:jl.Long => new ScalaVariousRangeListEnumerator[jl.Long](
                allowRepetitions, rangeFrom.asInstanceOf[ju.List[jl.Long]], rangeTo.asInstanceOf[ju.List[jl.Long]], (_ + new jl.Long(1))).asInstanceOf[ListEnumerator[T]]

            case _:Boolean => new ScalaVariousRangeListEnumerator[Boolean](
                allowRepetitions, rangeFrom.asInstanceOf[ju.List[Boolean]], rangeTo.asInstanceOf[ju.List[Boolean]], (x => !x)).asInstanceOf[ListEnumerator[T]]

            case x if x.getClass().isAssignableFrom(classOf[AnyVal]) => {
                createNumericInstance[AnyVal](allowRepetitions, rangeFrom.asInstanceOf[ju.List[AnyVal]], rangeTo.asInstanceOf[ju.List[AnyVal]]).asInstanceOf[ListEnumerator[T]]
            }

            case _ => throw new BndRuntimeException(rangeFrom.getClass().getSimpleName() + " is not valid list enumerator type!") 
        }

    override def createInstance[T](
       allowRepetitions: jl.Boolean,
       values : java.util.List[T]
    ) : ListEnumerator[T] = {
       val indexEnumerator = createInstance[Integer](allowRepetitions, 0, values.size - 1)
       new EnumListEnumerator[T](indexEnumerator, values)
    }

    private def createNumericInstance[T : Numeric](
    	allowRepetitions: jl.Boolean,
    	rangeFrom: T,
    	rangeTo: T
    ) : ListEnumerator[T] = {
    	val num = implicitly[Numeric[T]]
        new ScalaRangeListEnumerator[T](allowRepetitions, rangeFrom, rangeTo, num.plus(_,num.one))
    }

    private def createNumericInstance[T : Numeric](
    	allowRepetitions: jl.Boolean,
    	rangeFrom: ju.List[T],
    	rangeTo: ju.List[T]
    ) : ListEnumerator[T] = {
    	val num = implicitly[Numeric[T]]
        new ScalaVariousRangeListEnumerator[T](allowRepetitions, rangeFrom, rangeTo, num.plus(_,num.one))
    }
}