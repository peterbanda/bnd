package com.bnd.math.business.evo;

import java.util.ArrayList;
import java.util.List;

import com.bnd.core.domain.ValueBound;
import com.bnd.core.reflection.ReflectionProvider;
import com.bnd.core.reflection.ReflectionUtil;

import com.bnd.math.domain.evo.ArrayChromosome;
import com.bnd.math.domain.evo.BitMutationType;

public class EvoArrayChromManipulatorBO<E> extends EvoCompositeChromManipulatorBO<ArrayChromosome<E>, E[], E>{

	private final Class<E> topLevelClass;

	public EvoArrayChromManipulatorBO(
		ReflectionProvider<ArrayChromosome<E>> chromosomeRF,
		Class<E> topLevelClass,
		BitMutationType bitMutationType,
		Double pertrubMutationStrength,
		List<ValueBound<? extends E>> codeElementValueBounds
	) {
		this(chromosomeRF, topLevelClass, bitMutationType, pertrubMutationStrength, collectElementClasses(codeElementValueBounds).toArray(new Class[0]), codeElementValueBounds.toArray(new ValueBound[0]));
	}

	private static <E> List<Class<? extends E>> collectElementClasses(
		List<ValueBound<? extends E>> codeElementValueBounds
	) {
		List<Class<? extends E>> classes = new ArrayList<Class<? extends E>>();
		for (ValueBound<? extends E> bound : codeElementValueBounds) {
			classes.add((Class<E>) bound.getFrom().getClass());
		}
		return classes;
	}

	public EvoArrayChromManipulatorBO(
		ReflectionProvider<ArrayChromosome<E>> chromosomeRF,
		Class<E> toplevelClass,
		BitMutationType bitMutationType,
		Double pertrubMutationStrength,
		Class<E>[] elementClasses,
		ValueBound<E>[] codeElementValueBounds
	) {
		super(chromosomeRF, elementClasses.length, bitMutationType, pertrubMutationStrength, elementClasses, codeElementValueBounds);
		this.topLevelClass = toplevelClass;
	}

	public EvoArrayChromManipulatorBO(
		ReflectionProvider<ArrayChromosome<E>> chromosomeRF,
		int codeSize,
		BitMutationType bitMutationType,
		Double pertrubMutationStrength,
		Class<E> elementClass
	) {
		super(chromosomeRF, codeSize, bitMutationType, pertrubMutationStrength, elementClass);
		this.topLevelClass = elementClass;
	}

	public EvoArrayChromManipulatorBO(
		ReflectionProvider<ArrayChromosome<E>> chromosomeRF,
		BitMutationType bitMutationType,
		Double pertrubMutationStrength,
		Class<E> elementClass,
		ValueBound<E>[] codeElementValueBounds
	) {
		super(chromosomeRF, codeElementValueBounds.length, bitMutationType, pertrubMutationStrength, elementClass, codeElementValueBounds);
		this.topLevelClass = elementClass;
	}

	public EvoArrayChromManipulatorBO(
		ReflectionProvider<ArrayChromosome<E>> chromosomeRF,
		int codeSize,
		BitMutationType bitMutationType,
		Double pertrubMutationStrength,
		Class<E> elementClass,
		ValueBound<E> sharedCodeElementValueBound
	) {
		super(chromosomeRF, codeSize, bitMutationType, pertrubMutationStrength, elementClass, sharedCodeElementValueBound);
		this.topLevelClass = elementClass;
	}

	@Override
	protected E[] createBlankCode() {
		return (E[]) ReflectionUtil.createNewArray(topLevelClass, codeSize);
	}
}