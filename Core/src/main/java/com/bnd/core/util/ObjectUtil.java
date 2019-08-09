package com.bnd.core.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.bnd.core.BndRuntimeException;
import com.bnd.core.domain.DomainObject;
import com.bnd.core.domain.KeyExtractor;
import com.bnd.core.domain.TechnicalDomainObject;

/**
 * <code>ObjectUtil</code> is a helper class containing useful object related
 * methods such as checking object equality or object comparison.</p>
 * 
 * @author © Peter Banda
 * @since 2007
 */
public class ObjectUtil {

	/**
	 * Checks if the given objects are equal.
	 * 
	 * @param firstObject The first object to check
	 * @param secondObject The second object to check
	 * @return The result of the comparison
	 */
	public static boolean areObjectsEqual(Object firstObject, Object secondObject) {
		if (firstObject == null) {
			if (secondObject == null) {
				return true;
			}
			return false;
		}
		return firstObject == secondObject || firstObject.equals(secondObject);
	}

	/**
	 * Checks if the given objects are equal and not null.
	 * 
	 * @param firstObject The first object to check
	 * @param secondObject The second object to check
	 * @return The result of the comparison
	 */
	public static boolean areObjectsNotNullAndEqual(Object firstObject, Object secondObject) {
		if (firstObject == null && secondObject == null) {
			return false;
		}
		return areObjectsEqual(firstObject, secondObject);
	}

	/**
	 * Checks if the given list of objects are equal.
	 * 
	 * @param firstObjects The first list of objects to check
	 * @param secondObjects The second list of objects to check
	 * @return The result of the comparison
	 */
	public static boolean areObjectsEqual(Object[] firstObjects, Object[] secondObjects) {
		if (firstObjects.length != secondObjects.length) {
			return false;
		}
		int i = 0;
		for (Object object : firstObjects) {
			if (!areObjectsEqual(object, secondObjects[i])) {
				return false;
			}
			i++;
		}
		return true;
	}

	/**
	 * Compares 2 objects taking null value into count.
	 * 
	 * @param <T> The comparable object
	 * @param firstObject The first object to compare
	 * @param secondObject The second object to compare
	 * @return The result of the comparison
	 */
	public static <T extends Comparable<T>> int compareObjects(T firstObject, T secondObject) {
		if (firstObject == null) {
			if (secondObject == null) {
				return 0;
			}
			return -1;
		}
		if (secondObject == null) {
			return 1;
		}
		return firstObject.compareTo(secondObject);
	}

	/**
	 * Compares 2 object arrays taking null value into count.
	 * 
	 * @param <T> The comparable object
	 * @param firstObjects The first array to compare
	 * @param secondObjects The second array to compare
	 * @return The result of the comparison
	 */
	public static <T extends Comparable<T>> int compareArrays(T[] firstObjects, T[] secondObjects) {
		int liResult = 0;
		if (firstObjects == null || secondObjects == null) {
			throw new RuntimeException("Not null arrays expected!");
		}
		if (firstObjects.length != secondObjects.length) {
			throw new RuntimeException("Not equal length of arrays to compare!");
		}
		int li = 0;
		while ((li < firstObjects.length)
				&& ((liResult = compareObjects(firstObjects[li], secondObjects[li])) == 0)) {
			li++;
		}
		return liResult;
	}

	public static <T extends Comparable<T>> boolean isEqualOrLess(T firstObject, T secondObject) {
		return areObjectsEqual(firstObject, secondObject)
				|| compareObjects(firstObject, secondObject) == -1;
	}

	/**
	 * Counts default hash code for given object with seed of 1 and multiplier
	 * 31.
	 * 
	 * @param objects Object to handle
	 * @return Overall hash code
	 */
	public static int getHashCode(Object[] objects) {
		return getHashCode(Arrays.asList(objects));
	}

	/**
	 * Counts default hash code for given object with given seed.
	 * 
	 * @param objects Object to handle
	 * @param seed Seed used for hashing
	 * @return Overall hash code
	 */
	public static int getHashCode(Object[] objects, int seed) {
		return getHashCode(Arrays.asList(objects), seed);
	}

	/**
	 * Counts default hash code for given object with seed of 1 and multiplier 31.
	 * 
	 * @param objects Object to handle
	 * @return Overall hash code
	 */
	public static int getHashCode(Collection<?> objects) {
		return getHashCode(objects, 7);
	}

	/**
	 * Counts default hash code for given object with given seed.
	 * 
	 * @param objects Object to handle
	 * @param seed Seed used for hashing
	 * @return Overall hash code
	 */
	public static int getHashCode(Collection<?> objects, int seed) {
		return getHashCode(objects, seed, 31);
	}

	/**
	 * Counts default hash code for given object with given seed and multiplier.
	 * 
	 * @param objects Object to handle
	 * @param seed Seed used for hashing
	 * @param multiplier Multiplier for hashing
	 * @return Overall hash code
	 */
	public static int getHashCode(Collection<?> objects, int seed, int multiplier) {
		int liHash = seed;
		for (Object object : objects) {
			liHash = multiplier * liHash
					+ (object != null ? object.hashCode() : 0);
		}
		return liHash;
	}

	public static <O> O getFirst(Collection<O> objects) {
		return (objects != null && !objects.isEmpty()) ? objects.iterator().next() : null;
	}

	public static <O> O getSecond(Collection<O> objects) {
		if (objects != null && !objects.isEmpty() && objects.size() > 1) {
			Iterator<O> iterator = objects.iterator();
			iterator.next();
			return iterator.next();
		}
		return null;
	}

	public static <O> O getLast(Collection<O> objects) {
		Iterator<O> iterator = objects.iterator();
		O object = null;
		while (iterator.hasNext()) {
			object = iterator.next();
		}
		return object;
	}

	public static <O> O getLast(List<O> objects) {
		return getFromEnd(objects, 0);
	}

	public static <O> O getLastButOne(List<O> objects) {
		return getFromEnd(objects, 1);
	}

	public static <O> O getFromEnd(List<O> objects, int indexFromEnd) {
		return (objects != null && objects.size() > indexFromEnd) ? objects.get(objects.size() - indexFromEnd - 1) : null;
	}

	public static <K, V> Map<K, V> switchKeyWithValue(Map<V, K> map) {
		Map<K, V> switchedMap = new HashMap<K, V>();
		for (Entry<V, K> subtitutionPair : map.entrySet()) {
			switchedMap.put(subtitutionPair.getValue(), subtitutionPair.getKey());
		}
		return switchedMap;
	}

	/**
	 * Gets the title (caption) of a given object.
	 * 
	 * @return The object's title.
	 */
	public static String getTitle(Object object) {
		return object.getClass().getName();
	}

	/**
	 * Gets the title (caption) of domain object.
	 * 
	 * @return The title of domain object.
	 */
	public static String getTitleWithId(DomainObject<?> domainObject) {
		StringBuffer lSB = new StringBuffer();
		lSB.append(getTitle(domainObject));
		lSB.append(" ");
		lSB.append(domainObject.getKey());
		return lSB.toString();
	}

	/**
	 * Creates map from given domain objects by using associated keys.
	 * 
	 * @param <O> The handled DO Class type
	 * @param objects The collection of objects to handle
	 * @return The domain objects map
	 */
	public static <O extends DomainObject<KEY>, KEY extends Serializable & Comparable<KEY>> Map<KEY, O> createIdMap(Collection<O> objects) {
		Map<KEY, O> objectsMap = new HashMap<KEY, O>();
		for (O object : objects) {
			if (object != null && object.getKey() != null) {
				objectsMap.put(object.getKey(), object);
			}
		}
		return objectsMap;
	}

	/**
	 * Creates map from given domain objects by using associated keys.
	 * 
	 * @param <O> The handled DO Class type
	 * @param objects The collection of objects to handle
	 * @return The domain objects map
	 */
	public static <O, KEY extends Serializable> Map<KEY, O> createIdMap(Collection<O> objects, KeyExtractor<KEY, O> keyExtractor) {
		Map<KEY, O> objectsMap = new HashMap<KEY, O>();
		for (O object : objects) {
			if (object != null && keyExtractor.getKey(object) != null) {
				objectsMap.put(keyExtractor.getKey(object), object);
			}
		}
		return objectsMap;
	}

	/**
	 * Checks if there are no objects with the same (duplicated) key.
	 * 
	 * @param <O> The handled DO Class type
	 * @param objects The collection of objects to handle
	 */
	public static <O extends DomainObject<KEY>, KEY extends Serializable & Comparable<KEY>> Collection<O> getObjectsWithAmbiguousKey(Collection<O> objects) {
		Collection<O> conflictedObjects = new ArrayList<O>();
		Set<KEY> keys = new HashSet<KEY>();
		for (O object : objects) {
			if (keys.contains(object.getKey())) {
				conflictedObjects.add(object);
			} else {
				keys.add(object.getKey());
			}
		}
		return conflictedObjects;
	}

	/**
	 * Checks if there are no objects with the same (duplicated) key.
	 * 
	 * @param <O> The handled DO Class type
	 * @param objects The collection of objects to handle
	 */
	public static <O extends DomainObject<KEY>, KEY extends Serializable & Comparable<KEY>> void checkObjectsForKeyAmbiguity(Collection<O> objects) {
		Collection<O> conflictedObjects = getObjectsWithAmbiguousKey(objects);
		if (!conflictedObjects.isEmpty()) {
			DomainObject<?> object = conflictedObjects.iterator().next();
			throw new BndRuntimeException("Duplicated key: " + object.getKey() + " found for object " + getTitle(object));
		}
	}

	/**
	 * Gets max key of give objects.
	 * 
	 * @param <O>
	 * @param objects
	 * @return
	 */
	public static <O extends TechnicalDomainObject> Long getMaxKey(Collection<O> objects) {
		Long maxKey = null;
		for (TechnicalDomainObject object : objects) {
			if (compareObjects(object.getKey(), maxKey) > 0) {
				maxKey = object.getKey();
			}
		}
		return maxKey;
	}

	public static void nullIdAndVersion(TechnicalDomainObject domainObject) {
		if (domainObject == null) {
			return;
		}
		domainObject.setKey(null);
		domainObject.setVersion(new Long(1));
	}

	public static void nullIdAndVersion(Collection<? extends TechnicalDomainObject> domainObjects) {
		for (TechnicalDomainObject domainObject : domainObjects) {
			nullIdAndVersion(domainObject);
		}
	}

	public static <T> Collection<Collection<T>> splitIntoGroups(
		final Collection<T> objects,
		final int groupSize
	) {
		Collection<Collection<T>> groups = new ArrayList<Collection<T>>();
		Collection<T> group = new ArrayList<T>();
		for (final T object : objects) {
			group.add(object);
			if (group.size() == groupSize) {
				groups.add(group);
				group = new ArrayList<T>();
			}
		}
		if (!group.isEmpty()) {
			groups.add(group);
		}
		return groups;
	}
}