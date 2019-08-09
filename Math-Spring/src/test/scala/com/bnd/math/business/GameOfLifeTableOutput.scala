package com.bnd.math.business

import org.junit.Test
import java.{util => ju}
import scala.collection.JavaConversions._
import org.junit.Assert._
import org.springframework.beans.factory.annotation.Autowired
import com.bnd.function.enumerator.ListEnumeratorFactory
import org.apache.commons.lang.StringUtils

class GameOfLifeTableOutput extends MathTest {

    @Autowired
    val listEnumeratorFactory : ListEnumeratorFactory = null

//    private def gameOfLifeFun(inputs : Seq[Boolean]) : Boolean = {
//        val activeCell = inputs.head
//        val activeNeighborsCount = inputs.tail.count(_ == true)
//
//        if (activeCell)
//        	activeNeighborsCount == 2 || activeNeighborsCount == 3            
//        else
//            activeNeighborsCount == 3
//    }

    private def gameOfLifeFun(inputs : Seq[Boolean]) : Boolean = {
        val parts = inputs.splitAt(4)
        val inputsWoCell = parts._1 ++ parts._2.tail
        val activeCell = parts._2.head
        val activeNeighborsCount = inputsWoCell.count(_ == true)

        if (activeCell)
        	activeNeighborsCount == 2 || activeNeighborsCount == 3            
        else
            activeNeighborsCount == 3
    }

    @Test
    def tableOutput {
        // create input enumerator
		val inputEnumerator = listEnumeratorFactory.createInstance(true, false, true)

	    // enumerate inputs
		val inputs = inputEnumerator.enumerate(9)

		val outputs = inputs.map(input => if(gameOfLifeFun(input)) 1 else 0)
		println(StringUtils.join(outputs, ','))
    }
}