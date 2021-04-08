package com.bnd.network.business.integrator

import scala.collection.JavaConversions.seqAsJavaList

import org.junit.Test

import com.bnd.network.domain.StatesWeightsIntegratorType

import MetaStatesWeightsIntegratorFactory.createBooleanInstance
import MetaStatesWeightsIntegratorFactory.createNumericInstance

class StatesWeightsIntegratorTest {

    @Test
    def testIntegrate() {
        val integratorFactory = createNumericInstance[Double]
        val integrator = integratorFactory(StatesWeightsIntegratorType.LinearSum)
        val a = List(1.1d, 2.3d, 5.8d)
        val b = List(1.9d, 10.8d, 3.2d)
        val x = integrator(a,b)
        println(x)
    }

    @Test
    def testDoubleFactory() {
        val integratorFactory = createNumericInstance[Double]
        val integrator = integratorFactory(StatesWeightsIntegratorType.QuadSum)
        val a = List(1.1d, 2.3d, 5.8d)
        val b = List(1.9d, 10.8d, 3.2d)
        val x = integrator(a,b)
        println(x)
    }

    @Test
    def testIntFactory() {
        val integratorFactory = createNumericInstance[Int]
        val integrator = integratorFactory(StatesWeightsIntegratorType.LinearSum)
        val a = List(1, 3, 5)
        val b = List(2, 10, 6)
        val x = integrator(a,b)
        println(x)
        assert(x == 62)
    }

    @Test
    def testBooleanFactory() {
        val integratorFactory = createBooleanInstance
        val integrator = integratorFactory(StatesWeightsIntegratorType.LinearSum)
        val a = List(true, false,  false)
        val b = List(true, true, false)
        val x = integrator(a,b)
        println(x)
        assert(x)
    }    
}