package com.bnd.chemistry.business

import java.{lang => jl, util => ju}

import com.bnd.chemistry.business.MatrixMultiplicationTest._
import com.bnd.core.util.ConversionUtil
import com.bnd.core.util.{ConversionUtil, RandomUtil}
import org.jblas.DoubleMatrix
import org.junit.runners.MethodSorters
import org.junit.{BeforeClass, FixMethodOrder, Test}

object MatrixMultiplicationTest{

    val matrixNum = 400000
    val zeroColumnsNum = 3
    val n = 10
    val m = 15
    var matrices : Iterable[DoubleMatrix] = _
    var matricesWithZerosAndOnes : Iterable[DoubleMatrix] = _
    var matricesWithZeroColumn : Iterable[DoubleMatrix] = _
    var vectors : Iterable[DoubleMatrix] = _

    @BeforeClass
	def initialize {
//        matrices = for (_ <- 1 to matrixNum) yield {
//        	val matrix = for (_ <- 1 to n) yield 
//        		(for (_ <- 1 to m) yield RandomUtil.nextDouble : jl.Double).toArray
//        	new DoubleMatrix(ConversionUtil.toSimpleType(matrix.toArray))
//        }

//        matricesWithZerosAndOnes = for (_ <- 1 to matrixNum) yield {
//        	val matrix = for (_ <- 1 to n) yield 
//        		(for (_ <- 1 to m) yield if (RandomUtil.nextBoolean) 1 : jl.Double else 0 :jl.Double).toArray
//        	new DoubleMatrix(ConversionUtil.toSimpleType(matrix.toArray))
//        }

        matricesWithZeroColumn = for (_ <- 1 to matrixNum) yield {
        	val matrix = for (i <- 1 to n) yield {
        	    if (i >= zeroColumnsNum)
        	    	(for (_ <- 1 to m) yield if (RandomUtil.nextBoolean) 1 : jl.Double else 0 :jl.Double).toArray
        	    else
        	        Array.fill(m)(0d :jl.Double)
        	}
        	new DoubleMatrix(ConversionUtil.toSimpleType(matrix.toArray))
        }

        vectors = for (_ <- 1 to matrixNum) yield {
        	val vector = (for (_ <- 1 to m) yield RandomUtil.nextDouble : jl.Double).toArray
        	val vectorMatrix =  Array[Array[jl.Double]](vector)
        	new DoubleMatrix(ConversionUtil.toSimpleType(vectorMatrix))
        }
    }
}

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class MatrixMultiplicationTest extends ScalaChemistryTest {

    @Test
    def test1 {

    }

//    @Test
//    def test2MatrixMul {
//        println(matrices.size, " ", vectors.size) 
//        (matrices, vectors).zipped.foreach(_.mmul(_))
//    }

//    @Test
//    def test3ZeroesOnesMatrixMul {
//        println(matricesWithZerosAndOnes.size, " ", vectors.size) 
//        (matricesWithZerosAndOnes, vectors).zipped.foreach(_.mmul(_))
//    }

    @Test
    def test4ZeroColumnMatrixMul {
        println(matricesWithZeroColumn.size, " ", vectors.size) 
        (matricesWithZeroColumn, vectors).zipped.foreach(_.mmul(_))
    }
}