package com.bnd.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.bnd.core.util.ObjectUtil;
import junit.framework.TestCase;

import org.junit.Test;

public class ObjectUtilTest extends TestCase {

	private static final Integer FIRST = 3;
	private static final Integer SECOND = 1;
	private static final Integer LAST_BUT_ONE = 4;
	private static final Integer LAST = 5;

	@Test
	public void testGetFirstFromCollection() {
		Integer firstObject = ObjectUtil.getFirst(createTestCollection());
		assertEquals(FIRST, firstObject);
	}

	@Test
	public void testGetSecondFromCollection() {
		Integer secondObject = ObjectUtil.getSecond(createTestCollection());
		assertEquals(SECOND, secondObject);
	}

	@Test
	public void testGetLastFromCollection() {
		Integer lastObject = ObjectUtil.getLast(createTestCollection());
		assertEquals(LAST, lastObject);
	}

	@Test
	public void testGetFirstFromList() {
		Integer firstObject = ObjectUtil.getFirst(createTestList());
		assertEquals(FIRST, firstObject);
	}

	@Test
	public void testGetSecondFromList() {
		Integer secondObject = ObjectUtil.getSecond(createTestList());
		assertEquals(SECOND, secondObject);
	}

	@Test
	public void testGetLastFromList() {
		Integer lastObject = ObjectUtil.getLast(createTestList());
		assertEquals(LAST, lastObject);
	}

	@Test
	public void testGetLastButOneFromList() {
		Integer lastButOneObject = ObjectUtil.getLastButOne(createTestList());
		assertEquals(LAST_BUT_ONE, lastButOneObject);
	}

	private Collection<Integer> createTestCollection() {
		return createTestList();
	}

	private List<Integer> createTestList() {
		List<Integer> list = new ArrayList<Integer>();
		list.add(FIRST);
		list.add(SECOND);
		list.add(20);
		list.add(9);
		list.add(LAST_BUT_ONE);
		list.add(LAST);
		return list;
	}
}