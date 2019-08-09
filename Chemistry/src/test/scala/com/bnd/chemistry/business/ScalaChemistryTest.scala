package com.bnd.chemistry.business

import com.bnd.core.test.Spring4Test
import org.springframework.test.context.ContextConfiguration

/**
 * @author © Peter Banda
 * @since 2012  
 */
@ContextConfiguration(Array[String] {"classpath:chemistry-conf.xml"})
abstract class ScalaChemistryTest extends Spring4Test {

}