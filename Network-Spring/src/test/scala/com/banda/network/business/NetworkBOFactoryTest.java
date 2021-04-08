package com.bnd.network.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.bnd.core.domain.MultiStateUpdateType;
import com.bnd.core.metrics.MetricsType;
import com.bnd.core.util.ConversionUtil;
import com.bnd.core.util.RandomUtil;
import com.bnd.function.business.FunctionFactory;
import com.bnd.function.domain.BooleanFunction;
import com.bnd.function.domain.BooleanFunctionType;
import com.bnd.function.domain.Expression;
import com.bnd.network.domain.*;
import com.bnd.math.domain.rand.UniformDistribution;

/**
 * @author © Peter Banda
 * @since 2012  
 */
public class NetworkBOFactoryTest extends com.bnd.network.business.NetworkTest {

	// Constants
	private final static boolean SHOW_OUTPUTS = false;
	private final static int CELLS = 1000;
	private final static int REPETITIONS = 20;
	private final static int STEPS = 4000;

	@Autowired
	private NetworkBOFactory<Double> doubleNetworkFactory;

	@Autowired
	private NetworkBOFactory<Boolean> booleanNetworkFactory;

	private final FunctionFactory functionFactory = new FunctionFactory();

	@Test
	public void testCreateDoubleNetworkBO() {
		NetworkBO<Double> networkBO = doubleNetworkFactory.createNetworkBO(createDoubleNetworkTestData());
		assertNotNull(networkBO);
		assertNotEmpty(networkBO.getNodes());

		networkBO.setMutableNodeStates(createNetworkConfigurationTestData(networkBO.getNodes().size()));
		for (int i = 0; i < 100; i++) {
			if (SHOW_OUTPUTS) {
				System.out.println(networkBO.getNodeStates());
			}
			networkBO.updateState();
		}
		if (SHOW_OUTPUTS) {
			System.out.println(networkBO.getNodeStates());
			System.out.println("");
		}
		assertNotEmpty(networkBO.getNodeStates());
	}

	@Test
	public void testCreateBooleanNetworkBO1() {
		NetworkBO<Boolean> networkBO = booleanNetworkFactory.createNetworkBO(createBooleanNetworkTestData1());
		assertNotNull(networkBO);
		assertNotEmpty(networkBO.getNodes());

		networkBO.setNodeStates(createBooleanNetworkConfigurationTestData(networkBO.getNodes().size()));
		for (int i = 0; i < 100; i++) {
			if (SHOW_OUTPUTS) {
				System.out.println(networkBO.getNodeStates());
			}
			networkBO.updateState();
		}
		if (SHOW_OUTPUTS) {
			System.out.println(networkBO.getNodeStates());
			System.out.println("");
		}
		assertNotEmpty(networkBO.getNodeStates());
	}

	@Test
	public void testCreateBooleanNetworkBO2() {
		NetworkBO<Boolean> networkBO = booleanNetworkFactory.createNetworkBO(createBooleanNetworkTestData2());
		assertNotNull(networkBO);
		assertNotEmpty(networkBO.getNodes());

		networkBO.setNodeStates(createBooleanNetworkConfigurationTestData(networkBO.getNodes().size()));
		for (int i = 0; i < 100; i++) {
			if (SHOW_OUTPUTS) {
				System.out.println(networkBO.getNodeStatesInLocationOrder());
			}
			networkBO.updateState();
		}
		if (SHOW_OUTPUTS) {
			System.out.println(networkBO.getNodeStatesInLocationOrder());
			System.out.println("");
		}
		assertNotEmpty(networkBO.getNodeStates());
	}

	@Test
	public void testCreateBooleanNetworkBO3() {
		NetworkBO<Boolean> networkBO = booleanNetworkFactory.createNetworkBO(createBooleanNetworkTestData3());
		assertNotNull(networkBO);
		assertNotEmpty(networkBO.getNodes());

		long msSum = 0;
		for (int j = 0; j < REPETITIONS; j++) {
			Date startTime = new Date();
			networkBO.setNodeStates(createBooleanNetworkConfigurationTestData(networkBO.getNodes().size()));
			for (int i = 0; i < STEPS; i++) {
				if (SHOW_OUTPUTS) {
					System.out.println(networkBO.getNodeStatesInLocationOrder());
				}
				networkBO.updateState();
			}
			if (SHOW_OUTPUTS) {
				System.out.println(networkBO.getNodeStatesInLocationOrder());
				System.out.println("");
			}
			assertNotEmpty(networkBO.getNodeStates());
			Date endTime = new Date();
			msSum += endTime.getTime() - startTime.getTime();
		}
		System.out.println("Average Time (ms): " + msSum / REPETITIONS);
	}

	private List<Double> createNetworkConfigurationTestData(int nodeNum) {
		List<Double> configuration = new ArrayList<Double>();
		for (int i = 0; i < nodeNum; i++) {
			configuration.add(RandomUtil.nextDouble(0, 1));
		}
		return configuration;
	}

	private Network<Double> createDoubleNetworkTestData() {
		Network<Double> network = new Network<Double>();
		network.setTopology(createTopologyTestData1());
		network.setFunction(createDoubleNetworkFunctionTestData());
		network.setWeightSetting(createDoubleNetworkWeightSetting());
		return network;
	}

	private Topology createTopologyTestData1() {
		// layer 1
		FlatTopology layer1 = new FlatTopology();
		TopologicalNode node1 = new TopologicalNode();
		TopologicalNode node2 = new TopologicalNode();
		node1.setIndex(1);
		node2.setIndex(2);
		node2.addNewInEdgeFrom(node1);
		node1.addNewInEdgeFrom(node1);
		
		layer1.setId(1l);
		layer1.setIndex(1);
		layer1.addNode(node1);
		layer1.addNode(node2);

		// layer 2
		TemplateTopology layer2 = new TemplateTopology();
		layer2.setId(2l);
		layer2.setIndex(2);
		layer2.setNodesNum(20);
		layer2.setAllowSelfEdges(true);
		layer2.setAllowMultiEdges(false);
		layer2.setInEdgesNum(3);

		// top-level topology
		TemplateTopology topLevelTopology = new TemplateTopology();
		topLevelTopology.setId(3l);
		topLevelTopology.setIntraLayerInEdgesNum(1);
		topLevelTopology.addLayer(layer1);
		topLevelTopology.addLayer(layer2);

		return topLevelTopology;
	}

	private SpatialTopology createTopologyTestData2() {
		SpatialTopology spatialTopology = new SpatialTopology();
		spatialTopology.setMetricsType(MetricsType.Manhattan);
		spatialTopology.addSize(10);
		spatialTopology.setRadius(2);
		spatialTopology.setTorusFlag(true);

		return spatialTopology;
	}

	private SpatialTopology createTopologyTestData3() {
		SpatialTopology spatialTopology = new SpatialTopology();
		spatialTopology.setMetricsType(MetricsType.Manhattan);
		spatialTopology.addSize(CELLS);
		spatialTopology.setRadius(1);
		spatialTopology.setTorusFlag(true);
		spatialTopology.setItsOwnNeighor(true);

		return spatialTopology;
	}

	private Network<Boolean> createBooleanNetworkTestData1() {
		Network<Boolean> network = new Network<Boolean>();
		network.setTopology(createTopologyTestData1());
		network.setFunction(createBooleanNetworkFunctionTestData1());
		network.setWeightSetting(createBooleanNetworkWeightSetting());

		return network;
	}

	private Network<Boolean> createBooleanNetworkTestData2() {
		Network<Boolean> network = new Network<Boolean>();
		network.setTopology(createTopologyTestData2());
		network.setFunction(createBooleanNetworkFunctionTestData2());

		return network;
	}

	private Network<Boolean> createBooleanNetworkTestData3() {
		Network<Boolean> network = new Network<Boolean>();
		network.setTopology(createTopologyTestData3());
		network.setFunction(createBooleanNetworkFunctionTestData3());

		return network;
	}

	private NetworkFunction<Double> createDoubleNetworkFunctionTestData() {
		NetworkFunction<Double> layer1Function = new NetworkFunction<Double>();
		layer1Function.setMultiComponentUpdaterType(MultiStateUpdateType.Sync);
		layer1Function.setStatesWeightsIntegratorType(StatesWeightsIntegratorType.LinearSum);
		layer1Function.setFunction(new Expression<Double, Double>("tanh(x0)", Double.class, Double.class));

		NetworkFunction<Double> layer2Function = new NetworkFunction<Double>();
		layer2Function.setMultiComponentUpdaterType(MultiStateUpdateType.AsyncFixedOrder);
		layer2Function.setStatesWeightsIntegratorType(StatesWeightsIntegratorType.LinearSum);
		layer2Function.setFunction(new Expression<Double, Double>("x0 > 0", Double.class, Double.class));

		NetworkFunction<Double> topLevelFunction = new NetworkFunction<Double>();
		topLevelFunction.setMultiComponentUpdaterType(MultiStateUpdateType.AsyncRandom);
		topLevelFunction.addLayerFunction(layer1Function);
		topLevelFunction.addLayerFunction(layer2Function);

		return topLevelFunction;
	}

	private NetworkWeightSetting<Double> createDoubleNetworkWeightSetting() {
		TemplateNetworkWeightSetting<Double> networkWeightSetting = new TemplateNetworkWeightSetting<Double>();
		networkWeightSetting.setRandomDistribution(new UniformDistribution<Double>(-2.0, 2.0));
		return networkWeightSetting;
	}

	private List<Boolean> createBooleanNetworkConfigurationTestData(int nodeNum) {
		List<Boolean> configuration = new ArrayList<Boolean>();
		for (int i = 0; i < nodeNum; i++) {
			configuration.add(RandomUtil.nextBoolean());
		}
		return configuration;
	}

	private NetworkFunction<Boolean> createBooleanNetworkFunctionTestData1() {
		NetworkFunction<Boolean> layer1Function = new NetworkFunction<Boolean>();
		layer1Function.setMultiComponentUpdaterType(MultiStateUpdateType.Sync);
		layer1Function.setStatesWeightsIntegratorType(StatesWeightsIntegratorType.LinearSum);
		layer1Function.setFunction(new BooleanFunction(BooleanFunctionType.AND));

		NetworkFunction<Boolean> layer2Function = new NetworkFunction<Boolean>();
		layer2Function.setMultiComponentUpdaterType(MultiStateUpdateType.AsyncFixedOrder);
		layer2Function.setStatesWeightsIntegratorType(StatesWeightsIntegratorType.LinearSum);
		layer2Function.setFunction(new BooleanFunction(BooleanFunctionType.Parity));

		NetworkFunction<Boolean> topLevelFunction = new NetworkFunction<Boolean>();
		topLevelFunction.setMultiComponentUpdaterType(MultiStateUpdateType.AsyncRandom);
		topLevelFunction.addLayerFunction(layer1Function);
		topLevelFunction.addLayerFunction(layer2Function);

		return topLevelFunction;
	}

	private NetworkFunction<Boolean> createBooleanNetworkFunctionTestData2() {
		NetworkFunction<Boolean> parityFunction = new NetworkFunction<Boolean>();
		parityFunction.setMultiComponentUpdaterType(MultiStateUpdateType.Sync);
		parityFunction.setFunction(new BooleanFunction(BooleanFunctionType.Parity));
		return parityFunction;
	}

	private NetworkFunction<Boolean> createBooleanNetworkFunctionTestData3() {
		NetworkFunction<Boolean> function = new NetworkFunction<Boolean>();
		function.setMultiComponentUpdaterType(MultiStateUpdateType.Sync);
		List<Boolean> outputs = ConversionUtil.convertDecimalToBooleanList(110, 8);
		Collections.reverse(outputs);
		function.setFunction(functionFactory.createBoolTransitionTable(outputs));
		return function;
	}

	private NetworkWeightSetting<Boolean> createBooleanNetworkWeightSetting() {
		FixedNetworkWeightSetting<Boolean> networkWeightSetting = new FixedNetworkWeightSetting<Boolean>();
		for (int i = 0; i < 200; i++) {
			networkWeightSetting.addWeight(RandomUtil.nextBoolean());
		}
		return networkWeightSetting;
	}
}