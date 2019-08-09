package com.bnd.math.business.dynamics

class FullDynamicsAnalysisSpec[T](
    val timeStepLength: Double,
    val iterations: Int,
    val lyapunovPerturbationStrength: T,
    val derridaPerturbationStrength: T,
    val derridaTimeLength: Double,
    val vectorSpace: VectorSpace[T],
    val random: (T, T) => T,
    val timeStepToFilter: Int,
    val fixedPointDetectionPrecision: Double,
    val lowerBound: T,
    val upperBound: T,
    val stationaryPointDetectionPrecision: Double)