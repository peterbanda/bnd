package com.bnd.chemistry.business

import scala.jdk.CollectionConverters._
import com.bnd.chemistry.domain.AcReactionSet
import com.bnd.chemistry.domain.AcReaction
import com.bnd.chemistry.domain.AcSpecies
import com.bnd.chemistry.domain.AcSpeciesAssociationType
import com.bnd.chemistry.domain.AcSpeciesReactionAssociation
import com.bnd.math.business.MathUtil
import com.bnd.math.domain.Stats

class AcReactionNetworkPropertyBO(val reactionSet : AcReactionSet) {

    private val reactionNum = reactionSet.getReactionsNum()
    private val species = reactionSet.getSpecies()
    private val speciesNum = species.size()

    def calcSpeciesConnectivityStats(assocType : AcSpeciesAssociationType) : Stats = 
        MathUtil.calcStats(0,  calcSpeciesConnectivities(assocType))

    def calcSpeciesConnectivities(assocType : AcSpeciesAssociationType) : Iterable[Double] =
        species.asScala.map(calcSpeciesConnectivity(assocType)(_))

	def calcSpeciesConnectivity(assocType : AcSpeciesAssociationType)(species : AcSpecies) : Double = {
	    val stoichiometries = doWithReactionsAndMerge((_ : Iterable[Double]).sum)(assocType, {
	        assoc => if (assoc.getSpecies().equals(species)) assoc.getStoichiometricFactor() else 0D
	    })
	    stoichiometries.sum / reactionNum
	}

	private def doWithReactions[T](action : AcReaction => T) =
	    reactionSet.getReactions().asScala.view.map(action)

	private def doWithReactionsForType[T](asocType : AcSpeciesAssociationType)(action : AcSpeciesReactionAssociation => T) = 
	    doWithReactions { reaction => reaction.getSpeciesAssociations(asocType).asScala.view.map(action) }

	private def doWithReactionsAndMerge[T](merge : Iterable[T] => T) = 
	    doWithReactionsForType(_ : AcSpeciesAssociationType)(_ : AcSpeciesReactionAssociation => T).view.map(merge)
}