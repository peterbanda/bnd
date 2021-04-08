package com.bnd.network.business

import java.{lang => jl}

import com.bnd.function.business.FunctionEvaluatorFactoryImpl
import com.bnd.function.enumerator.{ListEnumeratorFactory, ListEnumeratorFactoryImpl}
import com.bnd.function.evaluator.FunctionEvaluatorFactory
import com.bnd.network.business.learning.{ReservoirRunnableFactory, ReservoirTrainerFactory}
import com.bnd.math.business.learning.IOStreamFactory
import com.bnd.network.business.function.{ActivationFunctionFactory, DoubleActivationFunctionFactory, JavaDoubleActivationFunctionFactory}
import com.bnd.network.business.integrator.{DoubleConvertibleSWIntegratorFactory, MetaStatesWeightsIntegratorFactory, StatesWeightsIntegratorFactory}
import com.bnd.network.metrics.{DoubleConvertibleMetricsFactory, DoubleMetricsFactory}
import com.bnd.core.metrics.MetricsFactory
import com.google.inject.name.Named
import com.google.inject.{Provides, Singleton}
import net.codingwell.scalaguice.ScalaModule

import collection.JavaConversions.mapAsJavaMap

/**
  * Guice network (Scala) module containing all the necessary components to create and run networks (including reservoirs)
  */
class NetworkModule extends ScalaModule {

  /////////////////
  // Basic Stuff //
  /////////////////

  @Provides @Singleton
  def listEnumeratorFactory: ListEnumeratorFactory =
    new ListEnumeratorFactoryImpl()

  @Provides @Singleton
  def functionEvaluatorFactory(listEnumeratorFactory: ListEnumeratorFactory): FunctionEvaluatorFactory =
    new FunctionEvaluatorFactoryImpl(listEnumeratorFactory)

  @Provides @Singleton
  def ioStreamFactory(functionEvaluatorFactory: FunctionEvaluatorFactory): IOStreamFactory =
    new IOStreamFactory(functionEvaluatorFactory)

  /////////////
  // Metrics //
  /////////////

  @Provides @Singleton
  def doubleMetricsFactory(listEnumeratorFactory: ListEnumeratorFactory): MetricsFactory[jl.Double] =
    new DoubleMetricsFactory(listEnumeratorFactory)

  @Provides @Singleton
  def integerMetricsFactory(doubleMetricFactory: MetricsFactory[jl.Double]): MetricsFactory[jl.Integer]  =
    new DoubleConvertibleMetricsFactory[jl.Integer](doubleMetricFactory, classOf[jl.Integer])

  @Provides @Singleton
  def topologyFactory(integerMetricsFactory: MetricsFactory[jl.Integer]): TopologyFactory =
    new TopologyFactoryImpl(integerMetricsFactory)

  ////////////////////
  // SW Integrators //
  ////////////////////

  @Provides @Singleton
  def doubleSWIntegratorFactory: StatesWeightsIntegratorFactory[jl.Double] =
    MetaStatesWeightsIntegratorFactory.createJavaDoubleInstance

  @Provides @Singleton
  def integerSWIntegratorFactory(
    doubleSWIntegratorFactory: StatesWeightsIntegratorFactory[jl.Double]
  ): StatesWeightsIntegratorFactory[jl.Integer] =
    DoubleConvertibleSWIntegratorFactory(doubleSWIntegratorFactory, classOf[jl.Integer])

  @Provides @Singleton
  def booleanSWIntegratorFactory(
    doubleSWIntegratorFactory: StatesWeightsIntegratorFactory[jl.Double]
  ): StatesWeightsIntegratorFactory[jl.Boolean] =
    DoubleConvertibleSWIntegratorFactory(doubleSWIntegratorFactory, classOf[jl.Boolean])

  @Provides @Singleton @Named("scalaBooleanSWIntegratorFactory")
  def scalaBooleanSWIntegratorFactory: StatesWeightsIntegratorFactory[Boolean] =
    MetaStatesWeightsIntegratorFactory.createBooleanInstance

  @Provides @Singleton @Named("scalaDoubleSWIntegratorFactory")
  def scalaDoubleSWIntegratorFactory: StatesWeightsIntegratorFactory[Double] =
    MetaStatesWeightsIntegratorFactory.createAnyValInstance(classOf[Double])

  ///////////////////////////////////
  // Activation Function Factories //
  ///////////////////////////////////

  @Provides @Singleton
  def scalaDoubleActivationFunctionFactory: ActivationFunctionFactory[Double] =
    new DoubleActivationFunctionFactory()

  @Provides @Singleton
  def doubleActivationFunctionFactory: ActivationFunctionFactory[jl.Double] =
    new JavaDoubleActivationFunctionFactory()

  /////////////////////
  // Weight Builders //
  /////////////////////

  @Provides @Singleton
  def numberNetworkWeightBuilder: UntypedNetworkWeightBuilder[jl.Number] =
    new NetworkWeightBuilderSwitch(new ComposedNetworkWeightBuilder(), new NumberFlatNetworkWeightBuilder())

  @Provides @Singleton
  def scalaNumberNetworkWeightBuilder: UntypedNetworkWeightBuilder[AnyVal] =
    new NetworkWeightBuilderSwitch(new ComposedNetworkWeightBuilder(), new AnyValFlatNetworkWeightBuilder())

  @Provides @Singleton @Named("noTemplateNetworkWeightBuilder")
  def noTemplateNetworkWeightBuilder: UntypedNetworkWeightBuilder[Any] =
    new NetworkWeightBuilderSwitch(new ComposedNetworkWeightBuilder(), new NoTemplateFlatNetworkWeightBuilder())

  ///////////////////////
  // Network Runnables //
  ///////////////////////

  @Provides @Singleton @Named("scalaBooleanNetworkRunnableFactory")
  def scalaBooleanNetworkRunnableFactory(
    functionEvaluatorFactory: FunctionEvaluatorFactory,
    topologyFactory: TopologyFactory,
    noTemplateNetworkWeightBuilder: UntypedNetworkWeightBuilder[Any],
    @Named("scalaBooleanSWIntegratorFactory") scalaBooleanSWIntegratorFactory: StatesWeightsIntegratorFactory[Boolean]
  ): NetworkRunnableFactory[Boolean] =
    NetworkRunnableFactoryUtil.apply(
      classOf[Boolean], functionEvaluatorFactory, topologyFactory, noTemplateNetworkWeightBuilder, scalaBooleanSWIntegratorFactory
    )

  @Provides @Singleton @Named("scalaDoubleNetworkRunnableFactory")
  def scalaDoubleNetworkRunnableFactory(
    functionEvaluatorFactory: FunctionEvaluatorFactory,
    topologyFactory: TopologyFactory,
    scalaNumberNetworkWeightBuilder: UntypedNetworkWeightBuilder[AnyVal],
    @Named("scalaDoubleSWIntegratorFactory") scalaDoubleSWIntegratorFactory: StatesWeightsIntegratorFactory[Double],
    scalaDoubleActivationFunctionFactory: ActivationFunctionFactory[Double]
  ): NetworkRunnableFactory[Double] =
    NetworkRunnableFactoryUtil.apply(
      classOf[Double], functionEvaluatorFactory, topologyFactory, scalaNumberNetworkWeightBuilder, scalaDoubleSWIntegratorFactory, scalaDoubleActivationFunctionFactory
    )

  @Provides @Singleton
  def booleanNetworkRunnableFactory(
    functionEvaluatorFactory: FunctionEvaluatorFactory,
    topologyFactory: TopologyFactory,
    noTemplateNetworkWeightBuilder: UntypedNetworkWeightBuilder[Any],
    booleanSWIntegratorFactory: StatesWeightsIntegratorFactory[jl.Boolean]
  ): NetworkRunnableFactory[jl.Boolean] =
    NetworkRunnableFactoryUtil.apply(
      classOf[jl.Boolean], functionEvaluatorFactory, topologyFactory, noTemplateNetworkWeightBuilder, booleanSWIntegratorFactory
    )

  @Provides @Singleton
  def doubleNetworkRunnableFactory(
    functionEvaluatorFactory: FunctionEvaluatorFactory,
    topologyFactory: TopologyFactory,
    numberNetworkWeightBuilder: UntypedNetworkWeightBuilder[jl.Number],
    doubleSWIntegratorFactory: StatesWeightsIntegratorFactory[jl.Double],
    doubleActivationFunctionFactory: ActivationFunctionFactory[jl.Double]
  ): NetworkRunnableFactory[jl.Double] =
    NetworkRunnableFactoryUtil.apply(
      classOf[jl.Double], functionEvaluatorFactory, topologyFactory, numberNetworkWeightBuilder, doubleSWIntegratorFactory, doubleActivationFunctionFactory
    )

  @Provides @Singleton
  def metaNetworkRunnableFactory(
    functionEvaluatorFactory: FunctionEvaluatorFactory,
    topologyFactory: TopologyFactory,
    numberNetworkWeightBuilder: UntypedNetworkWeightBuilder[jl.Number],
    scalaNumberNetworkWeightBuilder: UntypedNetworkWeightBuilder[AnyVal],
    noTemplateNetworkWeightBuilder: UntypedNetworkWeightBuilder[Any],
    @Named("scalaBooleanSWIntegratorFactory") scalaBooleanSWIntegratorFactory: StatesWeightsIntegratorFactory[Boolean],
    @Named("scalaDoubleSWIntegratorFactory") scalaDoubleSWIntegratorFactory: StatesWeightsIntegratorFactory[Double],
    booleanSWIntegratorFactory: StatesWeightsIntegratorFactory[jl.Boolean],
    doubleSWIntegratorFactory: StatesWeightsIntegratorFactory[jl.Double],
    integerSWIntegratorFactory: StatesWeightsIntegratorFactory[jl.Integer],
    scalaDoubleActivationFunctionFactory: ActivationFunctionFactory[Double],
    doubleActivationFunctionFactory: ActivationFunctionFactory[jl.Double]
  ): MetaNetworkRunnableFactory = {
    val map1: Map[Class[_], UntypedNetworkWeightBuilder[_]] = Map(
      classOf[jl.Number] -> numberNetworkWeightBuilder,
      classOf[jl.Object] -> noTemplateNetworkWeightBuilder,
      classOf[AnyVal] -> scalaNumberNetworkWeightBuilder,
      classOf[Boolean] -> noTemplateNetworkWeightBuilder
    )

    val map2: Map[Class[_], StatesWeightsIntegratorFactory[_]] = Map(
      classOf[jl.Boolean] -> booleanSWIntegratorFactory,
      classOf[jl.Double] -> doubleSWIntegratorFactory,
      classOf[jl.Integer] -> integerSWIntegratorFactory,
      classOf[Boolean] -> scalaBooleanSWIntegratorFactory,
      classOf[Double] -> scalaDoubleSWIntegratorFactory
    )

    val map3: Map[Class[_], ActivationFunctionFactory[_]] = Map(
      classOf[jl.Double] -> doubleActivationFunctionFactory,
      classOf[Double] -> scalaDoubleActivationFunctionFactory
    )

    new MetaNetworkRunnableFactoryImpl(
      functionEvaluatorFactory,
      topologyFactory,
      mapAsJavaMap(map1),
      mapAsJavaMap(map2),
      mapAsJavaMap(map3)
    )
  }

  @Provides @Singleton
  def reservoirTrainerFactory(
    metaNetworkRunnableFactory: MetaNetworkRunnableFactory,
    topologyFactory: TopologyFactory,
    doubleMetricsFactory: MetricsFactory[java.lang.Double]
  ) =
    new ReservoirTrainerFactory(metaNetworkRunnableFactory, topologyFactory, doubleMetricsFactory)

  @Provides @Singleton
  def reservoirRunnableFactory(
    metaNetworkRunnableFactory: MetaNetworkRunnableFactory,
    topologyFactory: TopologyFactory
  ) =
    new ReservoirRunnableFactory(metaNetworkRunnableFactory, topologyFactory)

  override def configure = {}
}