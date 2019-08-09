package com.bnd.network.business;

import java.io.Serializable;
import java.util.*;

import com.bnd.core.ListComparator;
import com.bnd.core.Pair;
import com.bnd.core.metrics.Metrics;
import com.bnd.core.metrics.MetricsFactory;
import com.bnd.core.metrics.MetricsType;
import com.bnd.core.util.RandomUtil;
import com.bnd.math.business.rand.RandomDistributionProvider;
import com.bnd.math.business.rand.RandomDistributionProviderFactory;
import com.bnd.math.domain.rand.DiscreteDistribution;
import com.bnd.math.domain.rand.RandomDistribution;
import com.bnd.network.BndNetworkException;
import com.bnd.network.domain.FlatTopology;
import com.bnd.network.domain.LayeredTopology;
import com.bnd.network.domain.SpatialNeighbor;
import com.bnd.network.domain.SpatialTopology;
import com.bnd.network.domain.TemplateTopology;
import com.bnd.network.domain.TopologicalNode;
import com.bnd.network.domain.Topology;

/**
 * @author © Peter Banda
 * @since 2012  
 */
final public class TopologyFactoryImpl implements TopologyFactory, Serializable {

	private final MetricsFactory<Integer> metricsFactory;

	public TopologyFactoryImpl(MetricsFactory<Integer> metricsFactory) {
		this.metricsFactory = metricsFactory;
	}

	@Override
	public Topology apply(Topology originalTopology) {
		Topology newTopology;
		// first create topology without considering layer hierarchy
		Collection<Topology> layers = originalTopology.getLayers();

		boolean generateBias = false;
		// if template topology check whether template layers should be generated
		if (originalTopology.isTemplate()) {
			TemplateTopology originalTemplateTopology = (TemplateTopology) originalTopology;
			if (originalTemplateTopology.getLayersNum() != null) {
				layers = createLayers(originalTemplateTopology);
			}
			generateBias = originalTemplateTopology.isGenerateBias();
		}

		if (layers == null || layers.isEmpty()) {
			newTopology = new FlatTopology();			
		} else {
			newTopology = new LayeredTopology();
			LayeredTopology newLayeredTopology = (LayeredTopology) newTopology;
			// create layers as topologies recursively
			for (Topology layer : layers) {
				if (generateBias && layer.isTemplate()) {
					((TemplateTopology) layer).setGenerateBias(true);
				}
				newLayeredTopology.addLayer(apply(layer));
			}
		}
		newTopology.copyFrom(originalTopology);

		// add nodes if needed
		addNodes(newTopology, originalTopology);

		// add connections
		addConnections(newTopology, originalTopology);
		return newTopology;
	}

	protected Collection<Topology> createLayers(TemplateTopology originalTopology) {
		Collection<Topology> layers = new ArrayList<Topology>();
		if (originalTopology.hasLayers()) {
			throw new BndNetworkException("The number of layer to generate and fixed layers must not be defined at the same time for topology '" + originalTopology.getId() + "'.");
		}
		if (originalTopology.getNodesNum() != null && originalTopology.getNodesPerLayer() != null) {
			throw new BndNetworkException("The number of nodes and the number of nodes per layer must not be defined at the same time for topology '" + originalTopology.getId() + "'.");
		}
		if (originalTopology.getNodesNum() == null && originalTopology.getNodesPerLayer() == null) {
			throw new BndNetworkException("The number of nodes or the number of nodes per layer expected for topology '" + originalTopology.getId() + "'.");
		}
		Integer nodesPerLayer = originalTopology.getNodesPerLayer();
		if (nodesPerLayer == null) {
			nodesPerLayer = originalTopology.getNodesNum() / originalTopology.getLayersNum();
		}
		for (int index = 0; index < originalTopology.getLayersNum(); index++) {
			TemplateTopology layer = new TemplateTopology();
			layer.setIndex(index);
			layer.setNodesNum(nodesPerLayer);
			layers.add(layer);
		}
		return layers;
	}

	protected void addNodes(Topology newTopology, Topology originalTopology) {
		if (newTopology.hasLayers()) {
			// if layers exist then there are no internal nodes but all nodes are included in layers
			return;
		}
		if (originalTopology.isTemplate()) {
			addNodes((FlatTopology) newTopology, (TemplateTopology) originalTopology);
		} else if (originalTopology.isSpatial()) {
			addNodes((FlatTopology) newTopology, (SpatialTopology) originalTopology);
		} else {
			addNodes((FlatTopology) newTopology, (FlatTopology) originalTopology);
		}
	}

	protected void addNodes(FlatTopology newTopology, FlatTopology originalTopology) {
		List<TopologicalNode> nodes = new ArrayList<TopologicalNode>();
		nodes.addAll(originalTopology.getAllNodes());
		newTopology.setNodes(nodes);
	}

	protected void addNodes(FlatTopology newTopology, TemplateTopology originalTemplateTopology) {
		Integer nodesNum = originalTemplateTopology.getNodesNum();
		if (nodesNum == null) {
			throw new BndNetworkException("The template topology '" + originalTemplateTopology.getId() + "' provided to create a flat topology must contain some nodes.");
		}
		int index = 0;
		if (originalTemplateTopology.isGenerateBias()) {
			TopologicalNode bias = new TopologicalNode();
			bias.setBias(true);
			bias.setIndex(index);
			newTopology.addNode(bias);
			index++;
			nodesNum++;
		}
		for (; index < nodesNum; index++) {
			TopologicalNode node = new TopologicalNode();
			node.setIndex(index);
			newTopology.addNode(node);
		}
	}

	protected void addNodes(FlatTopology newTopology, SpatialTopology originalSpatialTopology) {
		if (originalSpatialTopology.getSizes().isEmpty()) {
			throw new BndNetworkException("The spatial topology '" + originalSpatialTopology.getId() + "' provided to create a flat topology must contain some sizes.");
		}
		int nodesNum = 1;
		for (int size : originalSpatialTopology.getSizes()) {
			nodesNum *= size;
		}
		for (int index = 0; index < nodesNum; index++) {
			TopologicalNode node = new TopologicalNode();
			node.setIndex(index);
			newTopology.addNode(node);
		}
	}

	protected void addConnections(Topology newTopology, Topology originalTopology) {
		if (originalTopology.isTemplate()) {
			if (newTopology.hasLayers()) {
				addConnectionsLayered((LayeredTopology) newTopology, (TemplateTopology) originalTopology);
			} else {
				addConnectionsFlat((FlatTopology) newTopology, (TemplateTopology) originalTopology);
			}
		} else if (originalTopology.isSpatial()) {
			addConnectionsSpatial((FlatTopology) newTopology, (SpatialTopology) originalTopology);
		}
	}

	protected void addConnectionsFlat(FlatTopology newTopology, TemplateTopology originalTemplateTopology) {
		final boolean allowMultiEdges = originalTemplateTopology.isAllowMultiEdges();
		final boolean allowSelfEdges = originalTemplateTopology.isAllowSelfEdges();
		final Integer inEdgesNum = originalTemplateTopology.getInEdgesNum();
        final boolean preferentialAttachment = originalTemplateTopology.isPreferentialAttachment();
        final RandomDistribution<Integer> inEdgesDistribution = originalTemplateTopology.getInEdgesDistribution();
        final Integer edgesNum = originalTemplateTopology.getEdgesNum();

		// first add connections from bias nodes
		for (TopologicalNode biasNode : newTopology.getBiasNodes()) {
			for (TopologicalNode regularNode : newTopology.getNonBiasNodes()) {
				regularNode.addNewInEdgeFrom(biasNode);	
			}
		}		

		// add connections between non-bias nodes

		if (originalTemplateTopology.isAllEdges()) {
            // all edges
			for (TopologicalNode topologicalNode : newTopology.getNonBiasNodes())
				addAllConnections(newTopology, topologicalNode, allowSelfEdges);

		} else if (inEdgesNum != null) {
            // in edges num
            if (!preferentialAttachment)
			    for (TopologicalNode topologicalNode : newTopology.getNonBiasNodes())
				    addConnections(newTopology, topologicalNode, inEdgesNum, allowMultiEdges, allowSelfEdges);
            else
                addConnectionsAsPreferentialAttachment(newTopology.getNonBiasNodes(), inEdgesNum, allowMultiEdges);

		} else if (inEdgesDistribution != null) {
            // in edges distribution
            RandomDistributionProvider<Integer> inEdgesRDP = RandomDistributionProviderFactory.apply(inEdgesDistribution);
            for (TopologicalNode topologicalNode : newTopology.getNonBiasNodes())
                  addConnections(newTopology, topologicalNode, inEdgesRDP.next(), allowMultiEdges, allowSelfEdges);

        } else if (edgesNum != null) {
            // edges num
            if (allowMultiEdges)
              addConnectionsWithMultiAllowed(newTopology.getNonBiasNodes(), newTopology.getNonBiasNodes(), edgesNum, allowSelfEdges);
            else
              addConnectionsWithMultiNotAllowed(newTopology.getNonBiasNodes(), newTopology.getNonBiasNodes(), edgesNum, allowSelfEdges);
        }
	}

	protected void addConnectionsLayered(LayeredTopology newTopology, TemplateTopology originalTemplateTopology) {
		final boolean allEdges = originalTemplateTopology.isIntraLayerAllEdges();
		final Integer inEdgesNum = originalTemplateTopology.getIntraLayerInEdgesNum();
		final boolean allowMultiEdges = originalTemplateTopology.isAllowMultiEdges();
    final List<TemplateTopology.IntraLayerEdgeSpec> intraLayerEdgeSpecs = originalTemplateTopology.getIntraLayerEdgeSpecs();

    if (intraLayerEdgeSpecs.isEmpty()) {
      if (!allEdges && inEdgesNum == null) {
        throw new BndNetworkException("The template topology '" + originalTemplateTopology.getId() + "' provided to create a layered topology must contain the number of inner layer in-edges per node or have set the all inner edges flag.");
      }
      if (allEdges && inEdgesNum != null) {
        throw new BndNetworkException("The template topology '" + originalTemplateTopology.getId() + "' provided to create a layered topology must not contain (1) the number of inner layer in-edges per node and (2) have set the all inner edges flag at the same time.");
      }

      Topology previousLayer = null;
      for (Topology layer : newTopology.getLayers()) {
        if (layer.isTemplate()) {
          throw new BndNetworkException("At this point all layers should be initialized topologies, not templates.");
        }
        if (previousLayer != null) {
          for (TopologicalNode topologicalNode : layer.getNonBiasNodes()) {
            if (allEdges) {
              addAllConnections(previousLayer, topologicalNode, false);
            } else {
              addConnections(previousLayer, topologicalNode, inEdgesNum, allowMultiEdges, false);
            }
          }
        }
        previousLayer = layer;
      }
    } else {
      Iterator<TemplateTopology.IntraLayerEdgeSpec> specIterator = intraLayerEdgeSpecs.iterator();
      Topology previousLayer = null;
      for (Topology layer : newTopology.getLayers()) {
        if (layer.isTemplate()) {
          throw new BndNetworkException("At this point all layers should be initialized topologies, not templates.");
        }
        if (previousLayer != null) {
          final TemplateTopology.IntraLayerEdgeSpec spec = specIterator.next();
          if (spec.isAllEdges()) {
            for (TopologicalNode topologicalNode : layer.getNonBiasNodes())
              addAllConnections(previousLayer, topologicalNode, false);

          } else if (spec.getInEdgesNum() != null) {
            for (TopologicalNode topologicalNode : layer.getNonBiasNodes())
              addConnections(previousLayer, topologicalNode, spec.getInEdgesNum(), spec.isAllowMultiEdges(), false);

          } else if (spec.getEdgesNum() != null) {
            if (spec.isAllowMultiEdges())
              addConnectionsWithMultiAllowed(previousLayer.getNonBiasNodes(), layer.getNonBiasNodes(), spec.getEdgesNum(), false);
            else
              addConnectionsWithMultiNotAllowed(previousLayer.getNonBiasNodes(), layer.getNonBiasNodes(), spec.getEdgesNum(), false);
          }
        }
        previousLayer = layer;
      }
    }
	}

	protected void addConnectionsSpatial(FlatTopology newTopology, SpatialTopology originalSpatialTopology) {
		Map<List<Integer>, TopologicalNode> pointNodeMap = new HashMap<List<Integer>, TopologicalNode>();
		List<TopologicalNode> nodesToHandle = new ArrayList<TopologicalNode>();
		Iterator<TopologicalNode> nodesIterator = newTopology.getAllNodes().iterator();

		int dimensions = originalSpatialTopology.getDimensionsNumber();
		List<Integer> zeroPoint = new ArrayList<Integer>();
		for (int i = 0; i < dimensions; i++) {
			zeroPoint.add(0);
		}
		TopologicalNode firstNode = nodesIterator.next();
		firstNode.setLocation(zeroPoint);
		pointNodeMap.put(firstNode.getLocation(), firstNode);
		nodesToHandle.add(firstNode);

		Metrics<Integer> metrics = null;
		if (originalSpatialTopology.getMetricsType() != null) {
			metrics = metricsFactory.createInstance(originalSpatialTopology.getMetricsType());
		} else {
			metrics = metricsFactory.createInstance(MetricsType.Manhattan);
		}
		while (!nodesToHandle.isEmpty()) {
			TopologicalNode nodeToHandle = nodesToHandle.remove(0);

			// get the locations of neighbors
			Collection<List<Integer>> neighborLocations = null;
			if (originalSpatialTopology.getRadius() != null) {
				neighborLocations = getNeighborLocationsForRadius(nodeToHandle.getLocation(), metrics, originalSpatialTopology);
			} else if (originalSpatialTopology.getNeighborhood() != null) {
				neighborLocations = getNeighborLocationsForNeighborhood(nodeToHandle.getLocation(), metrics, originalSpatialTopology);
			} else {
				throw new BndNetworkException("Either radius or a neighborhood expected for spatial topology " + originalSpatialTopology.getId());
			}


			for (List<Integer> neighborLocation : neighborLocations) {
				if (neighborLocation == null) {
					throw new BndNetworkException("Neighbor location expected.");
				}
				TopologicalNode neighbor = pointNodeMap.get(neighborLocation);
				if (neighbor == null) {
					// node at given location has not been found,
					// thus we take first available node from collection and attach it to that location
					while ((neighbor == null || neighbor.hasLocation()) && nodesIterator.hasNext()) {
						neighbor = nodesIterator.next();
					}
					if (neighbor == null) {
						throw new BndNetworkException("No node available to become a neighbor at location " + neighborLocation + ".");
					}
					if (neighbor.hasLocation()) {
						// all nodes already have locations...bad! - this should never happen
						throw new BndNetworkException("Node with location " + neighborLocation + " has not been found, but all nodes already have locations!");
					} else {
						neighbor.setLocation(neighborLocation);
						pointNodeMap.put(neighbor.getLocation(), neighbor);
						nodesToHandle.add(neighbor);
					}
				}
				nodeToHandle.addNewInEdgeFrom(neighbor);
			}
		}
	}

	private Collection<List<Integer>> getNeighborLocationsForRadius(
		List<Integer> location,
		Metrics<Integer> metrics,
		SpatialTopology spatialTopology
	) {
		// first prepare relative neighbor locations in the spatial order
		if (location == null) {
			throw new BndNetworkException("Reference location expected for neighor locations.");
		}
		List<Integer> zeroPoint = new ArrayList<Integer>(location);
		Collections.fill(zeroPoint, 0);

		List<List<Integer>> relativeNeighborLocations = new ArrayList<List<Integer>>();
		if (spatialTopology.isItsOwnNeighor()) {
			relativeNeighborLocations.add(zeroPoint);
		}
		relativeNeighborLocations.addAll(metrics.getSurroundingPoints(zeroPoint, null, false, spatialTopology.getRadius()));
		Collections.sort(relativeNeighborLocations, new ListComparator<Integer>());

		Collection<List<Integer>> neighborLocations = new ArrayList<List<Integer>>();
		for (List<Integer> relativeNeighborLocation : relativeNeighborLocations) {
			final List<Integer> neighborLocation = metrics.getShiftedPoint(
				location,
				relativeNeighborLocation,
				spatialTopology.getSizes(),
				spatialTopology.isTorusFlag());

			if (neighborLocation != null) {
				neighborLocations.add(neighborLocation);
			}
		}
		return neighborLocations;
	}

	private Collection<List<Integer>> getNeighborLocationsForNeighborhood(
		List<Integer> location,
		Metrics<Integer> metrics,
		SpatialTopology spatialTopology
	) {
		Collection<List<Integer>> neighborLocations = new ArrayList<List<Integer>>();
		final List<SpatialNeighbor> spatialNeighbors = spatialTopology.getNeighborhood().getNeighbors();
		Collections.sort(spatialNeighbors);

		for (SpatialNeighbor spatialNeighbor : spatialNeighbors) {
			final List<Integer> neighborLocation = metrics.getShiftedPoint(
				location,
				spatialNeighbor.getCoordinateDiffs(),
				spatialTopology.getSizes(),
				spatialTopology.isTorusFlag());

			if (neighborLocation != null) {
				neighborLocations.add(neighborLocation);
			}
		}

		return neighborLocations;
	}

	private void addAllConnections(
		Topology fromLayer,
		TopologicalNode toNode,
		boolean allowSelfEdge
	) {
		Collection<TopologicalNode> fromNodes = null;
		if (allowSelfEdge) {
			fromNodes = fromLayer.getNonBiasNodes();
		} else {
			fromNodes = new ArrayList<TopologicalNode>(fromLayer.getNonBiasNodes());
			fromNodes.remove(toNode);
		}
		toNode.addNewInEdgesFrom(fromNodes);
	}

	private void addConnections(
		Topology fromLayer,
		TopologicalNode toNode,
		int inEdgesNum,
		boolean allowMultiEdges,
		boolean allowSelfEdge
	) {
		addConnections(fromLayer.getNonBiasNodes(), toNode, inEdgesNum, allowMultiEdges, allowSelfEdge);
	}

	private void addConnections(
		Collection<TopologicalNode> fromNodes,
		TopologicalNode toNode,
		int inEdgesNum,
		boolean allowMultiEdges,
		boolean allowSelfEdge
	) {
		if (!allowSelfEdge) {
			fromNodes.remove(toNode);
		}
		Collection<TopologicalNode> selectedNodes = null;
		if (allowMultiEdges) {
			selectedNodes = RandomUtil.nextElementsWithRepetitions(fromNodes, inEdgesNum);	
		} else {
			selectedNodes = RandomUtil.nextElementsWithoutRepetitions(fromNodes, inEdgesNum);
		}
		toNode.addNewInEdgesFrom(selectedNodes);
	}

  private void addConnectionsWithMultiAllowed(
    Collection<TopologicalNode> fromNodes,
    Collection<TopologicalNode> toNodes,
    int edgesNum,
    boolean allowSelfEdge
  ) {
    final Collection<TopologicalNode> selectedFromNodes = RandomUtil.nextElementsWithRepetitions(fromNodes, edgesNum);
    for (TopologicalNode fromNode : selectedFromNodes) {
      Collection<TopologicalNode> specToNodes = null;
        if (allowSelfEdge) {
          specToNodes = toNodes;
        } else {
          specToNodes = new ArrayList<>(toNodes);
          specToNodes.remove(fromNode);
        }
      fromNode.addNewOutEdgeTo(RandomUtil.nextElement(specToNodes));
    }
  }

  private void addConnectionsWithMultiNotAllowed(
    Collection<TopologicalNode> fromNodes,
    Collection<TopologicalNode> toNodes,
    int edgesNum,
    boolean allowSelfEdge
  ) {
    final Collection<Pair<TopologicalNode, TopologicalNode>> availableNodePairs = new ArrayList<>();
    for (TopologicalNode fromNode : fromNodes)
      for (TopologicalNode toNode : toNodes)
        if (allowSelfEdge || fromNode != toNode)
          availableNodePairs.add(new Pair(fromNode, toNode));

    final Collection<Pair<TopologicalNode, TopologicalNode>> selectedNodePairs = RandomUtil.nextElementsWithoutRepetitions(availableNodePairs, edgesNum);
    for (Pair<TopologicalNode, TopologicalNode> nodePair : selectedNodePairs)
      nodePair.getFirst().addNewOutEdgeTo(nodePair.getSecond());
  }

  private void addConnectionsAsPreferentialAttachment(
      Collection<TopologicalNode> nodes,
      int inEdgesNum,
      boolean allowMultiEdges
  ) {
      TopologicalNode[] nodesArray = nodes.toArray(new TopologicalNode[0]);
      Map<TopologicalNode, Integer> nodeIndexMap = new HashMap<>();
      int i = 0;
      for (TopologicalNode node : nodesArray) {
          nodeIndexMap.put(node, i);
          i++;
      }
      double[] inOutDegrees = new double[nodes.size()];
      double[] inDegrees = new double[nodes.size()];

      // create a complete network of size "inEdgesNum"
      Collection<TopologicalNode> handledNodes = RandomUtil.nextElementsWithoutRepetitions(nodes, inEdgesNum + 1);
      for (TopologicalNode fromNode : handledNodes) {
          int fromIndex = nodeIndexMap.get(fromNode);
          for (TopologicalNode toNode : handledNodes)
              if (fromNode != toNode) {
                  int toIndex = nodeIndexMap.get(fromNode);
                  fromNode.addNewOutEdgeTo(toNode);
                  inOutDegrees[fromIndex]++;
                  inOutDegrees[toIndex]++;
                  inDegrees[toIndex]++;
              }
      }

      Collection<TopologicalNode> remainingNodes = new ArrayList<>(nodes);
      remainingNodes.removeAll(handledNodes);

      for (TopologicalNode toNode : remainingNodes) {
          int toIndex = nodeIndexMap.get(toNode);
          for (int j = 0; j < inEdgesNum; j++) {
              if (allowMultiEdges) {
                  final RandomDistributionProvider<TopologicalNode> rdp = RandomDistributionProviderFactory.applyDiscrete(new DiscreteDistribution<>(inOutDegrees, nodesArray));
                  final TopologicalNode fromNode = rdp.next();
                  int fromIndex = nodeIndexMap.get(fromNode);

                  fromNode.addNewOutEdgeTo(toNode);
                  inOutDegrees[fromIndex]++;
                  inOutDegrees[toIndex]++;
                  inDegrees[toIndex]++;
              } else {
                  final double[] tempInOutDegrees = inOutDegrees.clone();
                  final RandomDistributionProvider<TopologicalNode> rdp = RandomDistributionProviderFactory.applyDiscrete(new DiscreteDistribution<>(tempInOutDegrees, nodesArray));
                  final TopologicalNode fromNode = rdp.next();
                  int fromIndex = nodeIndexMap.get(fromNode);

                  fromNode.addNewOutEdgeTo(toNode);
                  inOutDegrees[fromIndex]++;
                  inOutDegrees[toIndex]++;
                  inDegrees[toIndex]++;
                  // set the probability of a chosen node to zero not to be chosen again
                  tempInOutDegrees[fromIndex] = 0;
              }
          }
      }

//        final List<Integer> degrees = new ArrayList<>();
//        for (double degree : inOutDegrees) {
//            degrees.add((int) degree);
//        }
//        Collections.sort(degrees);
//        System.out.println("IN/OUT: " + StringUtils.join(degrees, ","));
//
//        final List<Integer> degrees2 = new ArrayList<>();
//        for (double degree : inDegrees) {
//            degrees2.add((int) degree);
//        }
//        Collections.sort(degrees2);
//        System.out.println("IN   : " + StringUtils.join(degrees2, ","));
//        System.out.println("");
  }
}