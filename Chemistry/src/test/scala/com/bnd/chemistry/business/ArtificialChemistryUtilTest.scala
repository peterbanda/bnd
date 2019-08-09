package com.bnd.chemistry.business

import java.{lang => jl, util => ju}

import com.bnd.chemistry.domain._
import org.junit.runners.MethodSorters
import org.junit.{FixMethodOrder, Test}

import scala.collection.JavaConversions._

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ArtificialChemistryUtilTest extends ScalaChemistryTest {

  val acUtil = ArtificialChemistryUtil.getInstance

  @Test
  def test1 {
    val reactantsString = "R +2.5F,     1.2 G"

    val speciesSet = new AcSpeciesSet
    speciesSet.addVariable(new AcSpecies("R"))
    speciesSet.addVariable(new AcSpecies("G"))
    speciesSet.addVariable(new AcSpecies("F"))

    val reactionSet = new AcReactionSet()
    reactionSet.setSpeciesSet(speciesSet)

    val reaction = new AcReaction
    reactionSet.addReaction(reaction)

    acUtil.setSpeciesAssociationsFromString(reactantsString, AcSpeciesAssociationType.Reactant, reaction)

    reaction.getSpeciesAssociations().foreach{ assoc =>
      println(assoc.getStoichiometricFactor + " " + assoc.getSpeciesLabel)
    }
  }
}