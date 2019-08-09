package com.bnd.chemistry.business;

import org.springframework.test.context.ContextConfiguration;

import com.bnd.core.test.Spring4Test;

/**
 * @author © Peter Banda
 * @since 2012  
 */
@ContextConfiguration(locations = {"classpath:chemistry-conf.xml"})
public abstract class ChemistryTest extends Spring4Test {

}
