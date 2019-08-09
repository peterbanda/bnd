package com.bnd.network.business;

import java.util.*;

import java.util.Map.Entry;

import com.bnd.function.business.FunctionEvaluatorFactoryImpl;
import com.bnd.function.evaluator.FunctionEvaluator;
import com.bnd.function.evaluator.FunctionEvaluatorFactory;
import com.bnd.network.BndNetworkException;
import com.bnd.network.business.integrator.JavaStatesWeightsIntegrator;
import com.bnd.network.business.integrator.StatesWeightsIntegratorFactory;
import com.bnd.network.domain.*;

/**
 * @author © Peter Banda
 * @since 2012  
 */
public class NetworkBOFactory<T> {

	private final FunctionEvaluatorFactory functionEvaluatorFactory;
	private final TopologyFactory topologyFactory;
	private final UntypedNetworkBOWeightBuilder<T> networkWeightBuilder;
	private final StatesWeightsIntegratorFactory<T> integratorFactory;

	protected NetworkBOFactory(
		FunctionEvaluatorFactory functionEvaluatorFuctory,
		TopologyFactory topologyFactory,
		UntypedNetworkBOWeightBuilder<T> networkWeightSetter,
		StatesWeightsIntegratorFactory<T> integratorFactory
	) {
		this.functionEvaluatorFactory = functionEvaluatorFuctory;
		this.topologyFactory = topologyFactory;
		this.networkWeightBuilder = networkWeightSetter;
		this.integratorFactory = integratorFactory;
	}

	protected NetworkBOFactory(
		FunctionEvaluatorFactoryImpl functionEvaluatorFuctory,
		TopologyFactory topologyFactory,
		UntypedNetworkBOWeightBuilder<T> networkWeightSetter
	) {
		this(functionEvaluatorFuctory, topologyFactory, networkWeightSetter, (StatesWeightsIntegratorFactory<T>) null);
	}

	public NetworkBO<T> createNetworkBO(Network<T> network) {
		StatesWeightsIntegratorType integratorType = network.getFunction().getStatesWeightsIntegratorType();
		if (integratorType != null && integratorFactory == null)
			throw new BndNetworkException("States-weights integrator of type '" + integratorType + "' specified for network '" + network.getId() + "' but no integrator provided.");

		final Topology topology = topologyFactory.apply(network.getTopology());
		if (topology.isTemplate()) {
			throw new BndNetworkException("At this point template the topology should already be initialized.");
		}
		// create network with layers and nodes
		NetworkBO<T> networkBO = createNetworkBOWithLayersAndNodes(topology, network.getFunction(), false);
		// set default immutable state if provided
		if (network.hasDefaultBiasState()) {
			for (NodeBO<T> nodeBO : networkBO.getImmutableNodes()) {
				if (nodeBO.isImmutable()) {
					nodeBO.setState(network.getDefaultBiasState());
				}
			}
		}
		// link nodes
		linkNodes(networkBO.getNodes());
		// add weights
		if (network.getWeightSetting() != null) {
			networkWeightBuilder.setWeights(networkBO, network.getWeightSetting());
		}
		return networkBO;
	}

	protected NetworkBO<T> createNetworkBOWithLayersAndNodes(
		Topology initializedTopology,
		NetworkFunction<T> networkFunction,
		boolean predecessorFunctionInherited
	) {
		NetworkBO<T> networkBO = null;
		// topology has either layers or nodes
		if (initializedTopology.hasLayers()) {
			networkBO = createNetworkBOWithLayers(initializedTopology, networkFunction, predecessorFunctionInherited);
		} else if (initializedTopology.hasNodes()) {
			networkBO = createNetworkBOWithNodes(initializedTopology, networkFunction);
		} else {
			throw new BndNetworkException("Initialized topology must have either layers or nodes, but none of them present.");
		}

		return networkBO;	
	}

	protected NetworkBO<T> createNetworkBOWithLayers(
		Topology initializedTopology,
		NetworkFunction<T> networkFunction,
		boolean predecessorFunctionInherited
	) {
		if (!initializedTopology.hasLayers()) {
			throw new BndNetworkException("A layered topology expected.");
		}
		List<NetworkFunction<T>> networkFunctions = networkFunction.getLayerFunctions();
		if (networkFunctions == null) {
			networkFunctions = new ArrayList<NetworkFunction<T>>();
		}
		Iterator<NetworkFunction<T>> layerFunctionIterator = networkFunctions.iterator();
		List<NetworkBO<T>> layerNetworkBOs = new ArrayList<NetworkBO<T>>();
		for (final Topology layer : initializedTopology.getLayers()) {
			NetworkBO<T> layerBO = null;
			if (predecessorFunctionInherited || !layerFunctionIterator.hasNext()) {
				layerBO = createNetworkBOWithLayersAndNodes(layer, networkFunction, true);
			} else {
				layerBO = createNetworkBOWithLayersAndNodes(layer, layerFunctionIterator.next(), false);
			}
			layerNetworkBOs.add(layerBO);
		}
		return new LayeredNetworkBO<T>(
			networkFunction.getMultiComponentUpdaterType(),
			layerNetworkBOs.toArray(new NetworkBO[0]));
	}

	protected NetworkBO<T> createNetworkBOWithNodes(
		Topology initializedTopology,
		NetworkFunction<T> networkFunction
	) {
		if (!initializedTopology.hasNodes()) {
			throw new BndNetworkException("A topology with nodes expected.");
		}
		JavaStatesWeightsIntegrator<T> statesWeightsIntegrator = null;
		if (integratorFactory != null) {
			// TODO: derelease
//			statesWeightsIntegrator = integratorFactory.createIntegrator(networkFunction.getStatesWeightsIntegratorType());
		}

		FunctionEvaluator<T, T> functionEvaluator = null;
		if (networkFunction.getFunction() != null) {
			functionEvaluator = functionEvaluatorFactory.createInstance(networkFunction.getFunction());
		}

		// create node business objects
		List<NodeBO<T>> nodes = new ArrayList<NodeBO<T>>();

		for (final TopologicalNode topologicalNode : initializedTopology.getAllNodes()) {
			nodes.add(new FunctionNodeBO<T>(topologicalNode, topologicalNode.isBias(), functionEvaluator, statesWeightsIntegrator));
		}

		return new FlatNetworkBO<T>(nodes, networkFunction.getMultiComponentUpdaterType());
	}

	private void linkNodes(Collection<NodeBO<T>> nodeBOs) {
		// first create a map for quick look-up
		Map<TopologicalNode, NodeBO<T>> topologicalNodeBOMap = new HashMap<TopologicalNode, NodeBO<T>>();
		for (NodeBO<T> nodeBO : nodeBOs) {
			topologicalNodeBOMap.put(nodeBO.getTopologicalNode(), nodeBO);
		}
		// link nodes without weights
		for (final Entry<TopologicalNode,NodeBO<T>> topologicalNodeBOPair : topologicalNodeBOMap.entrySet()) {
			final TopologicalNode topologicalNode = topologicalNodeBOPair.getKey();
			final NodeBO<T> nodeBO = topologicalNodeBOPair.getValue();
			for (final TopologicalNode inTopologicalNeighbor : topologicalNode.getInNeighbors()) {
				NodeBO<T> inNeighborBO = topologicalNodeBOMap.get(inTopologicalNeighbor);
				if (inNeighborBO == null) {
					throw new BndNetworkException("Topological node '" + inTopologicalNeighbor.getId() + "' cannot be found.");
				} 
				nodeBO.addNewInEdgeFrom(inNeighborBO);
			}
		}
	}
}