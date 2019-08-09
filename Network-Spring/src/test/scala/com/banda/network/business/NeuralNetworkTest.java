package com.bnd.network.business;

import scala.math.BigDecimal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.bnd.core.domain.MultiStateUpdateType;
import com.bnd.core.runnable.FullStateAccessible;
import com.bnd.core.runnable.TimeRunnable;
import com.bnd.core.util.ObjectUtil;
import com.bnd.function.domain.Expression;
import com.bnd.network.domain.*;

/**
 * @author © Peter Banda
 * @since 2012  
 */
public class NeuralNetworkTest extends NetworkTest {

	@Autowired
	private NetworkBOFactory<Double> doubleNetworkFactory;

	@Autowired
	private NetworkRunnableFactory<Double> doubleNetworkRunnableFactory;

	@Autowired
	private TopologyFactory topologyFactory;

	@Test
	public void testNeuralNetworkBO() {
		Network<Double>[] testNetworks = new Network[] {createTestNetwork1(), createTestNetwork2(), createTestNetwork3()};

		Collection<List<List<Double>>> multiConfigs = new ArrayList<List<List<Double>>>();
		multiConfigs.add(createTestNetworkConfigurations1());
		multiConfigs.add(createTestNetworkConfigurations2());
		multiConfigs.add(createTestNetworkConfigurations3());

		Iterator<List<List<Double>>> multiConfigIterator = multiConfigs.iterator();

		int networkIndex = 0;
		for (Network<Double> testNetwork : testNetworks) {
			System.out.println("Test network: " + networkIndex);

			final Topology topology = topologyFactory.apply(testNetwork.getTopology());
			testNetwork.setTopology(topology);

			final Topology inputLayer = ObjectUtil.getFirst(topology.getLayers());
			final Topology outputLayer = ObjectUtil.getLast(topology.getLayers());

			NetworkBO<Double> networkBO = doubleNetworkFactory.createNetworkBO(testNetwork);
			Object networkObject = doubleNetworkRunnableFactory.createNonInteractive(testNetwork, new NetworkSimulationConfig());

			TimeRunnable networkRunnable = (TimeRunnable) networkObject;
			FullStateAccessible<Double, TopologicalNode> networkAccessible = (FullStateAccessible<Double, TopologicalNode>) networkObject; 

			assertNotNull(networkBO);
			assertNotEmpty(networkBO.getNodes());

			reportNetworkBO((LayeredNetworkBO) networkBO);

			List<List<Double>> configs = multiConfigIterator.next();
			for (List<Double> config : configs) {

				System.out.println(StringUtils.rightPad("Input:" + config.toString(), 25));

				networkBO.setInput(config);
				networkBO.updateState();
				assertNotEmpty(networkBO.getOutput());
				System.out.println("->  Output (BO):" + networkBO.getOutput());
				System.out.println("->  All states (BO):" + networkBO.getStates());

				// set input
				Iterator<Double> configIterator = config.iterator();
				for (TopologicalNode topologicalNode : inputLayer.getNonBiasNodes()) {
					networkAccessible.setState(topologicalNode, configIterator.next());
				}

				networkRunnable.runFor(BigDecimal.valueOf(1d));

				// output
				List<Double> output = new ArrayList<Double>();
				for (TopologicalNode topologicalNode : outputLayer.getNonBiasNodes()) {
					output.add(networkAccessible.getState(topologicalNode));
				}

				assertNotEmpty(output);
				System.out.println("->  Output (runnable):" + output);
				System.out.println("->  All states (runnable):" + networkAccessible.getStates());
			}
		}
	}

	private Network<Double> createTestNetwork1() {
		Network<Double> network = new Network<Double>();
		network.setTopology(create_2_2_1_TestTopology());
		network.setFunction(createTestNetworkFunction());
		network.setWeightSetting(createTestNetworkWeightSetting1());
		network.setDefaultBiasState(1d);
		return network;
	}

	private Network<Double> createTestNetwork2() {
		Network<Double> network = new Network<Double>();
		network.setTopology(create_2_2_1_TestTopology());
		network.setFunction(createTestNetworkFunction());
		network.setWeightSetting(createTestNetworkWeightSetting2());
		network.setDefaultBiasState(1d);
		return network;
	}

	private Network<Double> createTestNetwork3() {
		Network<Double> network = new Network<Double>();
		network.setTopology(create_2_3_1_TestTopology());
		network.setFunction(createTestNetworkFunction());
		network.setWeightSetting(createTestNetworkWeightSetting3d());
		network.setDefaultBiasState(1d);
		return network;
	}

	private TemplateTopology create_2_2_1_TestTopology() {
		// input layer
		TemplateTopology inputLayer = new TemplateTopology();
		inputLayer.setIndex(1);
		inputLayer.setNodesNum(2);

		// hidden layer
		TemplateTopology hiddenLayer = new TemplateTopology();
		hiddenLayer.setIndex(2);
		hiddenLayer.setNodesNum(2);
		hiddenLayer.setGenerateBias(true);

		// output layer
		TemplateTopology outputLayer = new TemplateTopology();
		outputLayer.setIndex(3);
		outputLayer.setNodesNum(1);
		outputLayer.setGenerateBias(true);

		// top-level topology
		TemplateTopology topLevelTopology = new TemplateTopology();
		topLevelTopology.setIntraLayerAllEdges(true);
		topLevelTopology.addLayer(inputLayer);
		topLevelTopology.addLayer(hiddenLayer);
		topLevelTopology.addLayer(outputLayer);

		return topLevelTopology;
	}

	private TemplateTopology create_2_3_1_TestTopology() {
		// input layer
		TemplateTopology inputLayer = new TemplateTopology();
		inputLayer.setIndex(1);
		inputLayer.setNodesNum(2);

		// hidden layer
		TemplateTopology hiddenLayer = new TemplateTopology();
		hiddenLayer.setIndex(2);
		hiddenLayer.setNodesNum(3);
		hiddenLayer.setGenerateBias(true);

		// output layer
		TemplateTopology outputLayer = new TemplateTopology();
		outputLayer.setIndex(3);
		outputLayer.setNodesNum(1);
		outputLayer.setGenerateBias(true);

		// top-level topology
		TemplateTopology topLevelTopology = new TemplateTopology();
		topLevelTopology.setIntraLayerAllEdges(true);
		topLevelTopology.addLayer(inputLayer);
		topLevelTopology.addLayer(hiddenLayer);
		topLevelTopology.addLayer(outputLayer);

		return topLevelTopology;
	}

	private NetworkWeightSetting<Double> createTestNetworkWeightSetting1() {
		LayeredNetworkWeightSetting<Double> layeredNetworkWeightSetting = new LayeredNetworkWeightSetting<Double>();

		FixedNetworkWeightSetting<Double> hiddenLayerWeights = new FixedNetworkWeightSetting<Double>();
		hiddenLayerWeights.addWeight(-0.4802); // bias
		hiddenLayerWeights.addWeight(-0.9395);
		hiddenLayerWeights.addWeight(0.3926);

		hiddenLayerWeights.addWeight(0.9240);  // bias
		hiddenLayerWeights.addWeight(0.0394); 
		hiddenLayerWeights.addWeight(-0.8819);

		
		FixedNetworkWeightSetting<Double> outputLayerWeights = new FixedNetworkWeightSetting<Double>();
		outputLayerWeights.addWeight(0.0804);  // bias
		outputLayerWeights.addWeight(0.7801);
		outputLayerWeights.addWeight(-0.3396);

		layeredNetworkWeightSetting.addLayer(hiddenLayerWeights);
		layeredNetworkWeightSetting.addLayer(outputLayerWeights);
		return layeredNetworkWeightSetting;
	}

	private NetworkWeightSetting<Double> createTestNetworkWeightSetting2() {
		LayeredNetworkWeightSetting<Double> layeredNetworkWeightSetting = new LayeredNetworkWeightSetting<Double>();

		FixedNetworkWeightSetting<Double> hiddenLayerWeights = new FixedNetworkWeightSetting<Double>();
		hiddenLayerWeights.addWeight(-0.9980); // bias
		hiddenLayerWeights.addWeight(-0.6803);
		hiddenLayerWeights.addWeight(0.3337);

		hiddenLayerWeights.addWeight(-0.1637);  // bias
		hiddenLayerWeights.addWeight(-0.9642); 
		hiddenLayerWeights.addWeight(-0.7606);
		
		FixedNetworkWeightSetting<Double> outputLayerWeights = new FixedNetworkWeightSetting<Double>();
		outputLayerWeights.addWeight(-0.0230);  // bias
		outputLayerWeights.addWeight(0.9043);
		outputLayerWeights.addWeight(0.9517);

		layeredNetworkWeightSetting.addLayer(hiddenLayerWeights);
		layeredNetworkWeightSetting.addLayer(outputLayerWeights);
		return layeredNetworkWeightSetting;
	}

	private NetworkWeightSetting<Double> createTestNetworkWeightSetting3() {
		LayeredNetworkWeightSetting<Double> layeredNetworkWeightSetting = new LayeredNetworkWeightSetting<Double>();

		FixedNetworkWeightSetting<Double> hiddenLayerWeights = new FixedNetworkWeightSetting<Double>();
		hiddenLayerWeights.addWeight(-0.6423); // bias
		hiddenLayerWeights.addWeight(0.3279);
		hiddenLayerWeights.addWeight(-0.2216);

		hiddenLayerWeights.addWeight(0.0351);  // bias
		hiddenLayerWeights.addWeight(0.4800); 
		hiddenLayerWeights.addWeight(0.6353);

		hiddenLayerWeights.addWeight(0.2540);  // bias
		hiddenLayerWeights.addWeight(0.2007); 
		hiddenLayerWeights.addWeight(-0.8300);

		FixedNetworkWeightSetting<Double> outputLayerWeights = new FixedNetworkWeightSetting<Double>();
		outputLayerWeights.addWeight(0.8264);  // bias
		outputLayerWeights.addWeight(0.8447);
		outputLayerWeights.addWeight(-0.8928);
		outputLayerWeights.addWeight(0.0540);

		layeredNetworkWeightSetting.addLayer(hiddenLayerWeights);
		layeredNetworkWeightSetting.addLayer(outputLayerWeights);
		return layeredNetworkWeightSetting;
	}

	private NetworkWeightSetting<Double> createTestNetworkWeightSetting3b() {
		FixedNetworkWeightSetting<Double> mergedWeights = new FixedNetworkWeightSetting<Double>();
		mergedWeights.addWeight(-0.6423); // bias
		mergedWeights.addWeight(0.3279);
		mergedWeights.addWeight(-0.2216);

		mergedWeights.addWeight(0.0351);  // bias
		mergedWeights.addWeight(0.4800); 
		mergedWeights.addWeight(0.6353);

		mergedWeights.addWeight(0.2540);  // bias
		mergedWeights.addWeight(0.2007); 
		mergedWeights.addWeight(-0.8300);

		mergedWeights.addWeight(0.8264);  // bias
		mergedWeights.addWeight(0.8447);
		mergedWeights.addWeight(-0.8928);
		mergedWeights.addWeight(0.0540);

		return mergedWeights;
	}

	private NetworkWeightSetting<Double> createTestNetworkWeightSetting3c() {
		FixedNetworkWeightSetting<Double> mergedWeights = new FixedNetworkWeightSetting<Double>();
		mergedWeights.setSettingOrder(FixedNetworkWeightSettingOrder.ImmutableGlobalFirst);
		mergedWeights.addWeight(-0.6423); // bias
		mergedWeights.addWeight(0.0351);  // bias
		mergedWeights.addWeight(0.2540);  // bias
		mergedWeights.addWeight(0.8264);  // bias
		mergedWeights.addWeight(0.3279);
		mergedWeights.addWeight(-0.2216);
		mergedWeights.addWeight(0.4800);
		mergedWeights.addWeight(0.6353);
		mergedWeights.addWeight(0.2007);
		mergedWeights.addWeight(-0.8300);
		mergedWeights.addWeight(0.8447);
		mergedWeights.addWeight(-0.8928);
		mergedWeights.addWeight(0.0540);

		return mergedWeights;
	}

	private NetworkWeightSetting<Double> createTestNetworkWeightSetting3d() {
		FixedNetworkWeightSetting<Double> mergedWeights = new FixedNetworkWeightSetting<Double>();
		mergedWeights.setSettingOrder(FixedNetworkWeightSettingOrder.ImmutableWithinLayerFirst);
		mergedWeights.addWeight(-0.6423); // bias
		mergedWeights.addWeight(0.0351);  // bias
		mergedWeights.addWeight(0.2540);  // bias

		mergedWeights.addWeight(0.3279);
		mergedWeights.addWeight(-0.2216);
		mergedWeights.addWeight(0.4800); 
		mergedWeights.addWeight(0.6353);
		mergedWeights.addWeight(0.2007); 
		mergedWeights.addWeight(-0.8300);

		mergedWeights.addWeight(0.8264);  // bias
		mergedWeights.addWeight(0.8447);
		mergedWeights.addWeight(-0.8928);
		mergedWeights.addWeight(0.0540);

		return mergedWeights;
	}

	private NetworkFunction<Double> createTestNetworkFunction() {
		NetworkFunction<Double> inputLayerFunction = new NetworkFunction<Double>();
//		layer1Function.setMultiComponentUpdaterType(MultiStateUpdateType.Synch);
//		layer1Function.setStatesWeightsIntegratorType(StatesWeightsIntegratorType.LinearSum);
//		layer1Function.setFunction(new Expression<Double, Double>("tanh(x0)", Double.class, Double.class));

		NetworkFunction<Double> regularLayerFunction = new NetworkFunction<Double>();
		regularLayerFunction.setMultiComponentUpdaterType(MultiStateUpdateType.Sync);
		regularLayerFunction.setStatesWeightsIntegratorType(StatesWeightsIntegratorType.LinearSum);
		regularLayerFunction.setFunction(new Expression<Double, Double>("tanh(x0)", Double.class, Double.class));

		NetworkFunction<Double> topLevelFunction = new NetworkFunction<Double>();
		topLevelFunction.setMultiComponentUpdaterType(MultiStateUpdateType.AsyncFixedOrder);
		topLevelFunction.addLayerFunction(regularLayerFunction);
		topLevelFunction.addLayerFunction(regularLayerFunction);
		topLevelFunction.addLayerFunction(regularLayerFunction);

		return topLevelFunction;
	}

	private List<List<Double>> createTestNetworkConfigurations1() {
		final Double[][] configArrays = new Double[][] {
				{-0.5406, 0.5118},
				{-0.7721, 0.2066},
				{-0.3782, 0.5665},
				{-0.5431, -0.7721},
				{0.3040, 0.9571},
				{-0.8677, 0.6972},
				{-0.4491, -0.8987},
				{-0.4364, -0.0676},
				{ 0.7601, -0.3487},
				{-0.1113, 0.2604}};
		List<List<Double>> configs = new ArrayList<List<Double>>();
		for (Double[] config : configArrays) {
			configs.add(Arrays.asList(config));			
		}
		return configs;
	}

	private List<List<Double>> createTestNetworkConfigurations2() {
		final Double[][] configArrays = new Double[][] {
		   {-0.9382, 0.3607},
		   {-0.0122, -0.2424},
		   {0.7254, 0.2639},
		   {-0.5142, -0.5135},
		   {0.6685, 0.1428},
		   {0.6272, 0.9635},
		   {0.2579, 0.6994},
		   {-0.9955, -0.4331},
		   {-0.2406, 0.3649},
		   { 0.8088, -0.2838}};
		List<List<Double>> configs = new ArrayList<List<Double>>();
		for (Double[] config : configArrays) {
			configs.add(Arrays.asList(config));			
		}
		return configs;
	}

	private List<List<Double>> createTestNetworkConfigurations3() {
		final Double[][] configArrays = new Double[][] {
		   {-0.7623, -0.9003},
		   {-0.2397, 0.3706},
		   { 0.6257, 0.2406},
		   {-0.5118, 0.4934},
		   {0.7688, 0.9545},
		   {0.4253, -0.2322},
		   {-0.2437, -0.4796},
		   {-0.5022, 0.7549},
		   {-0.4943, 0.6122},
		   { 0.5345, -0.0778}};
		List<List<Double>> configs = new ArrayList<List<Double>>();
		for (Double[] config : configArrays) {
			configs.add(Arrays.asList(config));			
		}
		return configs;
	}

	private void reportNetworkBO(LayeredNetworkBO<?> layeredNetworkBO) {
		int layerIndex = 1;
		for (NetworkBO<?> layerBO : layeredNetworkBO.getLayers()) {
			System.out.println("Layer :" + layerIndex);
			System.out.println("--------------");
			for (NodeBO<?> nodeNodeBO : layerBO.getNodes()) {
				TopologicalNode topologicalNode = nodeNodeBO.getTopologicalNode();
				if (topologicalNode.isBias()) {
					System.out.println("Node (bias):" + topologicalNode.getIndex());
				} else {
					System.out.println("Node:" + topologicalNode.getIndex());
				}
				for (NodeBOEdge<?> inEdge : nodeNodeBO.getInEdges()) {
					final TopologicalNode startNode = inEdge.getStart().getTopologicalNode();
					System.out.println("Edge from :" + startNode.getTopology().getIndex() + "," + startNode.getIndex() + ", w: " + inEdge.getWeight());					
				}
			}
			System.out.println("--------------");
			System.out.println();
			layerIndex++;
		}
	}
}