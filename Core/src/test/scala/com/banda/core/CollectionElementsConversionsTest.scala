package com.bnd.core

import org.junit.Test
import CollectionElementsConversions._

class CollectionElementsConversionsTest {

    @Test
    def testScalaDoubleListToJavaDoubleList() {
    	val scalaDoubleList = List(2D, 3D, 5D)
    	println(scalaDoubleList)
    	val javaDoubleList : List[java.lang.Double] = scalaDoubleList
    	println(javaDoubleList)
    	assert(scalaDoubleList.size == javaDoubleList.size)
    }
}