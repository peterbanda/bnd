package com.bnd.math.business.rand;

abstract class PositiveNormalDistributionProvider<T> extends AbstractRandomDistributionProvider<T> {

	protected final RandomDistributionProvider<T> normalDistributionProvider;

	protected PositiveNormalDistributionProvider(Class<T> clazz, RandomDistributionProvider<T> normalDistributionProvider) {
		super(clazz);
		this.normalDistributionProvider = normalDistributionProvider;
	}

	@Override
	public Double mean() {
		// TODO: Not entirely true
		return normalDistributionProvider.mean();
	}

	@Override
	public Double variance() {
		// TODO: Not entirely true
		return normalDistributionProvider.variance();
	}

    @Override
    public T next() {
        // TODO
        return null;
    }
}