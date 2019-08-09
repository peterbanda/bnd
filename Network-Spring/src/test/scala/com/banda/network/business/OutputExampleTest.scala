package com.bnd.network.business

import org.junit.Test

/**
 * Created by peter on 3/19/15.
 */
class OutputExampleTest extends NetworkTest {

  def testSASPOutput() = {
    val saspOutput = Seq(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,1,1,0)
    val xStart = 24.45
    val xDelta = 3.09
    val output = saspOutput.zipWithIndex.map{ case (output, index) => "\\put(" + roundTwoDec(xStart + xDelta * index) + ",120){{\\tiny " + output + "}}"}.mkString("\n")
    println(output)
    println
  }

  @Test
  def testTaspOutput() = {
    val taspOutput = Seq(0,0,0,1,0,1,0,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,0,1,1,1,1,0,1,1,1,0)
    val xStart = 24.45
    val xDelta = 3.31
    println(outputString(taspOutput, xStart, xDelta))
  }

  private def roundTwoDec(x : Double) = math.round(100 * x).toDouble / 100

  private def outputString(output : Seq[Int], xStart : Double, xDelta : Double) =
    output.zipWithIndex.map{ case (output, index) => "\\put(" + roundTwoDec(xStart + xDelta * index) + ",120){{\\tiny " + output + "}}"}.mkString("\n")
}
