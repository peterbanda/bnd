package com.bnd.chemistry.business

import com.bnd.chemistry.domain.AcReactionSet
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import com.bnd.chemistry.domain.AcSpeciesAssociationType
import org.apache.commons.lang.StringUtils

object OctaveGenerator {

    val acUtil = ArtificialChemistryUtil.getInstance

	def apply(reactionSet : AcReactionSet) = if (reactionSet == null) "" else {
        val speciesExpressionMap = createSpeciesExpressionMap(reactionSet).filter{case (species, builder) => !builder.isEmpty}
        val speciesLabelAndExpressions = speciesExpressionMap.toList.map{
            case(species, builder) => 
            	(species.getLabel, species.getLabel + "dot = " + builder)}.sortBy(_._1)
       	val listedSpecies = StringUtils.join(speciesLabelAndExpressions.map(_._1), ", ")

        val result = new StringBuilder
       	result.append("function xdot = f(")
       	result.append(listedSpecies)
        result.append(", t)\n\n")
       	speciesLabelAndExpressions.foreach{ case (_,s) => result.append(s + ";\n")}
       	result.append("\nendfunction")
        result.toString
    }

	private def createSpeciesExpressionMap(reactionSet : AcReactionSet) = { 	   
	    val speciesExpressionMap = reactionSet.getSpecies().map((_, new StringBuilder)).toMap
	    for (reaction <- reactionSet.getReactions) yield {
			val rateFunction = AcKineticsBO.createInstance(reaction, true).createRateFunction
	    	val expression = acUtil.getFunctionAsString(rateFunction, reactionSet)

	    	// reactants
	    	reaction.getSpeciesAssociations(AcSpeciesAssociationType.Reactant).foreach(assoc =>
	    	    speciesExpressionMap(assoc.getSpecies).append(
	    	    	if (assoc.getStoichiometricFactor.equals(1d))
	    	    		" - " + expression
	    	    	else
	    	    		" - " + assoc.getStoichiometricFactor + " * " + expression))

	    	// products
	    	reaction.getSpeciesAssociations(AcSpeciesAssociationType.Product).foreach(assoc =>
	    	   	speciesExpressionMap(assoc.getSpecies).append(
	    	   			if (assoc.getStoichiometricFactor.equals(1d))
	    	   				" + " + expression
	    	   			else
	    	   				" + " + assoc.getStoichiometricFactor + " * " + expression))
	    }
	    speciesExpressionMap
	}
}