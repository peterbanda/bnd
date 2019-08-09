package com.bnd.core.domain;

import java.util.ArrayList;
import java.util.Collection;

import com.bnd.core.BndRuntimeException;

/**
 * @author © Peter Banda
 * @since 2011
 */
public class EitherOr<F, S> {

	private F first;
	private S second;

	public EitherOr(F first, S second) {
		if (first != null && second != null) {
			throw new BndRuntimeException("First and second attribute of EitherOr type can not be set at the same time.");
		}
		this.first = first;
		this.second = second;
	}

	public static <F, S> Collection<EitherOr<F, S>> first(Collection<F> firsts, S second) {
		Collection<EitherOr<F, S>> eitherOrs = new ArrayList<EitherOr<F, S>>();
		for (F first : firsts) {
			eitherOrs.add(new EitherOr<F, S>(first, null));
		}
		return eitherOrs;
	}

	public static <F, S> Collection<EitherOr<F, S>> second(Collection<S> seconds) {
		Collection<EitherOr<F, S>> eitherOrs = new ArrayList<EitherOr<F, S>>();
		for (S second : seconds) {
			eitherOrs.add(new EitherOr<F, S>(null, second));
		}
		return eitherOrs;
	}

	public boolean isFirst() {
		return first != null;
	}

	public F getFirst() {
		return first;
	}

	public boolean isSecond() {
		return second != null;
	}

	public S getSecond() {
		return second;
	}
}