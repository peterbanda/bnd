package com.bnd.network.business

import java.io.File

import com.bnd.core.util.FileUtil
import org.junit.Test

/**
 * @author © Peter Banda
 * @since 2015
 */
class LearnerResultTest extends NetworkTest {

  val fileUtil = FileUtil.getInstance

  def testChemicalLPResults() = {
    println("CH_LP_AnalogFuns")
    printBestResults("/usr/local/etc/tlabSVN/peter/pics/AASP_v4.4.3/CH_LP", "CH_LP_AnalogFuns_gamma")
    println
  }

  def testNeuralNetworkLPResults() = {
    println("NN_LP_AnalogFuns")
    printBestResults("/usr/local/etc/tlabSVN/peter/pics/AASP_v4.4.3/NN_LP", "NN_LP_AnalogFuns_alpha-lin")
    println
  }

  private def getLastValues(fileName : String) : Option[Array[Double]] =
    if (fileUtil.existFile(fileName))
      Some(fileUtil.readStringFromFileSafe(fileName).split("\n").map{line => line.split(',').last.toDouble})
    else
      None

  private def getFolderMinPairs(folder : String, fileName : String, minAllowedValue : Double = 0) =
    new File(folder).listFiles().filter(!_.getName.startsWith(".svn")).map { folder =>
      val lastValues = getLastValues(folder.getAbsolutePath + "/" + fileName)
      (folder.getName, if (lastValues.isDefined) Some(lastValues.get.filter(_ >= minAllowedValue).min) else None)
    }

  private def getFlatFolderMin(folder : String, fileName : String, minAllowedValue : Double = 0) = {
    val lastValues = getLastValues(folder + "/" + fileName)
    if (lastValues.isDefined) Some(lastValues.get.filter(_ >= minAllowedValue).min) else None
  }

  def testNeuralNetworkLPTimeSeriesResults() = {
    val folder = "/usr/local/etc/tlabSVN/peter/pics/AASP_v4.4.3_with_PDL/sage/neural_net/"
    val funEvals = Seq("LWMA2_RNMSE_NN", "LWMA2_SAMP_NN", "WMM2_RNMSE_NN", "WMM2_SAMP_NN", "NARMA10_RNMSE_NN", "NARMA10_SAMP_NN", "NARMA2_RNMSE_NN", "NARMA2_SAMP_NN")

    println("NN LP")
    println
    funEvals.foreach{funEvalName =>
      println(funEvalName + "\n")
      getFolderMinPairs(folder, funEvalName, if (funEvalName.equals("WMM2_SAMP_NN")) 2d else 0d).sorted.foreach{ case (folder, evalMin) => if (evalMin.isDefined) println(folder + " : " + evalMin.get)}
      println
    }
  }

  @Test
  def testChemicalLPTimeSeriesResults() = {
    val folder = "/usr/local/etc/tlabSVN/peter/pics/AASP_v4.4.3_with_PDL/sage/analytic/"
    val funEvals = Seq("LWMA2_RNMSE_LP", "LWMA2_SAMP_LP", "WMM2_RNMSE_LP", "WMM2_SAMP_LP", "NARMA10_RNMSE_LP", "NARMA10_SAMP_LP", "NARMA2_RNMSE_LP", "NARMA2_SAMP_LP")

    println("CH LP")
    println
    funEvals.foreach{funEvalName =>
      println(funEvalName + "\n")
      getFolderMinPairs(folder, funEvalName).sorted.foreach{ case (folder, evalMin) => if (evalMin.isDefined) println(folder + " : " + evalMin.get)}
      println
    }
  }

  def testLRegressionTimeSeriesResults() = {
    val folder = "/usr/local/etc/tlabSVN/peter/pics/AASP_v4.4.3_with_PDL/sage/lr/"
    val funEvals = Seq("LWMA2_RNMSE_LR", "LWMA2_SAMP_LR", "WMM2_RNMSE_LR", "WMM2_SAMP_LR", "NARMA10_RNMSE_LR", "NARMA10_SAMP_LR", "NARMA2_RNMSE_LR", "NARMA2_SAMP_LR")

    println("L Reg")
    println
    funEvals.foreach{funEvalName =>
      val evalMin = getFlatFolderMin(folder, funEvalName)
      println(funEvalName + ", " + evalMin.get)
    }
  }

  def printBestResults(folderName : String, prefix : String) = {
    val funResultMap = new File(folderName).listFiles().filter(_.getName.startsWith(prefix)).map{ file =>
      val name = file.getName
      fileUtil.readStringFromFileSafe(folderName + "/" + name).split('\n').map{ line =>
        val elems = line.split(',')
        (elems(0), name, elems(1).toDouble, elems(2).toDouble)
      }
    }.flatten.groupBy(_._1)

    val bestResults = funResultMap.keys.toList.sorted.map { funName =>
      val results = funResultMap(funName)
      val rnmseElMin = results.minBy(_._3)
      val sampElMin = results.minBy(_._4)
      (funName, rnmseElMin._3, sampElMin._4, rnmseElMin._2, sampElMin._2)
    }

    // first table
    println
    bestResults.map(result => result._1 + ", " + result._2 + ", " + result._3).foreach(println(_))

    // second table
    println
    bestResults.map(result => result._1 + ", " + result._4 + ", " + result._5).foreach(println(_))
  }
}