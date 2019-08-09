package com.bnd.network.business

import com.bnd.core.test.Spring4Test
import org.junit.runner.RunWith
import org.springframework.test.context.ContextConfiguration
import junit.framework.TestCase
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import java.util.Collection
import junit.framework.TestCase._

/**
 * @author © Peter Banda
 * @since 2012  
 */
@ContextConfiguration(Array[String] {"classpath:network-conf.xml"})
 // @RunWith(classOf[SpringJUnit4ClassRunner])
abstract class ScalaNetworkTest extends Spring4Test {
}