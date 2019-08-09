package com.bnd.math.business.learning

class IOStream[T](
    val inputStream : Stream[Seq[T]],
    val outputStream : Stream[Seq[T]],
    val outputShift : Int
) {
    def inputDim = inputStream.head.size
    def outputDim = outputStream.head.size

    def transformStream(inputFun : T => T, outputFun : T => T) = new IOStream[T](
        inputStream.map( _.map(inputFun)),
        outputStream.map( _.map(outputFun)),
        outputShift
    )
}