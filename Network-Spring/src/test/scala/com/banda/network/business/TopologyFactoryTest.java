package com.bnd.network.business;

import java.util.Iterator;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.bnd.core.metrics.MetricsType;
import com.bnd.network.domain.FlatTopology;
import com.bnd.network.domain.LayeredTopology;
import com.bnd.network.domain.SpatialNeighbor;
import com.bnd.network.domain.SpatialNeighborhood;
import com.bnd.network.domain.SpatialTopology;
import com.bnd.network.domain.TemplateTopology;
import com.bnd.network.domain.TopologicalNode;
import com.bnd.network.domain.Topology;

/**
 * @author © Peter Banda
 * @since 2012  
 */
public class TopologyFactoryTest extends NetworkTest {

	@Autowired
	private TopologyFactory topologyFactory;

	public void testCreateTopology1() {
		final TemplateTopology testTemplateTopology = createLayeredTopologyTestData1();
		final Topology topology = topologyFactory.apply(testTemplateTopology);

		checkTopology(topology, testTemplateTopology);
		reportLayeredTopology(topology);
	}

	public void testCreateTopology2() {
		final TemplateTopology testTemplateTopology = createLayeredTopologyTestData2();
		final Topology topology = topologyFactory.apply(testTemplateTopology);

		checkTopology(topology, testTemplateTopology);
		reportLayeredTopology(topology);
	}

	public void testCreateTopology3() {
		final TemplateTopology testTemplateTopology = createLayeredTopologyTestData3();
		final Topology topology = topologyFactory.apply(testTemplateTopology);

		checkTopology(topology, testTemplateTopology);
		reportLayeredTopology(topology);
	}

  @Test
  public void testCreateReservoirTopology() {
    final TemplateTopology testTemplateTopology = createLayeredReservoirTopologyTestData();
    final Topology topology = topologyFactory.apply(testTemplateTopology);

    checkTopology(topology, testTemplateTopology);
    reportLayeredTopology(topology);
  }

	public void testCreateSpatialTopology1() {
		final SpatialTopology testSpatialTopology = createSpatialTopologyTestData1();
		final Topology topology = topologyFactory.apply(testSpatialTopology);

		checkTopology(topology, testSpatialTopology);
		reportSpatialTopology(topology);
	}

	public void testCreateSpatialTopology2() {
		final SpatialTopology testSpatialTopology = createSpatialTopologyTestData2();
		final Topology topology = topologyFactory.apply(testSpatialTopology);

		checkTopology(topology, testSpatialTopology);
		reportSpatialTopology(topology);
	}

	public void testCreateSpatialTopology3() {
		final SpatialTopology testSpatialTopology = createSpatialTopologyTestData3();
		final Topology topology = topologyFactory.apply(testSpatialTopology);

		checkTopology(topology, testSpatialTopology);
		reportSpatialTopology(topology);
	}

	public void testCreateSpatialTopology4() {
		final SpatialTopology testSpatialTopology = createSpatialTopologyTestData4();
		final Topology topology = topologyFactory.apply(testSpatialTopology);

		checkTopology(topology, testSpatialTopology);
		reportSpatialTopology(topology);
	}

	private void checkTopology(final Topology topology, final Topology sourceTopology) {
		assertNotNull(topology);
		assertNotEmpty(topology.getAllNodes());

		if (sourceTopology.isTemplate()) {
			checkTemplateTopology(topology, (TemplateTopology) sourceTopology);
		} else if (sourceTopology.hasLayers()) {
			checkLayeredTopology(topology, (LayeredTopology) sourceTopology);
		} else if (sourceTopology.isSpatial()){
			checkSpatialTopology(topology, (SpatialTopology) sourceTopology);
		} else {
			checkFlatTopology(topology, (FlatTopology) sourceTopology);
		}
	}

	private void checkTemplateTopology(final Topology topology, final TemplateTopology sourceTemplateTopology) {
		if (sourceTemplateTopology.isGenerateBias()) {
			assertNotEmpty(topology.getBiasNodes());
		}

		if (sourceTemplateTopology.hasLayers()) {
			// it's layered
			assertEquals(sourceTemplateTopology.getLayers().size(), topology.getLayers().size());
			Iterator<Topology> sourceLayerIterator = sourceTemplateTopology.getLayers().iterator();
			for (Topology layer : topology.getLayers()) {
				checkTopology(layer, sourceLayerIterator.next());
			}			
		}
	}

	private void checkLayeredTopology(final Topology topology, final LayeredTopology sourceLayeredTopology) {
		// TODO
	}

	private void checkFlatTopology(final Topology topology, final FlatTopology sourceFlatTopology) {
		assertEquals(sourceFlatTopology.getNonBiasNodes(), topology.getNonBiasNodes());
	}

	private void checkSpatialTopology(final Topology topology, final SpatialTopology sourceSpatialTopology) {
		for (TopologicalNode topologicalNode : topology.getAllNodes()) {
			assertNotNull(topologicalNode.getLocation());
		}
//		assertEquals(topology.getAllNodes().size(), sourceFlatTopology.get...));
	}

	private TemplateTopology createLayeredTopologyTestData1() {
		// layer 1
		FlatTopology layer1 = new FlatTopology();
		layer1.setIndex(1);
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
		layer2.setIndex(2);
		layer2.setNodesNum(20);
		layer2.setAllowSelfEdges(true);
		layer2.setAllowMultiEdges(false);
		layer2.setInEdgesNum(3);

		// top-level topology
		TemplateTopology topLevelTopology = new TemplateTopology();
		topLevelTopology.setIntraLayerInEdgesNum(1);
		topLevelTopology.addLayer(layer1);
		topLevelTopology.addLayer(layer2);

		return topLevelTopology;
	}

	private TemplateTopology createLayeredTopologyTestData2() {
		// input layer
		TemplateTopology inputLayer = new TemplateTopology();
		inputLayer.setIndex(1);
		inputLayer.setNodesNum(2);

		// hidden layer
		FlatTopology hiddenLayer = new FlatTopology();
		hiddenLayer.setIndex(2);
		TopologicalNode bias = new TopologicalNode();
		bias.setBias(true);
		TopologicalNode node1 = new TopologicalNode();
		TopologicalNode node2 = new TopologicalNode();
		bias.setIndex(0);
		node1.setIndex(1);
		node2.setIndex(2);
		hiddenLayer.addNode(bias);
		hiddenLayer.addNode(node1);
		hiddenLayer.addNode(node2);
		node1.addNewInEdgeFrom(bias);
		node2.addNewInEdgeFrom(bias);

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

	private TemplateTopology createLayeredTopologyTestData3() {
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

  private TemplateTopology createLayeredReservoirTopologyTestData() {
    int inputNum = 2;
    int reservoirNum = 5;
    int outputNum = 6;

    // layer 1
    TemplateTopology inputLayer = new TemplateTopology();
    inputLayer.setIndex(1);
    inputLayer.setNodesNum(inputNum);

    // layer 2
    TemplateTopology reservoir = new TemplateTopology();
    reservoir.setIndex(2);
    reservoir.setNodesNum(reservoirNum);
    reservoir.setAllowSelfEdges(true);
    reservoir.setAllowMultiEdges(false);
    reservoir.setInEdgesNum(3);
    reservoir.setGenerateBias(true);

    // layer 3
    TemplateTopology readout = new TemplateTopology();
    readout.setIndex(3);
    readout.setNodesNum(outputNum);
    readout.setGenerateBias(true);

    // top-level topology
    TemplateTopology topLevelTopology = new TemplateTopology();
    topLevelTopology.setIntraLayerAllEdges(true);
    topLevelTopology.addLayer(inputLayer);
    topLevelTopology.addLayer(reservoir);
    topLevelTopology.addLayer(readout);

    return topLevelTopology;
  }

	private SpatialTopology createSpatialTopologyTestData1() {
		SpatialTopology spatialTopology = new SpatialTopology();
		spatialTopology.setMetricsType(MetricsType.Manhattan);
		spatialTopology.addSize(4);
		spatialTopology.addSize(5);
		spatialTopology.setRadius(1);

		return spatialTopology;
	}

	private SpatialTopology createSpatialTopologyTestData2() {
		SpatialTopology spatialTopology = new SpatialTopology();
		spatialTopology.setMetricsType(MetricsType.Manhattan);
		spatialTopology.addSize(5);
		spatialTopology.addSize(6);
		spatialTopology.setTorusFlag(true);
		spatialTopology.setRadius(2);

		return spatialTopology;
	}

	private SpatialTopology createSpatialTopologyTestData3() {
		SpatialTopology spatialTopology = new SpatialTopology();
		spatialTopology.setMetricsType(MetricsType.Manhattan);
		spatialTopology.addSize(5);
		spatialTopology.addSize(6);
		spatialTopology.setTorusFlag(true);

		SpatialNeighborhood spatialNeighborhood = new SpatialNeighborhood();
		spatialTopology.setNeighborhood(spatialNeighborhood);

		SpatialNeighbor spatialNeighbor1 = new SpatialNeighbor();
		spatialNeighbor1.setIndex(0);
		spatialNeighbor1.addCoordinateDiff(-1);
		spatialNeighbor1.addCoordinateDiff(-1);
		spatialNeighborhood.addNeighbor(spatialNeighbor1);

		SpatialNeighbor spatialNeighbor2 = new SpatialNeighbor();
		spatialNeighbor2.setIndex(1);
		spatialNeighbor2.addCoordinateDiff(1);
		spatialNeighbor2.addCoordinateDiff(0);
		spatialNeighborhood.addNeighbor(spatialNeighbor2);

		SpatialNeighbor spatialNeighbor3 = new SpatialNeighbor();
		spatialNeighbor3.setIndex(1);
		spatialNeighbor3.addCoordinateDiff(0);
		spatialNeighbor3.addCoordinateDiff(0);
		spatialNeighborhood.addNeighbor(spatialNeighbor3);

		SpatialNeighbor spatialNeighbor4 = new SpatialNeighbor();
		spatialNeighbor4.setIndex(1);
		spatialNeighbor4.addCoordinateDiff(1);
		spatialNeighbor4.addCoordinateDiff(1);
		spatialNeighborhood.addNeighbor(spatialNeighbor4);

		return spatialTopology;
	}

	private SpatialTopology createSpatialTopologyTestData4() {
		SpatialTopology spatialTopology = new SpatialTopology();
		spatialTopology.setMetricsType(MetricsType.Max);
		spatialTopology.addSize(4);
		spatialTopology.addSize(4);
		spatialTopology.setTorusFlag(true);
		spatialTopology.setItsOwnNeighor(true);
		spatialTopology.setRadius(1);

		return spatialTopology;
	}

	private void reportLayeredTopology(Topology topology) {
		for (Topology layer : topology.getLayers()) {
			System.out.println("Layer :" + layer.getIndex());
			System.out.println("--------------");
			for (TopologicalNode topologicalNode : layer.getAllNodes()) {
				if (topologicalNode.isBias()) {
					System.out.println("Node (bias):" + topologicalNode.getIndex());
				} else {
					System.out.println("Node:" + topologicalNode.getIndex());
				}
				for (TopologicalNode neighbor : topologicalNode.getInNeighbors()) {
					System.out.println("Edge from :" + neighbor.getTopology().getIndex() + "," + neighbor.getIndex());					
				}
			}
			System.out.println("--------------");
			System.out.println();
		}
	}

	private void reportSpatialTopology(Topology topology) {
		for (TopologicalNode topologicalNode : topology.getAllNodes()) {
			System.out.println("Node:" + topologicalNode.getIndex() + ", location: " + topologicalNode.getLocation());
			for (TopologicalNode neighbor : topologicalNode.getInNeighbors()) {
				System.out.println("Edge from :" + neighbor.getIndex() + ", location: " + neighbor.getLocation());
			}
		}
		System.out.println("");
	}
}