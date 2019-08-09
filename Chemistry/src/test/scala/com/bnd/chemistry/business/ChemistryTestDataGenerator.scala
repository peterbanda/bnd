package com.bnd.chemistry.business

import scala.collection.JavaConversions._
import com.bnd.chemistry.business.factory._
import com.bnd.chemistry.domain._
import com.bnd.function.business.ExpressionSupportedFunctionEvaluatorFactoryImpl
import com.bnd.function.domain.ODESolverType
import com.bnd.math.business.rand.RandomDistributionProviderFactory
import com.bnd.math.domain.rand.RandomDistribution
import java.{lang => jl}
import java.{util => ju}
import com.bnd.function.enumerator.ListEnumeratorFactory

object ChemistryTestDataGenerator {

  object Defaults {
    val acLowerThreshold = 0
    val acUpperThreshold = 1000d
    val acDiffThreshold = 0
  }

  val reactionSetFactory = AcReactionSetFactory.getInstance
  val speciesSetFactory = AcSpeciesSetFactory.getInstance
  val listEnumeratorFactory: ListEnumeratorFactory = null
  // = new ListEnumeratorFactoryImpl
  val functionEvaluatorFactory = new ExpressionSupportedFunctionEvaluatorFactoryImpl(listEnumeratorFactory)

  def createSpeciesSetWithPrefix(speciesNum: Int, prefix: String): AcSpeciesSet = {
    val speciesSet = speciesSetFactory.createFixedOrder(speciesNum)
    speciesSet.getVariables() foreach (s => s.setLabel(prefix + "_" + s.getLabel()))
    speciesSet
  }

  def createSpeciesSet(speciesNum: Int): AcSpeciesSet = speciesSetFactory.createFixedOrder(speciesNum)

  def createDNAStrandSpeciesSet(spec: AcDNAStrandSpec): AcDNAStrandSpeciesSet =
    speciesSetFactory.createDNAStrandSpeciesSet(spec)

  def reactionSet1(spec: AcSymmetricSpec): AcReactionSet = reactionSetFactory.createRandomReactionSymmetricRS(
    spec, speciesSetFactory.createFixedOrder(spec.getSpeciesNum()))

  def reactionSet2(
    speciesNum: Int,
    reactionNum: Int,
    randomDistribution: RandomDistribution[jl.Double]
  ): AcReactionSet = {
    val reactionSet = reactionSetFactory.createRandomRS(
      reactionNum, reactionNum, reactionNum, 0, 0, createSpeciesSet(speciesNum), AcReactionSpeciesForbiddenRedundancy.None)
    val filteredReactions = reactionSet.getReactions().filter(reaction => reaction.hasSpeciesAssociations(AcSpeciesAssociationType.Reactant)
      || reaction.hasSpeciesAssociations(AcSpeciesAssociationType.Product))
    setRateConstants(filteredReactions, randomDistribution)
    reactionSet.setReactions(filteredReactions)
    reactionSet
  }

  def reactionSet3(
    speciesNum: Int,
    randomDistribution: RandomDistribution[jl.Double]
  ): AcReactionSet = {
    val acReactionSetConstraints = new AcReactionSetConstraints() {
      setReactionToSpeciesConstraints(new AcReactionToSpeciesConstraints() {
        setMaxReactantsNum(2)
        setMinReactantsNum(1)
        setMaxProductsNum(2)
        setMaxCatalystsNum(0)
        setMaxInhibitorsNum(0)
      })
      setSpeciesToReactionConstraints(new AcSpeciesToReactionConstraints() {
        setFixedProductAssocsNum(2)
      })
      setReactionsPerSpeciesRatio(5d)
    }
    val speciesSet = createSpeciesSet(speciesNum)
    val reactionSet = reactionSetFactory.createComplexRS(speciesSet.getVariables(), acReactionSetConstraints, AcReactionSpeciesForbiddenRedundancy.None)
    reactionSet.setSpeciesSet(speciesSet)
    val filteredReactions = reactionSet.getReactions().filter(reaction => reaction.hasSpeciesAssociations(AcSpeciesAssociationType.Reactant)
      || reaction.hasSpeciesAssociations(AcSpeciesAssociationType.Product))

    setRateConstants(filteredReactions, randomDistribution)

    reactionSet.setReactions(filteredReactions)
    reactionSet
  }

  def dnaReactionSet(
    spec: AcDNAStrandSpec
  ): AcReactionSet = {
    val speciesSet = createDNAStrandSpeciesSet(spec)
    reactionSetFactory.createDNAStrandRS(spec, speciesSet)
  }

  def setRateConstants(
    reactions: ju.Collection[AcReaction],
    randomDistribution: RandomDistribution[jl.Double]
  ) {
    val distributionProvider = RandomDistributionProviderFactory.apply(randomDistribution)
    for (reaction <- reactions) {
      reaction.setForwardRateConstant(distributionProvider.next())
    }
  }

  def createSimulationConfig(
    odeSolverType: ODESolverType = ODESolverType.RungeKutta4,
    timeStep: Double = 0.1D,
    tolerance: Option[Double] = None,
    acLowerThreshold: Double = Defaults.acLowerThreshold,
    acUpperThreshold: Double = Defaults.acUpperThreshold,
    acDiffThreshold: Double = Defaults.acDiffThreshold
  ): AcSimulationConfig = new AcSimulationConfig() {
    setTimeStep(timeStep)
    setOdeSolverType(odeSolverType)
    setLowerThreshold(acLowerThreshold)
    setUpperThreshold(acUpperThreshold)
    setFixedPointDetectionPrecision(acDiffThreshold)
    setInfluxScale(0.1D)
    setTolerance(if (tolerance.isDefined) tolerance.get else null)
  }
}