package com.bnd.math.business.rand;

class NumberPositiveNormalDistributionProvider<T extends Number> extends PositiveNormalDistributionProvider<T> {

	protected NumberPositiveNormalDistributionProvider(Class<T> clazz, RandomDistributionProvider<T> normalDistributionProvider) {
		super(clazz, normalDistributionProvider);
	}

	@Override
	public T next() {
		// TODO: Potentially an infinity loop might emerge here!
		T value = null;
		do {
			value = normalDistributionProvider.next();
		} while (value.doubleValue() < 0);
		return value;
	}
}