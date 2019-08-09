package com.bnd.core.test;

import com.bnd.core.reflection.GenericReflectionProvider;
import junit.framework.TestCase;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Collection;

/**
 * An abstract super class of all Spring-based JUnit tests.
 * 
 * @author © Peter Banda
 * @since 2012  
 */
@ContextConfiguration(locations = {"classpath:core.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class Spring4Test extends TestCase {

	@Autowired
	GenericReflectionProvider genericReflectionProvider;

	protected void assertNotEmpty(Collection<?> collection) {
		TestCase.assertNotNull(collection);
		TestCase.assertTrue(!collection.isEmpty());
	}

	protected <T> T createRandomObject(Class<T> clazz) {
		return genericReflectionProvider.createRandomInstance(clazz);
	}

	protected <T> Collection<T> createRandomObjects(Class<T> clazz, int count) {
		Collection<T> objects = new ArrayList<T>();
		for (int i = 0; i < count; i++) {
			objects.add(createRandomObject(clazz));
		}
		return objects;
	}

	protected void reportValues(Object object) {
		System.out.println(genericReflectionProvider.getValuesAsString(object));
	}
}